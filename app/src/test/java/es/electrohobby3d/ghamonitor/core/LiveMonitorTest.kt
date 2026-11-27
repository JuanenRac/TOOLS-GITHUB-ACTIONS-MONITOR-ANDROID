// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the client and its real network code against RUNNING demo monitors. Skipped unless LIVE_MONITOR=1: the CI job "contract" starts them.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test

class LiveMonitorTest {
    private val config = Assets.config()

    // the same invented values as tools/contract/demo_server.py
    private val access = "demo-access-0123456789"
    private val user = "demo-admin"
    private val passphrase = "demo-passphrase-2468"

    @Before fun onlyWithTheMonitorsRunning() = assumeTrue(System.getenv("LIVE_MONITOR") == "1")

    private fun api(port: Int, token: String = access, session: String = "") = MonitorApi("http://127.0.0.1:$port", token, session, HttpUrlTransport(config.api), config)

    private fun refused(expected: ErrorKey, call: () -> Any?) {
        try {
            call()
        } catch (e: ApiException) {
            assertEquals(expected, e.key)
            return
        }
        fail("not refused: $expected")
    }

    @Test fun everyScreenReadsTheMonitor() {
        val one = api(19801)
        assertTrue(!one.me().needsSignIn)
        val summary = loadSummary(one)
        assertEquals(null, summary.error)
        assertTrue(summary.health!!.ok)
        assertTrue(summary.alerts!!.items.isNotEmpty())
        assertTrue(summary.team!!.repos.isNotEmpty())
        assertTrue(one.alerts(level = "error").items.all { it.level == "error" })
        assertTrue(one.teamHealth("gamma").repos.all { "gamma" in it.repo })
        assertTrue(one.proposals().items.isNotEmpty())
        assertEquals(emptyList<Account>(), one.accounts())
    }

    @Test fun aWrongTokenIsRefused() = refused(ErrorKey.UNAUTHORIZED) { api(19801, token = "wrong-token-000000").alerts() }

    @Test fun signingInAndOutInTheTeamMode() {
        val anonymous = api(19802)
        assertTrue(anonymous.me().needsSignIn)
        refused(ErrorKey.SIGN_IN) { anonymous.alerts() }
        refused(ErrorKey.INVALID_CREDENTIALS) { anonymous.login(user, "not the passphrase") }
        val session = anonymous.login(user, passphrase)
        val signed = api(19802, session = session.token)
        assertTrue(signed.me().authenticated)
        assertTrue(signed.alerts().items.isNotEmpty())
        assertTrue(signed.teamHealth().repos.isNotEmpty())
        signed.logout()
        refused(ErrorKey.SIGN_IN) { signed.alerts() }
    }

    @Test fun aMonitorThatGivesNoSessionsSaysSo() = refused(ErrorKey.BEARER_OFF) { api(19803).login(user, passphrase) }

    @Test fun severalAccounts() = assertEquals(3, api(19804).accounts().size)
}

// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the client against answers RECORDED from a demo monitor (app/src/test/resources/contract, made by tools/contract/record.py).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// Nothing here is written by hand to look like the monitor: a screen that would be empty or broken with a real answer fails here.
package es.electrohobby3d.ghamonitor.core

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class Recorded(val method: String, val path: String, val status: Int, val body: JSONObject?) {
    companion object {
        fun of(name: String): Recorded {
            val text = checkNotNull(Recorded::class.java.getResource("/contract/$name.json")) { "no recorded answer $name" }.readText(Charsets.UTF_8)
            val o = JSONObject(text)
            val request = o.getString("request").split(" ", limit = 2)
            return Recorded(request[0], request[1], o.getInt("status"), o.optJSONObject("body"))
        }
    }

    fun result() = HttpResult(status, body?.toString() ?: "")
}

class ContractTest {
    private val config = Assets.config()

    /** A client that gets one recorded answer, and the check that it asked the very request the answer was recorded from. */
    private fun <T> ask(name: String, session: String = "", call: (MonitorApi) -> T): T {
        val recorded = Recorded.of(name)
        val transport = FakeTransport { sent ->
            assertEquals(recorded.method, sent.method)
            assertEquals("http://192.168.1.5:8765" + recorded.path, sent.url)
            recorded.result()
        }
        return call(MonitorApi("http://192.168.1.5:8765", "access-token", session, transport, config))
    }

    private fun refusal(name: String, call: (MonitorApi) -> Any?): ErrorKey {
        try {
            ask(name, call = call)
        } catch (e: ApiException) {
            return e.key
        }
        fail("$name was not refused")
        throw IllegalStateException()
    }

    @Test fun healthSaysTheVersionAndTheEdition() {
        val health = ask("health") { it.health() }
        assertTrue(health.ok)
        assertFalse(health.lite)
        assertTrue(health.version.startsWith("2."))
    }

    @Test fun everyAlertOfTheAnswerIsRead() {
        val body = Recorded.of("alerts").body!!
        val alerts = ask("alerts") { it.alerts() }
        assertEquals(body.getJSONArray("alerts").length(), alerts.items.size)
        assertTrue(alerts.items.isNotEmpty())
        assertEquals(body.getJSONObject("summary").getInt("total"), alerts.total)
        assertTrue(alerts.byLevel.isNotEmpty())
        for (a in alerts.items) {
            assertTrue(a.id.isNotEmpty() && a.title.isNotEmpty() && a.createdAt.isNotEmpty())
            assertTrue(a.level, Level.parse(a.level) != null)
        }
    }

    @Test fun theRankingOfRepositoriesIsRead() {
        val body = Recorded.of("team_health").body!!
        val team = ask("team_health") { it.teamHealth() }
        assertEquals(body.getJSONArray("ranking").length(), team.repos.size)
        assertTrue(team.repos.isNotEmpty())
        assertEquals(team.all, team.ok + team.warn + team.bad + team.unknown)
        for (row in team.repos) assertTrue(row.state, row.state in listOf("ok", "warn", "bad", "unknown"))
        assertTrue(team.repos.any { it.redWorkflows > 0 || it.alerts > 0 })
    }

    @Test fun theProposalsShowTheExactCommand() {
        val list = ask("proposals") { it.proposals() }
        assertTrue(list.enabled)
        val p = list.items.first()
        assertTrue(p.command.startsWith("gh "))
        assertEquals(64, p.hash.length)
        assertEquals("pending", p.status)
        assertTrue(p.expiresInSeconds > 0)
        assertTrue(DeepLink.matches(DeepLink.parse(DeepLink.make(p.id, p.hash))!!, p))
    }

    @Test fun aMonitorOfOneAccountHasNoListOfAccounts() {
        assertEquals(404, Recorded.of("accounts_single").status)
        assertEquals(emptyList<Account>(), ask("accounts_single") { it.accounts() })
    }

    @Test fun aMonitorOfSeveralAccountsListsThem() {
        val accounts = ask("accounts") { it.accounts() }
        assertEquals(3, accounts.size)
        for (account in accounts) assertTrue(account.state, severityOf(account.state) >= 0)
        assertTrue(ask("health_accounts") { it.health() }.ok)
    }

    @Test fun theIndividualModeAsksForNoSignIn() {
        val auth = ask("auth_me") { it.me() }
        assertFalse(auth.teamMode)
        assertFalse(auth.needsSignIn)
    }

    @Test fun theTeamModeAsksForASignInUntilThereIsASession() {
        assertTrue(ask("auth_me_team") { it.me() }.needsSignIn)
        val signedIn = ask("auth_me_signed_in", session = "msn_x") { it.me() }
        assertTrue(signedIn.teamMode && signedIn.authenticated && !signedIn.needsSignIn)
        assertEquals("demo-admin", signedIn.user)
        assertEquals("admin", signedIn.role)
    }

    @Test fun signingInGivesASessionToken() {
        val session = ask("login") { it.login("demo-admin", "x") }
        assertTrue(session.token.startsWith("msn_"))
        assertEquals("demo-admin", session.user)
        ask("logout", session = session.token) { it.logout() }
    }

    @Test fun everyRefusalOfTheMonitorHasItsOwnWord() {
        assertEquals(ErrorKey.UNAUTHORIZED, refusal("access_refused") { it.alerts() })
        assertEquals(ErrorKey.HOST_NOT_ALLOWED, refusal("host_refused") { it.alerts() })
        assertEquals(ErrorKey.SIGN_IN, refusal("sign_in_needed") { it.alerts() })
        assertEquals(ErrorKey.INVALID_CREDENTIALS, refusal("login_wrong") { it.login("demo-admin", "x") })
        assertEquals(ErrorKey.BEARER_OFF, refusal("login_bearer_off") { it.login("demo-admin", "x") })
    }

    @Test fun theSummaryIsFilledFromTheRecordedAnswers() {
        val transport = FakeTransport { sent ->
            val path = sent.url.removePrefix("http://192.168.1.5:8765").substringBefore('?')
            Recorded.of(
                when (path) {
                    "/api/health" -> "health"
                    "/api/alerts" -> "alerts"
                    "/api/team/health" -> "team_health"
                    else -> "accounts_single"
                },
            ).result()
        }
        val summary = loadSummary(MonitorApi("http://192.168.1.5:8765", "t", "", transport, config), 1000)
        assertEquals(null, summary.error)
        assertTrue(summary.alerts!!.items.isNotEmpty())
        assertTrue(summary.team!!.repos.isNotEmpty())
    }
}

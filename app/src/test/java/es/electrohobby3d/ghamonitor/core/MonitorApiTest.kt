// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - what the client sends and what it makes of an answer that is not the promised one (the real shapes are in ContractTest).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

import java.io.ByteArrayInputStream
import java.io.IOException
import org.json.JSONObject
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MonitorApiTest {
    private val config = Assets.config()
    private val health = """{"ok":true,"version":"2.2.0","lite":false,"loaded":true,"offline":false}"""

    private fun api(transport: FakeTransport, session: String = "") = MonitorApi("https://gha.example", "the-access-token", session, transport, config)

    private fun failure(block: () -> Any?): ApiException {
        try {
            block()
        } catch (e: ApiException) {
            return e
        }
        fail("no failure")
        throw IllegalStateException()
    }

    @Test fun theAccessTokenTravelsInItsOwnHeader() {
        val transport = FakeTransport.ok(health)
        api(transport).health()
        val sent = transport.requests.single()
        assertEquals("the-access-token", sent.headers["X-Monitor-Access"])
        assertNull(sent.headers["Authorization"])
        assertEquals("https://gha.example/api/health", sent.url)
    }

    @Test fun theSessionOfAPersonGoesAsABearerBesideIt() {
        val transport = FakeTransport.ok(health)
        api(transport, session = "msn_session").health()
        val sent = transport.requests.single()
        assertEquals("the-access-token", sent.headers["X-Monitor-Access"])
        assertEquals("Bearer msn_session", sent.headers["Authorization"])
    }

    @Test fun signingInAsksForATokenForAnApp() {
        val transport = FakeTransport.ok("""{"ok":true,"user":"ana","role":"viewer","token":"msn_abc"}""")
        assertEquals(Session("msn_abc", "ana", "viewer"), api(transport).login("ana", "p"))
        val sent = transport.requests.single()
        assertEquals("POST", sent.method)
        assertEquals("application/json", sent.headers["Content-Type"])
        val body = JSONObject(sent.body!!)
        assertEquals("ana", body.getString("username"))
        assertEquals("api", body.getString("client"))
        assertFalse(sent.url.contains("ana"))
    }

    @Test fun theFiltersGoInTheQueryAndAreEncoded() {
        val transport = FakeTransport.ok("""{"alerts":[],"summary":{"total":0,"muted":0,"by_level":{}}}""")
        api(transport).alerts(level = "error", muted = "hide")
        assertEquals("https://gha.example/api/alerts?level=error&muted=hide", transport.requests.single().url)
        assertEquals("?q=a+b%26c", MonitorApi.queryString(listOf("q" to "a b&c", "empty" to "", "none" to null)))
    }

    @Test fun anAnswerWithoutItsListIsAnErrorAndNeverAnEmptyScreen() {
        for (body in listOf("""{"items":[]}""", """[]""", "not json", "")) {
            assertEquals(body, ErrorKey.BAD_RESPONSE, failure { api(FakeTransport.ok(body)).alerts() }.key)
            assertEquals(body, ErrorKey.BAD_RESPONSE, failure { api(FakeTransport.ok(body)).teamHealth() }.key)
            assertEquals(body, ErrorKey.BAD_RESPONSE, failure { api(FakeTransport.ok(body)).proposals() }.key)
        }
    }

    @Test fun aRefusalWithoutAReadableBodyIsToldByItsStatus() {
        val cases = mapOf(400 to ErrorKey.BAD_REQUEST, 401 to ErrorKey.UNAUTHORIZED, 403 to ErrorKey.FORBIDDEN, 404 to ErrorKey.NOT_FOUND, 429 to ErrorKey.RATE_LIMITED,
            503 to ErrorKey.SERVER_ERROR, 302 to ErrorKey.UNKNOWN)
        for ((status, key) in cases) assertEquals("$status", key, failure { api(FakeTransport.status(status, "<html>")).health() }.key)
    }

    @Test fun aLockOutIsToldApart() {
        assertEquals(ErrorKey.LOCKED, failure { api(FakeTransport.status(429, """{"error":"locked"}""")).health() }.key)
        assertEquals(ErrorKey.LOCKED, failure { api(FakeTransport.status(429, """{"ok":false,"error":"x","code":"locked"}""")).login("a", "b") }.key)
    }

    @Test fun noAnswerIsANetworkErrorThatNamesNothing() {
        val error = failure { api(FakeTransport.down()).health() }
        assertEquals(ErrorKey.NETWORK, error.key)
        assertFalse(error.message!!.contains("192.168"))
        assertFalse(error.message!!.contains("the-access-token"))
    }

    @Test fun aSummaryStopsAfterARefusedTokenAndDoesNotHammerTheMonitor() {
        val transport = FakeTransport.status(401, """{"error":"access"}""")
        val summary = loadSummary(api(transport), 0)
        assertEquals(ErrorKey.UNAUTHORIZED, summary.error)
        assertEquals(1, transport.requests.size)
    }

    @Test fun eachPartOfTheSummaryFailsByItself() {
        val transport = FakeTransport { sent ->
            when {
                sent.url.endsWith("/api/health") -> HttpResult(200, health)
                sent.url.contains("/api/team/health") -> HttpResult(500, "")
                sent.url.contains("/api/alerts") -> HttpResult(200, """{"alerts":[],"summary":{"total":0,"muted":0,"by_level":{}}}""")
                else -> HttpResult(404, "")
            }
        }
        val summary = loadSummary(api(transport), 0)
        assertEquals(ErrorKey.SERVER_ERROR, summary.error)
        assertTrue(summary.health != null && summary.alerts != null)
        assertNull(summary.team)
    }

    @Test fun theLiteEditionIsNotAskedForWhatItLacks() {
        val transport = FakeTransport.ok("""{"ok":true,"version":"2.2.0","lite":true}""")
        val summary = loadSummary(api(transport), 0)
        assertTrue(summary.health!!.lite)
        assertNull(summary.error)
        assertEquals(1, transport.requests.size)
    }

    @Test fun anAnswerOverTheLimitIsRefused() {
        assertArrayEquals(byteArrayOf(1, 2, 3), HttpUrlTransport.readLimited(ByteArrayInputStream(byteArrayOf(1, 2, 3)), 3))
        try {
            HttpUrlTransport.readLimited(ByteArrayInputStream(ByteArray(20_000)), 10_000)
            fail("no limit")
        } catch (e: IOException) {
            assertEquals("answer too large", e.message)
        }
    }
}

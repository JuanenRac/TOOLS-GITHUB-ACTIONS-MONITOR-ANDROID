package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class MonitorApiTest {
    private val config = Assets.config()
    private fun api(transport: Transport, token: String = "tok-1234567890") = MonitorApi("https://monitor.example", token, transport, config)

    @Test fun everyRequestCarriesTheBearerTokenAndTheVersion() {
        val transport = FakeTransport.ok("""{"ok":true,"edition":"complete","version":"1.9.0"}""")
        val health = api(transport).health()
        assertTrue(health.ok)
        assertEquals("1.9.0", health.version)
        val sent = transport.requests.single()
        assertEquals("GET", sent.method)
        assertEquals("https://monitor.example/api/v1/health", sent.url)
        assertEquals("Bearer tok-1234567890", sent.headers["Authorization"])
        assertEquals("1", sent.headers["X-API-Version"])
        assertEquals("mobile", sent.headers["X-Client-Profile"])
    }

    @Test fun alertsAreReadAsPagesWithTheNextCursor() {
        val body = """{"items":[{"id":"a1","code":"ci.red","level":"critical","repo":"o/r","created_at":"1700000000"},{"id":"a2","code":"ci.slow","level":"warning","repo":"o/s","title":null}],"next_cursor":"abc.def","limit":30}"""
        val page = api(FakeTransport.ok(body)).alerts()
        assertEquals(listOf("a1", "a2"), page.items.map { it.id })
        assertEquals("abc.def", page.nextCursor)
        assertEquals("", page.items[1].title)           // JSON null is an empty text, not the word "null"
        assertEquals("", page.items[0].sentinel)        // the compact profile leaves fields out
    }

    @Test fun theCursorAndFiltersGoInTheQueryAndAreEncoded() {
        val transport = FakeTransport.ok("""{"items":[],"next_cursor":null,"limit":30}""")
        val page = api(transport).alerts(cursor = "a b/c.d", level = "critical")
        assertNull(page.nextCursor)
        val url = transport.requests.single().url
        assertTrue(url, url.startsWith("https://monitor.example/api/v1/alerts?limit=30&cursor=a+b%2Fc.d&level=critical"))
        assertFalse(url.contains("account="))
    }

    @Test fun aBareArrayIsReadToo() {
        val page = api(FakeTransport.ok("""[{"id":"x","label":"Main"}]""")).accounts()
        assertEquals("Main", page.single().label)
    }

    @Test fun theErrorEnvelopeGivesAStableKey() {
        val transport = FakeTransport.status(400, """{"error":"bad_request","code":"api.bad_cursor","message":"x"}""")
        val e = assertThrows(ApiException::class.java) { api(transport).alerts(cursor = "zzz") }
        assertEquals(ErrorKey.BAD_CURSOR, e.key)
        assertEquals(400, e.status)
    }

    @Test fun aStatusWithoutAnEnvelopeStillGivesAKey() {
        assertEquals(ErrorKey.UNAUTHORIZED, assertThrows(ApiException::class.java) { api(FakeTransport.status(401, "<html>")).status() }.key)
        assertEquals(ErrorKey.FORBIDDEN, assertThrows(ApiException::class.java) { api(FakeTransport.status(403, "")).status() }.key)
        assertEquals(ErrorKey.NOT_FOUND, assertThrows(ApiException::class.java) { api(FakeTransport.status(404, "")).status() }.key)
        assertEquals(ErrorKey.RATE_LIMITED, assertThrows(ApiException::class.java) { api(FakeTransport.status(429, "")).status() }.key)
        assertEquals(ErrorKey.SERVER_ERROR, assertThrows(ApiException::class.java) { api(FakeTransport.status(503, "")).status() }.key)
        assertEquals(ErrorKey.UNKNOWN, assertThrows(ApiException::class.java) { api(FakeTransport.status(418, "")).status() }.key)
    }

    @Test fun everyCodeOfTheMonitorHasItsOwnKey() {
        val codes = listOf(
            "api.bad_request", "api.unauthorized", "api.forbidden", "api.not_found", "api.bad_cursor", "api.bad_limit", "api.unknown_field",
            "api.version_unsupported", "api.server_error", "act.not_allowed", "act.hash_mismatch", "act.expired", "act.exec_disabled", "act.bad_state",
        )
        for (code in codes) assertTrue(code, ErrorKey.forCode(code) != ErrorKey.UNKNOWN)
        assertEquals(codes.size, codes.map { ErrorKey.forCode(it) }.toSet().size)
    }

    @Test fun aNetworkFailureNeverCarriesTheAddress() {
        val e = assertThrows(ApiException::class.java) { api(FakeTransport.down()).health() }
        assertEquals(ErrorKey.NETWORK, e.key)
        assertFalse(e.message.orEmpty().contains("192.168"))
    }

    @Test fun theTokenIsNeverInAnErrorText() {
        val e = assertThrows(ApiException::class.java) { api(FakeTransport.status(401, ""), token = "secret-token-value").status() }
        assertFalse(e.toString().contains("secret-token-value"))
    }

    @Test fun anUnreadableBodyIsOneKindOfFailure() {
        assertEquals(ErrorKey.BAD_RESPONSE, assertThrows(ApiException::class.java) { api(FakeTransport.ok("not json")).health() }.key)
        assertEquals(ErrorKey.BAD_RESPONSE, assertThrows(ApiException::class.java) { api(FakeTransport.ok("{broken")).alerts() }.key)
    }

    @Test fun approvingSendsTheHashThePersonWasShown() {
        val hash = "b".repeat(64)
        val transport = FakeTransport.ok(
            """{"id":"p000001","account_id":"main","repo":"o/r","action_type":"rerun","state":"approved","content_hash":"$hash","approved_by":"ana","created_at":1,"expires_at":2}""",
        )
        val result = api(transport).approve("p000001", hash)
        assertEquals("approved", result.state)
        val sent = transport.requests.single()
        assertEquals("POST", sent.method)
        assertEquals("https://monitor.example/api/v1/actions/proposals/p000001/approve", sent.url)
        assertEquals("application/json", sent.headers["Content-Type"])
        assertTrue(sent.body!!.contains("\"expected_hash\":\"$hash\""))
    }

    @Test fun rejectingCutsTheNoteAndSendsNoHash() {
        val transport = FakeTransport.ok("""{"id":"p1x","state":"rejected"}""")
        api(transport).reject("p1x", "n".repeat(500))
        val body = transport.requests.single().body!!
        assertFalse(body.contains("expected_hash"))
        assertTrue(body.length < 260)
    }

    @Test fun anIdThatIsNotAPlainWordNeverReachesTheNetwork() {
        val transport = FakeTransport.ok("{}")
        val e = assertThrows(ApiException::class.java) { api(transport).approve("../x", "h") }
        assertEquals(ErrorKey.BAD_REQUEST, e.key)
        assertTrue(transport.requests.isEmpty())
    }

    @Test fun registeringADeviceGivesTheId() {
        val transport = FakeTransport.ok("""{"id":"abc123def456"}""")
        assertEquals("abc123def456", api(transport).registerDevice("t".repeat(30), "android"))
        val sent = transport.requests.single()
        assertEquals("https://monitor.example/api/v1/devices", sent.url)
        assertTrue(sent.body!!.contains("\"platform\":\"android\""))
    }

    @Test fun anAnswerWithoutADeviceIdIsRefused() {
        val e = assertThrows(ApiException::class.java) { api(FakeTransport.ok("{}")).registerDevice("t".repeat(30), "android") }
        assertEquals(ErrorKey.BAD_RESPONSE, e.key)
    }

    @Test fun unregisteringUsesDelete() {
        val transport = FakeTransport.status(204, "")
        api(transport).unregisterDevice("abc123def456")
        val sent = transport.requests.single()
        assertEquals("DELETE", sent.method)
        assertEquals("https://monitor.example/api/v1/devices/abc123def456", sent.url)
        assertNull(sent.body)
    }

    @Test fun theRepoHealthPathNamesOwnerAndRepo() {
        val transport = FakeTransport.ok("""{"repo":"acme/web","level":"warning"}""")
        assertEquals("warning", api(transport).repoHealth("acme", "web").level)
        assertEquals("https://monitor.example/api/v1/repos/acme/web/health", transport.requests.single().url)
    }
}

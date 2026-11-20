package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MonitorUrlTest {
    private fun good(input: String) = MonitorUrl.check(input) as UrlCheck.Good
    private fun bad(input: String) = (MonitorUrl.check(input) as UrlCheck.Bad).reason

    @Test fun anAddressWithoutASchemeIsHttps() {
        val g = good("monitor.example")
        assertEquals("https://monitor.example", g.base)
        assertTrue(g.secure)
    }

    @Test fun theTrailingSlashAndTheCaseOfTheHostAreNormalised() {
        assertEquals("https://monitor.example:8443/gha", good("  HTTPS://Monitor.Example:8443/gha/ ").base)
    }

    @Test fun httpIsAcceptedOnTheHomeNetworkOnly() {
        for (host in listOf("localhost", "monitor.local", "10.0.0.5", "192.168.0.180", "172.16.3.4", "172.31.255.255", "127.0.0.1")) {
            val g = good("http://$host:8765")
            assertFalse(host, g.secure)
        }
        for (host in listOf("example.com", "8.8.8.8", "172.32.0.1", "192.169.0.1", "172.15.0.1")) {
            assertEquals(host, UrlProblem.CLEAR_TEXT_OUTSIDE_HOME, bad("http://$host"))
        }
    }

    @Test fun otherSchemesAreRefused() {
        assertEquals(UrlProblem.SCHEME, bad("ftp://monitor.example"))
        assertEquals(UrlProblem.SCHEME, bad("javascript://x"))
    }

    @Test fun credentialsQueriesAndFragmentsAreRefused() {
        assertEquals(UrlProblem.CREDENTIALS, bad("https://user:pw@monitor.example"))
        assertEquals(UrlProblem.EXTRA, bad("https://monitor.example/?token=1"))
        assertEquals(UrlProblem.EXTRA, bad("https://monitor.example/#x"))
    }

    @Test fun emptyAndUnreadableAddressesAreRefused() {
        assertEquals(UrlProblem.EMPTY, bad("   "))
        assertEquals(UrlProblem.UNREADABLE, bad("https://exa mple.com"))
        assertEquals(UrlProblem.UNREADABLE, bad("https://"))
        assertEquals(UrlProblem.HOST, bad("https:///path"))
        assertFalse(MonitorUrl.isHomeHost("10.0.0.256"))
    }
}

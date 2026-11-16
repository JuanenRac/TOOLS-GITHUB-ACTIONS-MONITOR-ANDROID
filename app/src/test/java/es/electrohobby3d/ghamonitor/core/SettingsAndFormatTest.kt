package es.electrohobby3d.ghamonitor.core

import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsAndFormatTest {
    // ---- settings ----

    @Test fun theTokenIsKeptSealedAndNeverAsPlainText() {
        val store = MemoryStore()
        val repo = SettingsRepository(store, ReverseBox())
        repo.save("https://monitor.example", "  my-secret-token  ", " Ana ")
        assertTrue(store.map.values.none { it.contains("my-secret-token") })
        val loaded = repo.load()
        assertEquals(MonitorSettings("https://monitor.example", "my-secret-token", "Ana"), loaded)
        assertTrue(loaded.configured)
    }

    @Test fun savingWithABlankTokenKeepsTheOldOne() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "first-token", "")
        repo.save("https://b.example", "   ", "Bea")
        val loaded = repo.load()
        assertEquals("first-token", loaded.token)
        assertEquals("https://b.example", loaded.baseUrl)
    }

    @Test fun aTokenThatCannotBeOpenedIsDroppedNotGuessed() {
        val store = MemoryStore()
        store.put(SettingsRepository.KEY_URL, "https://a.example")
        store.put(SettingsRepository.KEY_TOKEN, "garbage")
        val loaded = SettingsRepository(store, ReverseBox()).load()
        assertEquals("", loaded.token)
        assertFalse(loaded.configured)
        assertNull(store.get(SettingsRepository.KEY_TOKEN))
    }

    @Test fun forgettingTheTokenKeepsTheAddress() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "tok-123456", "")
        repo.forgetToken()
        val loaded = repo.load()
        assertEquals("https://a.example", loaded.baseUrl)
        assertEquals("", loaded.token)
    }

    @Test fun clearingRemovesEverything() {
        val store = MemoryStore()
        val repo = SettingsRepository(store, ReverseBox())
        repo.save("https://a.example", "tok-123456", "Ana")
        repo.clearAll()
        assertTrue(store.map.isEmpty())
    }

    @Test fun theNameIsCutToSixtyFourCharacters() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "tok-123456", "n".repeat(200))
        assertEquals(64, repo.load().userName.length)
    }

    // ---- text rules ----

    private val utc = ZoneId.of("UTC")

    @Test fun momentsAreReadInThreeShapes() {
        assertEquals("2023-11-14 22:13", Format.moment("1700000000", utc))
        assertEquals("2023-11-14 22:13", Format.moment("1700000000000", utc))
        assertEquals("2023-11-14 22:13", Format.moment("2023-11-14T22:13:20Z", utc))
        assertEquals("not a date", Format.moment("not a date", utc))
        assertEquals("", Format.moment("", utc))
    }

    @Test fun durationsAreShort() {
        assertEquals("<1m", Format.duration(59))
        assertEquals("7m", Format.duration(7 * 60 + 5))
        assertEquals("5h 12m", Format.duration(5 * 3600 + 12 * 60))
        assertEquals("2d 03h", Format.duration(2 * 86_400 + 3 * 3600))
    }

    @Test fun reposAreSortedByTheirWorstAlert() {
        fun alert(repo: String, level: String) = Alert("i-$repo-$level", "c", level, "", "", repo, "", "")
        val rows = Format.reposByWorst(
            listOf(alert("z/z", "warning"), alert("a/a", "info"), alert("b/b", "critical"), alert("b/b", "warning"), alert("a/b", "critical"), alert("", "critical")),
        )
        assertEquals(listOf("a/b" to "critical", "b/b" to "critical", "z/z" to "warning", "a/a" to "info"), rows)
    }

    @Test fun aRepoNameIsSplitOrRefused() {
        assertEquals("acme" to "web.site-1", Format.splitRepo(" acme/web.site-1 "))
        for (bad in listOf("", "acme", "acme/", "/web", "a/b/c", "a b/c", "acme/web?x=1")) assertNull(bad, Format.splitRepo(bad))
    }

    // ---- words ----

    @Test fun severityWordsAreMappedAndUnknownOnesAreNotGuessed() {
        assertEquals(0, severityOf("ok"))
        assertEquals(0, severityOf("GREEN"))
        assertEquals(1, severityOf("warning"))
        assertEquals(2, severityOf("error"))
        assertEquals(3, severityOf("critical"))
        assertEquals(-1, severityOf("purple"))
        assertEquals(-1, severityOf(null))
    }

    @Test fun levelsAreOrdered() {
        assertTrue(Level.CRITICAL.rank > Level.ERROR.rank && Level.ERROR.rank > Level.WARNING.rank && Level.WARNING.rank > Level.INFO.rank)
        assertEquals(Level.WARNING, Level.parse("warning"))
        assertNull(Level.parse("Warning"))
    }

    @Test fun aProposalIsReadWithItsNumbers() {
        val p = Parsers.proposal(org.json.JSONObject("""{"id":"p1","account_id":"main","repo":"o/r","action_type":"label","state":"pending","created_at":1700000000,"expires_at":null,"content_hash":"h","approved_by":null}"""))
        assertEquals("label", p.actionType)
        assertEquals(1_700_000_000L, p.createdAt)
        assertEquals(0L, p.expiresAt)
        assertEquals("", p.approvedBy)
    }
}

package es.electrohobby3d.ghamonitor.core

import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsAndFormatTest {
    // ---- settings ----

    @Test fun theTokensAreKeptSealedAndNeverAsPlainText() {
        val store = MemoryStore()
        val repo = SettingsRepository(store, ReverseBox())
        repo.save("https://monitor.example", "  my-access-token  ")
        repo.saveSession(" Ana ", "msn_session-token")
        assertTrue(store.map.values.none { it.contains("my-access-token") || it.contains("msn_session-token") })
        val loaded = repo.load()
        assertEquals(MonitorSettings("https://monitor.example", "my-access-token", "msn_session-token", "Ana"), loaded)
        assertTrue(loaded.configured)
    }

    @Test fun savingTheSameAddressWithABlankTokenKeepsTokenAndSession() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "first-token")
        repo.saveSession("ana", "msn_one")
        repo.save("https://a.example", "   ")
        assertEquals("first-token", repo.load().token)
        assertEquals("msn_one", repo.load().session)
    }

    @Test fun anotherAddressNeverReceivesTheOldTokens() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "first-token")
        repo.saveSession("ana", "msn_one")
        repo.save("https://b.example", "")
        val loaded = repo.load()
        assertEquals("https://b.example", loaded.baseUrl)
        assertEquals("", loaded.token)
        assertEquals("", loaded.session)
        assertFalse(loaded.configured)
    }

    @Test fun aNewTokenEndsTheOldSession() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "first-token")
        repo.saveSession("ana", "msn_one")
        repo.save("https://a.example", "second-token")
        assertEquals("", repo.load().session)
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

    @Test fun forgettingTheTokenKeepsTheAddressAndEndsTheSession() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.save("https://a.example", "tok-123456")
        repo.saveSession("ana", "msn_one")
        repo.forgetToken()
        val loaded = repo.load()
        assertEquals("https://a.example", loaded.baseUrl)
        assertEquals("", loaded.token)
        assertEquals("", loaded.session)
    }

    @Test fun theNameIsCutToSixtyFourCharacters() {
        val repo = SettingsRepository(MemoryStore(), ReverseBox())
        repo.saveSession("n".repeat(200), "msn_x")
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

    // ---- words ----

    @Test fun severityWordsAreMappedAndUnknownOnesAreNotGuessed() {
        assertEquals(0, severityOf("ok"))
        assertEquals(0, severityOf("OK"))
        assertEquals(1, severityOf("warn"))
        assertEquals(2, severityOf("bad"))
        assertEquals(-1, severityOf("unknown"))
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
}

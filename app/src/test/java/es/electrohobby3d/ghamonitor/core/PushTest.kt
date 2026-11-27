package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PushTest {
    private val config = Assets.config().push

    private fun message(level: String = "critical", id: String = "alert-1", title: String = "Build failed") =
        mapOf("alert_id" to id, "level" to level, "code" to "ci.red", "title" to title, "view" to "alerts")

    @Test fun theSwitchIsOffUntilThePersonTurnsItOn() {
        assertNull(PushGate(config).accept(message(), enabled = false))
        assertNotNullMessage(PushGate(config).accept(message(), enabled = true))
    }

    private fun assertNotNullMessage(m: PushMessage?) {
        assertEquals("alert-1", m?.alertId)
        assertEquals(Level.CRITICAL, m?.level)
    }

    @Test fun onlyCriticalAlertsGetThrough() {
        val gate = PushGate(config)
        for (level in listOf("info", "warning", "error", "loud", "")) assertNull(level, gate.accept(message(level = level), true))
        assertNotNullMessage(gate.accept(message(), true))
    }

    @Test fun anAlertIsShownOnce() {
        val gate = PushGate(config)
        assertTrue(gate.accept(message(), true) != null)
        assertNull(gate.accept(message(), true))
        assertTrue(gate.accept(message(id = "alert-2"), true) != null)
    }

    @Test fun aDroppedMessageDoesNotUseUpItsId() {
        val gate = PushGate(config)
        assertNull(gate.accept(message(), false))
        assertTrue(gate.accept(message(), true) != null)
    }

    @Test fun aMessageWithoutAnIdIsDropped() {
        assertNull(PushGate(config).accept(message(id = "  "), true))
        assertNull(PushGate(config).accept(message(id = "x".repeat(121)), true))
        assertNull(PushGate(config).accept(mapOf("level" to "critical"), true))
    }

    @Test fun theMemoryOfShownAlertsIsBounded() {
        val gate = PushGate(config)
        for (i in 0 until config.rememberAlertIds + 5) gate.accept(message(id = "id-$i"), true)
        assertTrue(gate.accept(message(id = "id-0"), true) != null)      // the oldest was forgotten
        assertNull(gate.accept(message(id = "id-${config.rememberAlertIds + 4}"), true))
    }

    @Test fun theTitleIsCleanedAndCut() {
        val m = PushGate(config).accept(message(title = "  Build\u0000 failed\n\n on   main " + "z".repeat(300)), true)!!
        assertFalse(m.title.contains("\u0000"))
        assertFalse(m.title.contains("\n"))
        assertTrue(m.title.length <= config.maxTitleChars)
        assertTrue(m.title.startsWith("Build failed on main"))
    }
}

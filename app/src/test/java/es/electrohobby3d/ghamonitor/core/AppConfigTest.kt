package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppConfigTest {
    @Test fun theShippedFileIsValidAndNamesEveryEndpoint() {
        val config = Assets.config()
        assertEquals(AppConfig.REQUIRED_ENDPOINTS.toSet(), config.endpoints.keys)
        assertTrue(config.endpoints.values.all { it.startsWith("/api/") })
    }

    @Test fun pushIsOffByDefaultAndCriticalOnly() {
        val push = Assets.config().push
        assertFalse(push.enabledByDefault)
        assertEquals(Level.CRITICAL, push.onlyLevel)
    }

    @Test fun theEndpointsAreTheRoutesTheMonitorServes() {
        val config = Assets.config()
        assertEquals("/api/alerts", config.path("alerts"))
        assertEquals("/api/team/health", config.path("team_health"))
        assertEquals("/api/ai/actions", config.path("proposals"))
        assertEquals("/api/auth/login", config.path("login"))
    }

    @Test fun anUnknownEndpointIsAnError() {
        assertThrows(ConfigException::class.java) { Assets.config().path("nope") }
    }

    @Test fun brokenJsonIsAConfigException() {
        assertThrows(ConfigException::class.java) { AppConfig.parse("{not json") }
        assertThrows(ConfigException::class.java) { AppConfig.parse("{}") }
    }

    @Test fun aMissingEndpointIsRefusedWhenTheFileIsRead() {
        val text = Assets.configText().replace("\"alerts\": \"/api/alerts\",", "")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }

    @Test fun anEndpointOutsideTheApiIsRefused() {
        val text = Assets.configText().replace("/api/health", "/admin/health")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }

    @Test fun valuesOutOfRangeAreRefused() {
        val text = Assets.configText().replace("\"connect_timeout_ms\": 8000", "\"connect_timeout_ms\": 5")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }

    @Test fun anUnknownPushLevelIsRefused() {
        val text = Assets.configText().replace("\"only_level\": \"critical\"", "\"only_level\": \"loud\"")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }
}

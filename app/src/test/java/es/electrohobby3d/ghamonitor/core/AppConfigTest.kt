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
        assertTrue(config.endpoints.values.all { it.startsWith("/api/v1/") })
    }

    @Test fun pushIsOffByDefaultAndCriticalOnly() {
        val push = Assets.config().push
        assertFalse(push.enabledByDefault)
        assertEquals(Level.CRITICAL, push.onlyLevel)
    }

    @Test fun placeholdersAreFilled() {
        assertEquals("/api/v1/repos/acme/web/health", Assets.config().path("repo_health", "owner" to "acme", "repo" to "web"))
    }

    @Test fun aValueThatCouldAddASegmentOrAQueryIsRefused() {
        val config = Assets.config()
        assertThrows(ConfigException::class.java) { config.path("proposal", "id" to "../admin") }
        assertThrows(ConfigException::class.java) { config.path("proposal", "id" to "a?b=1") }
        assertThrows(ConfigException::class.java) { config.path("proposal", "id" to "") }
    }

    @Test fun anUnfilledPlaceholderIsAnError() {
        assertThrows(ConfigException::class.java) { Assets.config().path("repo_health", "owner" to "acme") }
    }

    @Test fun anUnknownEndpointIsAnError() {
        assertThrows(ConfigException::class.java) { Assets.config().path("nope") }
    }

    @Test fun brokenJsonIsAConfigException() {
        assertThrows(ConfigException::class.java) { AppConfig.parse("{not json") }
        assertThrows(ConfigException::class.java) { AppConfig.parse("{}") }
    }

    @Test fun aMissingEndpointIsRefusedWhenTheFileIsRead() {
        val text = Assets.configText().replace("\"devices\": \"/api/v1/devices\",", "")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }

    @Test fun anEndpointOutsideTheApiIsRefused() {
        val text = Assets.configText().replace("/api/v1/health", "/admin/health")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }

    @Test fun valuesOutOfRangeAreRefused() {
        val text = Assets.configText().replace("\"page_size\": 30", "\"page_size\": 5000")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }

    @Test fun anUnknownPushLevelIsRefused() {
        val text = Assets.configText().replace("\"only_level\": \"critical\"", "\"only_level\": \"loud\"")
        assertThrows(ConfigException::class.java) { AppConfig.parse(text) }
    }
}

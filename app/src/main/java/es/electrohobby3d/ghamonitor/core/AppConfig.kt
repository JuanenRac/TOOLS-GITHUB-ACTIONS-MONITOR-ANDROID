// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the tuned values of the app, read from assets/app_config.json (nothing tuned lives in the code).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

import org.json.JSONException
import org.json.JSONObject

/** The file is damaged or lacks something the app cannot work without. */
class ConfigException(message: String) : Exception(message)

data class ApiConfig(
    val version: String,
    val profile: String,
    val connectTimeoutMs: Int,
    val readTimeoutMs: Int,
    val maxResponseBytes: Int,
    val pageSize: Int,
    val maxPagesInMemory: Int,
)

data class PushConfig(
    val enabledByDefault: Boolean,
    val onlyLevel: Level,
    val maxTitleChars: Int,
    val rememberAlertIds: Int,
    val channelId: String,
)

data class ProposalsConfig(val defaultStateFilter: String, val fourEyesHint: Boolean)

data class DeepLinkConfig(val scheme: String, val host: String)

data class AppConfig(
    val api: ApiConfig,
    val endpoints: Map<String, String>,
    val push: PushConfig,
    val proposals: ProposalsConfig,
    val deepLink: DeepLinkConfig,
) {
    /** The path of an endpoint with its `{name}` placeholders filled; a value is checked so it cannot add a segment or a query. */
    fun path(name: String, vararg values: Pair<String, String>): String {
        var path = endpoints[name] ?: throw ConfigException("no endpoint named $name")
        for ((key, value) in values) {
            if (!SEGMENT.matches(value)) throw ConfigException("bad value for $key")
            path = path.replace("{$key}", value)
        }
        if ('{' in path) throw ConfigException("a placeholder of $name was not filled")
        return path
    }

    companion object {
        /** Names every endpoint the app calls: a file without one is refused when it is read, not when the screen opens. */
        val REQUIRED_ENDPOINTS = listOf(
            "health", "status", "accounts", "account_status", "alerts", "repo_health",
            "proposals", "proposal", "proposal_approve", "proposal_reject", "devices", "device",
        )
        private val SEGMENT = Regex("^[A-Za-z0-9_.-]{1,100}\$")
        private val PATH = Regex("^/api/v1/[A-Za-z0-9_./{}-]{1,150}\$")

        fun parse(text: String): AppConfig {
            try {
                val root = JSONObject(text)
                val api = root.getJSONObject("api")
                val endpoints = root.getJSONObject("endpoints")
                val push = root.getJSONObject("push")
                val proposals = root.getJSONObject("proposals")
                val link = root.getJSONObject("deep_link")
                val map = LinkedHashMap<String, String>()
                for (name in REQUIRED_ENDPOINTS) {
                    val value = endpoints.getString(name)
                    if (!PATH.matches(value)) throw ConfigException("endpoint $name is not a path of the API v1")
                    map[name] = value
                }
                val level = Level.parse(push.getString("only_level")) ?: throw ConfigException("push.only_level is not a level")
                return AppConfig(
                    api = ApiConfig(
                        version = api.getString("version"),
                        profile = api.getString("profile"),
                        connectTimeoutMs = ranged(api.getInt("connect_timeout_ms"), 500, 120_000, "connect_timeout_ms"),
                        readTimeoutMs = ranged(api.getInt("read_timeout_ms"), 500, 300_000, "read_timeout_ms"),
                        maxResponseBytes = ranged(api.getInt("max_response_bytes"), 10_000, 50_000_000, "max_response_bytes"),
                        pageSize = ranged(api.getInt("page_size"), 1, 200, "page_size"),
                        maxPagesInMemory = ranged(api.getInt("max_pages_in_memory"), 1, 100, "max_pages_in_memory"),
                    ),
                    endpoints = map,
                    push = PushConfig(
                        enabledByDefault = push.getBoolean("enabled_by_default"),
                        onlyLevel = level,
                        maxTitleChars = ranged(push.getInt("max_title_chars"), 10, 300, "max_title_chars"),
                        rememberAlertIds = ranged(push.getInt("remember_alert_ids"), 10, 5000, "remember_alert_ids"),
                        channelId = push.getString("channel_id").also { if (!SEGMENT.matches(it)) throw ConfigException("bad channel id") },
                    ),
                    proposals = ProposalsConfig(proposals.getString("default_state_filter"), proposals.getBoolean("four_eyes_hint")),
                    deepLink = DeepLinkConfig(link.getString("scheme"), link.getString("host")),
                )
            } catch (e: JSONException) {
                throw ConfigException("app_config.json: " + (e.message ?: "unreadable"))
            }
        }

        private fun ranged(value: Int, low: Int, high: Int, name: String): Int {
            if (value !in low..high) throw ConfigException("$name must be between $low and $high")
            return value
        }
    }
}

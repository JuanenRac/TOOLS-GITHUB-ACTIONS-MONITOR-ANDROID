// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the client of the monitor: the routes it really serves, authenticated the way it asks.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// A monitor that a phone can reach runs in the network mode: EVERY request carries the access token (header X-Monitor-Access). When the monitor also asks people to sign in (the team
// mode), the session token of a person goes along as "Authorization: Bearer msn_..." (the monitor gives one only while auth.allow_bearer is on). The app only reads, except for signing
// in and out. The network is behind [Transport], so everything here runs on the JVM. No token is ever written to a log or to an error text; a redirect is never followed.
package es.electrohobby3d.ghamonitor.core

import java.io.IOException
import java.net.URLEncoder
import org.json.JSONException
import org.json.JSONObject

data class HttpResult(val status: Int, val body: String)

interface Transport {
    /** Sends one request. Throws [IOException] when there is no answer (no network, timeout, a body over the limit). */
    @Throws(IOException::class)
    fun send(method: String, url: String, headers: Map<String, String>, body: String?): HttpResult
}

/** The ways a call can fail, as one word the screens turn into a text in the language of the phone. */
enum class ErrorKey {
    UNAUTHORIZED,        // the access token is not the one of the monitor
    HOST_NOT_ALLOWED,    // the monitor does not accept the name or address it was called by (listen.allowed_hosts)
    LOCKED,              // too many wrong tokens or passwords: wait
    SIGN_IN,             // the monitor asks for the session of a person and there is none, or it ran out
    INVALID_CREDENTIALS,
    BEARER_OFF,          // the monitor does not give session tokens to apps (auth.allow_bearer)
    BOOTSTRAP,           // the team mode is on and has no administrator yet
    FORBIDDEN,
    ACCOUNT_SCOPE,
    NOT_FOUND,
    RATE_LIMITED,
    BAD_REQUEST,
    SERVER_ERROR,
    NETWORK,
    BAD_RESPONSE,
    NOT_CONFIGURED,
    UNKNOWN,
}

class ApiException(val key: ErrorKey, val status: Int = 0, val serverCode: String = "", val reason: String = "") : Exception(key.name + (if (reason.isEmpty()) "" else ": $reason"))

class MonitorApi(
    private val baseUrl: String,
    accessToken: String,
    sessionToken: String,
    private val transport: Transport,
    private val config: AppConfig,
) {
    private val headers: Map<String, String> = buildMap {
        put("Accept", "application/json")
        put("X-Monitor-Access", accessToken)
        if (sessionToken.isNotEmpty()) put("Authorization", "Bearer $sessionToken")
    }

    fun me(): AuthState = read("auth_me", emptyList(), Parsers::auth)

    /** Signs a person in. The password goes in the body of this one request and is not kept. */
    fun login(user: String, password: String): Session =
        guard { Parsers.session(obj(send("POST", config.path("login"), JSONObject().put("username", user).put("password", password).put("client", "api")))) }

    fun logout() {
        send("POST", config.path("logout"), JSONObject())
    }

    fun health(): Health = read("health", emptyList(), Parsers::health)

    fun alerts(level: String? = null, muted: String? = null): AlertList = read("alerts", listOf("level" to level, "muted" to muted), Parsers::alerts)

    fun teamHealth(text: String? = null): TeamHealth = read("team_health", listOf("q" to text), Parsers::teamHealth)

    /** The accounts of a monitor that watches several; empty for a monitor of one account, which does not have the route. */
    fun accounts(): List<Account> = try {
        read("accounts", emptyList(), Parsers::accounts)
    } catch (e: ApiException) {
        if (e.key == ErrorKey.NOT_FOUND) emptyList() else throw e
    }

    fun proposals(): ProposalList = read("proposals", emptyList(), Parsers::proposals)

    private fun <T> read(endpoint: String, query: List<Pair<String, String?>>, parse: (JSONObject) -> T): T =
        guard { parse(obj(send("GET", config.path(endpoint) + queryString(query), null))) }

    /** A body that is not the JSON the monitor promises is one kind of failure, not a crash. */
    private fun <T> guard(block: () -> T): T = try {
        block()
    } catch (e: JSONException) {
        throw ApiException(ErrorKey.BAD_RESPONSE, reason = "unreadable body")
    }

    private fun send(method: String, pathAndQuery: String, json: JSONObject?): String {
        val sent = LinkedHashMap(headers)
        if (json != null) sent["Content-Type"] = "application/json"
        val result = try {
            transport.send(method, baseUrl + pathAndQuery, sent, json?.toString())
        } catch (e: IOException) {
            throw ApiException(ErrorKey.NETWORK)     // the message of an IO error can name the address: it is not kept
        } catch (e: IllegalArgumentException) {
            throw ApiException(ErrorKey.BAD_REQUEST, reason = "a header cannot carry that text")
        }
        if (result.status in 200..299) return result.body
        throw errorOf(result)
    }

    companion object {
        fun queryString(pairs: List<Pair<String, String?>>): String {
            val parts = pairs.filter { !it.second.isNullOrEmpty() }.map { URLEncoder.encode(it.first, "UTF-8") + "=" + URLEncoder.encode(it.second!!, "UTF-8") }
            return if (parts.isEmpty()) "" else "?" + parts.joinToString("&")
        }

        /** The refusal of the monitor. It answers {"error", "code"} (a stable code), or only {"error": word} from the network guard, or nothing readable. */
        fun errorOf(result: HttpResult): ApiException {
            val body = try {
                JSONObject(result.body)
            } catch (e: JSONException) {
                JSONObject()
            }
            val code = body.text("code")
            val word = body.text("error")
            val named = when (code) {
                "invalid_credentials" -> ErrorKey.INVALID_CREDENTIALS
                "auth_required" -> ErrorKey.SIGN_IN
                "bearer_off" -> ErrorKey.BEARER_OFF
                "locked" -> ErrorKey.LOCKED
                "rate" -> ErrorKey.RATE_LIMITED
                "bootstrap_required" -> ErrorKey.BOOTSTRAP
                "forbidden_role" -> ErrorKey.FORBIDDEN
                "account_scope" -> ErrorKey.ACCOUNT_SCOPE
                else -> null
            }
            val key = named ?: when {
                result.status == 401 -> ErrorKey.UNAUTHORIZED
                result.status == 403 && word == "forbidden" -> ErrorKey.HOST_NOT_ALLOWED
                result.status == 429 && word == "locked" -> ErrorKey.LOCKED
                result.status == 400 -> ErrorKey.BAD_REQUEST
                result.status == 403 -> ErrorKey.FORBIDDEN
                result.status == 404 -> ErrorKey.NOT_FOUND
                result.status == 429 -> ErrorKey.RATE_LIMITED
                result.status in 500..599 -> ErrorKey.SERVER_ERROR
                else -> ErrorKey.UNKNOWN
            }
            return ApiException(key, result.status, code)
        }

        private fun obj(body: String): JSONObject = try {
            JSONObject(body)
        } catch (e: JSONException) {
            throw ApiException(ErrorKey.BAD_RESPONSE, reason = "not an object")
        }
    }
}

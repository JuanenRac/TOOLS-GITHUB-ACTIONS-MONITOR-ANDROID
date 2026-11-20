// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the client of the read API v1 of the monitor, with a bearer token.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// The network is behind [Transport], so everything here runs on the JVM with a fake one. The token is sent only to the address of the monitor that was typed and
// is never written to a log or to an error text; a redirect is never followed (it could carry the token to another host).
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
enum class ErrorKey(val code: String) {
    BAD_REQUEST("api.bad_request"),
    UNAUTHORIZED("api.unauthorized"),
    FORBIDDEN("api.forbidden"),
    NOT_FOUND("api.not_found"),
    BAD_CURSOR("api.bad_cursor"),
    BAD_LIMIT("api.bad_limit"),
    UNKNOWN_FIELD("api.unknown_field"),
    VERSION_UNSUPPORTED("api.version_unsupported"),
    SERVER_ERROR("api.server_error"),
    NOT_ALLOWED("act.not_allowed"),
    HASH_MISMATCH("act.hash_mismatch"),
    EXPIRED("act.expired"),
    EXEC_DISABLED("act.exec_disabled"),
    BAD_STATE("act.bad_state"),
    RATE_LIMITED("api.rate_limited"),
    NETWORK("app.network"),
    BAD_RESPONSE("app.bad_response"),
    NOT_CONFIGURED("app.not_configured"),
    UNKNOWN("app.unknown");

    companion object {
        fun forCode(code: String?): ErrorKey = entries.firstOrNull { it.code == code } ?: UNKNOWN
    }
}

class ApiException(val key: ErrorKey, val status: Int = 0, val serverCode: String = "", val reason: String = "") : Exception(key.code + (if (reason.isEmpty()) "" else ": $reason"))

class MonitorApi(
    private val baseUrl: String,
    private val token: String,
    private val transport: Transport,
    private val config: AppConfig,
) : DeviceApi {
    private val headers: Map<String, String> = mapOf(
        "Authorization" to "Bearer $token",
        "Accept" to "application/json",
        "X-API-Version" to config.api.version,
        "X-Client-Profile" to config.api.profile,
    )

    fun health(): Health = guard { Parsers.health(obj(get(config.path("health"), emptyList()))) }

    fun status(): StatusInfo = guard { Parsers.status(obj(get(config.path("status"), emptyList()))) }

    fun accounts(): List<Account> = guard { Parsers.page(get(config.path("accounts"), emptyList()), Parsers::account).items }

    fun accountStatus(id: String): StatusInfo = guard { Parsers.status(obj(get(config.path("account_status", "id" to id), emptyList()))) }

    fun alerts(cursor: String? = null, level: String? = null, account: String? = null): Page<Alert> = guard {
        Parsers.page(
            get(config.path("alerts"), listOf("limit" to config.api.pageSize.toString(), "cursor" to cursor, "level" to level, "account" to account)),
            Parsers::alert,
        )
    }

    fun repoHealth(owner: String, repo: String): RepoHealth =
        guard { Parsers.repoHealth(obj(get(config.path("repo_health", "owner" to owner, "repo" to repo), emptyList()))) }

    fun proposals(state: String? = null, cursor: String? = null): Page<Proposal> = guard {
        Parsers.page(
            get(config.path("proposals"), listOf("limit" to config.api.pageSize.toString(), "cursor" to cursor, "state" to state, "profile" to "full")),
            Parsers::proposal,
        )
    }

    fun proposal(id: String): Proposal = guard { Parsers.proposal(obj(get(config.path("proposal", "id" to id), listOf("profile" to "full")))) }

    /** Approves the proposal the person was shown: the hash of that content goes along, so a proposal that changed in between is refused by the server. */
    fun approve(id: String, expectedHash: String): Proposal =
        guard { Parsers.proposal(obj(send("POST", config.path("proposal_approve", "id" to id), JSONObject().put("expected_hash", expectedHash)))) }

    fun reject(id: String, note: String): Proposal =
        guard { Parsers.proposal(obj(send("POST", config.path("proposal_reject", "id" to id), JSONObject().put("note", note.take(200))))) }

    /** Tells the monitor where to send critical alerts; answers the id the monitor gives the device. */
    override fun registerDevice(deviceToken: String, platform: String): String = guard {
        val answer = obj(send("POST", config.path("devices"), JSONObject().put("token", deviceToken).put("platform", platform)))
        answer.text("id").ifEmpty { throw ApiException(ErrorKey.BAD_RESPONSE, reason = "no device id") }
    }

    override fun unregisterDevice(id: String) {
        guard { send("DELETE", config.path("device", "id" to id), null) }
    }

    /** A body that is not the JSON the contract promises is one kind of failure, not a crash. */
    private fun <T> guard(block: () -> T): T = try {
        block()
    } catch (e: JSONException) {
        throw ApiException(ErrorKey.BAD_RESPONSE, reason = "unreadable body")
    } catch (e: ConfigException) {
        throw ApiException(ErrorKey.BAD_REQUEST, reason = "bad value")      // an id with characters a path cannot carry never leaves the phone
    }

    private fun get(path: String, query: List<Pair<String, String?>>): String = send("GET", path + queryString(query), null)

    private fun send(method: String, pathAndQuery: String, json: JSONObject?): String {
        val sent = LinkedHashMap(headers)
        if (json != null) sent["Content-Type"] = "application/json"
        val result = try {
            transport.send(method, baseUrl + pathAndQuery, sent, json?.toString())
        } catch (e: IOException) {
            throw ApiException(ErrorKey.NETWORK)     // the message of an IO error can name the address: it is not kept
        }
        if (result.status in 200..299) return result.body
        throw errorOf(result)
    }

    companion object {
        fun queryString(pairs: List<Pair<String, String?>>): String {
            val parts = pairs.filter { !it.second.isNullOrEmpty() }.map { URLEncoder.encode(it.first, "UTF-8") + "=" + URLEncoder.encode(it.second!!, "UTF-8") }
            return if (parts.isEmpty()) "" else "?" + parts.joinToString("&")
        }

        /** The error the monitor sent ({"error", "code", "message"}), or one made from the status alone when the body is not an envelope. */
        fun errorOf(result: HttpResult): ApiException {
            val code = try {
                JSONObject(result.body).text("code")
            } catch (e: JSONException) {
                ""
            }
            val key = ErrorKey.forCode(code)
            if (key != ErrorKey.UNKNOWN) return ApiException(key, result.status, code)
            return ApiException(
                when (result.status) {
                    400 -> ErrorKey.BAD_REQUEST
                    401 -> ErrorKey.UNAUTHORIZED
                    403 -> ErrorKey.FORBIDDEN
                    404 -> ErrorKey.NOT_FOUND
                    429 -> ErrorKey.RATE_LIMITED
                    in 500..599 -> ErrorKey.SERVER_ERROR
                    else -> ErrorKey.UNKNOWN
                },
                result.status,
                code,
            )
        }

        private fun obj(body: String): JSONObject = try {
            JSONObject(body)
        } catch (e: JSONException) {
            throw ApiException(ErrorKey.BAD_RESPONSE, reason = "not an object")
        }
    }
}

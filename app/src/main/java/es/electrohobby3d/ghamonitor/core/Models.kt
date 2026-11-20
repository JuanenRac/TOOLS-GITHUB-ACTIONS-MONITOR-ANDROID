// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - what the read API v1 of the monitor says, as plain data.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

import org.json.JSONArray
import org.json.JSONObject

/** The levels of an alert, from the least to the most serious (the same words the server uses). */
enum class Level(val code: String, val rank: Int) {
    INFO("info", 0), WARNING("warning", 1), ERROR("error", 2), CRITICAL("critical", 3);

    companion object {
        fun parse(text: String?): Level? = entries.firstOrNull { it.code == text }
    }
}

/**
 * How serious a status word is, for the colour of a card: 0 fine, 1 warning, 2 error, 3 critical, -1 a word the app does not know (shown as it is, never guessed).
 * The server's status words are not all alert levels, so the common colour names are accepted too.
 */
fun severityOf(word: String?): Int = when (word?.lowercase()) {
    "ok", "success", "green", "healthy", "info" -> 0
    "warning", "warn", "yellow", "degraded" -> 1
    "error", "red", "failure", "failed" -> 2
    "critical" -> 3
    else -> -1
}

data class Health(val ok: Boolean, val edition: String, val version: String)
data class Account(val id: String, val label: String)
data class StatusInfo(val id: String, val level: String, val updatedAt: String)
data class Alert(
    val id: String,
    val code: String,
    val level: String,
    val sentinel: String,
    val accountId: String,
    val repo: String,
    val title: String,
    val createdAt: String,
)
data class RepoHealth(val repo: String, val level: String)

data class Proposal(
    val id: String,
    val accountId: String,
    val repo: String,
    val actionType: String,
    val reason: String,
    val createdBy: String,
    val createdAt: Long,
    val expiresAt: Long,
    val state: String,
    val contentHash: String,
    val approvedBy: String,
)

/** One page of a list: the items and the cursor of the next page (null when this is the last one). */
data class Page<T>(val items: List<T>, val nextCursor: String?, val limit: Int)

/** A text of a JSON object, or "" when it is missing or null (org.json would answer the word "null"). */
fun JSONObject.text(name: String): String = if (isNull(name)) "" else optString(name, "")

fun JSONObject.number(name: String): Long = if (isNull(name)) 0L else optLong(name, 0L)

object Parsers {
    fun health(o: JSONObject) = Health(o.optBoolean("ok", false), o.text("edition"), o.text("version"))
    fun account(o: JSONObject) = Account(o.text("id"), o.text("label"))
    fun status(o: JSONObject) = StatusInfo(o.text("id"), o.text("level"), o.text("updated_at"))
    fun alert(o: JSONObject) = Alert(
        o.text("id"), o.text("code"), o.text("level"), o.text("sentinel"), o.text("account_id"), o.text("repo"), o.text("title"), o.text("created_at"),
    )
    fun repoHealth(o: JSONObject) = RepoHealth(o.text("repo"), o.text("level"))
    fun proposal(o: JSONObject) = Proposal(
        id = o.text("id"), accountId = o.text("account_id"), repo = o.text("repo"), actionType = o.text("action_type"), reason = o.text("reason"),
        createdBy = o.text("created_by"), createdAt = o.number("created_at"), expiresAt = o.number("expires_at"), state = o.text("state"),
        contentHash = o.text("content_hash"), approvedBy = o.text("approved_by"),
    )

    /** A list answer is either the page object {"items", "next_cursor", "limit"} or a bare array; both are read. */
    fun <T> page(body: String, read: (JSONObject) -> T): Page<T> {
        val trimmed = body.trim()
        if (trimmed.startsWith("[")) return Page(rows(JSONArray(trimmed), read), null, 0)
        val o = JSONObject(trimmed)
        val items = o.optJSONArray("items") ?: JSONArray()
        val cursor = if (o.isNull("next_cursor")) null else o.optString("next_cursor", "").ifEmpty { null }
        return Page(rows(items, read), cursor, o.optInt("limit", 0))
    }

    private fun <T> rows(array: JSONArray, read: (JSONObject) -> T): List<T> {
        val out = ArrayList<T>(array.length())
        for (i in 0 until array.length()) {
            val row = array.optJSONObject(i) ?: continue
            out.add(read(row))
        }
        return out
    }
}

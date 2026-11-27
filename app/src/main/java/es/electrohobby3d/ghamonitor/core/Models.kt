// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - what the monitor answers, as plain data. The shapes are the ones of the real monitor (see app/src/test/resources/contract).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// The list that makes an answer what it is ("alerts", "ranking", "proposals", "accounts") is REQUIRED: an answer without it is an unreadable answer, never an empty list.
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
 * How serious a word is, for the colour of a card: 0 fine, 1 warning, 2 error, 3 critical, -1 a word the app does not know (shown as it is, never guessed).
 * Alerts use levels (info, warning, error, critical), repositories states (ok, warn, bad, unknown) and accounts their own (ok, starting, offline, stuck, blind, down).
 */
fun severityOf(word: String?): Int = when (word?.lowercase()) {
    "ok", "success", "info" -> 0
    "warning", "warn", "starting", "offline" -> 1
    "error", "bad", "failure", "failed", "stuck", "blind", "down" -> 2
    "critical" -> 3
    else -> -1
}

/** GET /api/health */
data class Health(val ok: Boolean, val version: String, val lite: Boolean, val loaded: Boolean, val offline: Boolean)

/** GET /api/auth/me: whether the monitor asks every person to sign in, and whether this request is signed in. */
data class AuthState(val teamMode: Boolean, val authenticated: Boolean, val bootstrap: Boolean, val user: String, val role: String) {
    val needsSignIn: Boolean get() = teamMode && !authenticated
}

/** POST /api/auth/login with client "api". */
data class Session(val token: String, val user: String, val role: String)

/** One row of GET /api/alerts. [repo] is the name alone (no owner) and is empty for an alert of the monitor itself. */
data class Alert(
    val id: String,
    val code: String,
    val level: String,
    val sentinel: String,
    val repo: String,
    val title: String,
    val createdAt: String,
    val muted: Boolean,
)

data class AlertList(val items: List<Alert>, val total: Int, val muted: Int, val byLevel: Map<String, Int>)

/** One row of the ranking of GET /api/team/health. */
data class RepoRow(
    val repo: String,
    val ecosystem: String,
    val state: String,
    val risk: Int,
    val redWorkflows: Int,
    val alerts: Int,
    val failures24h: Int,
    val lastRunAt: String,
)

data class TeamHealth(val all: Int, val shown: Int, val truncated: Boolean, val ok: Int, val warn: Int, val bad: Int, val unknown: Int, val repos: List<RepoRow>)

/** One account of a monitor that watches several (GET /api/accounts). */
data class Account(val id: String, val state: String, val version: String)

/** One row of "proposals" of GET /api/ai/actions: a command an AI proposes. It is approved or rejected only on the computer of the monitor. */
data class Proposal(
    val id: String,
    val action: String,
    val command: String,
    val repo: String,
    val hash: String,
    val status: String,
    val creator: String,
    val by: String,
    val approvals: Int,
    val ageSeconds: Long,
    val expiresInSeconds: Long,
)

data class ProposalList(val enabled: Boolean, val items: List<Proposal>)

/** A text of a JSON object, or "" when it is missing or null (org.json would answer the word "null"). */
fun JSONObject.text(name: String): String = if (isNull(name)) "" else optString(name, "")

fun JSONObject.number(name: String): Long = if (isNull(name)) 0L else optLong(name, 0L)

private fun JSONObject.count(name: String): Int = number(name).toInt()

private fun <T> JSONArray.rows(read: (JSONObject) -> T): List<T> {
    val out = ArrayList<T>(length())
    for (i in 0 until length()) {
        val row = optJSONObject(i) ?: continue
        out.add(read(row))
    }
    return out
}

object Parsers {
    fun health(o: JSONObject) = Health(o.getBoolean("ok"), o.text("version"), o.optBoolean("lite", false), o.optBoolean("loaded", false), o.optBoolean("offline", false))

    fun auth(o: JSONObject): AuthState {
        val bootstrap = o.optBoolean("bootstrap", false)
        return AuthState(o.getString("mode") == "team" || bootstrap, o.getBoolean("authenticated"), bootstrap, o.text("user"), o.text("role"))
    }

    fun session(o: JSONObject) = Session(o.getString("token"), o.text("user"), o.text("role"))

    fun alert(o: JSONObject) = Alert(
        id = o.getString("id"), code = o.text("code"), level = o.text("level"), sentinel = o.text("sentinel"), repo = o.text("repo"), title = o.text("title"),
        createdAt = o.text("created_at"), muted = !o.isNull("muted_until"),
    )

    fun alerts(o: JSONObject): AlertList {
        val items = o.getJSONArray("alerts").rows(::alert)
        val summary = o.getJSONObject("summary")
        val levels = LinkedHashMap<String, Int>()
        summary.optJSONObject("by_level")?.let { by -> for (key in by.keys()) levels[key] = by.count(key) }
        return AlertList(items, summary.count("total"), summary.count("muted"), levels)
    }

    fun repoRow(o: JSONObject) = RepoRow(
        repo = o.getString("repo"), ecosystem = o.text("ecosystem"), state = o.text("state"), risk = o.count("risk"), redWorkflows = o.count("red_workflows"),
        alerts = o.count("alerts"), failures24h = o.count("failures_24h"), lastRunAt = o.text("last_run_at"),
    )

    fun teamHealth(o: JSONObject): TeamHealth {
        val totals = o.getJSONObject("totals")
        return TeamHealth(
            all = o.count("all"), shown = o.count("shown"), truncated = o.optBoolean("truncated", false),
            ok = totals.count("ok"), warn = totals.count("warn"), bad = totals.count("bad"), unknown = totals.count("unknown"),
            repos = o.getJSONArray("ranking").rows(::repoRow),
        )
    }

    fun accounts(o: JSONObject): List<Account> = o.getJSONArray("accounts").rows { Account(it.getString("id"), it.text("state"), it.text("version")) }

    fun proposal(o: JSONObject) = Proposal(
        id = o.getString("id"), action = o.text("action"), command = o.text("command"), repo = o.text("repo"), hash = o.text("hash"), status = o.text("status"),
        creator = o.text("creator"), by = o.text("by"), approvals = o.count("approvals"), ageSeconds = o.number("age_s"), expiresInSeconds = o.number("expires_in_s"),
    )

    fun proposals(o: JSONObject) = ProposalList(o.getJSONObject("state").getBoolean("enabled"), o.getJSONArray("proposals").rows(::proposal))
}

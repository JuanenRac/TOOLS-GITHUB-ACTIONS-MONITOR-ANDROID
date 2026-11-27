// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the summary screen asks several things; each one succeeds or fails by itself.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

/** A part that failed is null and the others are shown; [error] is the failure to tell. */
data class Summary(val health: Health?, val alerts: AlertList?, val team: TeamHealth?, val accounts: List<Account>, val error: ErrorKey?, val takenAtSeconds: Long)

/** With one of these there is no point in asking the rest: it would only add wrong tries, and the monitor locks a phone out after a few. */
private val STOPPERS = setOf(ErrorKey.NETWORK, ErrorKey.UNAUTHORIZED, ErrorKey.HOST_NOT_ALLOWED, ErrorKey.LOCKED)

fun loadSummary(api: MonitorApi, nowSeconds: Long = System.currentTimeMillis() / 1000): Summary {
    var first: ErrorKey? = null
    fun <T> part(block: () -> T): T? = try {
        block()
    } catch (e: ApiException) {
        if (first == null || e.key == ErrorKey.SIGN_IN) first = e.key
        null
    }
    val health = part { api.health() }
    if (health == null && first in STOPPERS) return Summary(null, null, null, emptyList(), first, nowSeconds)
    if (health?.lite == true) return Summary(health, null, null, emptyList(), null, nowSeconds)
    val alerts = part { api.alerts(muted = "hide") }
    if (alerts == null && first in STOPPERS) return Summary(health, null, null, emptyList(), first, nowSeconds)
    val team = part { api.teamHealth() }
    val accounts = part { api.accounts() } ?: emptyList()
    return Summary(health, alerts, team, accounts, first, nowSeconds)
}

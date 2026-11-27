// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - turns the words of the core (error keys, levels, states) into the texts of the phone's language, and severities into colours.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import es.electrohobby3d.ghamonitor.core.ErrorKey
import es.electrohobby3d.ghamonitor.core.UrlProblem
import es.electrohobby3d.ghamonitor.core.severityOf

@StringRes
fun ErrorKey.res(): Int = when (this) {
    ErrorKey.UNAUTHORIZED -> R.string.err_unauthorized
    ErrorKey.HOST_NOT_ALLOWED -> R.string.err_host_not_allowed
    ErrorKey.LOCKED -> R.string.err_locked
    ErrorKey.SIGN_IN -> R.string.err_sign_in
    ErrorKey.INVALID_CREDENTIALS -> R.string.err_invalid_credentials
    ErrorKey.BEARER_OFF -> R.string.err_bearer_off
    ErrorKey.BOOTSTRAP -> R.string.err_bootstrap
    ErrorKey.FORBIDDEN -> R.string.err_forbidden
    ErrorKey.ACCOUNT_SCOPE -> R.string.err_account_scope
    ErrorKey.NOT_FOUND -> R.string.err_not_found
    ErrorKey.RATE_LIMITED -> R.string.err_rate_limited
    ErrorKey.BAD_REQUEST -> R.string.err_bad_request
    ErrorKey.SERVER_ERROR -> R.string.err_server_error
    ErrorKey.NETWORK -> R.string.err_network
    ErrorKey.BAD_RESPONSE -> R.string.err_bad_response
    ErrorKey.NOT_CONFIGURED -> R.string.err_not_configured
    ErrorKey.UNKNOWN -> R.string.err_unknown
}

@StringRes
fun UrlProblem.res(): Int = when (this) {
    UrlProblem.EMPTY -> R.string.url_empty
    UrlProblem.UNREADABLE -> R.string.url_unreadable
    UrlProblem.SCHEME -> R.string.url_scheme
    UrlProblem.HOST -> R.string.url_host
    UrlProblem.CREDENTIALS -> R.string.url_credentials
    UrlProblem.EXTRA -> R.string.url_extra
    UrlProblem.CLEAR_TEXT_OUTSIDE_HOME -> R.string.url_clear_text
    UrlProblem.PORT -> R.string.url_port
}

/** The text of a level or state word the monitor sent; a word the app does not know is shown as it is. */
@Composable
fun wordText(word: String): String {
    val res = when (word.lowercase()) {
        "info" -> R.string.level_info
        "warning", "warn" -> R.string.level_warning
        "error", "bad" -> R.string.level_error
        "critical" -> R.string.level_critical
        "ok" -> R.string.level_ok
        "unknown" -> R.string.level_unknown
        "starting" -> R.string.account_starting
        "offline" -> R.string.account_offline
        "stuck" -> R.string.account_stuck
        "blind" -> R.string.account_blind
        "down" -> R.string.account_down
        else -> 0
    }
    return if (res == 0) word else stringResource(res)
}

/** The closed list of actions of the monitor. */
@Composable
fun actionText(type: String): String {
    val res = when (type) {
        "rerun" -> R.string.act_rerun
        "rerun_failed" -> R.string.act_rerun_failed
        "close_issue" -> R.string.act_close_issue
        "act_local" -> R.string.act_local
        else -> 0
    }
    return if (res == 0) type else stringResource(res)
}

@Composable
fun statusText(status: String): String {
    val res = when (status) {
        "pending" -> R.string.state_pending
        "approved" -> R.string.state_approved
        "rejected" -> R.string.state_rejected
        else -> 0
    }
    return if (res == 0) status else stringResource(res)
}

/** Colour is never the only sign of a level: the word is always shown next to it. */
fun severityColor(word: String): Color = when (severityOf(word)) {
    0 -> MonitorColors.Ok
    1 -> MonitorColors.Warning
    2 -> MonitorColors.Error
    3 -> MonitorColors.Critical
    else -> MonitorColors.Muted
}

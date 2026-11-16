// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - turns the words of the core (error keys, levels, states) into the texts of the phone's language, and levels into colours.
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
    ErrorKey.BAD_REQUEST -> R.string.err_bad_request
    ErrorKey.UNAUTHORIZED -> R.string.err_unauthorized
    ErrorKey.FORBIDDEN -> R.string.err_forbidden
    ErrorKey.NOT_FOUND -> R.string.err_not_found
    ErrorKey.BAD_CURSOR -> R.string.err_bad_cursor
    ErrorKey.BAD_LIMIT -> R.string.err_bad_limit
    ErrorKey.UNKNOWN_FIELD -> R.string.err_unknown_field
    ErrorKey.VERSION_UNSUPPORTED -> R.string.err_version_unsupported
    ErrorKey.SERVER_ERROR -> R.string.err_server_error
    ErrorKey.NOT_ALLOWED -> R.string.err_not_allowed
    ErrorKey.HASH_MISMATCH -> R.string.err_hash_mismatch
    ErrorKey.EXPIRED -> R.string.err_expired
    ErrorKey.EXEC_DISABLED -> R.string.err_exec_disabled
    ErrorKey.BAD_STATE -> R.string.err_bad_state
    ErrorKey.RATE_LIMITED -> R.string.err_rate_limited
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

/** The text of a level or status word the monitor sent; a word the app does not know is shown as it is. */
@Composable
fun wordText(word: String): String {
    val res = when (word.lowercase()) {
        "info" -> R.string.level_info
        "warning", "warn" -> R.string.level_warning
        "error", "failure", "failed" -> R.string.level_error
        "critical" -> R.string.level_critical
        "ok", "green", "success", "healthy" -> R.string.level_ok
        else -> 0
    }
    return if (res == 0) word else stringResource(res)
}

@Composable
fun actionText(type: String): String {
    val res = when (type) {
        "rerun" -> R.string.act_rerun
        "rerun_failed" -> R.string.act_rerun_failed
        "label" -> R.string.act_label
        "comment" -> R.string.act_comment
        "close" -> R.string.act_close
        "dispatch" -> R.string.act_dispatch
        "merge" -> R.string.act_merge
        else -> 0
    }
    return if (res == 0) type else stringResource(res)
}

@Composable
fun stateText(state: String): String {
    val res = when (state) {
        "pending" -> R.string.state_pending
        "approved" -> R.string.state_approved
        "rejected" -> R.string.state_rejected
        "executed" -> R.string.state_executed
        "failed" -> R.string.state_failed
        "expired" -> R.string.state_expired
        else -> 0
    }
    return if (res == 0) state else stringResource(res)
}

/** Colour is never the only sign of a level: the word is always shown next to it. */
fun severityColor(word: String): Color = when (severityOf(word)) {
    0 -> Color(0xFF2EA043)
    1 -> Color(0xFFD29922)
    2 -> Color(0xFFF85149)
    3 -> Color(0xFFFF7B72)
    else -> Color(0xFF8B949E)
}

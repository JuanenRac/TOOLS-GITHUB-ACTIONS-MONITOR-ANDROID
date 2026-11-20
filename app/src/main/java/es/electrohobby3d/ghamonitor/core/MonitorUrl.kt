// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the address of the monitor the person types: what is accepted and what is refused.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// https is accepted for any host. Plain http is accepted only for a host on the home network (localhost, a .local name or a private IPv4 literal), because a monitor at home often has
// no certificate; a token is never sent in clear text over the internet. Credentials in the address, a query and a fragment are refused.
package es.electrohobby3d.ghamonitor.core

import java.net.URI
import java.net.URISyntaxException

sealed class UrlCheck {
    /** [base] has no trailing slash and no path other than the one the person typed (a reverse proxy may mount the monitor under one). */
    data class Good(val base: String, val secure: Boolean) : UrlCheck()

    /** [reason] is a word the screen turns into a text. */
    data class Bad(val reason: UrlProblem) : UrlCheck()
}

enum class UrlProblem { EMPTY, UNREADABLE, SCHEME, HOST, CREDENTIALS, EXTRA, CLEAR_TEXT_OUTSIDE_HOME, PORT }

object MonitorUrl {
    fun check(input: String): UrlCheck {
        val typed = input.trim()
        if (typed.isEmpty()) return UrlCheck.Bad(UrlProblem.EMPTY)
        val text = if ("://" in typed) typed else "https://$typed"
        val uri = try {
            URI(text)
        } catch (e: URISyntaxException) {
            return UrlCheck.Bad(UrlProblem.UNREADABLE)
        }
        val scheme = uri.scheme?.lowercase()
        if (scheme != "https" && scheme != "http") return UrlCheck.Bad(UrlProblem.SCHEME)
        val host = uri.host?.lowercase()
        if (host.isNullOrEmpty()) return UrlCheck.Bad(UrlProblem.HOST)
        if (uri.userInfo != null) return UrlCheck.Bad(UrlProblem.CREDENTIALS)
        if (uri.rawQuery != null || uri.rawFragment != null) return UrlCheck.Bad(UrlProblem.EXTRA)
        if (uri.port != -1 && uri.port !in 1..65535) return UrlCheck.Bad(UrlProblem.PORT)
        if (scheme == "http" && !isHomeHost(host)) return UrlCheck.Bad(UrlProblem.CLEAR_TEXT_OUTSIDE_HOME)
        val path = (uri.rawPath ?: "").trimEnd('/')
        val port = if (uri.port == -1) "" else ":${uri.port}"
        return UrlCheck.Good("$scheme://$host$port$path", scheme == "https")
    }

    /** localhost, a name ending in .local, or a private IPv4 literal (10/8, 172.16/12, 192.168/16, 127/8). */
    fun isHomeHost(host: String): Boolean {
        if (host == "localhost" || host.endsWith(".local")) return true
        val parts = host.split(".")
        if (parts.size != 4) return false
        val n = parts.map { it.toIntOrNull() ?: return false }
        if (n.any { it !in 0..255 }) return false
        return n[0] == 10 || n[0] == 127 || (n[0] == 192 && n[1] == 168) || (n[0] == 172 && n[1] in 16..31)
    }
}

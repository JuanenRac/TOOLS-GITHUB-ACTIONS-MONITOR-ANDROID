// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - small text rules the screens share (no Android classes, so they are tested on the JVM).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Format {
    private val DIGITS = Regex("^[0-9]{9,13}\$")

    /** A moment the monitor sent (epoch seconds, epoch milliseconds or ISO-8601) as "yyyy-MM-dd HH:mm" in [zone]; text that is none of those comes back as it is. */
    fun moment(raw: String, zone: ZoneId): String {
        if (raw.isEmpty()) return ""
        val instant = try {
            if (DIGITS.matches(raw)) {
                val n = raw.toLong()
                if (raw.length >= 12) Instant.ofEpochMilli(n) else Instant.ofEpochSecond(n)
            } else {
                Instant.parse(raw)
            }
        } catch (e: DateTimeParseException) {
            return raw
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(zone).format(instant)
    }

    /** "2d 03h", "5h 12m", "7m" or "<1m" for a number of seconds. */
    fun duration(seconds: Long): String {
        if (seconds < 60) return "<1m"
        val days = seconds / 86_400
        val hours = (seconds % 86_400) / 3_600
        val minutes = (seconds % 3_600) / 60
        return when {
            days > 0 -> "${days}d ${hours.toString().padStart(2, '0')}h"
            hours > 0 -> "${hours}h ${minutes.toString().padStart(2, '0')}m"
            else -> "${minutes}m"
        }
    }

    /** The repositories of a list of alerts with the most serious level each one has, worst first and then by name. */
    fun reposByWorst(alerts: List<Alert>): List<Pair<String, String>> {
        val worst = LinkedHashMap<String, Alert>()
        for (a in alerts) {
            if (a.repo.isEmpty()) continue
            val old = worst[a.repo]
            if (old == null || severityOf(a.level) > severityOf(old.level)) worst[a.repo] = a
        }
        return worst.values.sortedWith(compareByDescending<Alert> { severityOf(it.level) }.thenBy { it.repo }).map { it.repo to it.level }
    }

    /** "owner/name" split into its two parts, or null when it is not that form. */
    fun splitRepo(full: String): Pair<String, String>? {
        val match = Regex("^([A-Za-z0-9_.-]{1,39})/([A-Za-z0-9_.-]{1,100})\$").matchEntire(full.trim()) ?: return null
        return match.groupValues[1] to match.groupValues[2]
    }
}

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
}

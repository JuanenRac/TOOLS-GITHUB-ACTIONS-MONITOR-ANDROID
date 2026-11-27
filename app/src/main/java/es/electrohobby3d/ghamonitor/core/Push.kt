// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - push messages: which ones a phone would show.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// The rule for the day a monitor sends push messages: only a critical alert is shown, once, with a short cleaned title. The monitor has no push service yet and this build links none,
// so nothing calls it: there is no registration, no notification permission and no switch.
package es.electrohobby3d.ghamonitor.core

data class PushMessage(val alertId: String, val level: Level, val code: String, val title: String)

class PushGate(private val config: PushConfig) {
    private val shown = ArrayDeque<String>()

    /** The message to show, or null when it must be dropped. [data] is the map of strings the push service delivered. */
    @Synchronized
    fun accept(data: Map<String, String>, enabled: Boolean): PushMessage? {
        if (!enabled) return null
        val level = Level.parse(data["level"]) ?: return null
        if (level != config.onlyLevel) return null
        val id = data["alert_id"].orEmpty().trim()
        if (id.isEmpty() || id.length > 120 || id in shown) return null
        shown.addLast(id)
        while (shown.size > config.rememberAlertIds) shown.removeFirst()
        return PushMessage(id, level, clean(data["code"].orEmpty(), 60), clean(data["title"].orEmpty(), config.maxTitleChars))
    }

    companion object {
        /** A text without control characters, with its spaces collapsed, cut to [max] characters. */
        fun clean(text: String, max: Int): String = text.filter { !it.isISOControl() || it == ' ' }.trim().replace(Regex("\\s+"), " ").take(max)
    }
}

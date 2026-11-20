// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - push notifications: critical alerts only, OFF by default, and the Firebase side behind an interface.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// The monitor decides which alerts are critical and sends a minimal message (an id, a level, a code, a short cleaned title and the view to open). The app trusts that but checks it
// again: a message below the level, a switch that is off, a message it has already shown, or one without an id is dropped. No Firebase library is linked: [PushRegistrar] is the
// place where one will be plugged in once the maintainer has a Google project; until then [NullPushRegistrar] says "not available" and the switch cannot be turned on.
package es.electrohobby3d.ghamonitor.core

data class PushMessage(val alertId: String, val level: Level, val code: String, val title: String)

/** Gives the app a token of the push service of this phone. */
interface PushRegistrar {
    /** False when this build has no push service (no Firebase project was configured). */
    val available: Boolean

    /** Asks for the token; [onResult] gets the token, or null when the service did not give one. */
    fun fetchToken(onResult: (String?) -> Unit)
}

/** The implementation of a build without Firebase: nothing is available and nothing is ever asked. */
object NullPushRegistrar : PushRegistrar {
    override val available: Boolean = false
    override fun fetchToken(onResult: (String?) -> Unit) = onResult(null)
}

/** The few calls to the monitor that the push switch needs. */
interface DeviceApi {
    fun registerDevice(deviceToken: String, platform: String): String
    fun unregisterDevice(id: String)
}

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

/** The result of turning the switch on or off. */
enum class PushState { OFF, ON, NOT_AVAILABLE, REGISTER_FAILED }

/**
 * The life of the push switch. It starts off. Turning it on needs a push service ([PushRegistrar.available]) and a token, and the monitor must accept the device; if any step fails
 * the switch stays off. Turning it off tells the monitor to forget the device (the switch goes off even if the monitor cannot be reached, and the id is kept for a retry).
 */
class PushController(private val registrar: PushRegistrar, private val api: DeviceApi, private val store: KeyValueStore) {
    val enabled: Boolean get() = store.get(KEY_ENABLED) == "1"
    val deviceId: String? get() = store.get(KEY_DEVICE)

    fun turnOn(onDone: (PushState) -> Unit) {
        if (!registrar.available) return onDone(PushState.NOT_AVAILABLE)
        registrar.fetchToken { token ->
            if (token.isNullOrBlank()) return@fetchToken onDone(PushState.REGISTER_FAILED)
            val id = try {
                api.registerDevice(token, "android")
            } catch (e: ApiException) {
                null
            }
            if (id == null) return@fetchToken onDone(PushState.REGISTER_FAILED)
            store.put(KEY_DEVICE, id)
            store.put(KEY_ENABLED, "1")
            onDone(PushState.ON)
        }
    }

    fun turnOff(): PushState {
        store.put(KEY_ENABLED, "0")
        val id = store.get(KEY_DEVICE)
        if (id != null) {
            try {
                api.unregisterDevice(id)
                store.remove(KEY_DEVICE)
            } catch (e: ApiException) {
                // kept: the next time the switch is turned off (or the settings are cleared) the monitor is told again
            }
        }
        return PushState.OFF
    }

    companion object {
        const val KEY_ENABLED = "push_enabled"
        const val KEY_DEVICE = "push_device_id"
    }
}

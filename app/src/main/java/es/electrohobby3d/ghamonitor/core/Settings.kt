// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - what the person typed in the settings, and how it is kept: the token only sealed.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

/** The few places the settings are written. */
interface KeyValueStore {
    fun get(key: String): String?
    fun put(key: String, value: String)
    fun remove(key: String)
}

/** Seals and opens a text; what is sealed can only be opened by the same box (the Android Keystore in the app, a fake in the tests). */
interface SecretBox {
    fun seal(plain: String): String

    /** The text, or null when it was not sealed by this box (a new phone, a restored backup, tampering). */
    fun open(sealed: String): String?
}

data class MonitorSettings(val baseUrl: String, val token: String, val userName: String) {
    val configured: Boolean get() = baseUrl.isNotEmpty() && token.isNotEmpty()
}

class SettingsRepository(private val store: KeyValueStore, private val box: SecretBox) {
    /** What is kept; an empty [MonitorSettings] when nothing is, and a token that cannot be opened is dropped (it is not guessed). */
    fun load(): MonitorSettings {
        val sealed = store.get(KEY_TOKEN)
        val token = if (sealed == null) "" else box.open(sealed) ?: run { store.remove(KEY_TOKEN); "" }
        return MonitorSettings(store.get(KEY_URL).orEmpty(), token, store.get(KEY_USER).orEmpty())
    }

    /** Keeps the address (already checked by [MonitorUrl]), the token sealed and the name. A blank token keeps the one that was there. */
    fun save(baseUrl: String, token: String, userName: String) {
        store.put(KEY_URL, baseUrl)
        if (token.isNotBlank()) store.put(KEY_TOKEN, box.seal(token.trim()))
        store.put(KEY_USER, userName.trim().take(64))
    }

    /** Forgets the token only (signing out of this monitor); the address stays. */
    fun forgetToken() { store.remove(KEY_TOKEN) }

    fun clearAll() {
        for (key in listOf(KEY_URL, KEY_TOKEN, KEY_USER)) store.remove(key)
    }

    companion object {
        const val KEY_URL = "monitor_url"
        const val KEY_TOKEN = "monitor_token_sealed"
        const val KEY_USER = "user_name"
    }
}

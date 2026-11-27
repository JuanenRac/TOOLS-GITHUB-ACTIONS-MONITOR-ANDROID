// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - what the person typed in the settings, and how it is kept: the tokens only sealed, the password never.
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

/** [token] is the access token of the monitor; [session] the session of a person ("" unless the monitor asks to sign in and they did). */
data class MonitorSettings(val baseUrl: String, val token: String, val session: String = "", val userName: String = "") {
    val configured: Boolean get() = baseUrl.isNotEmpty() && token.isNotEmpty()
}

class SettingsRepository(private val store: KeyValueStore, private val box: SecretBox) {
    /** What is kept; a token that cannot be opened is dropped (it is not guessed). */
    fun load(): MonitorSettings = MonitorSettings(store.get(KEY_URL).orEmpty(), open(KEY_TOKEN), open(KEY_SESSION), store.get(KEY_USER).orEmpty())

    private fun open(key: String): String {
        val sealed = store.get(key) ?: return ""
        return box.open(sealed) ?: run { store.remove(key); "" }
    }

    /**
     * Keeps the address (already checked by [MonitorUrl]) and the token sealed. A blank token keeps the one that was there, but only for the same address: with another address the old
     * token and session are dropped, so they are never sent to a host they were not typed for.
     */
    fun save(baseUrl: String, token: String) {
        if (baseUrl != store.get(KEY_URL)) forgetToken()
        store.put(KEY_URL, baseUrl)
        if (token.isNotBlank()) {
            store.put(KEY_TOKEN, box.seal(token.trim()))
            forgetSession()
        }
    }

    fun saveSession(userName: String, session: String) {
        store.put(KEY_USER, userName.trim().take(64))
        store.put(KEY_SESSION, box.seal(session))
    }

    fun forgetSession() { store.remove(KEY_SESSION) }

    /** Forgets the token and the session; the address stays. */
    fun forgetToken() {
        store.remove(KEY_TOKEN)
        forgetSession()
    }

    companion object {
        const val KEY_URL = "monitor_url"
        const val KEY_TOKEN = "monitor_token_sealed"
        const val KEY_SESSION = "monitor_session_sealed"
        const val KEY_USER = "user_name"
    }
}

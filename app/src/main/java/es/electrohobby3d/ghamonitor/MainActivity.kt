// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the one activity: the five screens and the links (ghamon://proposal/...).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    companion object {
        const val TAB_SUMMARY = 0
        const val TAB_ALERTS = 1
        const val TAB_REPOS = 2
        const val TAB_PROPOSALS = 3
        const val TAB_SETTINGS = 4
    }

    private val model: MonitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) model.handleIntent(intent)
        setContent {
            MonitorTheme { MonitorApp(model) }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        model.handleIntent(intent)
    }
}

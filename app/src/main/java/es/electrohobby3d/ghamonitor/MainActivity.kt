// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the one activity: the five screens, the links (ghamon://proposal/...) and the permission for notifications.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_TAB = "tab"
        const val TAB_SUMMARY = 0
        const val TAB_ALERTS = 1
        const val TAB_REPOS = 2
        const val TAB_PROPOSALS = 3
        const val TAB_SETTINGS = 4
    }

    private val model: MonitorViewModel by viewModels()

    private val askNotifications = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) model.setPush(true) else model.pushPermissionDenied()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) model.handleIntent(intent)
        setContent {
            MonitorTheme {
                MonitorApp(model) { on ->
                    val needsPermission = on && Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    if (needsPermission) askNotifications.launch(Manifest.permission.POST_NOTIFICATIONS) else model.setPush(on)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        model.handleIntent(intent)
    }
}

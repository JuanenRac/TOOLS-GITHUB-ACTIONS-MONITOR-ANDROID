// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - where push messages end up, and the one place a Firebase implementation will be plugged in.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// This build links no Firebase library and carries no google-services file: [registrar] is the null implementation. When the maintainer has a Google project, a class that extends
// FirebaseMessagingService calls [onMessage] with the data of the message, and a PushRegistrar that asks Firebase for the token replaces [NullPushRegistrar] below.
package es.electrohobby3d.ghamonitor

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.electrohobby3d.ghamonitor.core.AppConfig
import es.electrohobby3d.ghamonitor.core.NullPushRegistrar
import es.electrohobby3d.ghamonitor.core.PushController
import es.electrohobby3d.ghamonitor.core.PushGate
import es.electrohobby3d.ghamonitor.core.PushMessage
import es.electrohobby3d.ghamonitor.core.PushRegistrar

object PushHub {
    /** Replaced by the Firebase implementation once a Google project exists. */
    val registrar: PushRegistrar = NullPushRegistrar

    private var gate: PushGate? = null

    /** Shows the notification for the data of one push message, if the switch is on and the message is critical and new. Returns true when something was shown. */
    @Synchronized
    fun onMessage(context: Context, config: AppConfig, data: Map<String, String>): Boolean {
        val app = context.applicationContext
        val store = PreferencesStore(app.getSharedPreferences(MonitorViewModel.PREFS, Context.MODE_PRIVATE))
        val enabled = store.get(PushController.KEY_ENABLED) == "1"
        val current = gate ?: PushGate(config.push).also { gate = it }
        val message = current.accept(data, enabled) ?: return false
        return show(app, config, message)
    }

    // the permission is checked on the first line of the function; lint cannot follow that through the early return
    @SuppressLint("MissingPermission")
    private fun show(context: Context, config: AppConfig, message: PushMessage): Boolean {
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return false
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(config.push.channelId, context.getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_HIGH)
                .apply { description = context.getString(R.string.notif_channel_desc) },
        )
        val open = Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP; putExtra(MainActivity.EXTRA_TAB, MainActivity.TAB_ALERTS) }
        val pending = PendingIntent.getActivity(context, 0, open, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, config.push.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notif_title_critical))
            .setContentText(message.title.ifEmpty { message.code })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        NotificationManagerCompat.from(context).notify(message.alertId.hashCode(), notification)
        return true
    }
}

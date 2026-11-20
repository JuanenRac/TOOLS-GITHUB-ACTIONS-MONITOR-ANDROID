// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the settings: the address of the monitor, the token (sealed in the Keystore), the name used for four eyes, and the push switch (off).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import es.electrohobby3d.ghamonitor.core.PushState

@Composable
fun SettingsScreen(ui: UiState, model: MonitorViewModel, onPushSwitch: (Boolean) -> Unit) {
    var url by rememberSaveable(ui.settings.baseUrl) { mutableStateOf(ui.settings.baseUrl) }
    var token by rememberSaveable { mutableStateOf("") }
    var user by rememberSaveable(ui.settings.userName) { mutableStateOf(ui.settings.userName) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text(stringResource(R.string.settings_url)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        ui.urlProblem?.let { Text(stringResource(it.res()), color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text(stringResource(R.string.settings_token)) },
            supportingText = { Text(stringResource(if (ui.settings.token.isEmpty()) R.string.settings_token_empty else R.string.settings_token_kept)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
        OutlinedTextField(
            value = user,
            onValueChange = { user = it.take(64) },
            label = { Text(stringResource(R.string.settings_user)) },
            supportingText = { Text(stringResource(R.string.settings_user_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { model.saveSettings(url, token, user); token = "" }) { Text(stringResource(R.string.btn_save)) }
            OutlinedButton(onClick = { model.forgetToken() }, enabled = ui.settings.token.isNotEmpty()) { Text(stringResource(R.string.btn_forget_token)) }
        }
        when (ui.notice) {
            Notice.SETTINGS_SAVED -> Text(stringResource(R.string.settings_saved), modifier = Modifier.padding(top = 8.dp))
            Notice.TOKEN_FORGOTTEN -> Text(stringResource(R.string.settings_token_forgotten), modifier = Modifier.padding(top = 8.dp))
            else -> Unit
        }

        Text(stringResource(R.string.push_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
        Text(stringResource(R.string.push_description), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Switch(checked = ui.pushEnabled, onCheckedChange = { onPushSwitch(it) }, enabled = ui.settings.configured && (ui.pushAvailable || ui.pushEnabled))
            Text(stringResource(if (ui.pushEnabled) R.string.push_on else R.string.push_off))
        }
        if (!ui.pushAvailable) Text(stringResource(R.string.push_not_available), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        when (ui.pushState) {
            PushState.REGISTER_FAILED -> Text(stringResource(R.string.push_failed), color = MaterialTheme.colorScheme.error)
            else -> Unit
        }
        Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 24.dp))
    }
}

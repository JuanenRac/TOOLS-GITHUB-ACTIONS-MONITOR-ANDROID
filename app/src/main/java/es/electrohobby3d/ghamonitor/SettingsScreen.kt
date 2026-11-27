// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the connection: the address of the monitor and its access token (sealed in the Keystore), and signing in when the monitor asks for it.
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(ui: UiState, model: MonitorViewModel) {
    var url by rememberSaveable(ui.settings.baseUrl) { mutableStateOf(ui.settings.baseUrl) }
    var token by remember { mutableStateOf("") }          // not saveable: a token or a password never goes into the saved state of the activity
    var user by rememberSaveable(ui.settings.userName) { mutableStateOf(ui.settings.userName) }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        ScreenTitle(stringResource(R.string.settings_title), stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text(stringResource(R.string.settings_url)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("settings-url"),
        )
        ui.urlProblem?.let { Text(stringResource(it.res()), color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text(stringResource(R.string.settings_token)) },
            supportingText = { Text(stringResource(if (ui.settings.token.isEmpty()) R.string.settings_token_empty else R.string.settings_token_kept)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("settings-token"),
        )
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { model.saveSettings(url, token); token = "" }, modifier = Modifier.testTag("settings-save")) { Text(stringResource(R.string.btn_save)) }
            OutlinedButton(onClick = { model.forgetToken() }, enabled = ui.settings.token.isNotEmpty()) { Text(stringResource(R.string.btn_forget_token)) }
        }
        when (ui.notice) {
            Notice.SETTINGS_SAVED -> Text(stringResource(R.string.settings_saved), modifier = Modifier.padding(top = 8.dp))
            Notice.TOKEN_FORGOTTEN -> Text(stringResource(R.string.settings_token_forgotten), modifier = Modifier.padding(top = 8.dp))
            Notice.CONNECTED -> Text(stringResource(R.string.settings_connected), color = MonitorColors.Ok, modifier = Modifier.padding(top = 8.dp).testTag("connected"))
            Notice.SIGNED_OUT -> Text(stringResource(R.string.settings_signed_out), modifier = Modifier.padding(top = 8.dp))
            null -> Unit
        }
        ErrorLine(ui.error)

        if (ui.settings.session.isNotEmpty()) {
            Panel(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text(stringResource(R.string.settings_signed_in, ui.settings.userName))
                OutlinedButton(onClick = { model.signOut() }, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(R.string.btn_sign_out)) }
            }
        } else if (ui.auth?.needsSignIn == true) {
            Panel(Modifier.fillMaxWidth().padding(top = 12.dp), tint = MonitorColors.Accent) {
                Text(stringResource(R.string.settings_sign_in_needed))
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it.take(64) },
                    label = { Text(stringResource(R.string.settings_user)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("settings-user"),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it.take(256) },
                    label = { Text(stringResource(R.string.settings_password)) },
                    supportingText = { Text(stringResource(R.string.settings_password_hint)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("settings-password"),
                )
                Button(onClick = { model.signIn(user, password); password = "" }, modifier = Modifier.padding(top = 8.dp).testTag("settings-signin")) { Text(stringResource(R.string.btn_sign_in)) }
            }
        }

        Text(stringResource(R.string.settings_help_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
        Text(stringResource(R.string.settings_help), style = MaterialTheme.typography.bodySmall, color = MonitorColors.Muted, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
    }
}

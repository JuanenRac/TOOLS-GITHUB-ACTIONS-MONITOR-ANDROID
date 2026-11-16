// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the frame with its five tabs, and the summary, alerts and repositories screens (the same views as the phone web app of the monitor).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.electrohobby3d.ghamonitor.core.Alert
import es.electrohobby3d.ghamonitor.core.ErrorKey
import es.electrohobby3d.ghamonitor.core.Format
import java.time.ZoneId

@Composable
fun MonitorApp(model: MonitorViewModel, onPushSwitch: (Boolean) -> Unit) {
    val ui by model.ui.collectAsStateWithLifecycle()
    // What a tab shows is loaded the first time the tab is opened (and whenever the settings change), never in the background.
    LaunchedEffect(ui.tab, ui.settings) {
        if (!ui.settings.configured) return@LaunchedEffect
        when (ui.tab) {
            MainActivity.TAB_SUMMARY -> { if (ui.summary == null) model.refreshSummary() }
            MainActivity.TAB_ALERTS -> { if (ui.alerts.isEmpty()) model.loadAlerts(false) }
            MainActivity.TAB_REPOS -> { if (ui.repos.isEmpty()) model.loadRepos() }
            MainActivity.TAB_PROPOSALS -> { if (ui.proposals.isEmpty() && ui.detail == null) model.loadProposals(false) }
            else -> Unit
        }
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(ui.tab == MainActivity.TAB_SUMMARY, { model.selectTab(MainActivity.TAB_SUMMARY) }, { Icon(Icons.Filled.Home, null) }, label = { Text(stringResource(R.string.tab_summary)) })
                NavigationBarItem(ui.tab == MainActivity.TAB_ALERTS, { model.selectTab(MainActivity.TAB_ALERTS) }, { Icon(Icons.Filled.Notifications, null) }, label = { Text(stringResource(R.string.tab_alerts)) })
                NavigationBarItem(ui.tab == MainActivity.TAB_REPOS, { model.selectTab(MainActivity.TAB_REPOS) }, { Icon(Icons.Filled.Search, null) }, label = { Text(stringResource(R.string.tab_repos)) })
                NavigationBarItem(ui.tab == MainActivity.TAB_PROPOSALS, { model.selectTab(MainActivity.TAB_PROPOSALS) }, { Icon(Icons.Filled.Check, null) }, label = { Text(stringResource(R.string.tab_proposals)) })
                NavigationBarItem(ui.tab == MainActivity.TAB_SETTINGS, { model.selectTab(MainActivity.TAB_SETTINGS) }, { Icon(Icons.Filled.Settings, null) }, label = { Text(stringResource(R.string.tab_settings)) })
            }
        },
    ) { padding ->
        Surface(Modifier.padding(padding).fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (ui.configProblem) {
                Message(stringResource(R.string.err_config))
            } else if (ui.tab == MainActivity.TAB_SETTINGS) {
                SettingsScreen(ui, model, onPushSwitch)
            } else if (!ui.settings.configured) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.not_configured_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.not_configured_text), modifier = Modifier.padding(vertical = 8.dp))
                    Button(onClick = { model.selectTab(MainActivity.TAB_SETTINGS) }) { Text(stringResource(R.string.tab_settings)) }
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    when (ui.tab) {
                        MainActivity.TAB_SUMMARY -> SummaryScreen(ui, model)
                        MainActivity.TAB_ALERTS -> AlertsScreen(ui, model)
                        MainActivity.TAB_REPOS -> ReposScreen(ui, model)
                        else -> ProposalsScreen(ui, model)
                    }
                    if (ui.loading) CircularProgressIndicator(Modifier.align(Alignment.TopEnd).padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun Message(text: String) {
    Text(text, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun ErrorLine(key: ErrorKey?) {
    if (key != null) Text(stringResource(key.res()), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun LevelText(word: String) {
    Text(wordText(word), color = severityColor(word), style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
}

@Composable
fun AlertCard(a: Alert) {
    val zone = remember { ZoneId.systemDefault() }
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LevelText(a.level)
                Spacer(Modifier.width(8.dp))
                Text(a.repo, style = MaterialTheme.typography.bodyMedium)
            }
            if (a.title.isNotEmpty()) Text(a.title, style = MaterialTheme.typography.bodyMedium)
            Text(listOf(a.code, Format.moment(a.createdAt, zone)).filter { it.isNotEmpty() }.joinToString(" - "), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SummaryScreen(ui: UiState, model: MonitorViewModel) {
    val zone = remember { ZoneId.systemDefault() }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        ErrorLine(ui.error)
        val data = ui.summary
        if (data != null) {
            Text(stringResource(R.string.summary_overall), style = MaterialTheme.typography.titleMedium)
            LevelText(data.status.level)
            Text(stringResource(R.string.summary_updated, Format.moment(data.status.updatedAt, zone)), style = MaterialTheme.typography.labelSmall)
            Text(stringResource(R.string.summary_monitor, data.health.version, data.health.edition), style = MaterialTheme.typography.labelSmall)
            SectionTitle(stringResource(R.string.summary_accounts))
            if (data.accounts.isEmpty()) Text(stringResource(R.string.empty_list))
            data.accounts.forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(row.account.label.ifEmpty { row.account.id })
                    LevelText(row.status?.level ?: "?")
                }
            }
            SectionTitle(stringResource(R.string.summary_recent))
            if (data.recent.isEmpty()) Text(stringResource(R.string.empty_list))
            data.recent.forEach { AlertCard(it) }
        }
        OutlinedButton(onClick = { model.refreshSummary() }, modifier = Modifier.padding(top = 16.dp)) { Text(stringResource(R.string.refresh)) }
    }
}

@Composable
private fun AlertsScreen(ui: UiState, model: MonitorViewModel) {
    val filters = listOf<String?>(null, "critical", "error", "warning", "info")
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { level ->
                val label = if (level == null) stringResource(R.string.alerts_filter_all) else wordText(level)
                if (ui.alertLevel == level) Button(onClick = { model.setAlertLevel(level) }) { Text(label) } else OutlinedButton(onClick = { model.setAlertLevel(level) }) { Text(label) }
            }
        }
        ErrorLine(ui.error)
        LazyColumn(Modifier.weight(1f)) {
            items(ui.alerts) { AlertCard(it) }
            item {
                if (ui.alerts.isEmpty() && !ui.loading) Text(stringResource(R.string.empty_list), modifier = Modifier.padding(vertical = 8.dp))
                if (ui.alertsCursor != null) OutlinedButton(onClick = { model.loadAlerts(true) }, modifier = Modifier.padding(vertical = 8.dp)) { Text(stringResource(R.string.load_more)) }
                OutlinedButton(onClick = { model.loadAlerts(false) }, modifier = Modifier.padding(bottom = 8.dp)) { Text(stringResource(R.string.refresh)) }
            }
        }
    }
}

@Composable
private fun ReposScreen(ui: UiState, model: MonitorViewModel) {
    var typed by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(stringResource(R.string.repos_hint), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = typed,
            onValueChange = { typed = it },
            label = { Text(stringResource(R.string.repos_lookup_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
        Button(onClick = { model.lookupRepo(typed) }, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(R.string.repos_lookup_button)) }
        if (ui.repoLookupInvalid) Text(stringResource(R.string.repos_invalid), color = MaterialTheme.colorScheme.error)
        ui.repoLookup?.let { found ->
            Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(found.repo)
                Spacer(Modifier.width(8.dp))
                LevelText(found.level)
            }
        }
        ErrorLine(ui.error)
        SectionTitle(stringResource(R.string.repos_with_alerts))
        if (ui.repos.isEmpty() && !ui.loading) Text(stringResource(R.string.empty_list))
        ui.repos.forEach { (repo, level) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(repo, modifier = Modifier.weight(1f))
                LevelText(level)
                OutlinedButton(onClick = { typed = repo; model.lookupRepo(repo) }, modifier = Modifier.padding(start = 8.dp)) { Text(stringResource(R.string.repos_check)) }
            }
        }
        OutlinedButton(onClick = { model.loadRepos() }, modifier = Modifier.padding(top = 16.dp)) { Text(stringResource(R.string.refresh)) }
    }
}

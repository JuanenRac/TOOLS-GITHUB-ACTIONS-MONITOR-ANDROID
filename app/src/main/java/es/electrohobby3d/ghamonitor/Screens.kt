// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the frame with its five tabs, and the summary, alerts and repositories screens.
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
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.electrohobby3d.ghamonitor.core.Alert
import es.electrohobby3d.ghamonitor.core.ErrorKey
import es.electrohobby3d.ghamonitor.core.Format
import es.electrohobby3d.ghamonitor.core.RepoRow
import java.time.ZoneId

private class Tab(val index: Int, val tag: String, val icon: ImageVector, val label: Int)

private val TABS = listOf(
    Tab(MainActivity.TAB_SUMMARY, "tab-summary", Icons.Filled.Dashboard, R.string.tab_summary),
    Tab(MainActivity.TAB_ALERTS, "tab-alerts", Icons.Filled.Notifications, R.string.tab_alerts),
    Tab(MainActivity.TAB_REPOS, "tab-repos", Icons.Filled.Folder, R.string.tab_repos),
    Tab(MainActivity.TAB_PROPOSALS, "tab-proposals", Icons.Filled.Lightbulb, R.string.tab_proposals),
    Tab(MainActivity.TAB_SETTINGS, "tab-settings", Icons.Filled.Settings, R.string.tab_settings),
)

@Composable
fun MonitorApp(model: MonitorViewModel) {
    val ui by model.ui.collectAsStateWithLifecycle()
    // What a tab shows is loaded the first time the tab is opened (and whenever the settings change), never in the background.
    LaunchedEffect(ui.tab, ui.settings) {
        if (!ui.settings.configured) return@LaunchedEffect
        when (ui.tab) {
            MainActivity.TAB_SUMMARY -> { if (ui.summary == null) model.refreshSummary() }
            MainActivity.TAB_ALERTS -> { if (ui.alerts == null) model.loadAlerts() }
            MainActivity.TAB_REPOS -> { if (ui.repos == null) model.loadRepos() }
            MainActivity.TAB_PROPOSALS -> { if (ui.proposals == null) model.loadProposals() }
            else -> { if (ui.auth == null) model.checkConnection() }
        }
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                TABS.forEach { tab ->
                    NavigationBarItem(
                        selected = ui.tab == tab.index,
                        onClick = { model.selectTab(tab.index) },
                        icon = { Icon(tab.icon, null) },
                        label = { Text(stringResource(tab.label), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag(tab.tag),
                    )
                }
            }
        },
    ) { padding ->
        Surface(Modifier.padding(padding).fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (ui.configProblem) {
                Message(stringResource(R.string.err_config))
            } else if (ui.tab == MainActivity.TAB_SETTINGS) {
                SettingsScreen(ui, model)
            } else if (!ui.settings.configured) {
                Column(Modifier.padding(16.dp)) {
                    ScreenTitle(stringResource(R.string.not_configured_title))
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
                    if (ui.loading) CircularProgressIndicator(Modifier.align(Alignment.TopEnd).padding(16.dp).testTag("loading"))
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
    if (key != null) {
        Panel(Modifier.fillMaxWidth().padding(vertical = 4.dp), tint = MonitorColors.Error) {
            Text(stringResource(key.res()), color = MonitorColors.Text, modifier = Modifier.testTag("error"))
        }
    }
}

@Composable
fun LevelText(word: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StatusDot(severityColor(word))
        Spacer(Modifier.width(6.dp))
        Text(wordText(word), color = severityColor(word), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
}

@Composable
private fun Muted(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MonitorColors.Muted)
}

@Composable
fun AlertCard(a: Alert) {
    val zone = remember { ZoneId.systemDefault() }
    Panel(Modifier.fillMaxWidth().padding(vertical = 4.dp), tint = severityColor(a.level).takeIf { !a.muted }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LevelText(a.level)
            Spacer(Modifier.width(8.dp))
            Text(a.repo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (a.muted) Muted(stringResource(R.string.alerts_silenced))
        }
        if (a.title.isNotEmpty()) Text(a.title, style = MaterialTheme.typography.bodyMedium)
        Muted(listOf(a.code, Format.moment(a.createdAt, zone)).filter { it.isNotEmpty() }.joinToString(" - "))
    }
}

@Composable
private fun RepoTotals(ok: Int, warn: Int, bad: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatTile(bad.toString(), stringResource(R.string.level_error), MonitorColors.Error, Modifier.weight(1f))
        StatTile(warn.toString(), stringResource(R.string.level_warning), MonitorColors.Warning, Modifier.weight(1f))
        StatTile(ok.toString(), stringResource(R.string.level_ok), MonitorColors.Ok, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryScreen(ui: UiState, model: MonitorViewModel) {
    val zone = remember { ZoneId.systemDefault() }
    val data = ui.summary
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        val health = data?.health
        ScreenTitle(stringResource(R.string.tab_summary), if (health != null) stringResource(R.string.summary_monitor, health.version) else "")
        ErrorLine(ui.error ?: data?.error)
        if (data != null) {
            if (health?.lite == true) Message(stringResource(R.string.summary_lite))
            if (health?.offline == true) Message(stringResource(R.string.summary_offline))
            if (health != null && !health.loaded) Message(stringResource(R.string.summary_loading))
            data.team?.let { team ->
                SectionTitle(stringResource(R.string.summary_repos, team.all))
                RepoTotals(team.ok, team.warn, team.bad)
            }
            data.alerts?.let { alerts ->
                SectionTitle(stringResource(R.string.summary_alerts, alerts.total))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("critical", "error", "warning").forEach { level ->
                        StatTile((alerts.byLevel[level] ?: 0).toString(), wordText(level), severityColor(level), Modifier.weight(1f))
                    }
                }
            }
            if (data.accounts.isNotEmpty()) {
                SectionTitle(stringResource(R.string.summary_accounts))
                data.accounts.forEach { account ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(account.id)
                        LevelText(account.state)
                    }
                }
            }
            data.alerts?.let { alerts ->
                SectionTitle(stringResource(R.string.summary_recent))
                if (alerts.items.isEmpty()) Muted(stringResource(R.string.empty_list))
                alerts.items.take(5).forEach { AlertCard(it) }
            }
            Muted(stringResource(R.string.summary_updated, Format.moment(data.takenAtSeconds.toString(), zone)))
        }
        OutlinedButton(onClick = { model.refreshSummary() }, modifier = Modifier.padding(vertical = 12.dp)) { Text(stringResource(R.string.refresh)) }
    }
}

@Composable
private fun AlertsScreen(ui: UiState, model: MonitorViewModel) {
    val filters = listOf<String?>(null, "critical", "error", "warning", "info")
    val list = ui.alerts
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        ScreenTitle(stringResource(R.string.tab_alerts), if (list != null) stringResource(R.string.alerts_counts, list.total, list.muted) else "")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { level ->
                FilterChip(
                    selected = ui.alertLevel == level,
                    onClick = { model.setAlertLevel(level) },
                    label = { Text(if (level == null) stringResource(R.string.alerts_filter_all) else wordText(level)) },
                )
            }
        }
        ErrorLine(ui.error)
        LazyColumn(Modifier.weight(1f)) {
            items(list?.items.orEmpty()) { AlertCard(it) }
            item {
                if (list != null && list.items.isEmpty()) Muted(stringResource(R.string.empty_list))
                OutlinedButton(onClick = { model.loadAlerts() }, modifier = Modifier.padding(vertical = 8.dp)) { Text(stringResource(R.string.refresh)) }
            }
        }
    }
}

@Composable
private fun RepoCard(row: RepoRow) {
    val zone = remember { ZoneId.systemDefault() }
    Panel(Modifier.fillMaxWidth().padding(vertical = 4.dp), tint = severityColor(row.state).takeIf { row.state == "bad" || row.state == "warn" }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(row.repo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            LevelText(row.state)
        }
        Text(stringResource(R.string.repo_figures, row.redWorkflows, row.alerts, row.failures24h), style = MaterialTheme.typography.bodyMedium)
        if (row.lastRunAt.isNotEmpty()) Muted(stringResource(R.string.repo_last_run, Format.moment(row.lastRunAt, zone)))
    }
}

@Composable
private fun ReposScreen(ui: UiState, model: MonitorViewModel) {
    var typed by rememberSaveable { mutableStateOf(ui.repoFilter) }
    val team = ui.repos
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            ScreenTitle(stringResource(R.string.tab_repos), if (team != null) stringResource(R.string.repos_counts, team.shown, team.all) else "")
            OutlinedTextField(value = typed, onValueChange = { typed = it.take(80) }, label = { Text(stringResource(R.string.repos_filter)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(onClick = { model.loadRepos(typed) }, modifier = Modifier.padding(vertical = 8.dp)) { Text(stringResource(R.string.repos_search)) }
            ErrorLine(ui.error)
            if (team != null) {
                RepoTotals(team.ok, team.warn, team.bad)
                if (team.truncated) Muted(stringResource(R.string.repos_truncated, team.repos.size))
                if (team.repos.isEmpty()) Muted(stringResource(R.string.empty_list))
            }
        }
        items(team?.repos.orEmpty()) { RepoCard(it) }
        item { OutlinedButton(onClick = { model.loadRepos(typed) }, modifier = Modifier.padding(vertical = 8.dp)) { Text(stringResource(R.string.refresh)) } }
    }
}

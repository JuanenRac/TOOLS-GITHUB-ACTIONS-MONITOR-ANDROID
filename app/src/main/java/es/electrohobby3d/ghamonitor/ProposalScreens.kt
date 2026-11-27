// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the proposals of the AI of the monitor: the list and one of them. Read only: the monitor accepts approval from its own computer alone.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import es.electrohobby3d.ghamonitor.core.Format
import es.electrohobby3d.ghamonitor.core.Proposal

@Composable
fun ProposalsScreen(ui: UiState, model: MonitorViewModel) {
    val detail = ui.detail
    BackHandler(enabled = detail != null) { model.closeProposal() }
    if (detail != null) {
        ProposalDetailScreen(detail, model)
        return
    }
    val list = ui.proposals
    val shown = list?.items.orEmpty().filter { ui.proposalFilter == null || it.status == ui.proposalFilter }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            ScreenTitle(stringResource(R.string.tab_proposals), stringResource(R.string.proposal_read_only))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = ui.proposalFilter == null, onClick = { model.setProposalFilter(null) }, label = { Text(stringResource(R.string.alerts_filter_all)) })
                FilterChip(selected = ui.proposalFilter == "pending", onClick = { model.setProposalFilter("pending") }, label = { Text(stringResource(R.string.state_pending)) })
            }
            ErrorLine(ui.error)
            if (list != null && !list.enabled) Text(stringResource(R.string.proposal_off), modifier = Modifier.padding(vertical = 8.dp))
            if (list != null && shown.isEmpty()) Text(stringResource(R.string.empty_list), modifier = Modifier.padding(vertical = 8.dp))
        }
        items(shown) { p -> ProposalCard(p) { model.openProposal(p) } }
        item { OutlinedButton(onClick = { model.loadProposals() }, modifier = Modifier.padding(vertical = 8.dp)) { Text(stringResource(R.string.refresh)) } }
    }
}

@Composable
private fun ProposalCard(p: Proposal, onOpen: () -> Unit) {
    Panel(Modifier.fillMaxWidth().padding(vertical = 4.dp), tint = MonitorColors.Warning.takeIf { p.status == "pending" }, onClick = onOpen) {
        Text(stringResource(R.string.proposal_what, actionText(p.action), p.repo), style = MaterialTheme.typography.titleMedium)
        Text(statusText(p.status), style = MaterialTheme.typography.labelSmall, color = MonitorColors.Muted)
        Text(p.command, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun ProposalDetailScreen(detail: ProposalDetail, model: MonitorViewModel) {
    val p = detail.proposal
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        ScreenTitle(actionText(p.action), p.repo)
        if (detail.linkMismatch) {
            Panel(Modifier.fillMaxWidth().padding(bottom = 8.dp), tint = MonitorColors.Error) { Text(stringResource(R.string.proposal_link_mismatch)) }
        }
        Panel(Modifier.fillMaxWidth()) {
            SelectionContainer { Text(p.command, fontFamily = FontFamily.Monospace) }
        }
        Text(statusText(p.status), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
        if (p.creator.isNotEmpty()) Text(stringResource(R.string.proposal_created_by, p.creator))
        if (p.by.isNotEmpty()) Text(stringResource(R.string.proposal_decided_by, p.by))
        if (p.approvals > 0) Text(stringResource(R.string.proposal_approvals, p.approvals))
        Text(stringResource(R.string.proposal_age, Format.duration(p.ageSeconds)))
        Text(stringResource(R.string.proposal_expires_in, Format.duration(p.expiresInSeconds)))
        SelectionContainer { Text(stringResource(R.string.proposal_hash, p.hash), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp)) }
        Text(stringResource(R.string.proposal_read_only), modifier = Modifier.padding(top = 12.dp))
        OutlinedButton(onClick = { model.closeProposal() }, modifier = Modifier.padding(vertical = 12.dp)) { Text(stringResource(R.string.btn_back)) }
    }
}

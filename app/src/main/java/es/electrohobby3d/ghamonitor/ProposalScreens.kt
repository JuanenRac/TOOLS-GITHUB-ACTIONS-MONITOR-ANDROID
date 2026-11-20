// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the proposals: the list, and one proposal with its approve and reject buttons (the person always confirms; the server enforces four eyes).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.electrohobby3d.ghamonitor.core.Decision
import es.electrohobby3d.ghamonitor.core.Format
import es.electrohobby3d.ghamonitor.core.Proposal
import es.electrohobby3d.ghamonitor.core.ProposalRules
import java.time.ZoneId

@Composable
fun ProposalsScreen(ui: UiState, model: MonitorViewModel) {
    val detail = ui.detail
    BackHandler(enabled = detail != null) { model.closeProposal() }
    if (detail != null) {
        ProposalDetailScreen(ui, detail, model)
        return
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val pending = ui.proposalFilter == "pending"
            if (pending) Button(onClick = { model.setProposalFilter("pending") }) { Text(stringResource(R.string.state_pending)) } else OutlinedButton(onClick = { model.setProposalFilter("pending") }) { Text(stringResource(R.string.state_pending)) }
            if (!pending) Button(onClick = { model.setProposalFilter(null) }) { Text(stringResource(R.string.alerts_filter_all)) } else OutlinedButton(onClick = { model.setProposalFilter(null) }) { Text(stringResource(R.string.alerts_filter_all)) }
        }
        ErrorLine(ui.error)
        LazyColumn(Modifier.weight(1f)) {
            items(ui.proposals) { p -> ProposalCard(p) { model.openProposal(p.id) } }
            item {
                if (ui.proposals.isEmpty() && !ui.loading) Text(stringResource(R.string.empty_list), modifier = Modifier.padding(vertical = 8.dp))
                if (ui.proposalsCursor != null) OutlinedButton(onClick = { model.loadProposals(true) }, modifier = Modifier.padding(vertical = 8.dp)) { Text(stringResource(R.string.load_more)) }
                OutlinedButton(onClick = { model.loadProposals(false) }, modifier = Modifier.padding(bottom = 8.dp)) { Text(stringResource(R.string.refresh)) }
            }
        }
    }
}

@Composable
private fun ProposalCard(p: Proposal, onOpen: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onOpen)) {
        Column(Modifier.padding(12.dp)) {
            Text(stringResource(R.string.proposal_what, actionText(p.actionType), p.repo), style = MaterialTheme.typography.titleSmall)
            Text(stateText(p.state), style = MaterialTheme.typography.labelLarge)
            if (p.reason.isNotEmpty()) Text(p.reason, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ProposalDetailScreen(ui: UiState, detail: ProposalDetail, model: MonitorViewModel) {
    val p = detail.proposal
    val zone = remember { ZoneId.systemDefault() }
    val now = System.currentTimeMillis() / 1000
    val decision = ProposalRules.canApprove(p, ui.settings.userName, now, ui.fourEyesHint)
    val canApprove = decision == Decision.ALLOWED && !detail.linkMismatch
    val canReject = ProposalRules.canReject(p, now)
    var note by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(stringResource(R.string.proposal_what, actionText(p.actionType), p.repo), style = MaterialTheme.typography.titleMedium)
        Text(stateText(p.state), style = MaterialTheme.typography.labelLarge)
        if (p.reason.isNotEmpty()) Text(p.reason, modifier = Modifier.padding(vertical = 8.dp))
        Text(stringResource(R.string.proposal_account, p.accountId), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.proposal_created_by, p.createdBy), style = MaterialTheme.typography.bodyMedium)
        if (p.createdAt > 0) Text(stringResource(R.string.proposal_created_at, Format.moment(p.createdAt.toString(), zone)), style = MaterialTheme.typography.bodyMedium)
        if (p.state == "pending" && p.expiresAt > 0) Text(stringResource(R.string.proposal_expires_in, Format.duration(ProposalRules.secondsLeft(p, now))), style = MaterialTheme.typography.bodyMedium)
        if (p.approvedBy.isNotEmpty()) Text(stringResource(R.string.proposal_approved_by, p.approvedBy), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.proposal_hash, p.contentHash), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
        if (detail.linkMismatch) Text(stringResource(R.string.proposal_link_mismatch), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        when (decision) {
            Decision.OWN_PROPOSAL -> Text(stringResource(R.string.proposal_own), modifier = Modifier.padding(top = 8.dp))
            Decision.EXPIRED -> Text(stringResource(R.string.state_expired), modifier = Modifier.padding(top = 8.dp))
            Decision.NOT_PENDING, Decision.ALLOWED -> Unit
        }
        ErrorLine(ui.error)
        ui.notice?.let { notice ->
            when (notice) {
                Notice.PROPOSAL_APPROVED -> Text(stringResource(R.string.proposal_done_approved), modifier = Modifier.padding(top = 8.dp))
                Notice.PROPOSAL_REJECTED -> Text(stringResource(R.string.proposal_done_rejected), modifier = Modifier.padding(top = 8.dp))
                else -> Unit
            }
        }
        Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { model.askDecision(PendingDecision.APPROVE) }, enabled = canApprove) { Text(stringResource(R.string.btn_approve)) }
            OutlinedButton(onClick = { model.askDecision(PendingDecision.REJECT) }, enabled = canReject) { Text(stringResource(R.string.btn_reject)) }
            TextButton(onClick = { model.closeProposal() }) { Text(stringResource(R.string.btn_back)) }
        }
    }
    val pending = detail.pending
    if (pending != null) {
        val approving = pending == PendingDecision.APPROVE
        AlertDialog(
            onDismissRequest = { model.askDecision(null) },
            title = { Text(stringResource(if (approving) R.string.confirm_approve_title else R.string.confirm_reject_title)) },
            text = {
                Column {
                    Text(stringResource(if (approving) R.string.confirm_approve_text else R.string.confirm_reject_text, p.contentHash.take(12), p.repo))
                    if (!approving) {
                        OutlinedTextField(value = note, onValueChange = { note = it.take(200) }, label = { Text(stringResource(R.string.reject_note)) }, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { model.confirmDecision(note) }) { Text(stringResource(R.string.btn_confirm)) } },
            dismissButton = { TextButton(onClick = { model.askDecision(null) }) { Text(stringResource(R.string.btn_cancel)) } },
        )
    }
}

// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the state of every screen and the calls to the monitor, off the main thread.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.electrohobby3d.ghamonitor.core.Account
import es.electrohobby3d.ghamonitor.core.Alert
import es.electrohobby3d.ghamonitor.core.ApiException
import es.electrohobby3d.ghamonitor.core.AppConfig
import es.electrohobby3d.ghamonitor.core.ConfigException
import es.electrohobby3d.ghamonitor.core.DeepLink
import es.electrohobby3d.ghamonitor.core.DeviceApi
import es.electrohobby3d.ghamonitor.core.ErrorKey
import es.electrohobby3d.ghamonitor.core.Format
import es.electrohobby3d.ghamonitor.core.Health
import es.electrohobby3d.ghamonitor.core.HttpUrlTransport
import es.electrohobby3d.ghamonitor.core.MonitorApi
import es.electrohobby3d.ghamonitor.core.MonitorSettings
import es.electrohobby3d.ghamonitor.core.MonitorUrl
import es.electrohobby3d.ghamonitor.core.Proposal
import es.electrohobby3d.ghamonitor.core.ProposalLink
import es.electrohobby3d.ghamonitor.core.PushController
import es.electrohobby3d.ghamonitor.core.PushRegistrar
import es.electrohobby3d.ghamonitor.core.PushState
import es.electrohobby3d.ghamonitor.core.RepoHealth
import es.electrohobby3d.ghamonitor.core.SettingsRepository
import es.electrohobby3d.ghamonitor.core.StatusInfo
import es.electrohobby3d.ghamonitor.core.UrlCheck
import es.electrohobby3d.ghamonitor.core.UrlProblem
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AccountRow(val account: Account, val status: StatusInfo?)
data class SummaryData(val health: Health, val status: StatusInfo, val accounts: List<AccountRow>, val recent: List<Alert>)
enum class PendingDecision { APPROVE, REJECT }
enum class Notice { SETTINGS_SAVED, TOKEN_FORGOTTEN, PROPOSAL_APPROVED, PROPOSAL_REJECTED }

/** The proposal open on the screen: [linkMismatch] is true when it was opened by a link whose hash is not the content the monitor holds now (then approving is not offered). */
data class ProposalDetail(val proposal: Proposal, val linkMismatch: Boolean, val pending: PendingDecision? = null)

data class UiState(
    val configProblem: Boolean = false,
    val settings: MonitorSettings = MonitorSettings("", "", ""),
    val tab: Int = 0,
    val loading: Boolean = false,
    val error: ErrorKey? = null,
    val notice: Notice? = null,
    val urlProblem: UrlProblem? = null,
    val summary: SummaryData? = null,
    val alerts: List<Alert> = emptyList(),
    val alertsCursor: String? = null,
    val alertLevel: String? = null,
    val repos: List<Pair<String, String>> = emptyList(),
    val repoLookup: RepoHealth? = null,
    val repoLookupInvalid: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val proposalsCursor: String? = null,
    val proposalFilter: String? = null,
    val detail: ProposalDetail? = null,
    val pushEnabled: Boolean = false,
    val pushAvailable: Boolean = false,
    val pushState: PushState? = null,
    val fourEyesHint: Boolean = true,
)

class MonitorViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val PREFS = "ghamonitor_settings"
    }

    private val store = PreferencesStore(app.getSharedPreferences(PREFS, Context.MODE_PRIVATE))
    private val settingsRepo = SettingsRepository(store, KeystoreBox())
    private val config: AppConfig? = try {
        AppConfig.parse(app.assets.open("app_config.json").bufferedReader().use { it.readText() })
    } catch (e: ConfigException) {
        null
    } catch (e: IOException) {
        null
    }

    private val _ui = MutableStateFlow(UiState(configProblem = config == null))
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    private var api: MonitorApi? = null
    private var controller: PushController? = null

    init {
        reloadSettings()
    }

    // ---- settings -------------------------------------------------------------------------------------------------------------------------------------

    private fun reloadSettings() {
        val cfg = config ?: return
        val settings = settingsRepo.load()
        api = if (settings.configured) MonitorApi(settings.baseUrl, settings.token, HttpUrlTransport(cfg.api), cfg) else null
        val registrar = wrap(PushHub.registrar)
        controller = PushController(registrar, object : DeviceApi {
            override fun registerDevice(deviceToken: String, platform: String): String = (api ?: throw ApiException(ErrorKey.NOT_CONFIGURED)).registerDevice(deviceToken, platform)
            override fun unregisterDevice(id: String) { (api ?: throw ApiException(ErrorKey.NOT_CONFIGURED)).unregisterDevice(id) }
        }, store)
        _ui.update { it.copy(settings = settings, pushEnabled = controller?.enabled == true, pushAvailable = registrar.available, fourEyesHint = cfg.proposals.fourEyesHint) }
    }

    /** The push service calls back on any thread; the controller talks to the network, so the callback is moved to a background one. */
    private fun wrap(inner: PushRegistrar): PushRegistrar = object : PushRegistrar {
        override val available: Boolean = inner.available
        override fun fetchToken(onResult: (String?) -> Unit) = inner.fetchToken { token -> viewModelScope.launch(Dispatchers.IO) { onResult(token) } }
    }

    fun saveSettings(url: String, token: String, userName: String) {
        when (val checked = MonitorUrl.check(url)) {
            is UrlCheck.Bad -> _ui.update { it.copy(urlProblem = checked.reason, notice = null) }
            is UrlCheck.Good -> {
                val changed = checked.base != _ui.value.settings.baseUrl
                settingsRepo.save(checked.base, token, userName)
                if (changed) forgetDevice()
                reloadSettings()
                _ui.update { it.copy(urlProblem = null, notice = Notice.SETTINGS_SAVED, error = null) }
            }
        }
    }

    fun forgetToken() {
        settingsRepo.forgetToken()
        reloadSettings()
        _ui.update { it.copy(notice = Notice.TOKEN_FORGOTTEN) }
    }

    /** A new address means a different monitor: the device registered at the old one is forgotten here, and the switch goes off. */
    private fun forgetDevice() {
        store.remove(PushController.KEY_DEVICE)
        store.put(PushController.KEY_ENABLED, "0")
    }

    fun clearMessages() { _ui.update { it.copy(notice = null, error = null, pushState = null) } }

    // ---- push -----------------------------------------------------------------------------------------------------------------------------------------

    fun setPush(on: Boolean) {
        val c = controller ?: return
        viewModelScope.launch(Dispatchers.IO) {
            if (on) {
                c.turnOn { state -> _ui.update { it.copy(pushEnabled = c.enabled, pushState = state) } }
            } else {
                val state = c.turnOff()
                _ui.update { it.copy(pushEnabled = c.enabled, pushState = state) }
            }
        }
    }

    fun pushPermissionDenied() { _ui.update { it.copy(pushState = PushState.REGISTER_FAILED, pushEnabled = false) } }

    // ---- calls ----------------------------------------------------------------------------------------------------------------------------------------

    /** Runs [block] with the client off the main thread; [done] runs on the main one. Any failure ends in [UiState.error]. */
    private fun <T> call(block: (MonitorApi) -> T, done: (T) -> Unit) {
        val client = api
        if (client == null) {
            _ui.update { it.copy(error = ErrorKey.NOT_CONFIGURED) }
            return
        }
        _ui.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val result: Result<T> = withContext(Dispatchers.IO) {
                try {
                    Result.success(block(client))
                } catch (e: ApiException) {
                    Result.failure(e)
                } catch (e: Exception) {
                    Result.failure(ApiException(ErrorKey.UNKNOWN))
                }
            }
            val value = result.getOrNull()
            if (result.isSuccess && value != null) {
                done(value)
                _ui.update { it.copy(loading = false) }
            } else {
                val key = (result.exceptionOrNull() as? ApiException)?.key ?: ErrorKey.UNKNOWN
                _ui.update { it.copy(loading = false, error = key) }
            }
        }
    }

    fun refreshSummary() = call({ client ->
        val health = client.health()
        val status = client.status()
        val rows = client.accounts().take(20).map { account -> AccountRow(account, try { client.accountStatus(account.id) } catch (e: ApiException) { null }) }
        SummaryData(health, status, rows, client.alerts().items.take(5))
    }) { data -> _ui.update { it.copy(summary = data) } }

    fun loadAlerts(more: Boolean) {
        val cursor = if (more) _ui.value.alertsCursor else null
        if (more && cursor == null) return
        val level = _ui.value.alertLevel
        val keep = config?.let { it.api.pageSize * it.api.maxPagesInMemory } ?: 300
        call({ it.alerts(cursor, level) }) { page ->
            _ui.update { s -> s.copy(alerts = if (more) (s.alerts + page.items).takeLast(keep) else page.items, alertsCursor = page.nextCursor) }
        }
    }

    fun setAlertLevel(level: String?) {
        _ui.update { it.copy(alertLevel = level, alerts = emptyList(), alertsCursor = null) }
        loadAlerts(false)
    }

    fun loadRepos() {
        val pages = config?.api?.maxPagesInMemory ?: 1
        call({ client ->
            val all = ArrayList<Alert>()
            var cursor: String? = null
            var n = 0
            do {
                val page = client.alerts(cursor)
                all.addAll(page.items)
                cursor = page.nextCursor
                n++
            } while (cursor != null && n < pages)
            Format.reposByWorst(all)
        }) { repos -> _ui.update { it.copy(repos = repos) } }
    }

    fun lookupRepo(text: String) {
        val parts = Format.splitRepo(text)
        if (parts == null) {
            _ui.update { it.copy(repoLookupInvalid = true, repoLookup = null) }
            return
        }
        _ui.update { it.copy(repoLookupInvalid = false) }
        call({ it.repoHealth(parts.first, parts.second) }) { health -> _ui.update { it.copy(repoLookup = health) } }
    }

    fun loadProposals(more: Boolean) {
        val cursor = if (more) _ui.value.proposalsCursor else null
        if (more && cursor == null) return
        val state = _ui.value.proposalFilter
        call({ it.proposals(state, cursor) }) { page ->
            _ui.update { s -> s.copy(proposals = if (more) s.proposals + page.items else page.items, proposalsCursor = page.nextCursor) }
        }
    }

    fun setProposalFilter(state: String?) {
        _ui.update { it.copy(proposalFilter = state, proposals = emptyList(), proposalsCursor = null) }
        loadProposals(false)
    }

    fun openProposal(id: String, link: ProposalLink? = null) {
        call({ it.proposal(id) }) { proposal ->
            val mismatch = link != null && !DeepLink.matches(link, proposal)
            _ui.update { it.copy(detail = ProposalDetail(proposal, mismatch)) }
        }
    }

    fun closeProposal() { _ui.update { it.copy(detail = null) } }

    fun askDecision(decision: PendingDecision?) { _ui.update { s -> s.copy(detail = s.detail?.copy(pending = decision)) } }

    /** Sends the decision the person confirmed. An approval carries the hash of the content that was on the screen. */
    fun confirmDecision(note: String) {
        val detail = _ui.value.detail ?: return
        val decision = detail.pending ?: return
        val proposal = detail.proposal
        call({ client ->
            if (decision == PendingDecision.APPROVE) client.approve(proposal.id, proposal.contentHash) else client.reject(proposal.id, note)
        }) { updated ->
            _ui.update { s ->
                s.copy(
                    detail = ProposalDetail(updated, false),
                    proposals = s.proposals.map { if (it.id == updated.id) updated else it },
                    notice = if (decision == PendingDecision.APPROVE) Notice.PROPOSAL_APPROVED else Notice.PROPOSAL_REJECTED,
                )
            }
        }
    }

    // ---- navigation -----------------------------------------------------------------------------------------------------------------------------------

    fun selectTab(tab: Int) { _ui.update { it.copy(tab = tab, error = null, notice = null) } }

    /** A link (ghamon://proposal/...) or a tap on a notification: open what it names. A link never approves anything by itself. */
    fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val link = DeepLink.parse(intent.dataString)
        if (link != null) {
            selectTab(MainActivity.TAB_PROPOSALS)
            openProposal(link.proposalId, link)
            return
        }
        val tab = intent.getIntExtra(MainActivity.EXTRA_TAB, -1)
        if (tab in 0..MainActivity.TAB_SETTINGS) selectTab(tab)
    }
}

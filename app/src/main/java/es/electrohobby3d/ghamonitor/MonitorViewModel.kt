// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the state of every screen and the calls to the monitor, off the main thread.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.electrohobby3d.ghamonitor.core.AlertList
import es.electrohobby3d.ghamonitor.core.ApiException
import es.electrohobby3d.ghamonitor.core.AppConfig
import es.electrohobby3d.ghamonitor.core.AuthState
import es.electrohobby3d.ghamonitor.core.ConfigException
import es.electrohobby3d.ghamonitor.core.DeepLink
import es.electrohobby3d.ghamonitor.core.ErrorKey
import es.electrohobby3d.ghamonitor.core.HttpUrlTransport
import es.electrohobby3d.ghamonitor.core.MonitorApi
import es.electrohobby3d.ghamonitor.core.MonitorSettings
import es.electrohobby3d.ghamonitor.core.MonitorUrl
import es.electrohobby3d.ghamonitor.core.Proposal
import es.electrohobby3d.ghamonitor.core.ProposalLink
import es.electrohobby3d.ghamonitor.core.ProposalList
import es.electrohobby3d.ghamonitor.core.SettingsRepository
import es.electrohobby3d.ghamonitor.core.Summary
import es.electrohobby3d.ghamonitor.core.TeamHealth
import es.electrohobby3d.ghamonitor.core.UrlCheck
import es.electrohobby3d.ghamonitor.core.UrlProblem
import es.electrohobby3d.ghamonitor.core.loadSummary
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Notice { SETTINGS_SAVED, TOKEN_FORGOTTEN, CONNECTED, SIGNED_OUT }

/** The proposal open on the screen: [linkMismatch] is true when it was opened by a link whose hash is not the content the monitor holds now. */
data class ProposalDetail(val proposal: Proposal, val linkMismatch: Boolean)

data class UiState(
    val configProblem: Boolean = false,
    val settings: MonitorSettings = MonitorSettings("", ""),
    val tab: Int = 0,
    val loading: Boolean = false,
    val error: ErrorKey? = null,
    val notice: Notice? = null,
    val urlProblem: UrlProblem? = null,
    val auth: AuthState? = null,
    val summary: Summary? = null,
    val alerts: AlertList? = null,
    val alertLevel: String? = null,
    val repos: TeamHealth? = null,
    val repoFilter: String = "",
    val proposals: ProposalList? = null,
    val proposalFilter: String? = null,
    val detail: ProposalDetail? = null,
)

class MonitorViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val PREFS = "ghamonitor_settings"
    }

    private val settingsRepo = SettingsRepository(PreferencesStore(app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)), KeystoreBox())
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

    init {
        reloadSettings()
    }

    // ---- settings and signing in ----------------------------------------------------------------------------------------------------------------------

    /** Reads what is kept and makes a client for it; what was shown belonged to the settings before, so it goes. */
    private fun reloadSettings() {
        val cfg = config ?: return
        val settings = settingsRepo.load()
        api = if (settings.configured) MonitorApi(settings.baseUrl, settings.token, settings.session, HttpUrlTransport(cfg.api), cfg) else null
        _ui.update { it.copy(settings = settings, loading = false, auth = null, summary = null, alerts = null, repos = null, proposals = null, detail = null) }
    }

    /** Keeps the address and the token, then asks the monitor who we are: that says whether the token is right and whether it also asks a person to sign in. */
    fun saveSettings(url: String, token: String) {
        when (val checked = MonitorUrl.check(url)) {
            is UrlCheck.Bad -> _ui.update { it.copy(urlProblem = checked.reason, notice = null) }
            is UrlCheck.Good -> {
                settingsRepo.save(checked.base, token)
                reloadSettings()
                _ui.update { it.copy(urlProblem = null, notice = Notice.SETTINGS_SAVED, error = null) }
                if (api != null) checkConnection()
            }
        }
    }

    fun checkConnection() {
        call({ it.me() }) { auth -> _ui.update { it.copy(auth = auth, notice = if (auth.needsSignIn) null else Notice.CONNECTED, error = if (auth.bootstrap) ErrorKey.BOOTSTRAP else null) } }
    }

    /** The password goes to the monitor and nowhere else; what is kept is the session it gives back, sealed. */
    fun signIn(user: String, password: String) {
        if (user.isBlank() || password.isEmpty()) {
            _ui.update { it.copy(error = ErrorKey.INVALID_CREDENTIALS, notice = null) }
            return
        }
        call({ it.login(user.trim(), password) }) { session ->
            settingsRepo.saveSession(session.user, session.token)
            reloadSettings()
            checkConnection()
        }
    }

    fun signOut() {
        val client = api
        settingsRepo.forgetSession()
        reloadSettings()
        _ui.update { it.copy(notice = Notice.SIGNED_OUT, error = null) }
        if (client != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    client.logout()
                } catch (e: ApiException) {
                    // the session is gone from this phone either way; the monitor ends it by itself when it runs out
                }
            }
        }
    }

    fun forgetToken() {
        settingsRepo.forgetToken()
        reloadSettings()
        _ui.update { it.copy(notice = Notice.TOKEN_FORGOTTEN, error = null) }
    }

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
            if (client !== api) return@launch      // the settings changed while the monitor was being asked
            val value = result.getOrNull()
            if (result.isSuccess && value != null) {
                _ui.update { it.copy(loading = false) }
                done(value)
            } else {
                failed((result.exceptionOrNull() as? ApiException)?.key ?: ErrorKey.UNKNOWN)
            }
        }
    }

    /** A session that ran out is forgotten, and the settings show the sign-in again. */
    private fun failed(key: ErrorKey) {
        if (key == ErrorKey.SIGN_IN) {
            settingsRepo.forgetSession()
            reloadSettings()
            _ui.update { it.copy(auth = AuthState(teamMode = true, authenticated = false, bootstrap = false, user = "", role = "")) }
        }
        _ui.update { it.copy(loading = false, error = key, notice = null) }
    }

    fun refreshSummary() = call({ loadSummary(it) }) { data ->
        if (data.error == ErrorKey.SIGN_IN) failed(ErrorKey.SIGN_IN) else _ui.update { it.copy(summary = data) }
    }

    fun loadAlerts() {
        val level = _ui.value.alertLevel
        call({ it.alerts(level) }) { list -> _ui.update { it.copy(alerts = list) } }
    }

    fun setAlertLevel(level: String?) {
        _ui.update { it.copy(alertLevel = level, alerts = null) }
        loadAlerts()
    }

    fun loadRepos(filter: String = _ui.value.repoFilter) {
        val text = filter.trim().take(80)
        _ui.update { it.copy(repoFilter = text) }
        call({ it.teamHealth(text) }) { team -> _ui.update { it.copy(repos = team) } }
    }

    fun loadProposals() = call({ it.proposals() }) { list -> _ui.update { it.copy(proposals = list) } }

    fun setProposalFilter(status: String?) { _ui.update { it.copy(proposalFilter = status) } }

    fun openProposal(proposal: Proposal) { _ui.update { it.copy(detail = ProposalDetail(proposal, false)) } }

    private fun openLinked(link: ProposalLink) = call({ it.proposals() }) { list ->
        val found = list.items.firstOrNull { it.id == link.proposalId }
        _ui.update { s ->
            if (found == null) s.copy(proposals = list, error = ErrorKey.NOT_FOUND) else s.copy(proposals = list, detail = ProposalDetail(found, !DeepLink.matches(link, found)))
        }
    }

    fun closeProposal() { _ui.update { it.copy(detail = null) } }

    // ---- navigation -----------------------------------------------------------------------------------------------------------------------------------

    fun selectTab(tab: Int) { _ui.update { it.copy(tab = tab, error = null, notice = null, detail = null) } }

    /** A link (ghamon://proposal/...): open what it names. A link never decides anything. */
    fun handleIntent(intent: Intent?) {
        val link = DeepLink.parse(intent?.dataString) ?: return
        selectTab(MainActivity.TAB_PROPOSALS)
        openLinked(link)
    }
}

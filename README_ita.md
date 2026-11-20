<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | 🇮🇹 <b>Italiano</b> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### App Android prevista di GitHub Actions Monitor: lo stato dei tuoi workflow e gli avvisi del tuo monitor sul telefono. Solo impalcatura: non è ancora implementato nulla.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose%20(planned)-blue.svg" alt="Kotlin and Jetpack Compose (planned)">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-none%20yet-lightgrey.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Verifica di onestà - ciò che gira oggi:** niente dell'app. Questo repository contiene solo la sua base: i documenti, il manifesto, la licenza e i controlli condivisi del repository. Non ci sono codice, APK, schermate, servizio di notifiche né test dell'app, e nulla è stato provato su un telefono.

---

## 📋 In breve

| | |
|---|---|
| Versione | `0.0.1` |
| Maturità | Impalcatura |
| Test | Nessuno per ora (solo i controlli del repository) |
| Stack tecnologico | Kotlin e Jetpack Compose (previsto) |
| Su hardware reale | Mai eseguita su un telefono |

**Vai a:** [Panoramica](#-panoramica) · [Architettura](#-architettura) · [Avvio rapido](#-avvio-rapido) · [Test](#-test) · [Struttura del repository](#-struttura-del-repository) · [Progetti correlati](#-progetti-correlati)

## 🚦 Stato del progetto

Ogni riga è una parte di questo repository e il semaforo dice a che punto è oggi. È scritta in base a ciò che il repository può mostrare (test, compilazioni, esecuzioni), non a ciò che è previsto.

🟢 Fatto e verificato · 🟡 Funziona, con limiti noti · 🟠 Fatto a metà o non ancora verificato · 🔴 Non fatto o non funzionante

| Area | Stato | Cosa significa qui |
|---|---|---|
| Base del repository | 🟡 | Il manifesto, i README in otto lingue, la licenza e i controlli condivisi del repository girano nella CI. |
| Codice dell'app | 🔴 | Non iniziato: questo repository non ha ancora alcun progetto Android. |
| Test | 🔴 | L'app non ha test; girano solo i controlli del repository. |
| Collegamento al monitor | 🔴 | Non iniziato. Il monitor offre un'API di lettura che un client può usare; questa app non la usa ancora. |
| Telefono e notifiche | 🔴 | Non iniziato e mai eseguito su un telefono; nessun servizio di notifiche è collegato. |

## 🎯 Panoramica

* **Cosa sarà:** un'app Android che mostra, sul tuo telefono, lo stato dei workflow e gli avvisi del tuo GitHub Actions Monitor.
* **Sola lettura:** leggerà soltanto dal tuo monitor. Non scriverà su GitHub e non cambierà nulla nel monitor.
* **Il tuo monitor, i tuoi dati:** parlerà con il monitor che esegui tu stesso, con un token di accesso che le dai; non c'è alcun servizio nostro nel mezzo.
* **Cosa non è (ancora):** non c'è alcuna app da installare, né release, né data. Questo repository riserva solo il suo posto e dice con onestà ciò che è previsto.

## 🧩 Ruolo in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID vuole essere il client Android di GitHub Actions Monitor ([TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)). Mostrerebbe ciò che il monitor già sa e non deciderebbe nulla da sé. È prevista una versione minima per smartwatch, [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH), che segua questa app.

## 🏛️ Architettura

<p align="center">
  <img src="images/architecture.svg" alt="Architettura di TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: chi lo usa, che cosa contiene e da che cosa dipende" width="100%">
</p>

Come si incastrano le parti di questo repository: chi lo usa, che cosa contiene e da che cosa dipende. L'immagine è disegnata da `docs/architecture.json` e verificata rispetto al codice.

## ✨ Funzionalità

**Previsto, e nulla di tutto ciò esiste ancora:**
* Il riepilogo dei tuoi workflow (cosa è rosso, bloccato o in attesa) e gli avvisi del monitor.
* Un elenco di repository con il loro stato.
* Notifiche facoltative per gli avvisi importanti, disattivate per impostazione predefinita.
* Testi in otto lingue.

**Non previsto:** scrivere su GitHub, rieseguire workflow dal telefono o inviare i tuoi dati a un servizio diverso dal tuo monitor.

## 🔒 Sicurezza e protezione

* Previsto: l'app legge soltanto, e il token di accesso resta sul telefono e viene inviato solo all'indirizzo del tuo monitor.
* Nulla di segreto deve stare in questo repository: né chiavi, né token, né file di servizi di notifica, né chiavi di firma.
* Sono intenzioni di progetto. Nulla di tutto ciò esiste ancora, quindi nessuno ha potuto verificarlo.

## 🚀 Avvio rapido

Non c'è ancora nulla da installare o eseguire. Per controllare la base di questo repository serve Python 3.10 o successivo:

```bash
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Test

La CI esegue i controlli del repository e i test di quei controlli. L'app non ha test propri perché non ha ancora codice, e qui non si dichiara alcun numero di test.

## 🏗️ Compilazione e rilascio

Non ci sono compilazione né release, perché non esiste alcuna versione dell'app. La versione `0.0.1` in `VERSION`, nel manifesto e nel changelog è quella della base del repository.

## 📂 Struttura del repository

<p align="center">
  <img src="images/repository-structure.svg" alt="Struttura del repository TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Versione testuale dell'albero</summary>

```text
MONITOR-ANDROID/
├── tools/
│   ├── ci_validate.py                           manifesto, versioni, parità dei README
│   └── make_architecture.py make_structure.py   disegnano le due immagini del README
├── docs/                                        le sorgenti delle due immagini
├── images/                                      banner e le due immagini
├── .github/                                     workflow di CI e modelli
├── VERSION                                      la versione del repository
├── electro-hobby-3d.project.json                il manifesto del progetto
├── CHANGELOG.md                                 cosa è cambiato in ogni versione
├── SECURITY.md SUPPORT.md                       come segnalare una vulnerabilità e dove chiedere
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE   come contribuire, codice di condotta e licenza
```

</details>

## 🗺️ Roadmap e limiti noti

**Limiti noti oggi**

* Non c'è codice: né app, né APK, né schermata.
* Nulla è stato provato su un telefono né con un monitor reale.
* L'ambito finale dipende dall'API di lettura del monitor e può cambiare.

**Prossimo passo**

* Decidere le prime schermate, costruire l'app passo dopo passo con i suoi test nella CI e solo allora descriverla qui come funzionante.

## 🌍 Lingue

Il README e i testi esistono in inglese, spagnolo, francese, italiano, tedesco, cinese, giapponese e portoghese europeo.

## 🔗 Progetti correlati

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): il monitor che questa app vuole leggere
* [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH): la versione minima per smartwatch prevista, che segue questa app
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): le convenzioni e i controlli condivisi

## 📚 Documentazione

* [Le convenzioni della suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Registro delle modifiche](CHANGELOG.md)

## 🤝 Contribuire e comunità

* Prima di una pull request esegui `python -I tools/ci_validate.py`; gli altri controlli girano nella CI. Vedi [CONTRIBUTING.md](CONTRIBUTING.md) e il [codice di condotta](CODE_OF_CONDUCT.md).
* Il README esiste in otto lingue: una modifica al testo si fa in tutti e otto i file, e le due immagini sono disegnate da `docs/architecture.json` e `docs/structure.json` con `tools/make_architecture.py` e `tools/make_structure.py`.
* Segnala una vulnerabilità in privato: [SECURITY.md](SECURITY.md). Domande, idee e segnalazioni: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTORE

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENZA

Software: GPL-3.0-or-later. Vedi [LICENSE](LICENSE). I progetti hardware della suite usano la CERN OHL-S.

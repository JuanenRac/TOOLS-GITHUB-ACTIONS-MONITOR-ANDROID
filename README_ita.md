<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | 🇮🇹 <b>Italiano</b> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Client Android nativo di GitHub Actions Monitor: riepilogo, avvisi, repository e proposte sul telefono, con notifiche push solo critiche e disattivate per impostazione predefinita.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-77-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Verifica di onestà - cosa funziona oggi:** le regole e il client dell'API sono scritti con test unitari eseguiti sulla JVM nella CI; l'app non è ancora stata compilata in un APK, non è stata mostrata su un telefono e non ha mai parlato con un monitor reale. Le notifiche push sono disattivate per impostazione predefinita e nessun servizio push è collegato.

---

> **Stato di sicurezza funzionale e informatica:** questa app legge un monitor e può approvare o rifiutare proposte di azione che possono rieseguire workflow o commentare su GitHub. Un'approvazione è legata al contenuto esatto mostrato, deve approvare un'altra persona (lo impone il monitor) e un link non approva mai nulla da solo. Non è un sistema di sicurezza (safety) per alcuna macchina.

## 📋 In breve

| | |
|---|---|
| Versione | `0.0.1` |
| Maturità | Impalcatura |
| Test | 77 |
| Stack tecnologico | Kotlin, Jetpack Compose (Android 8.0+) |
| Su hardware reale | Non ancora eseguita su un telefono |

**Vai a:** [Panoramica](#-panoramica) · [Architettura](#-architettura) · [Avvio rapido](#-avvio-rapido) · [Test](#-test) · [Struttura del repository](#-struttura-del-repository) · [Progetti correlati](#-progetti-correlati)

## 🚦 Stato del progetto

Ogni riga è una parte di questo repository e il semaforo dice a che punto è oggi. È scritta in base a ciò che il repository può mostrare (test, compilazioni, esecuzioni), non a ciò che è previsto.

🟢 Fatto e verificato · 🟡 Funziona, con limiti noti · 🟠 Fatto a metà o non ancora verificato · 🔴 Non fatto o non funzionante

| Area | Stato | Cosa significa qui |
|---|---|---|
| Controlli del repository | 🟡 | Il controllo del repository, quello delle immagini, i controlli leggeri dell'app (otto lingue, risorse, segreti) e i loro test vengono eseguiti nella CI. |
| Regole e client dell'API | 🟠 | Sono scritti 69 test unitari sulla JVM (approvazione, link, filtro push, impostazioni, il client contro una rete simulata); la build Gradle che li esegue non è ancora stata eseguita. |
| Schermate e Keystore | 🔴 | Scritte, ma mai compilate, mai mostrate su uno schermo e mai eseguite su un telefono. |
| Notifiche push | 🟠 | Solo critiche e disattivate per impostazione predefinita; la parte Firebase è un'implementazione nulla finché non esiste un progetto Google. |
| Percorsi del monitor | 🟠 | I percorsi di lettura seguono il contratto dell'API v1 del monitor; i percorsi di proposte e dispositivi che l'app chiama non sono ancora nel monitor. |

## 🎯 Panoramica

* **Il monitor in tasca:** il riepilogo, gli avvisi, i repository e le proposte di un GitHub Actions Monitor, letti con un token bearer tramite la sua API v1.
* **Decisioni a quattro occhi:** approva o rifiuta una proposta dopo aver visto il suo contenuto esatto; l'approvazione è legata all'hash di quel contenuto e il monitor la rifiuta se la stessa persona ha creato la proposta.
* **Silenziosa per impostazione predefinita:** le notifiche push sono disattivate, solo critiche, una volta per avviso, con un titolo breve e nient'altro.
* **Niente di segreto nel repository:** il token è sigillato con una chiave del Keystore di Android; qui non ci sono file Firebase, chiavi o keystore.

## 🧩 Ruolo in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID è l'app Android di GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). È un client: non calcola regole proprie su avvisi o proposte; il monitor decide cosa è critico e cosa è una proposta, e l'app lo mostra e interpella una persona.

## 🏛️ Architettura

<p align="center">
  <img src="images/architecture.svg" alt="Architettura di TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: chi lo usa, che cosa contiene e da che cosa dipende" width="100%">
</p>

Come si incastrano le parti di questo repository: chi lo usa, che cosa contiene e da che cosa dipende. L'immagine è disegnata da `docs/architecture.json` e verificata rispetto al codice.

## ✨ Funzionalità

* Cinque schermate: riepilogo, avvisi (filtro per livello, paginazione con cursori), repository (quelli con avvisi e una ricerca di qualsiasi repository), proposte (in attesa o tutte, con dettaglio) e impostazioni.
* Approvare o rifiutare una proposta dopo una conferma che cita l'hash del contenuto; se indichi il tuo nome nelle impostazioni, una proposta creata da te non ti viene offerta per l'approvazione.
* I link `ghamon://proposal/<id>?hash=<hex>` aprono una proposta; se il suo contenuto è cambiato dalla creazione del link, l'approvazione non viene offerta.
* Registrazione del dispositivo per notifiche push solo critiche, disattivate per impostazione predefinita.
* Otto lingue che seguono la lingua del telefono: inglese, spagnolo, tedesco, francese, italiano, giapponese, cinese e portoghese del Portogallo.

## 🔒 Sicurezza e protezione

* Il token è sigillato con AES-GCM sotto una chiave che vive nel Keystore di Android, non viene mai scritto in un log né in un testo di errore ed è inviato solo all'indirizzo digitato; i reindirizzamenti non vengono mai seguiti.
* `http://` in chiaro è accettato solo per localhost, nomi `.local` e indirizzi IPv4 privati; qualsiasi altro host richiede `https://`, verificato solo con le autorità del sistema.
* Un'approvazione porta l'hash del contenuto che era sullo schermo; un link apre soltanto una proposta e non approva né esegue mai nulla.
* Le notifiche push sono disattivate per impostazione predefinita, solo critiche e con un titolo breve e ripulito: nessun log, nessun segreto dei repository.
* I dati dell'app sono esclusi dai backup, e `python -I tools/check_app.py` fallisce se nell'albero c'è un file Firebase, un keystore o una chiave.

## 🔌 Interfacce

* Letture del monitor: `GET /api/v1/health`, `/status`, `/accounts`, `/accounts/{id}/status`, `/alerts` (paginazione a cursore, filtri) e `/repos/{owner}/{repo}/health`, con `Authorization: Bearer` e `X-API-Version: 1`.
* Proposte (percorsi che l'app presume, tutti nel file di configurazione): `GET /api/v1/actions/proposals`, `GET .../{id}`, `POST .../{id}/approve` con `expected_hash` e `POST .../{id}/reject`.
* Dispositivi: `POST /api/v1/devices` e `DELETE /api/v1/devices/{id}`.
* Link: `ghamon://proposal/<id>?hash=<64 caratteri esadecimali>`.

## ⚙️ Configurazione

Ogni valore regolabile è in `app/src/main/assets/app_config.json`: i percorsi, i timeout, la dimensione della pagina, i valori push e lo schema del link. L'indirizzo del monitor, il suo token e il tuo nome si digitano nelle impostazioni dell'app e restano sul telefono. Nulla di regolabile è nel codice.

## 🚀 Avvio rapido

Per compilare servono JDK 21 e l'SDK di Android (Android Studio porta entrambi); i controlli leggeri richiedono solo Python 3.10 o successivo. Apri l'app, vai in Impostazioni, digita l'indirizzo del tuo monitor e il suo token e salva.

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Test

69 test unitari girano sulla JVM (`./gradlew testDebugUnitTest`) senza telefono: il client dell'API contro una rete simulata, le regole, i link, il filtro push e le impostazioni. 8 test Python coprono il repository e i controlli leggeri dell'app. La CI li esegue tutti; nulla qui è stato eseguito su un telefono.

## 🏗️ Compilazione e rilascio

La versione vive in `VERSION`, in `electro-hobby-3d.project.json` e in `app/build.gradle.kts` (la build si ferma se differiscono), e ogni versione ha la sua voce in [CHANGELOG.md](CHANGELOG.md). La build di rilascio è ridotta con R8 e firmata poi con la chiave propria del maintainer, tenuta fuori dal repository. Nessun APK è ancora pubblicato.

## 📂 Struttura del repository

<p align="center">
  <img src="images/repository-structure.svg" alt="Struttura del repository TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Versione testuale dell'albero</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                la compilazione e le regole di riduzione
│   ├── src/main/AndroidManifest.xml                       permessi, activity, link ghamon://
│   ├── src/main/assets/app_config.json                    percorsi, timeout, pagina, valori push
│   ├── src/main/res/                                      i testi in otto lingue, icone e temi
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   regole testate sulla JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        schermate, view model, Keystore, push
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   i test unitari sulla JVM
├── tools/
│   ├── ci_validate.py                                     manifesto, versioni, parità dei README
│   ├── check_app.py                                       lingue, risorse, configurazione e segreti dell'app
│   └── make_architecture.py make_structure.py             disegnano le due immagini del README
├── docs/                                                  le sorgenti delle due immagini
├── gradle/                                                i file del wrapper Gradle
├── gradlew gradlew.bat                                    il wrapper Gradle
├── build.gradle.kts settings.gradle.kts                   i plugin e il nome della build
├── gradle.properties                                      la memoria della build
├── images/                                                banner e le due immagini
├── .github/                                               workflow di CI e modelli
├── VERSION                                                la versione del repository
├── CHANGELOG.md                                           cosa è cambiato in ogni versione
├── SECURITY.md SUPPORT.md                                 come segnalare una vulnerabilità e dove chiedere
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             come contribuire, codice di condotta e licenza
```

</details>

## 🗺️ Roadmap e limiti noti

**Limiti noti oggi**

* L'app non è stata compilata in un APK, eseguita su un telefono né provata con un monitor reale.
* I percorsi di proposte e dispositivi che chiama non sono ancora nel monitor.
* Non c'è un servizio push: servono un progetto Google e una chiave che solo il maintainer può creare.
* Non c'è ancora la conferma biometrica prima di un'approvazione.

**Prossimi passi**

* Eseguire la prima CI e correggere ciò che trova, collegare i percorsi del monitor e poi aggiungere il servizio push Firebase quando esisterà il progetto Google.

## 🌍 Lingue

Il README e i testi esistono in inglese, spagnolo, francese, italiano, tedesco, cinese, giapponese e portoghese europeo.

## 🔗 Progetti correlati

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): il monitor di cui questa app è client
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): le convenzioni e i controlli condivisi

## 📚 Documentazione

* [Le convenzioni della suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Registro delle modifiche](CHANGELOG.md)

## 🤝 Contribuire e comunità

* Prima di una pull request esegui `python -I tools/ci_validate.py` e `python -I tools/check_app.py`; la build Gradle, i test e il lint girano nella CI. Vedi [CONTRIBUTING.md](CONTRIBUTING.md) e il [codice di condotta](CODE_OF_CONDUCT.md).
* Il README esiste in otto lingue: una modifica al testo si fa in tutti e otto i file, e le due immagini sono disegnate da `docs/architecture.json` e `docs/structure.json` con `tools/make_architecture.py` e `tools/make_structure.py`.
* Segnala una vulnerabilità in privato: [SECURITY.md](SECURITY.md). Domande, idee e segnalazioni: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTORE

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENZA

Software: GPL-3.0-or-later. Vedi [LICENSE](LICENSE). I progetti hardware della suite usano la CERN OHL-S.

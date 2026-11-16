<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | 🇩🇪 <b>Deutsch</b> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Native Android-App für den GitHub Actions Monitor: Übersicht, Warnungen, Repositorys und Vorschläge auf dem Telefon, mit Push-Benachrichtigungen nur für kritische Fälle, standardmäßig aus.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-77-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Ehrlichkeitscheck - was heute tatsächlich läuft:** Die Regeln und der API-Client sind mit Unit-Tests geschrieben, die im CI auf der JVM laufen; die App wurde noch nicht zu einer APK gebaut, nicht auf einem Telefon gezeigt und hat nie mit einem echten Monitor gesprochen. Push-Benachrichtigungen sind standardmäßig aus und kein Push-Dienst ist verbunden.

---

> **Stand von Sicherheit und IT-Sicherheit:** Diese App liest einen Monitor und kann Aktionsvorschläge genehmigen oder ablehnen, die Workflows wiederholen oder auf GitHub kommentieren können. Eine Genehmigung ist an den genau gezeigten Inhalt gebunden, eine andere Person muss genehmigen (der Monitor erzwingt das) und ein Link genehmigt nie von selbst. Sie ist kein Sicherheitssystem (Safety) für eine Maschine.

## 📋 Auf einen Blick

| | |
|---|---|
| Version | `0.0.1` |
| Reifegrad | Gerüst |
| Tests | 77 |
| Technologie-Stack | Kotlin, Jetpack Compose (Android 8.0+) |
| Auf echter Hardware | Noch nicht auf einem Telefon ausgeführt |

**Springe zu:** [Überblick](#-überblick) · [Architektur](#-architektur) · [Schnellstart](#-schnellstart) · [Tests](#-tests) · [Struktur des Repositorys](#-struktur-des-repositorys) · [Verwandte Projekte](#-verwandte-projekte)

## 🚦 Projektstatus

Jede Zeile ist ein Teil dieses Repositorys, und die Ampel sagt, wie weit er heute ist. Sie beruht auf dem, was das Repository zeigen kann (Tests, Builds, Läufe), nicht auf dem, was geplant ist.

🟢 Fertig und überprüft · 🟡 Funktioniert, mit bekannten Grenzen · 🟠 Teilweise fertig oder noch nicht überprüft · 🔴 Nicht gemacht oder funktioniert nicht

| Bereich | Stand | Was das hier bedeutet |
|---|---|---|
| Repository-Prüfungen | 🟡 | Die Repository-Prüfung, die Bildprüfung, die leichten Prüfungen der App (acht Sprachen, Ressourcen, Geheimnisse) und ihre Tests laufen im CI. |
| Regeln und API-Client | 🟠 | 69 JVM-Unit-Tests sind geschrieben (Genehmigung, Links, Push-Filter, Einstellungen, der Client gegen ein simuliertes Netz); der Gradle-Build, der sie ausführt, ist noch nicht gelaufen. |
| Bildschirme und Keystore | 🔴 | Geschrieben, aber nie gebaut, nie auf einem Bildschirm gezeigt und nie auf einem Telefon ausgeführt. |
| Push-Benachrichtigungen | 🟠 | Nur kritisch und standardmäßig aus; die Firebase-Seite ist eine Null-Implementierung, bis es ein Google-Projekt gibt. |
| Routen des Monitors | 🟠 | Die Lese-Routen folgen dem API-v1-Vertrag des Monitors; die Vorschlags- und Geräte-Routen, die die App aufruft, sind noch nicht im Monitor. |

## 🎯 Überblick

* **Der Monitor in der Tasche:** Übersicht, Warnungen, Repositorys und Vorschläge eines GitHub Actions Monitors, gelesen mit einem Bearer-Token über seine API v1.
* **Entscheidungen im Vier-Augen-Prinzip:** Einen Vorschlag genehmigen oder ablehnen, nachdem man seinen genauen Inhalt gesehen hat; die Genehmigung ist an den Hash dieses Inhalts gebunden und der Monitor lehnt sie ab, wenn dieselbe Person den Vorschlag erstellt hat.
* **Standardmäßig still:** Push-Benachrichtigungen sind aus, nur kritisch, einmal pro Warnung, mit kurzem Titel und sonst nichts.
* **Nichts Geheimes im Repository:** Der Token wird mit einem Schlüssel des Android Keystore versiegelt; hier liegen keine Firebase-Datei, kein Schlüssel und kein Keystore.

## 🧩 Rolle in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID ist die Android-App des GitHub Actions Monitors (TOOLS-GITHUB-ACTIONS-MONITOR). Sie ist ein Client: Sie berechnet keine eigenen Regeln zu Warnungen oder Vorschlägen; der Monitor entscheidet, was kritisch ist und was ein Vorschlag ist, und die App zeigt es an und fragt eine Person.

## 🏛️ Architektur

<p align="center">
  <img src="images/architecture.svg" alt="Architektur von TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: wer es nutzt, was es enthält und wovon es abhängt" width="100%">
</p>

Wie die Teile dieses Repositorys zusammenpassen: wer es nutzt, was es enthält und wovon es abhängt. Das Bild wird aus `docs/architecture.json` gezeichnet und gegen den Code geprüft.

## ✨ Funktionen

* Fünf Bildschirme: Übersicht, Warnungen (Filter nach Stufe, Blättern mit Cursorn), Repositorys (die mit Warnungen und eine Suche für jedes Repository), Vorschläge (ausstehend oder alle, mit Details) und Einstellungen.
* Einen Vorschlag nach einer Bestätigung genehmigen oder ablehnen, die den Inhalts-Hash nennt; hast du in den Einstellungen deinen Namen angegeben, wird dir ein selbst erstellter Vorschlag nicht zur Genehmigung angeboten.
* Links `ghamon://proposal/<id>?hash=<hex>` öffnen einen Vorschlag; hat sich sein Inhalt seit dem Erstellen des Links geändert, wird keine Genehmigung angeboten.
* Geräteregistrierung für Push-Benachrichtigungen nur bei kritischen Fällen, standardmäßig aus.
* Acht Sprachen, die der Sprache des Telefons folgen: Englisch, Spanisch, Deutsch, Französisch, Italienisch, Japanisch, Chinesisch und europäisches Portugiesisch.

## 🔒 Sicherheit und Schutz

* Der Token wird mit AES-GCM unter einem Schlüssel versiegelt, der im Android Keystore liegt, wird nie in ein Log oder einen Fehlertext geschrieben und nur an die eingegebene Adresse gesendet; Weiterleitungen werden nie verfolgt.
* Unverschlüsseltes `http://` wird nur für localhost, `.local`-Namen und private IPv4-Adressen akzeptiert; jeder andere Host braucht `https://`, geprüft nur gegen die Zertifizierungsstellen des Systems.
* Eine Genehmigung trägt den Hash des Inhalts, der auf dem Bildschirm stand; ein Link öffnet nur einen Vorschlag und genehmigt oder führt nie etwas aus.
* Push-Benachrichtigungen sind standardmäßig aus, nur kritisch und tragen einen kurzen, bereinigten Titel: keine Logs, keine Repository-Geheimnisse.
* Die App-Daten sind von Sicherungen ausgenommen, und `python -I tools/check_app.py` schlägt fehl, wenn eine Firebase-Datei, ein Keystore oder ein Schlüssel im Baum liegt.

## 🔌 Schnittstellen

* Lesezugriffe auf den Monitor: `GET /api/v1/health`, `/status`, `/accounts`, `/accounts/{id}/status`, `/alerts` (Cursor-Paging, Filter) und `/repos/{owner}/{repo}/health`, mit `Authorization: Bearer` und `X-API-Version: 1`.
* Vorschläge (Routen, die die App annimmt, alle in der Konfigurationsdatei): `GET /api/v1/actions/proposals`, `GET .../{id}`, `POST .../{id}/approve` mit `expected_hash` und `POST .../{id}/reject`.
* Geräte: `POST /api/v1/devices` und `DELETE /api/v1/devices/{id}`.
* Link: `ghamon://proposal/<id>?hash=<64 Hexadezimalzeichen>`.

## ⚙️ Konfiguration

Jeder einstellbare Wert steht in `app/src/main/assets/app_config.json`: die Pfade, die Zeitlimits, die Seitengröße, die Push-Vorgaben und das Link-Schema. Die Adresse des Monitors, sein Token und dein Name werden in den Einstellungen der App eingegeben und auf dem Telefon gespeichert. Nichts Einstellbares steht im Code.

## 🚀 Schnellstart

Zum Bauen brauchst du JDK 21 und das Android SDK (Android Studio bringt beides mit); die leichten Prüfungen brauchen nur Python 3.10 oder neuer. Öffne die App, gehe zu Einstellungen, gib die Adresse deines Monitors und seinen Token ein und speichere.

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Tests

69 Unit-Tests laufen auf der JVM (`./gradlew testDebugUnitTest`) ohne Telefon: der API-Client gegen ein simuliertes Netz, die Regeln, die Links, der Push-Filter und die Einstellungen. 8 Python-Tests decken das Repository und die leichten Prüfungen der App ab. Das CI führt alle aus; nichts davon wurde auf einem Telefon ausgeführt.

## 🏗️ Build und Veröffentlichung

Die Version steht in `VERSION`, in `electro-hobby-3d.project.json` und in `app/build.gradle.kts` (der Build bricht ab, wenn sie sich unterscheiden), und jede Version hat ihren Eintrag im [CHANGELOG.md](CHANGELOG.md). Der Release-Build wird mit R8 verkleinert und danach mit dem eigenen Schlüssel des Maintainers signiert, der außerhalb des Repositorys liegt. Es ist noch keine APK veröffentlicht.

## 📂 Struktur des Repositorys

<p align="center">
  <img src="images/repository-structure.svg" alt="Struktur des Repositorys TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Textversion des Baums</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                der Build und die Verkleinerungsregeln
│   ├── src/main/AndroidManifest.xml                       Berechtigungen, Aktivität, Link ghamon://
│   ├── src/main/assets/app_config.json                    Pfade, Zeitlimits, Seitengröße, Push-Vorgaben
│   ├── src/main/res/                                      die Texte in acht Sprachen, Symbole und Themes
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   auf der JVM getestete Regeln
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        Bildschirme, View-Model, Keystore, Push
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   die Unit-Tests auf der JVM
├── tools/
│   ├── ci_validate.py                                     Manifest, Versionen, README-Parität
│   ├── check_app.py                                       Sprachen, Ressourcen, Konfiguration und Geheimnisse der App
│   └── make_architecture.py make_structure.py             zeichnen die beiden Bilder des README
├── docs/                                                  die Quellen der beiden Bilder
├── gradle/                                                die Dateien des Gradle-Wrappers
├── gradlew gradlew.bat                                    der Gradle-Wrapper
├── build.gradle.kts settings.gradle.kts                   die Plugins und der Name des Builds
├── gradle.properties                                      der Speicher des Builds
├── images/                                                Banner und die beiden Bilder
├── .github/                                               CI-Workflows und Vorlagen
├── VERSION                                                die Version des Repositorys
├── CHANGELOG.md                                           was sich in jeder Version geändert hat
├── SECURITY.md SUPPORT.md                                 Schwachstellen melden und Fragen stellen
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             Mitwirken, Verhaltenskodex und Lizenz
```

</details>

## 🗺️ Roadmap und bekannte Grenzen

**Bekannte Grenzen heute**

* Die App wurde nicht zu einer APK gebaut, nicht auf einem Telefon ausgeführt und nicht gegen einen echten Monitor erprobt.
* Die Vorschlags- und Geräte-Routen, die sie aufruft, sind noch nicht im Monitor.
* Es gibt keinen Push-Dienst: Er braucht ein Google-Projekt und einen Schlüssel, die nur der Maintainer erstellen kann.
* Noch keine biometrische Bestätigung vor einer Genehmigung.

**Als Nächstes**

* Das erste CI ausführen und beheben, was es findet, die Routen des Monitors verbinden und dann den Firebase-Push-Dienst ergänzen, wenn das Google-Projekt existiert.

## 🌍 Sprachen

README und Texte gibt es auf Englisch, Spanisch, Französisch, Italienisch, Deutsch, Chinesisch, Japanisch und europäischem Portugiesisch.

## 🔗 Verwandte Projekte

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): der Monitor, dessen Client diese App ist
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): die Konventionen und die gemeinsamen Prüfungen

## 📚 Dokumentation

* [Die Konventionen der Suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Änderungsprotokoll](CHANGELOG.md)

## 🤝 Mitwirken und Community

* Führe vor einer Pull Request `python -I tools/ci_validate.py` und `python -I tools/check_app.py` aus; der Gradle-Build, die Tests und das Lint laufen im CI. Siehe [CONTRIBUTING.md](CONTRIBUTING.md) und den [Verhaltenskodex](CODE_OF_CONDUCT.md).
* Das README gibt es in acht Sprachen: Eine Textänderung wird in allen acht Dateien gemacht, und die beiden Bilder werden aus `docs/architecture.json` und `docs/structure.json` mit `tools/make_architecture.py` und `tools/make_structure.py` gezeichnet.
* Melde eine Schwachstelle privat: [SECURITY.md](SECURITY.md). Fragen, Ideen und Meldungen: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LIZENZ

Software: GPL-3.0-or-later. Siehe [LICENSE](LICENSE). Die Hardware-Entwürfe der Suite verwenden die CERN OHL-S.

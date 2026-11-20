<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | 🇩🇪 <b>Deutsch</b> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Geplante Android-App für GitHub Actions Monitor: der Zustand deiner Workflows und die Warnungen deines eigenen Monitors auf dem Smartphone. Nur ein Gerüst: noch nichts ist umgesetzt.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose%20(planned)-blue.svg" alt="Kotlin and Jetpack Compose (planned)">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-none%20yet-lightgrey.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Ehrlichkeitsprüfung - was heute tatsächlich läuft:** nichts von der App. Dieses Repository enthält nur ihre Grundlage: die Dokumente, das Manifest, die Lizenz und die gemeinsamen Repository-Prüfungen. Es gibt keinen Code, kein APK, keinen Bildschirm, keinen Benachrichtigungsdienst und keinen Test der App, und nichts wurde auf einem Smartphone ausprobiert.

---

## 📋 Auf einen Blick

| | |
|---|---|
| Version | `0.0.1` |
| Reifegrad | Gerüst |
| Tests | Noch keine (nur die Prüfungen des Repositorys) |
| Technologie-Stack | Kotlin und Jetpack Compose (geplant) |
| Auf echter Hardware | Nicht auf einem Smartphone ausgeführt |

**Springe zu:** [Überblick](#-überblick) · [Architektur](#-architektur) · [Schnellstart](#-schnellstart) · [Tests](#-tests) · [Struktur des Repositorys](#-struktur-des-repositorys) · [Verwandte Projekte](#-verwandte-projekte)

## 🚦 Projektstatus

Jede Zeile ist ein Teil dieses Repositorys, und die Ampel sagt, wie weit er heute ist. Sie beruht auf dem, was das Repository zeigen kann (Tests, Builds, Läufe), nicht auf dem, was geplant ist.

🟢 Fertig und überprüft · 🟡 Funktioniert, mit bekannten Grenzen · 🟠 Teilweise fertig oder noch nicht überprüft · 🔴 Nicht gemacht oder funktioniert nicht

| Bereich | Stand | Was das hier bedeutet |
|---|---|---|
| Grundlage des Repositorys | 🟡 | Das Manifest, die README-Dateien in acht Sprachen, die Lizenz und die gemeinsamen Repository-Prüfungen laufen in der CI. |
| Code der App | 🔴 | Nicht begonnen: in diesem Repository gibt es noch kein Android-Projekt. |
| Tests | 🔴 | Die App hat keine Tests; es laufen nur die Prüfungen des Repositorys. |
| Verbindung zum Monitor | 🔴 | Nicht begonnen. Der Monitor bietet eine Lese-API, die ein Client nutzen kann; diese App nutzt sie noch nicht. |
| Smartphone und Benachrichtigungen | 🔴 | Nicht begonnen und nie auf einem Smartphone ausgeführt; kein Benachrichtigungsdienst ist angebunden. |

## 🎯 Überblick

* **Was es sein wird:** eine Android-App, die auf deinem Smartphone den Zustand der Workflows und die Warnungen deines eigenen GitHub Actions Monitors zeigt.
* **Nur Lesen:** sie liest nur aus deinem Monitor. Sie schreibt nicht in GitHub und ändert nichts im Monitor.
* **Dein Monitor, deine Daten:** sie spricht mit dem Monitor, den du selbst betreibst, mit einem Zugriffstoken, das du ihr gibst; es liegt kein Dienst von uns dazwischen.
* **Was es (noch) nicht ist:** es gibt keine App zum Installieren, kein Release und kein Datum. Dieses Repository reserviert nur ihren Platz und sagt ehrlich, was geplant ist.

## 🧩 Rolle in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID soll der Android-Client von GitHub Actions Monitor werden ([TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)). Sie würde zeigen, was der Monitor schon weiß, und selbst nichts entscheiden. Eine minimale Smartwatch-Version, [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH), soll dieser App folgen.

## 🏛️ Architektur

<p align="center">
  <img src="images/architecture.svg" alt="Architektur von TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: wer es nutzt, was es enthält und wovon es abhängt" width="100%">
</p>

Wie die Teile dieses Repositorys zusammenpassen: wer es nutzt, was es enthält und wovon es abhängt. Das Bild wird aus `docs/architecture.json` gezeichnet und gegen den Code geprüft.

## ✨ Funktionen

**Geplant, und nichts davon existiert bisher:**
* Die Zusammenfassung deiner Workflows (was rot ist, hängt oder wartet) und die Warnungen des Monitors.
* Eine Liste der Repositorys mit ihrem Zustand.
* Optionale Benachrichtigungen bei wichtigen Warnungen, standardmäßig aus.
* Texte in acht Sprachen.

**Nicht geplant:** in GitHub zu schreiben, Workflows vom Smartphone aus neu zu starten oder deine Daten an einen anderen Dienst als deinen eigenen Monitor zu senden.

## 🔒 Sicherheit und Schutz

* Beabsichtigt: die App liest nur, und das Zugriffstoken bleibt auf dem Smartphone und wird nur an die Adresse deines Monitors gesendet.
* Nichts Geheimes gehört in dieses Repository: kein Schlüssel, kein Token, keine Datei eines Benachrichtigungsdienstes und kein Signaturschlüssel.
* Das sind Entwurfsabsichten. Nichts davon existiert bisher, daher konnte es niemand prüfen.

## 🚀 Schnellstart

Es gibt noch nichts zu installieren oder auszuführen. Um die Grundlage dieses Repositorys zu prüfen, brauchst du Python 3.10 oder neuer:

```bash
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Tests

Die CI führt die Prüfungen des Repositorys und die Tests dieser Prüfungen aus. Die App hat keine eigenen Tests, weil sie noch keinen Code hat, und hier wird keine Testzahl behauptet.

## 🏗️ Build und Veröffentlichung

Es gibt weder Build noch Release, weil keine Version der App existiert. Die Version `0.0.1` in `VERSION`, im Manifest und im Changelog ist die der Grundlage des Repositorys.

## 📂 Struktur des Repositorys

<p align="center">
  <img src="images/repository-structure.svg" alt="Struktur des Repositorys TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Textversion des Baums</summary>

```text
MONITOR-ANDROID/
├── tools/
│   ├── ci_validate.py                           Manifest, Versionen, README-Parität
│   └── make_architecture.py make_structure.py   zeichnen die beiden Bilder des README
├── docs/                                        die Quellen der beiden Bilder
├── images/                                      Banner und die beiden Bilder
├── .github/                                     CI-Workflows und Vorlagen
├── VERSION                                      die Version des Repositorys
├── electro-hobby-3d.project.json                das Manifest des Projekts
├── CHANGELOG.md                                 was sich in jeder Version geändert hat
├── SECURITY.md SUPPORT.md                       Schwachstellen melden und Fragen stellen
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE   Mitwirken, Verhaltenskodex und Lizenz
```

</details>

## 🗺️ Roadmap und bekannte Grenzen

**Bekannte Grenzen heute**

* Es gibt keinen Code: keine App, kein APK, keinen Bildschirm.
* Nichts wurde auf einem Smartphone oder mit einem echten Monitor ausprobiert.
* Der endgültige Umfang hängt von der Lese-API des Monitors ab und kann sich ändern.

**Als Nächstes**

* Die ersten Bildschirme festlegen, die App Schritt für Schritt mit ihren Tests in der CI bauen und sie erst dann hier als funktionierend beschreiben.

## 🌍 Sprachen

README und Texte gibt es auf Englisch, Spanisch, Französisch, Italienisch, Deutsch, Chinesisch, Japanisch und europäischem Portugiesisch.

## 🔗 Verwandte Projekte

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): der Monitor, den diese App lesen soll
* [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH): die geplante minimale Smartwatch-Version, die dieser App folgt
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): die Konventionen und die gemeinsamen Prüfungen

## 📚 Dokumentation

* [Die Konventionen der Suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Änderungsprotokoll](CHANGELOG.md)

## 🤝 Mitwirken und Community

* Führe vor einem Pull Request `python -I tools/ci_validate.py` aus; die übrigen Prüfungen laufen in der CI. Siehe [CONTRIBUTING.md](CONTRIBUTING.md) und den [Verhaltenskodex](CODE_OF_CONDUCT.md).
* Das README gibt es in acht Sprachen: Eine Textänderung wird in allen acht Dateien vorgenommen, und die beiden Bilder werden aus `docs/architecture.json` und `docs/structure.json` mit `tools/make_architecture.py` und `tools/make_structure.py` gezeichnet.
* Melde eine Sicherheitslücke privat: [SECURITY.md](SECURITY.md). Fragen, Ideen und Meldungen: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LIZENZ

Software: GPL-3.0-or-later. Siehe [LICENSE](LICENSE). Die Hardware-Entwürfe der Suite verwenden die CERN OHL-S.

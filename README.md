<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center">🇺🇸 <b>English</b> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Native Android client of GitHub Actions Monitor: summary, alerts, repositories and proposals on the phone, with critical-only push notifications that are off by default.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-77-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Honesty check - what actually runs today:** the rules and the API client are written with unit tests that run on the JVM in CI; the app has not been built into an APK yet, has not been shown on a phone and has never talked to a real monitor. Push notifications are off by default and no push service is connected.

---

> **Safety and security status:** this app reads a monitor and can approve or reject action proposals that may re-run workflows or comment on GitHub. An approval is bound to the exact content shown, a different person must approve (the monitor enforces it) and a link never approves anything by itself. It is not a safety system for any machine.

## 📋 At a glance

| | |
|---|---|
| Version | `0.0.1` |
| Maturity | Scaffolding |
| Tests | 77 |
| Stack | Kotlin, Jetpack Compose (Android 8.0+) |
| On real hardware | Not run on a phone yet |

**Jump to:** [Overview](#-overview) · [Architecture](#-architecture) · [Quick start](#-quick-start) · [Testing](#-testing) · [Repository Structure](#-repository-structure) · [Related Projects](#-related-projects)

## 🚦 Project status

Each line is one part of this repository and the light says how far it is today. It is written from what the repository can show (tests, builds, runs), not from what is planned.

🟢 Done and verified · 🟡 Works, with known limits · 🟠 Partly done or not verified yet · 🔴 Not done or not working

| Area | State | What it means here |
|---|---|---|
| Repository checks | 🟡 | The repository check, the check of the pictures, the light checks of the app (eight languages, resources, secrets) and their tests run in CI. |
| Rules and API client | 🟠 | 69 JVM unit tests are written (approval, links, push gate, settings, the client against a fake network); the Gradle build that runs them has not run yet. |
| Screens and Keystore | 🔴 | Written, but never built, never shown on a screen and never run on a phone. |
| Push notifications | 🟠 | Critical-only and off by default; the Firebase side is a null implementation until a Google project exists. |
| Monitor routes | 🟠 | The read routes follow the API v1 contract of the monitor; the proposal and device routes the app calls are not in the monitor yet. |

## 🎯 Overview

* **The monitor in your pocket:** the summary, the alerts, the repositories and the proposals of a GitHub Actions Monitor, read with a bearer token over its API v1.
* **Decisions with four eyes:** approve or reject a proposal after seeing its exact content; the approval is bound to the hash of that content and the monitor refuses it when the same person created the proposal.
* **Quiet by default:** push notifications are off, critical-only, once per alert, with a short title and nothing else.
* **Nothing secret in the repository:** the token is sealed with a key of the Android Keystore; there is no Firebase file, key or keystore here.

## 🧩 Role in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID is the Android app of GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). It is a client: it computes no rules of its own about alerts or proposals; the monitor decides what is critical and what a proposal is, and the app shows it and asks a person.

## 🏛️ Architecture

<p align="center">
  <img src="images/architecture.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID architecture: who uses it, what it holds and what it depends on" width="100%">
</p>

How the parts of this repository fit together: who uses it, what it holds and what it depends on. The picture is drawn from `docs/architecture.json` and checked against the code.

## ✨ Features

* Five screens: summary, alerts (filter by level, paging with cursors), repositories (the ones with alerts and a look-up of any repository), proposals (pending or all, with details) and settings.
* Approve or reject a proposal after a confirmation that names the content hash; a proposal you created is not offered for approval when you gave your name in the settings.
* Links `ghamon://proposal/<id>?hash=<hex>` open one proposal; if its content changed since the link was made, approval is not offered.
* Device registration for critical-only push notifications, off by default.
* Eight languages that follow the language of the phone: English, Spanish, German, French, Italian, Japanese, Chinese and European Portuguese.

## 🔒 Security & Safety

* The token is sealed with AES-GCM under a key that lives in the Android Keystore, is never written to a log or an error text, and is sent only to the address that was typed; redirects are never followed.
* Plain `http://` is accepted only for localhost, `.local` names and private IPv4 addresses; any other host needs `https://`, checked against the system authorities only.
* An approval carries the hash of the content that was on the screen; a link only opens a proposal and never approves or executes anything.
* Push notifications are off by default, critical-only and carry a short cleaned title: no logs, no repository secrets.
* App data is excluded from backups, and `python -I tools/check_app.py` fails when a Firebase file, a keystore or a key is in the tree.

## 🔌 Interfaces

* Reads of the monitor: `GET /api/v1/health`, `/status`, `/accounts`, `/accounts/{id}/status`, `/alerts` (cursor paging, filters) and `/repos/{owner}/{repo}/health`, with `Authorization: Bearer` and `X-API-Version: 1`.
* Proposals (routes the app assumes, all in the configuration file): `GET /api/v1/actions/proposals`, `GET .../{id}`, `POST .../{id}/approve` with `expected_hash` and `POST .../{id}/reject`.
* Devices: `POST /api/v1/devices` and `DELETE /api/v1/devices/{id}`.
* Link: `ghamon://proposal/<id>?hash=<64 hex characters>`.

## ⚙️ Configuration

Every tuned value is in `app/src/main/assets/app_config.json`: the paths, the timeouts, the page size, the push defaults and the link scheme. The address of the monitor, its token and your name are typed in the settings of the app and kept on the phone. Nothing tuned is in the code.

## 🚀 Quick start

You need JDK 21 and the Android SDK (Android Studio brings both) to build; the light checks need only Python 3.10 or newer. Open the app, go to Settings, type the address of your monitor and its token, and save.

```bash
./gradlew testDebugUnitTest lintDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Testing

69 unit tests run on the JVM (`./gradlew testDebugUnitTest`) with no phone: the API client against a fake network, the rules, the links, the push gate and the settings. 8 Python tests cover the repository and the light checks of the app. The CI runs all of them; nothing here has been run on a phone.

## 🏗️ Build & Release

The version lives in `VERSION`, in `electro-hobby-3d.project.json` and in `app/build.gradle.kts` (the build stops when they differ), and each version has its entry in [CHANGELOG.md](CHANGELOG.md). The release build is shrunk with R8 and is signed afterwards with the maintainer's own key, kept outside the repository. No APK is published yet.

## 📂 Repository Structure

<p align="center">
  <img src="images/repository-structure.svg" alt="Repository structure of TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Text version of the tree</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                the build and the shrinking rules
│   ├── src/main/AndroidManifest.xml                       permissions, activity, ghamon:// link
│   ├── src/main/assets/app_config.json                    paths, timeouts, page size, push defaults
│   ├── src/main/res/                                      the texts in eight languages, icons and themes
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   rules tested on the JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        screens, view model, Keystore, push
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   the unit tests on the JVM
├── tools/
│   ├── ci_validate.py                                     manifest, versions, README parity
│   ├── check_app.py                                       languages, resources, config and secrets of the app
│   └── make_architecture.py make_structure.py             draw the two pictures of the README
├── docs/                                                  the sources of the two pictures
├── gradle/                                                the files of the Gradle wrapper
├── gradlew                                                the Gradle wrapper
├── gradlew.bat                                            the Gradle wrapper
├── build.gradle.kts                                       the plugins and the name of the build
├── settings.gradle.kts                                    the plugins and the name of the build
├── gradle.properties                                      the memory of the build
├── images/                                                banner and the two pictures
├── .github/                                               CI workflows and templates
├── VERSION                                                the version of the repository
├── electro-hobby-3d.project.json                          the manifest of the project
├── CHANGELOG.md                                           what changed in each version
├── SECURITY.md SUPPORT.md                                 how to report a vulnerability and where to ask
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             how to contribute, the code of conduct and the licence
```

</details>

## 🗺️ Roadmap & Known limits

**Known limits today**

* The app has not been built into an APK, run on a phone or tried against a real monitor.
* The proposal and device routes it calls are not in the monitor yet.
* There is no push service: it needs a Google project and a key that only the maintainer can create.
* No biometric confirmation before an approval yet.

**Next**

* Run the first CI and fix what it finds, connect the monitor routes, then add the Firebase push service when the Google project exists.

## 🌍 Languages

README and texts exist in English, Spanish, French, Italian, German, Chinese, Japanese and European Portuguese.

## 🔗 Related Projects

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): the monitor this app is a client of
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): the conventions and the shared checks

## 📚 Documentation

* [The conventions of the suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Changelog](CHANGELOG.md)

## 🤝 Contributing & Community

* Before a pull request run `python -I tools/ci_validate.py` and `python -I tools/check_app.py`; the Gradle build, the tests and the lint run in the CI. See [CONTRIBUTING.md](CONTRIBUTING.md) and the [code of conduct](CODE_OF_CONDUCT.md).
* The README exists in eight languages: a change to the text is made in all eight files, and the two pictures are drawn from `docs/architecture.json` and `docs/structure.json` by `tools/make_architecture.py` and `tools/make_structure.py`.
* Report a vulnerability privately: [SECURITY.md](SECURITY.md). Questions, ideas and reports: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTHOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENSE

Software: GPL-3.0-or-later. See [LICENSE](LICENSE). Hardware designs of the suite use CERN OHL-S.

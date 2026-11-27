<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center">🇺🇸 <b>English</b> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Native Android client of GitHub Actions Monitor: summary, alerts, repositories and the proposals of its AI on the phone, read only.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-75-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.1.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Preliminary: not tested on a real phone.** The client is checked against demo monitors (invented data) in GitHub Actions and the screens are pictured in an emulator, but the app has not been shown on a phone and has not talked to a monitor in daily use. It sends no push notifications: the monitor has no push service yet.

---

> **Safety and security status:** this app reads a monitor; apart from signing in and out it changes nothing, and proposals are approved or rejected only on the computer of the monitor. It is not a safety system for any machine.

## 📋 At a glance

| | |
|---|---|
| Version | `0.1.1` |
| Maturity | Scaffolding |
| Tests | 75 |
| Stack | Kotlin, Jetpack Compose (Android 8.0+) |
| On real hardware | Not tested on a real phone (preliminary) |

**Jump to:** [Overview](#-overview) · [Architecture](#-architecture) · [Quick start](#-quick-start) · [Testing](#-testing) · [Repository Structure](#-repository-structure) · [Related Projects](#-related-projects)

## 🚦 Project status

Each line is one part of this repository and the light says how far it is today. It is written from what the repository can show (tests, builds, runs), not from what is planned.

🟢 Done and verified · 🟡 Works, with known limits · 🟠 Partly done or not verified yet · 🔴 Not done or not working

| Area | State | What it means here |
|---|---|---|
| Repository checks | 🟡 | The repository check, the check of the pictures, the light checks of the app (eight languages, resources, secrets) and their tests run in CI. |
| Rules and monitor client | 🟡 | 64 JVM unit tests are written, among them the client against answers recorded from a demo monitor; the CI runs them with Gradle, checks how many ran, and runs the client against running demo monitors. |
| Screens and Keystore | 🟠 | Pictured in an emulator against a demo monitor; never run on a phone. |
| Push notifications | 🔴 | The monitor has no push service, so the app offers no switch and asks no permission. |
| Monitor routes | 🟡 | Checked against the demo monitor of the commit pinned in `.github/workflows/ci.yml`; not against a monitor in daily use. |

## 🎯 Overview

* **The monitor in your pocket:** the summary, the alerts, the repositories and the proposals of a GitHub Actions Monitor in network mode.
* **It asks the way the monitor asks:** the access token of the network mode, and the session of a person when the monitor has users.
* **Read only:** proposals show the exact command and its hash; approving happens on the computer of the monitor.
* **Nothing secret in the repository:** the tokens are sealed with a key of the Android Keystore; there is no Firebase file, key or keystore here.

## 🧩 Role in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID is the Android app of GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). It is a client: it computes no rules of its own about alerts or proposals; the monitor decides what is critical and what a proposal is, and the app shows it and asks a person.

## 🏛️ Architecture

<p align="center">
  <img src="images/architecture.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID architecture: who uses it, what it holds and what it depends on" width="100%">
</p>

How the parts of this repository fit together: who uses it, what it holds and what it depends on. The picture is drawn from `docs/architecture.json` and checked against the code.

## ✨ Features

* Five screens: summary, alerts (filter by level), repositories (ranked by risk, search by name), proposals (pending or all, with details) and settings.
* Sign in and out when the monitor works in team mode; each refusal of the monitor has its own message.
* Links `ghamon://proposal/<id>?hash=<hex>` open one proposal and warn when its content changed since the link was made.
* Eight languages that follow the language of the phone: English, Spanish, German, French, Italian, Japanese, Chinese and European Portuguese.

The pictures are of the app in an emulator, against a demo monitor with invented data:

<table><tr><td align="center"><img src="images/screenshots/sign-in.png" alt="Signing in" width="180"><br>Signing in</td><td align="center"><img src="images/screenshots/summary.png" alt="Summary" width="180"><br>Summary</td><td align="center"><img src="images/screenshots/alerts.png" alt="Alerts" width="180"><br>Alerts</td><td align="center"><img src="images/screenshots/repos.png" alt="Repositories" width="180"><br>Repositories</td><td align="center"><img src="images/screenshots/proposals.png" alt="Proposals" width="180"><br>Proposals</td></tr></table>

## 🔒 Security & Safety

* The access token and the session are sealed with AES-GCM under a key that lives in the Android Keystore, are never written to a log or an error text, and are sent only to the address that was typed; changing the address drops them, and redirects are never followed.
* The password is sent once to sign in and is not kept.
* Plain `http://` is accepted only for localhost, `.local` names and private IPv4 addresses; any other host needs `https://`, checked against the system authorities only.
* A link only opens a proposal; nothing is approved or executed from the phone.
* App data is excluded from backups, and `python -I tools/check_app.py` fails when a Firebase file, a keystore or a key is in the tree.

## 🔌 Interfaces

* Reads: `GET /api/auth/me`, `/api/health`, `/api/alerts`, `/api/team/health`, `/api/ai/actions` and `/api/accounts` (several accounts only).
* Signing in and out: `POST /api/auth/login` (`client: "api"`) and `POST /api/auth/logout`.
* Headers: `X-Monitor-Access` with the access token and, in team mode, `Authorization: Bearer` with the session.
* Link: `ghamon://proposal/<id>?hash=<64 hex characters>`.

## ⚙️ Configuration

Every tuned value is in `app/src/main/assets/app_config.json`: the paths, the timeouts, the size limit, the push rule and the link scheme. The address of the monitor, its access token and your user are typed in the settings of the app and kept on the phone.

The monitor needs `listen.network` on; a name (not an address) used to reach it goes in `listen.allowed_hosts`; set the access token in `MONITOR_ACCESS_TOKEN` so that it does not change at each start; with users (team mode) turn on `auth.allow_bearer` too.

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

64 unit tests run on the JVM (`./gradlew testDebugUnitTest`) with no phone: the client against answers recorded from a demo monitor, its refusals, the links, the settings and the push rule. Five of them talk to running demo monitors and are skipped unless the CI job "contract" starts those. 11 Python tests cover the repository and the light checks of the app. Nothing here has been run on a phone.

## 🏗️ Build & Release

The version lives in `VERSION`, in `electro-hobby-3d.project.json` and in `app/build.gradle.kts` (the build stops when they differ), and each version has its entry in [CHANGELOG.md](CHANGELOG.md). The release build is shrunk with R8 and is signed afterwards with the maintainer's own key, kept outside the repository. The first release is preliminary: it has not been tested on a real phone. GitHub Actions builds the APK (the workflow "APK build (manual)", started by hand, with no signing key in it) and the maintainer signs it with their own key and publishes it as a pre-release together with `SHA256SUMS`. **To install:** download `TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk` from the latest release on the Releases page, check it with `sha256sum -c SHA256SUMS`, allow installing from that source in the settings of the phone (Android 8.0 or later) and open the file, or run `adb install TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk`. **To update:** install the newer APK over the old one; the settings stay because the signature is the same and the version code is higher, and the token stays sealed in the Keystore. If Android refuses the update, the signature is not the same: remove the old app first (its token and settings are lost).

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
│   ├── src/main/assets/app_config.json                    paths, timeouts, size limit, push rule
│   ├── src/main/res/                                      the texts in eight languages, icons and themes
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   rules tested on the JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        screens, view model, theme, Keystore
│   ├── src/test/java/es/electrohobby3d/ghamonitor/core/   the unit tests on the JVM
│   ├── src/test/resources/contract/                       answers recorded from a demo monitor
│   └── src/androidTest/                                   the screenshots in an emulator
├── tools/
│   ├── ci_validate.py                                     manifest, versions, README parity
│   ├── check_app.py                                       languages, resources, config and secrets of the app
│   ├── make_architecture.py make_structure.py             draw the two pictures of the README
│   └── contract/                                          demo monitors and recording of answers
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

* Preliminary release: the app has not been run on a phone.
* The Lite edition of the monitor has no alerts, repositories or proposals, so the app shows only that.
* There is no push service: the monitor does not send push messages yet.
* Pairing a phone with the QR code of the monitor is not supported; the access token is typed.

**Next**

* Try it on a phone against a real monitor, then pairing by QR code, and push when the monitor offers it.

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

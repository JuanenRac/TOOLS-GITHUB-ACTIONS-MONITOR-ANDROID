<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center">🇺🇸 <b>English</b> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Planned Android app of GitHub Actions Monitor: the state of your workflows and the alerts of your own monitor on your phone. Scaffolding only: nothing is implemented yet.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose%20(planned)-blue.svg" alt="Kotlin and Jetpack Compose (planned)">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-none%20yet-lightgrey.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Honesty check - what actually runs today:** nothing of the app. This repository holds only its base: the documents, the manifest, the licence and the shared repository checks. There is no code, no APK, no screen, no notification service and no test of the app, and nothing has been tried on a phone.

---

## 📋 At a glance

| | |
|---|---|
| Version | `0.0.1` |
| Maturity | Scaffolding |
| Tests | None yet (only the checks of the repository) |
| Stack | Kotlin and Jetpack Compose (planned) |
| On real hardware | Not run on a phone |

**Jump to:** [Overview](#-overview) · [Architecture](#-architecture) · [Quick start](#-quick-start) · [Testing](#-testing) · [Repository Structure](#-repository-structure) · [Related Projects](#-related-projects)

## 🚦 Project status

Each line is one part of this repository and the light says how far it is today. It is written from what the repository can show (tests, builds, runs), not from what is planned.

🟢 Done and verified · 🟡 Works, with known limits · 🟠 Partly done or not verified yet · 🔴 Not done or not working

| Area | State | What it means here |
|---|---|---|
| Repository base | 🟡 | The manifest, the README files in eight languages, the licence and the shared repository checks run in CI. |
| App code | 🔴 | Not started: there is no Android project in this repository yet. |
| Tests | 🔴 | There are no tests of the app; only the checks of the repository run. |
| Connection to the monitor | 🔴 | Not started. The monitor offers a read API that a client can use; this app does not use it yet. |
| Phone and notifications | 🔴 | Not started and never run on a phone; no notification service is connected. |

## 🎯 Overview

* **What it will be:** an Android app that shows, on your phone, the state of the workflows and the alerts of your own GitHub Actions Monitor.
* **Read only:** it will only read from your monitor. It will not write to GitHub and it will not change anything in the monitor.
* **Your monitor, your data:** it will talk to the monitor that you run yourself, with an access token that you give it; there is no service of ours in between.
* **What it is not (yet):** there is no app to install, no release and no date. This repository only reserves its place and says honestly what is planned.

## 🧩 Role in EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID is meant to be the Android client of GitHub Actions Monitor ([TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)). It would show what the monitor already knows and decide nothing of its own. A minimal watch version, [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH), is planned to follow this app.

## 🏛️ Architecture

<p align="center">
  <img src="images/architecture.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID architecture: who uses it, what it holds and what it depends on" width="100%">
</p>

How the parts of this repository fit together: who uses it, what it holds and what it depends on. The picture is drawn from `docs/architecture.json` and checked against the code.

## ✨ Features

**Planned, and none of it exists yet:**
* The summary of your workflows (what is red, what is stuck, what is waiting) and the alerts of the monitor.
* A list of repositories with their state.
* Optional notifications for important alerts, off by default.
* Texts in eight languages.

**Not planned:** writing to GitHub, re-running workflows from the phone, or sending your data to any service other than your own monitor.

## 🔒 Security & Safety

* Intended: the app only reads, and the access token stays on the phone and is sent only to the address of your monitor.
* Nothing secret belongs in this repository: no key, no token, no push-service file and no signing key.
* These are design intentions. None of it exists yet, so nobody has been able to review it.

## 🚀 Quick start

There is nothing to install or run yet. To check the base of this repository you need Python 3.10 or newer:

```bash
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Testing

The CI runs the checks of the repository and the tests of those checks. The app has no tests of its own because it has no code yet, and no counter of tests is claimed here.

## 🏗️ Build & Release

There is no build and no release, because no version of the app exists. The version `0.0.1` in `VERSION`, in the manifest and in the changelog is that of the base of the repository.

## 📂 Repository Structure

<p align="center">
  <img src="images/repository-structure.svg" alt="Repository structure of TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Text version of the tree</summary>

```text
MONITOR-ANDROID/
├── tools/
│   ├── ci_validate.py                           manifest, versions, README parity
│   └── make_architecture.py make_structure.py   draw the two pictures of the README
├── docs/                                        the sources of the two pictures
├── images/                                      banner and the two pictures
├── .github/                                     CI workflows and templates
├── VERSION                                      the version of the repository
├── electro-hobby-3d.project.json                the manifest of the project
├── CHANGELOG.md                                 what changed in each version
├── SECURITY.md SUPPORT.md                       how to report a vulnerability and where to ask
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE   how to contribute, the code of conduct and the licence
```

</details>

## 🗺️ Roadmap & Known limits

**Known limits today**

* There is no code: no app, no APK, no screen.
* Nothing has been tried on a phone or against a real monitor.
* The final scope depends on the read API of the monitor and may change.

**Next**

* Decide the first screens, then build the app step by step with its tests in CI, and only then describe it here as working.

## 🌍 Languages

README and texts exist in English, Spanish, French, Italian, German, Chinese, Japanese and European Portuguese.

## 🔗 Related Projects

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): the monitor that this app is meant to read
* [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH): the planned minimal watch version that follows this app
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): the conventions and the shared checks

## 📚 Documentation

* [The conventions of the suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Changelog](CHANGELOG.md)

## 🤝 Contributing & Community

* Before a pull request run `python -I tools/ci_validate.py`; the other checks run in the CI. See [CONTRIBUTING.md](CONTRIBUTING.md) and the [code of conduct](CODE_OF_CONDUCT.md).
* The README exists in eight languages: a change to the text is made in all eight files, and the two pictures are drawn from `docs/architecture.json` and `docs/structure.json` by `tools/make_architecture.py` and `tools/make_structure.py`.
* Report a vulnerability privately: [SECURITY.md](SECURITY.md). Questions, ideas and reports: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTHOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENSE

Software: GPL-3.0-or-later. See [LICENSE](LICENSE). Hardware designs of the suite use CERN OHL-S.

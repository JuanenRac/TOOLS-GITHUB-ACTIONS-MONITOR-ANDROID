# Changelog

All notable changes to this project are documented here.

## [Unreleased]

## [0.1.1] - The app talks to the routes the monitor really serves

This release is preliminary: the app has not been tested on a real phone. It was checked against demo monitors in GitHub Actions and pictured in an emulator.

- **The screens read the real routes:** the summary asked for a status route the monitor does not have and failed whole, and the alerts, accounts and repositories were read with a shape the monitor never sends, so they stayed empty. The app now asks `/api/health`, `/api/alerts`, `/api/team/health`, `/api/ai/actions` and, for several accounts, `/api/accounts`, and each part of the summary fails by itself. An answer without the list it should have is an error, never an empty screen.
- **Signing in as the monitor asks:** the access token of the network mode travels in `X-Monitor-Access`; when the monitor also has users (team mode) the app asks for the user and password once, keeps only the session it gets back, sealed, and signs out on request. Each refusal has its own words: wrong token, address not allowed, locked out, sessions for apps switched off, no administrator yet. A change of address drops the stored token and session, so they are never sent to another host.
- **Proposals are read only:** the monitor accepts approval and rejection only on its own computer, so the approve and reject buttons are gone; the app shows the exact command, its hash, who asked and how long it has left. The links `ghamon://proposal/<id>?hash=<hex>` still open one proposal.
- **No push switch:** the monitor has no push service and no route to register a phone, so the switch and the notification permission are gone until it does.
- **The look and the pictures:** the colours of the monitor with the cards, headings, status lights and one icon per tab that the other apps of the author share, and the first screenshots, taken in an emulator against a demo monitor.
- **Tests against the real shapes:** the client is tested with answers recorded from the demo monitors, and the CI starts those monitors from a pinned commit, checks that the recorded answers still hold and runs the client and its network code against them.

## [0.1.0] - The first preliminary release: the Android client, a manual APK build and eight languages

This release is preliminary: the app has not been tested on a real phone and has never talked to a real monitor.

- **The Android client of GitHub Actions Monitor:** a Kotlin and Jetpack Compose project (Gradle wrapper, Android 8.0 and later) with five screens: summary, alerts, repositories, proposals and settings. The client of the read API v1 uses a bearer token, cursor paging, the error envelope turned into stable keys, no redirects and a limit on the size of an answer, and every path, timeout and page size is read from `app/src/main/assets/app_config.json`.
- **Proposals with four eyes and links:** a list with details, and approve or reject after a confirmation that names the content hash; the approval carries that hash, and a proposal the person created is not offered for approval when they gave their name. The links `ghamon://proposal/<id>?hash=<hex>` open one proposal and never approve anything; if the content changed since the link was made, approval is not offered.
- **Settings and push notifications:** the address of the monitor is checked (https, or http only on the home network), the token is sealed with an Android Keystore key and can be forgotten, and the device can be registered with the monitor. Push notifications are critical-only and off by default: a gate drops anything that is not critical, repeated or without an id, and the push service sits behind an interface whose implementation does nothing until a Google project exists; no Firebase file or key is in the repository.
- **A manual workflow builds the release APK:** `apk-build.yml` is started by hand (a dry run by default), compiles the release build twice from a commit that is already in main, compares the two results, checks that the APK carries the version of the manifest, that it has the expected application id and that it is unsigned, and uploads it with `SHA256SUMS`, a build manifest and a provenance attestation. No signing key exists in the workflow: the APK is signed afterwards on the maintainer's machine. With the dry run off, a draft pre-release waits for the approval of the owner.
- **Eight languages and installation notes:** every text of the app exists in English, Spanish, German, French, Italian, Japanese, Chinese and European Portuguese, with a check that the keys and placeholders are the same in all of them. The README files in the eight languages carry the notice that the release is preliminary and explain how to install and update the APK.
- **Tests and CI:** 69 unit tests on the JVM (the client, the rules, the links, the push gate and the settings), light checks of the languages, the resources, the configuration and the secrets, and tests of the repository and of the APK workflow; the CI runs the unit tests and lint with Gradle and checks that the number of tests that ran is the one stated.
- **Security of the workflows:** the dashboard notifier reads the repository, ref and commit from environment variables, so the step has nothing to inject into and passes the workflow security scan.

## [0.0.1] - The base of the repository

- **The base that every repository of the suite has:** the manifest, the README files in eight languages, this changelog, the CI workflow, the issue and pull-request templates, and the shared checks of EH3D-DOCS (the repository check and the drawers of the two README pictures).

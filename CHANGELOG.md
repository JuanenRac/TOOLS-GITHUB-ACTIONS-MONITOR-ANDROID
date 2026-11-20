# Changelog

All notable changes to this project are documented here.

## [Unreleased]

## [0.0.1] - The base of the repository

- **The base that every repository of the suite has:** the manifest, the README files in eight languages, this changelog, the CI workflow, the issue and pull-request templates, and the shared checks of EH3D-DOCS (the repository check and the drawers of the two README pictures). There is no application code yet.
- **The Android app of GitHub Actions Monitor:** a Kotlin and Jetpack Compose project (Gradle wrapper, Android 8.0 and later) with five screens: summary, alerts, repositories, proposals and settings.
- **Client of the read API v1:** bearer token, cursor paging, the error envelope turned into stable keys, no redirects, a limit on the size of an answer, and every path, timeout and page size read from `app/src/main/assets/app_config.json`.
- **Proposals with four eyes:** a list with details, and approve or reject after a confirmation that names the content hash; the approval carries that hash, and a proposal the person created is not offered for approval when they gave their name.
- **Links `ghamon://proposal/<id>?hash=<hex>`:** they open one proposal and never approve anything; if the content changed since the link was made, approval is not offered.
- **Settings:** the address of the monitor is checked (https, or http only on the home network), the token is sealed with an Android Keystore key and can be forgotten, and the device can be registered with the monitor.
- **Critical-only push notifications, off by default:** a gate that drops anything that is not critical, repeated or without an id, and a push service behind an interface whose implementation does nothing until a Google project exists; no Firebase file or key is in the repository.
- **Eight languages** (English, Spanish, German, French, Italian, Japanese, Chinese and European Portuguese) for every text of the app, with a check that the keys and placeholders are the same in all of them.
- **Tests and CI:** unit tests on the JVM for the client, the rules, the links, the push gate and the settings; light checks of the languages, the resources, the configuration and the secrets; a CI job that builds, tests and lints with Gradle.
- **README files in eight languages** with the Architecture and Repository Structure pictures, the manifest of the repository and the shared checks of the suite.

## [0.0.1] - The base of the repository

- **The base that every repository of the suite has:** the manifest, the README files in eight languages, this changelog, the CI workflow, the issue and pull-request templates, and the shared checks of EH3D-DOCS (the repository check and the drawers of the two README pictures).

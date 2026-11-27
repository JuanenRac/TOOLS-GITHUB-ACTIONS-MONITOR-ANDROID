<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | 🇫🇷 <b>Français</b> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Client Android natif de GitHub Actions Monitor : résumé, alertes, dépôts et propositions de son IA sur le téléphone, en lecture seule.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-75-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.1.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Préliminaire : non testée sur un vrai téléphone.** Le client est vérifié contre des moniteurs de démonstration (données inventées) dans GitHub Actions et les écrans sont photographiés dans un émulateur, mais l’application n’a pas été vue sur un téléphone et n’a pas parlé à un moniteur en usage quotidien. Elle n’envoie aucune notification push : le moniteur n’a pas encore de service push.

---

> **État de la sûreté et de la sécurité :** cette application lit un moniteur ; hormis la connexion et la déconnexion elle ne change rien, et les propositions ne sont approuvées ou refusées que sur l’ordinateur du moniteur. Ce n’est pas un système de sûreté d’une machine.

## 📋 En un coup d'œil

| | |
|---|---|
| Version | `0.1.1` |
| Maturité | Échafaudage |
| Tests | 75 |
| Pile technique | Kotlin, Jetpack Compose (Android 8.0+) |
| Sur du matériel réel | Non testée sur un vrai téléphone (préliminaire) |

**Aller à  :** [Présentation](#-présentation) · [Architecture](#-architecture) · [Démarrage rapide](#-démarrage-rapide) · [Tests](#-tests) · [Structure du dépôt](#-structure-du-dépôt) · [Projets liés](#-projets-liés)

## 🚦 État du projet

Chaque ligne est une partie de ce dépôt et le feu dit où elle en est aujourd'hui. Elle est écrite d'après ce que le dépôt peut montrer (tests, compilations, exécutions), pas d'après ce qui est prévu.

🟢 Fait et vérifié · 🟡 Fonctionne, avec des limites connues · 🟠 À moitié fait ou pas encore vérifié · 🔴 Pas fait ou ne fonctionne pas

| Domaine | État | Ce que cela veut dire ici |
|---|---|---|
| Contrôles du dépôt | 🟡 | Le contrôle du dépôt, celui des images, les contrôles légers de l'application (huit langues, ressources, secrets) et leurs tests s'exécutent dans la CI. |
| Règles et client du moniteur | 🟡 | 64 tests unitaires JVM sont écrits, dont le client contre des réponses enregistrées d’un moniteur de démonstration ; la CI les exécute avec Gradle, vérifie leur nombre et lance le client contre des moniteurs de démonstration en marche. |
| Écrans et Keystore | 🟠 | Photographiés dans un émulateur contre un moniteur de démonstration ; jamais exécutés sur un téléphone. |
| Notifications push | 🔴 | Le moniteur n’a pas de service push : l’application ne propose ni interrupteur ni demande d’autorisation. |
| Routes du moniteur | 🟡 | Vérifiées contre le moniteur de démonstration du commit fixé dans `.github/workflows/ci.yml` ; pas contre un moniteur en usage quotidien. |

## 🎯 Présentation

* **Le moniteur dans la poche :** le résumé, les alertes, les dépôts et les propositions d’un GitHub Actions Monitor en mode réseau.
* **Il s’identifie comme le moniteur le demande :** le jeton d’accès du mode réseau et, si le moniteur a des utilisateurs, la session d’une personne.
* **Lecture seule :** les propositions montrent la commande exacte et son hash ; l’approbation se fait sur l’ordinateur du moniteur.
* **Rien de secret dans le dépôt :** les jetons sont scellés avec une clé du Keystore Android ; il n’y a ici aucun fichier Firebase, clé ni keystore.

## 🧩 Rôle dans EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID est l'application Android de GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). C'est un client : elle ne calcule aucune règle propre sur les alertes ou les propositions ; le moniteur décide ce qui est critique et ce qu'est une proposition, et l'application l'affiche et interroge une personne.

## 🏛️ Architecture

<p align="center">
  <img src="images/architecture.svg" alt="Architecture de TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID : qui l'utilise, ce qu'il contient et de quoi il dépend" width="100%">
</p>

Comment les parties de ce dépôt s'articulent : qui l'utilise, ce qu'il contient et de quoi il dépend. L'image est dessinée à partir de `docs/architecture.json` et vérifiée par rapport au code.

## ✨ Fonctionnalités

* Cinq écrans : résumé, alertes (filtre par niveau), dépôts (classés par risque, recherche par nom), propositions (en attente ou toutes, avec détail) et réglages.
* Connexion et déconnexion quand le moniteur fonctionne en mode équipe ; chaque refus du moniteur a son propre message.
* Les liens `ghamon://proposal/<id>?hash=<hex>` ouvrent une proposition et préviennent si son contenu a changé depuis la création du lien.
* Huit langues qui suivent la langue du téléphone : anglais, espagnol, allemand, français, italien, japonais, chinois et portugais du Portugal.

Les captures montrent l’application dans un émulateur, face à un moniteur de démonstration aux données inventées :

<table><tr><td align="center"><img src="images/screenshots/sign-in.png" alt="Connexion" width="180"><br>Connexion</td><td align="center"><img src="images/screenshots/summary.png" alt="Résumé" width="180"><br>Résumé</td><td align="center"><img src="images/screenshots/alerts.png" alt="Alertes" width="180"><br>Alertes</td><td align="center"><img src="images/screenshots/repos.png" alt="Dépôts" width="180"><br>Dépôts</td><td align="center"><img src="images/screenshots/proposals.png" alt="Propositions" width="180"><br>Propositions</td></tr></table>

## 🔒 Sécurité et protection

* Le jeton d’accès et la session sont scellés en AES-GCM avec une clé du Keystore Android, ne sont jamais écrits dans un journal ni un message d’erreur et ne sont envoyés qu’à l’adresse saisie ; changer d’adresse les efface, et aucune redirection n’est suivie.
* Le mot de passe est envoyé une fois pour se connecter et n’est pas conservé.
* `http://` en clair n’est accepté que pour localhost, les noms `.local` et les adresses IPv4 privées ; tout autre hôte exige `https://`, vérifié uniquement avec les autorités du système.
* Un lien ouvre seulement une proposition ; rien n’est approuvé ni exécuté depuis le téléphone.
* Les données de l’application sont exclues des sauvegardes, et `python -I tools/check_app.py` échoue si un fichier Firebase, un keystore ou une clé se trouve dans l’arborescence.

## 🔌 Interfaces

* Lectures : `GET /api/auth/me`, `/api/health`, `/api/alerts`, `/api/team/health`, `/api/ai/actions` et `/api/accounts` (plusieurs comptes seulement).
* Connexion et déconnexion : `POST /api/auth/login` (`client: "api"`) et `POST /api/auth/logout`.
* En-têtes : `X-Monitor-Access` avec le jeton d’accès et, en mode équipe, `Authorization: Bearer` avec la session.
* Lien : `ghamon://proposal/<id>?hash=<64 caractères hexadécimaux>`.

## ⚙️ Configuration

Toutes les valeurs réglables sont dans `app/src/main/assets/app_config.json` : les chemins, les délais, la limite de taille, la règle push et le schéma du lien. L’adresse du moniteur, son jeton d’accès et votre utilisateur sont saisis dans les réglages et conservés sur le téléphone.

Le moniteur doit avoir `listen.network` activé ; un nom (et non une adresse) utilisé pour le joindre va dans `listen.allowed_hosts` ; placez le jeton d’accès dans `MONITOR_ACCESS_TOKEN` pour qu’il ne change pas à chaque démarrage ; avec des utilisateurs (mode équipe), activez aussi `auth.allow_bearer`.

## 🚀 Démarrage rapide

Pour compiler, il faut le JDK 21 et le SDK Android (Android Studio apporte les deux) ; les contrôles légers n'exigent que Python 3.10 ou plus récent. Ouvrez l'application, allez dans Réglages, saisissez l'adresse de votre moniteur et son jeton, puis enregistrez.

```bash
./gradlew testDebugUnitTest lintDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Tests

64 tests unitaires s’exécutent sur la JVM (`./gradlew testDebugUnitTest`) sans téléphone : le client contre des réponses enregistrées d’un moniteur de démonstration, ses refus, les liens, les réglages et la règle push. Cinq d’entre eux parlent à des moniteurs de démonstration en marche et sont ignorés sauf si la tâche CI "contract" les démarre. 11 tests Python couvrent le dépôt et les contrôles légers de l’application. Rien n’a été exécuté sur un téléphone.

## 🏗️ Compilation et publication

La version vit dans `VERSION`, dans `electro-hobby-3d.project.json` et dans `app/build.gradle.kts` (la compilation s'arrête si elles diffèrent), et chaque version a son entrée dans [CHANGELOG.md](CHANGELOG.md). La compilation de publication est réduite avec R8 puis signée avec la clé propre du mainteneur, conservée hors du dépôt. La première version est préliminaire : elle n'a pas été testée sur un vrai téléphone. GitHub Actions compile l'APK (le workflow "APK build (manual)", lancé à la main, sans clé de signature) et le mainteneur le signe avec sa propre clé et le publie comme préversion avec `SHA256SUMS`. **Pour installer :** téléchargez `TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk` depuis la dernière version de la page Releases, vérifiez-le avec `sha256sum -c SHA256SUMS`, autorisez l'installation depuis cette source dans les réglages du téléphone (Android 8.0 ou plus récent) et ouvrez le fichier, ou lancez `adb install TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk`. **Pour mettre à jour :** installez l'APK plus récent par-dessus l'ancien ; les réglages sont conservés car la signature est la même et le code de version est plus élevé, et le jeton reste scellé dans le Keystore. Si Android refuse la mise à jour, la signature n'est pas la même : supprimez d'abord l'ancienne application (son jeton et ses réglages sont perdus).

## 📂 Structure du dépôt

<p align="center">
  <img src="images/repository-structure.svg" alt="Structure du dépôt TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Version texte de l'arborescence</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                la compilation et les règles de réduction
│   ├── src/main/AndroidManifest.xml                       permissions, activité, lien ghamon://
│   ├── src/main/assets/app_config.json                    chemins, délais, taille maximale, règle push
│   ├── src/main/res/                                      les textes en huit langues, icônes et thèmes
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   règles testées sur la JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        écrans, view model, thème, Keystore
│   ├── src/test/java/es/electrohobby3d/ghamonitor/core/   les tests unitaires sur la JVM
│   ├── src/test/resources/contract/                       réponses enregistrées d'un moniteur de démo
│   └── src/androidTest/                                   les captures dans un émulateur
├── tools/
│   ├── ci_validate.py                                     manifeste, versions, parité des README
│   ├── check_app.py                                       langues, ressources, configuration et secrets de l'application
│   ├── make_architecture.py make_structure.py             dessinent les deux images du README
│   └── contract/                                          moniteurs de démo et enregistrement
├── docs/                                                  les sources des deux images
├── gradle/                                                les fichiers du wrapper Gradle
├── gradlew                                                le wrapper Gradle
├── gradlew.bat                                            le wrapper Gradle
├── build.gradle.kts                                       les plugins et le nom du build
├── settings.gradle.kts                                    les plugins et le nom du build
├── gradle.properties                                      la mémoire du build
├── images/                                                bannière et les deux images
├── .github/                                               workflows de CI et modèles
├── VERSION                                                la version du dépôt
├── electro-hobby-3d.project.json                          le manifeste du projet
├── CHANGELOG.md                                           ce qui a changé dans chaque version
├── SECURITY.md SUPPORT.md                                 signaler une vulnérabilité et poser des questions
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             contribuer, code de conduite et licence
```

</details>

## 🗺️ Feuille de route et limites connues

**Limites connues aujourd’hui**

* Version préliminaire : l’application n’a pas été exécutée sur un téléphone.
* L’édition Lite du moniteur n’a ni alertes, ni dépôts, ni propositions ; l’application ne peut que le dire.
* Aucun service push : le moniteur n’envoie pas encore de messages push.
* L’appairage par le code QR du moniteur n’est pas pris en charge ; le jeton d’accès se saisit.

**Ensuite**

* L’essayer sur un téléphone face à un vrai moniteur, puis l’appairage par code QR, et le push quand le moniteur le proposera.

## 🌍 Langues

Le README et les textes existent en anglais, espagnol, français, italien, allemand, chinois, japonais et portugais européen.

## 🔗 Projets liés

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR) : le moniteur dont cette application est cliente
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS) : les conventions et les contrôles partagés

## 📚 Documentation

* [Les conventions de la suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Journal des modifications](CHANGELOG.md)

## 🤝 Contribuer et communauté

* Avant une pull request, exécutez `python -I tools/ci_validate.py` et `python -I tools/check_app.py` ; la compilation Gradle, les tests et le lint s'exécutent dans la CI. Voir [CONTRIBUTING.md](CONTRIBUTING.md) et le [code de conduite](CODE_OF_CONDUCT.md).
* Le README existe en huit langues : une modification du texte se fait dans les huit fichiers, et les deux images sont dessinées à partir de `docs/architecture.json` et `docs/structure.json` par `tools/make_architecture.py` et `tools/make_structure.py`.
* Signalez une vulnérabilité en privé : [SECURITY.md](SECURITY.md). Questions, idées et rapports : electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTEUR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENCE

Logiciel : GPL-3.0-or-later. Voir [LICENSE](LICENSE). Les conceptions matérielles de la suite utilisent la CERN OHL-S.

<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | 🇫🇷 <b>Français</b> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Application Android prévue de GitHub Actions Monitor : l'état de vos workflows et les alertes de votre propre moniteur sur votre téléphone. Simple structure de départ : rien n'est encore implémenté.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose%20(planned)-blue.svg" alt="Kotlin and Jetpack Compose (planned)">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-none%20yet-lightgrey.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Vérification d'honnêteté - ce qui s'exécute aujourd'hui :** rien de l'application. Ce dépôt ne contient que sa base : les documents, le manifeste, la licence et les contrôles communs du dépôt. Il n'y a ni code, ni APK, ni écran, ni service de notifications, ni test de l'application, et rien n'a été essayé sur un téléphone.

---

## 📋 En un coup d'œil

| | |
|---|---|
| Version | `0.0.1` |
| Maturité | Échafaudage |
| Tests | Aucun pour l'instant (seulement les contrôles du dépôt) |
| Pile technique | Kotlin et Jetpack Compose (prévu) |
| Sur du matériel réel | Jamais exécuté sur un téléphone |

**Aller à  :** [Présentation](#-présentation) · [Architecture](#-architecture) · [Démarrage rapide](#-démarrage-rapide) · [Tests](#-tests) · [Structure du dépôt](#-structure-du-dépôt) · [Projets liés](#-projets-liés)

## 🚦 État du projet

Chaque ligne est une partie de ce dépôt et le feu dit où elle en est aujourd'hui. Elle est écrite d'après ce que le dépôt peut montrer (tests, compilations, exécutions), pas d'après ce qui est prévu.

🟢 Fait et vérifié · 🟡 Fonctionne, avec des limites connues · 🟠 À moitié fait ou pas encore vérifié · 🔴 Pas fait ou ne fonctionne pas

| Domaine | État | Ce que cela veut dire ici |
|---|---|---|
| Base du dépôt | 🟡 | Le manifeste, les README en huit langues, la licence et les contrôles communs du dépôt s'exécutent dans le CI. |
| Code de l'application | 🔴 | Pas commencé : ce dépôt ne contient pas encore de projet Android. |
| Tests | 🔴 | L'application n'a pas de tests ; seuls les contrôles du dépôt s'exécutent. |
| Connexion au moniteur | 🔴 | Pas commencée. Le moniteur offre une API de lecture qu'un client peut utiliser ; cette application ne l'utilise pas encore. |
| Téléphone et notifications | 🔴 | Pas commencé et jamais exécuté sur un téléphone ; aucun service de notifications n'est connecté. |

## 🎯 Présentation

* **Ce que ce sera :** une application Android qui affiche, sur votre téléphone, l'état des workflows et les alertes de votre propre GitHub Actions Monitor.
* **Lecture seule :** elle ne fera que lire depuis votre moniteur. Elle n'écrira pas dans GitHub et ne changera rien dans le moniteur.
* **Votre moniteur, vos données :** elle parlera au moniteur que vous exécutez vous-même, avec un jeton d'accès que vous lui donnez ; aucun service de notre part n'est placé entre les deux.
* **Ce que ce n'est pas (encore) :** il n'y a pas d'application à installer, ni de release, ni de date. Ce dépôt réserve seulement sa place et dit honnêtement ce qui est prévu.

## 🧩 Rôle dans EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID est destiné à être le client Android de GitHub Actions Monitor ([TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)). Il montrerait ce que le moniteur sait déjà et ne déciderait rien de lui-même. Une version minimale pour montre, [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH), est prévue pour suivre cette application.

## 🏛️ Architecture

<p align="center">
  <img src="images/architecture.svg" alt="Architecture de TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID : qui l'utilise, ce qu'il contient et de quoi il dépend" width="100%">
</p>

Comment les parties de ce dépôt s'articulent : qui l'utilise, ce qu'il contient et de quoi il dépend. L'image est dessinée à partir de `docs/architecture.json` et vérifiée par rapport au code.

## ✨ Fonctionnalités

**Prévu, et rien de cela n'existe encore :**
* Le résumé de vos workflows (ce qui est en rouge, bloqué ou en attente) et les alertes du moniteur.
* Une liste de dépôts avec leur état.
* Des notifications facultatives pour les alertes importantes, désactivées par défaut.
* Des textes en huit langues.

**Non prévu :** écrire dans GitHub, relancer des workflows depuis le téléphone ou envoyer vos données à un service autre que votre propre moniteur.

## 🔒 Sécurité et protection

* Prévu : l'application ne fait que lire, et le jeton d'accès reste sur le téléphone et n'est envoyé qu'à l'adresse de votre moniteur.
* Rien de secret n'a sa place dans ce dépôt : ni clé, ni jeton, ni fichier de service de notifications, ni clé de signature.
* Ce sont des intentions de conception. Rien de tout cela n'existe encore, donc personne n'a pu le relire.

## 🚀 Démarrage rapide

Il n'y a encore rien à installer ni à exécuter. Pour vérifier la base de ce dépôt, il faut Python 3.10 ou plus récent :

```bash
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Tests

Le CI exécute les contrôles du dépôt et les tests de ces contrôles. L'application n'a pas de tests propres car elle n'a pas encore de code, et aucun nombre de tests n'est annoncé ici.

## 🏗️ Compilation et publication

Il n'y a ni compilation ni release, car aucune version de l'application n'existe. La version `0.0.1` de `VERSION`, du manifeste et du journal des modifications est celle de la base du dépôt.

## 📂 Structure du dépôt

<p align="center">
  <img src="images/repository-structure.svg" alt="Structure du dépôt TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Version texte de l'arborescence</summary>

```text
MONITOR-ANDROID/
├── tools/
│   ├── ci_validate.py                           manifeste, versions, parité des README
│   └── make_architecture.py make_structure.py   dessinent les deux images du README
├── docs/                                        les sources des deux images
├── images/                                      bannière et les deux images
├── .github/                                     workflows de CI et modèles
├── VERSION                                      la version du dépôt
├── electro-hobby-3d.project.json                le manifeste du projet
├── CHANGELOG.md                                 ce qui a changé dans chaque version
├── SECURITY.md SUPPORT.md                       signaler une vulnérabilité et poser des questions
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE   contribuer, code de conduite et licence
```

</details>

## 🗺️ Feuille de route et limites connues

**Limites connues aujourd'hui**

* Il n'y a pas de code : ni application, ni APK, ni écran.
* Rien n'a été essayé sur un téléphone ni avec un vrai moniteur.
* Le périmètre final dépend de l'API de lecture du moniteur et peut changer.

**Ensuite**

* Décider les premiers écrans, construire l'application pas à pas avec ses tests dans le CI, et seulement ensuite la décrire ici comme fonctionnelle.

## 🌍 Langues

Le README et les textes existent en anglais, espagnol, français, italien, allemand, chinois, japonais et portugais européen.

## 🔗 Projets liés

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR) : le moniteur que cette application est destinée à lire
* [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH) : la version minimale pour montre prévue, qui suit cette application
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS) : les conventions et les contrôles communs

## 📚 Documentation

* [Les conventions de la suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Journal des modifications](CHANGELOG.md)

## 🤝 Contribuer et communauté

* Avant une pull request, exécutez `python -I tools/ci_validate.py` ; les autres contrôles s'exécutent dans le CI. Voir [CONTRIBUTING.md](CONTRIBUTING.md) et le [code de conduite](CODE_OF_CONDUCT.md).
* Le README existe en huit langues : une modification du texte se fait dans les huit fichiers, et les deux images sont dessinées à partir de `docs/architecture.json` et `docs/structure.json` par `tools/make_architecture.py` et `tools/make_structure.py`.
* Signalez une vulnérabilité en privé : [SECURITY.md](SECURITY.md). Questions, idées et signalements : electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTEUR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENCE

Logiciel : GPL-3.0-or-later. Voir [LICENSE](LICENSE). Les conceptions matérielles de la suite utilisent la CERN OHL-S.

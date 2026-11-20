<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | 🇯🇵 <b>日本語</b> | <a href="README_por.md">🇵🇹 Português</a></p>

### GitHub Actions Monitor の Android アプリ（計画中）：あなたのワークフローの状態と、あなた自身のモニターのアラートをスマートフォンで確認します。足場のみで、まだ何も実装されていません。

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose%20(planned)-blue.svg" alt="Kotlin and Jetpack Compose (planned)">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-none%20yet-lightgrey.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**正直さの確認 - 今日実際に動くもの：** アプリそのものは何もありません。このリポジトリにあるのは土台だけです。ドキュメント、マニフェスト、ライセンス、共通のリポジトリ チェックです。コード、APK、画面、通知サービス、アプリのテストはなく、スマートフォンでは何も試していません。

---

## 📋 ひと目で

| | |
|---|---|
| バージョン | `0.0.1` |
| 成熟度 | 足場 |
| テスト | まだなし（リポジトリのチェックのみ） |
| 技術スタック | Kotlin と Jetpack Compose（計画） |
| 実機での動作 | スマートフォンでは未実行 |

**移動先：** [概要](#-概要) · [アーキテクチャ](#-アーキテクチャ) · [クイックスタート](#-クイックスタート) · [テスト](#-テスト) · [リポジトリの構成](#-リポジトリの構成) · [関連プロジェクト](#-関連プロジェクト)

## 🚦 プロジェクトの状況

各行はこのリポジトリの一部分で、信号は今日どこまで進んでいるかを示します。計画ではなく、リポジトリが示せるもの（テスト、ビルド、実行）に基づいて書かれています。

🟢 完了・検証済み · 🟡 動作する（既知の制限あり） · 🟠 一部のみ、または未検証 · 🔴 未実装または動作しない

| 領域 | 状態 | ここでの意味 |
|---|---|---|
| リポジトリの土台 | 🟡 | マニフェスト、8 言語の README、ライセンス、共通のリポジトリ チェックが CI で実行されます。 |
| アプリのコード | 🔴 | 未着手：このリポジトリにはまだ Android プロジェクトがありません。 |
| テスト | 🔴 | アプリのテストはなく、リポジトリのチェックだけが実行されます。 |
| モニターとの接続 | 🔴 | 未着手。モニターはクライアントが使える読み取り API を提供していますが、このアプリはまだ使っていません。 |
| スマートフォンと通知 | 🔴 | 未着手で、スマートフォンでは一度も実行されていません。通知サービスは接続されていません。 |

## 🎯 概要

* **これが何になるか：** あなた自身の GitHub Actions Monitor のワークフローの状態とアラートを、スマートフォンに表示する Android アプリです。
* **読み取り専用：** あなたのモニターから読み取るだけです。GitHub に書き込まず、モニターの内容も変更しません。
* **あなたのモニター、あなたのデータ：** あなたが自分で運用するモニターと、あなたが渡すアクセス トークンで通信します。間に当方のサービスは入りません。
* **（まだ）そうではないもの：** インストールできるアプリも、リリースも、日付もありません。このリポジトリは場所を確保し、計画していることを正直に伝えるだけです。

## 🧩 EH3D での役割

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID は、GitHub Actions Monitor（[TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)）の Android クライアントになることを目指しています。モニターがすでに知っていることを表示するだけで、自分では何も決めません。最小構成のウォッチ版 [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH) が、このアプリに続く計画です。

## 🏛️ アーキテクチャ

<p align="center">
  <img src="images/architecture.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID のアーキテクチャ：誰が使い、何を持ち、何に依存するか" width="100%">
</p>

このリポジトリの各部分の関係：誰が使い、何を持ち、何に依存するか。図は `docs/architecture.json` から描かれ、コードと照合されます。

## ✨ 機能

**計画していますが、まだ何も存在しません：**
* ワークフローの概要（赤いもの、止まっているもの、待っているもの）とモニターのアラート。
* 状態付きのリポジトリ一覧。
* 重要なアラートのための任意の通知（初期設定ではオフ）。
* 8 言語のテキスト。

**計画していないこと：** GitHub への書き込み、スマートフォンからのワークフローの再実行、あなた自身のモニター以外のサービスへのデータ送信。

## 🔒 セキュリティと安全

* 意図：アプリは読み取るだけで、アクセス トークンはスマートフォンに残り、あなたのモニターのアドレスにのみ送られます。
* このリポジトリに秘密情報は置きません。キー、トークン、プッシュ サービスのファイル、署名キーはありません。
* これらは設計上の意図です。まだ何も存在しないため、誰もレビューできていません。

## 🚀 クイックスタート

インストールしたり実行したりするものは、まだありません。このリポジトリの土台を確認するには Python 3.10 以降が必要です：

```bash
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 テスト

CI はリポジトリのチェックと、そのチェック自体のテストを実行します。アプリにはまだコードがないため独自のテストはなく、ここではテスト数を主張しません。

## 🏗️ ビルドとリリース

ビルドもリリースもありません。アプリのバージョンがまだ存在しないためです。`VERSION`、マニフェスト、変更履歴にあるバージョン `0.0.1` は、リポジトリの土台のものです。

## 📂 リポジトリの構成

<p align="center">
  <img src="images/repository-structure.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID のリポジトリ構成" width="100%">
</p>

<details>
<summary>ツリーのテキスト版</summary>

```text
MONITOR-ANDROID/
├── tools/
│   ├── ci_validate.py                           マニフェスト、バージョン、README の整合
│   └── make_architecture.py make_structure.py   README の 2 つの図を描く
├── docs/                                        2 つの図の元データ
├── images/                                      バナーと 2 つの図
├── .github/                                     CI ワークフローとテンプレート
├── VERSION                                      リポジトリのバージョン
├── electro-hobby-3d.project.json                プロジェクトのマニフェスト
├── CHANGELOG.md                                 各バージョンの変更点
├── SECURITY.md SUPPORT.md                       脆弱性の報告方法と問い合わせ先
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE   貢献方法、行動規範、ライセンス
```

</details>

## 🗺️ ロードマップと既知の制限

**現在の既知の制限**

* コードがありません。アプリも APK も画面もありません。
* スマートフォンでも実際のモニターに対しても、何も試していません。
* 最終的な範囲はモニターの読み取り API に依存し、変わる可能性があります。

**次のステップ**

* 最初の画面を決め、テストを CI に載せながらアプリを少しずつ作り、動くようになってから初めてここにそう記載します。

## 🌍 言語

README とテキストは、英語、スペイン語、フランス語、イタリア語、ドイツ語、中国語、日本語、ヨーロッパポルトガル語で提供されています。

## 🔗 関連プロジェクト

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)：このアプリが読み取る予定のモニター
* [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH)：このアプリに続く、計画中の最小構成のウォッチ版
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS)：規約と共通のチェック

## 📚 ドキュメント

* [スイートの規約](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [変更履歴](CHANGELOG.md)

## 🤝 コントリビュートとコミュニティ

* プルリクエストの前に `python -I tools/ci_validate.py` を実行してください。その他のチェックは CI で実行されます。[CONTRIBUTING.md](CONTRIBUTING.md) と[行動規範](CODE_OF_CONDUCT.md)を参照してください。
* README は 8 言語で提供されています。文章を変更するときは 8 つのファイルすべてを変更し、2 つの図は `tools/make_architecture.py` と `tools/make_structure.py` が `docs/architecture.json` と `docs/structure.json` から描画します。
* 脆弱性は非公開で報告してください：[SECURITY.md](SECURITY.md)。質問、アイデア、報告：electrohobby3d@gmail.com（[SUPPORT.md](SUPPORT.md)）。

## 👤 作者

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 ライセンス

ソフトウェア：GPL-3.0-or-later。[LICENSE](LICENSE) を参照。スイートのハードウェア設計には CERN OHL-S を使います。

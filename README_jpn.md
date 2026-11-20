<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | 🇯🇵 <b>日本語</b> | <a href="README_por.md">🇵🇹 Português</a></p>

### GitHub Actions Monitor のネイティブ Android クライアント：概要、アラート、リポジトリ、提案をスマートフォンで確認できます。プッシュ通知は重大なものだけで、初期設定ではオフです。

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-77-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**正直さの確認 - 今日実際に動くもの：** ルールと API クライアントは、CI の JVM 上で動くユニットテスト付きで書かれています。アプリはまだ APK としてビルドされておらず、スマートフォンで表示されたこともなく、実際のモニターと通信したこともありません。プッシュ通知は初期設定でオフで、プッシュサービスは接続されていません。

---

> **安全性とセキュリティの現状：** このアプリはモニターを読み取り、ワークフローの再実行や GitHub へのコメントを行うアクション提案を承認または却下できます。承認は表示された内容そのものに結び付き、別の人が承認する必要があり（モニターが強制します）、リンクが単独で何かを承認することはありません。機械の安全（セーフティ）システムではありません。

## 📋 ひと目で

| | |
|---|---|
| バージョン | `0.0.1` |
| 成熟度 | 足場 |
| テスト | 77 |
| 技術スタック | Kotlin、Jetpack Compose（Android 8.0 以上） |
| 実機での動作 | スマートフォンではまだ実行していません |

**移動先：** [概要](#-概要) · [アーキテクチャ](#-アーキテクチャ) · [クイックスタート](#-クイックスタート) · [テスト](#-テスト) · [リポジトリの構成](#-リポジトリの構成) · [関連プロジェクト](#-関連プロジェクト)

## 🚦 プロジェクトの状況

各行はこのリポジトリの一部分で、信号は今日どこまで進んでいるかを示します。計画ではなく、リポジトリが示せるもの（テスト、ビルド、実行）に基づいて書かれています。

🟢 完了・検証済み · 🟡 動作する（既知の制限あり） · 🟠 一部のみ、または未検証 · 🔴 未実装または動作しない

| 領域 | 状態 | ここでの意味 |
|---|---|---|
| リポジトリ チェック | 🟡 | リポジトリのチェック、画像のチェック、アプリの軽量チェック（8 言語、リソース、機密）とそのテストが CI で実行されます。 |
| ルールと API クライアント | 🟠 | JVM ユニットテストが 69 件書かれています（承認、リンク、プッシュのゲート、設定、模擬ネットワークに対するクライアント）。それらを実行する Gradle ビルドはまだ実行されていません。 |
| 画面と Keystore | 🔴 | 書かれていますが、一度もビルドされず、画面に表示されず、スマートフォンで実行されたこともありません。 |
| プッシュ通知 | 🟠 | 重大なものだけで、初期設定ではオフです。Firebase 側は Google プロジェクトができるまで何もしない実装です。 |
| モニターのルート | 🟠 | 読み取りルートはモニターの API v1 の契約に従います。アプリが呼び出す提案とデバイスのルートは、まだモニターにありません。 |

## 🎯 概要

* **ポケットの中のモニター：** GitHub Actions Monitor の概要、アラート、リポジトリ、提案を、API v1 とベアラートークンで読み取ります。
* **二人承認の判断：** 提案の内容そのものを確認してから承認または却下します。承認はその内容のハッシュに結び付き、同じ人が作成した提案はモニターが拒否します。
* **初期設定では静か：** プッシュ通知はオフで、重大なものだけ、アラートごとに 1 回、短いタイトルのみです。
* **リポジトリに秘密はありません：** トークンは Android Keystore の鍵で保護されます。Firebase のファイル、鍵、キーストアはここにありません。

## 🧩 EH3D での役割

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID は GitHub Actions Monitor（TOOLS-GITHUB-ACTIONS-MONITOR）の Android アプリです。クライアントであり、アラートや提案について独自のルールを計算しません。何が重大か、何が提案かはモニターが決め、アプリはそれを表示して人に確認します。

## 🏛️ アーキテクチャ

<p align="center">
  <img src="images/architecture.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID のアーキテクチャ：誰が使い、何を持ち、何に依存するか" width="100%">
</p>

このリポジトリの各部分の関係：誰が使い、何を持ち、何に依存するか。図は `docs/architecture.json` から描かれ、コードと照合されます。

## ✨ 機能

* 5 つの画面：概要、アラート（レベルで絞り込み、カーソルによるページ送り）、リポジトリ（アラートのあるものと任意のリポジトリの検索）、提案（保留中またはすべて、詳細付き）、設定。
* 内容のハッシュを示す確認のあとで提案を承認または却下します。設定に名前を入力すると、自分が作成した提案は承認の対象として表示されません。
* リンク `ghamon://proposal/<id>?hash=<hex>` で提案を開きます。リンク作成後に内容が変わっていた場合、承認はできません。
* 重大なものだけのプッシュ通知のためのデバイス登録（初期設定ではオフ）。
* スマートフォンの言語に従う 8 言語：英語、スペイン語、ドイツ語、フランス語、イタリア語、日本語、中国語、ポルトガル（ポルトガル）語。

## 🔒 セキュリティと安全

* トークンは Android Keystore にある鍵で AES-GCM により保護され、ログやエラーメッセージには決して書かれず、入力したアドレスにのみ送られます。リダイレクトには従いません。
* 暗号化されない `http://` は localhost、`.local` の名前、プライベート IPv4 アドレスにのみ許可されます。それ以外のホストには `https://` が必要で、システムの認証局に対してのみ検証されます。
* 承認は、画面に表示されていた内容のハッシュを伴います。リンクは提案を開くだけで、承認や実行は決して行いません。
* プッシュ通知は初期設定でオフ、重大なものだけで、短く整えたタイトルのみです。ログやリポジトリの機密は含みません。
* アプリのデータはバックアップから除外されます。Firebase のファイル、キーストア、鍵がツリーにあると `python -I tools/check_app.py` が失敗します。

## 🔌 インターフェース

* モニターの読み取り：`GET /api/v1/health`、`/status`、`/accounts`、`/accounts/{id}/status`、`/alerts`（カーソルによるページ送り、フィルター）、`/repos/{owner}/{repo}/health`。`Authorization: Bearer` と `X-API-Version: 1` を使います。
* 提案（アプリが前提とするルートで、すべて設定ファイルにあります）：`GET /api/v1/actions/proposals`、`GET .../{id}`、`expected_hash` 付きの `POST .../{id}/approve`、`POST .../{id}/reject`。
* デバイス：`POST /api/v1/devices` と `DELETE /api/v1/devices/{id}`。
* リンク：`ghamon://proposal/<id>?hash=<16 進数 64 文字>`。

## ⚙️ 設定

調整できる値はすべて `app/src/main/assets/app_config.json` にあります：パス、タイムアウト、ページサイズ、プッシュの初期値、リンクのスキーム。モニターのアドレス、トークン、あなたの名前はアプリの設定で入力し、スマートフォンに保存されます。調整できる値はコードにありません。

## 🚀 クイックスタート

ビルドには JDK 21 と Android SDK が必要です（Android Studio に両方含まれます）。軽量チェックには Python 3.10 以上のみが必要です。アプリを開き、設定でモニターのアドレスとトークンを入力して保存してください。

```bash
./gradlew testDebugUnitTest lintDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 テスト

69 件のユニットテストが、スマートフォンなしで JVM 上で実行されます（`./gradlew testDebugUnitTest`）：模擬ネットワークに対する API クライアント、ルール、リンク、プッシュのゲート、設定。8 件の Python テストがリポジトリとアプリの軽量チェックをカバーします。CI がすべてを実行します。スマートフォンでの実行はまだありません。

## 🏗️ ビルドとリリース

バージョンは `VERSION`、`electro-hobby-3d.project.json`、`app/build.gradle.kts` にあり（異なるとビルドが止まります）、各バージョンには [CHANGELOG.md](CHANGELOG.md) の項目があります。リリースビルドは R8 で縮小され、そのあとリポジトリの外に保管されたメンテナー自身の鍵で署名されます。APK はまだ公開されていません。

## 📂 リポジトリの構成

<p align="center">
  <img src="images/repository-structure.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID のリポジトリ構成" width="100%">
</p>

<details>
<summary>ツリーのテキスト版</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                ビルドと縮小ルール
│   ├── src/main/AndroidManifest.xml                       権限、アクティビティ、ghamon:// リンク
│   ├── src/main/assets/app_config.json                    パス、タイムアウト、ページサイズ、プッシュ初期値
│   ├── src/main/res/                                      8 言語のテキスト、アイコン、テーマ
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   JVM でテスト済みのルール
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        画面、ViewModel、Keystore、プッシュ
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   JVM 上のユニットテスト
├── tools/
│   ├── ci_validate.py                                     マニフェスト、バージョン、README の整合
│   ├── check_app.py                                       アプリの言語、リソース、設定、機密
│   └── make_architecture.py make_structure.py             README の 2 つの図を描く
├── docs/                                                  2 つの図の元データ
├── gradle/                                                Gradle ラッパーのファイル
├── gradlew                                                Gradle ラッパー
├── gradlew.bat                                            Gradle ラッパー
├── build.gradle.kts                                       プラグインとビルド名
├── settings.gradle.kts                                    プラグインとビルド名
├── gradle.properties                                      ビルドのメモリ設定
├── images/                                                バナーと 2 つの図
├── .github/                                               CI ワークフローとテンプレート
├── VERSION                                                リポジトリのバージョン
├── electro-hobby-3d.project.json                          プロジェクトのマニフェスト
├── CHANGELOG.md                                           各バージョンの変更点
├── SECURITY.md SUPPORT.md                                 脆弱性の報告方法と問い合わせ先
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             貢献方法、行動規範、ライセンス
```

</details>

## 🗺️ ロードマップと既知の制限

**現時点で分かっている制限**

* アプリは APK にビルドされておらず、スマートフォンで実行されたことも、実際のモニターで試されたこともありません。
* アプリが呼び出す提案とデバイスのルートは、まだモニターにありません。
* プッシュサービスがありません。Google プロジェクトと、メンテナーだけが作れる鍵が必要です。
* 承認の前の生体認証による確認はまだありません。

**次のステップ**

* 最初の CI を実行して見つかった問題を直し、モニターのルートをつなぎ、Google プロジェクトができたら Firebase のプッシュサービスを追加します。

## 🌍 言語

README とテキストは、英語、スペイン語、フランス語、イタリア語、ドイツ語、中国語、日本語、ヨーロッパポルトガル語で提供されています。

## 🔗 関連プロジェクト

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)：このアプリがクライアントとなるモニター
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS)：規約と共通チェック

## 📚 ドキュメント

* [スイートの規約](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [変更履歴](CHANGELOG.md)

## 🤝 コントリビュートとコミュニティ

* プルリクエストの前に `python -I tools/ci_validate.py` と `python -I tools/check_app.py` を実行してください。Gradle のビルド、テスト、lint は CI で実行されます。[CONTRIBUTING.md](CONTRIBUTING.md) と[行動規範](CODE_OF_CONDUCT.md)を参照してください。
* README は 8 言語あります。文章を変更するときは 8 つのファイルすべてを変更し、2 つの図は `docs/architecture.json` と `docs/structure.json` から `tools/make_architecture.py` と `tools/make_structure.py` で描画します。
* 脆弱性は非公開で報告してください：[SECURITY.md](SECURITY.md)。質問、アイデア、報告：electrohobby3d@gmail.com（[SUPPORT.md](SUPPORT.md)）。

## 👤 作者

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 ライセンス

ソフトウェア：GPL-3.0-or-later。[LICENSE](LICENSE) を参照。スイートのハードウェア設計には CERN OHL-S を使います。

<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | 🇨🇳 <b>中文</b> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### GitHub Actions Monitor 的原生 Android 客户端：在手机上查看概览、告警、仓库和提案，推送通知仅限严重级别，默认关闭。

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-80-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.1.0-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**预览版：未在真实手机上测试。** 规则和 API 客户端由在 CI 的 JVM 上运行的单元测试覆盖，发布的 APK 由 GitHub Actions 构建，但应用从未在手机上显示过，也从未与真实的监控器通信。推送通知默认关闭，也没有连接任何推送服务。

---

> **安全（safety）与信息安全现状：** 此应用读取监控器，并可批准或拒绝可能重新运行工作流或在 GitHub 上发表评论的操作提案。批准与所显示的确切内容绑定，必须由另一个人批准（由监控器强制），链接永远不会自行批准任何事情。它不是任何机器的安全（safety）系统。

## 📋 速览

| | |
|---|---|
| 版本 | `0.1.0` |
| 成熟度 | 脚手架 |
| 测试 | 80 |
| 技术栈 | Kotlin、Jetpack Compose（Android 8.0+） |
| 真实硬件 | 未在真实手机上测试（预览版） |

**跳转到：** [概述](#-概述) · [架构](#-架构) · [快速开始](#-快速开始) · [测试](#-测试) · [仓库结构](#-仓库结构) · [相关项目](#-相关项目)

## 🚦 项目状态

每一行是本仓库的一个部分，灯号表示它今天做到了哪一步。内容依据仓库能够展示的证据（测试、构建、运行），而不是计划。

🟢 已完成并验证 · 🟡 可用，有已知限制 · 🟠 部分完成或尚未验证 · 🔴 未完成或不可用

| 领域 | 状态 | 在这里的含义 |
|---|---|---|
| 仓库检查 | 🟡 | 仓库检查、图片检查、应用的轻量检查（八种语言、资源、机密）及其测试都在 CI 中运行。 |
| 规则与 API 客户端 | 🟠 | 已编写 69 个 JVM 单元测试（批准、链接、推送过滤、设置、针对模拟网络的客户端）；CI 用 Gradle 运行它们并检查运行的数量。 |
| 界面与 Keystore | 🔴 | 已编写并在 CI 中编译，但从未在屏幕上显示、从未在手机上运行。 |
| 推送通知 | 🟠 | 仅限严重级别且默认关闭；Firebase 部分在存在 Google 项目之前是空实现。 |
| 监控器路由 | 🟠 | 读取路由遵循监控器的 API v1 契约；应用调用的提案和设备路由目前尚未在监控器中。 |

## 🎯 概述

* **口袋里的监控器：** 通过 API v1 和 bearer 令牌读取 GitHub Actions Monitor 的概览、告警、仓库和提案。
* **双人复核的决定：** 在看到提案的确切内容后批准或拒绝；批准与该内容的哈希绑定，若提案由同一个人创建，监控器会拒绝。
* **默认安静：** 推送通知默认关闭，仅限严重级别，每条告警一次，只有简短标题。
* **仓库中没有机密：** 令牌用 Android Keystore 的密钥加密保存；这里没有 Firebase 文件、密钥或密钥库。

## 🧩 在 EH3D 中的角色

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID 是 GitHub Actions Monitor（TOOLS-GITHUB-ACTIONS-MONITOR）的 Android 应用。它是客户端：不自行计算关于告警或提案的规则；什么是严重、什么是提案由监控器决定，应用负责显示并询问用户。

## 🏛️ 架构

<p align="center">
  <img src="images/architecture.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID 的架构：谁在使用、包含什么、依赖什么" width="100%">
</p>

本仓库各部分如何协作：谁在使用它、它包含什么、它依赖什么。图片由 `docs/architecture.json` 绘制，并与代码核对。

## ✨ 功能

* 五个界面：概览、告警（按级别过滤，游标分页）、仓库（有告警的仓库以及任意仓库的查询）、提案（待处理或全部，含详情）和设置。
* 在显示内容哈希的确认之后批准或拒绝提案；如果在设置中填写了你的名字，则不会提供批准你自己创建的提案。
* 链接 `ghamon://proposal/<id>?hash=<hex>` 会打开一个提案；若其内容在链接生成后发生了变化，则不提供批准。
* 为仅限严重级别的推送通知注册设备，默认关闭。
* 八种跟随手机语言的界面语言：英语、西班牙语、德语、法语、意大利语、日语、中文和葡萄牙（葡萄牙）语。

## 🔒 安全与防护

* 令牌使用存放在 Android Keystore 中的密钥以 AES-GCM 加密，绝不写入日志或错误文本，只发送到输入的地址；从不跟随重定向。
* 明文 `http://` 仅接受 localhost、`.local` 名称和私有 IPv4 地址；其他主机必须使用 `https://`，且仅依据系统证书颁发机构验证。
* 批准携带屏幕上所显示内容的哈希；链接只会打开提案，绝不会批准或执行任何操作。
* 推送通知默认关闭，仅限严重级别，只含简短且经过清理的标题：没有日志，没有仓库机密。
* 应用数据不参与备份；如果树中出现 Firebase 文件、密钥库或密钥，`python -I tools/check_app.py` 会失败。

## 🔌 接口

* 对监控器的读取：`GET /api/v1/health`、`/status`、`/accounts`、`/accounts/{id}/status`、`/alerts`（游标分页、过滤）和 `/repos/{owner}/{repo}/health`，使用 `Authorization: Bearer` 和 `X-API-Version: 1`。
* 提案（应用假定的路由，全部在配置文件中）：`GET /api/v1/actions/proposals`、`GET .../{id}`、带 `expected_hash` 的 `POST .../{id}/approve` 以及 `POST .../{id}/reject`。
* 设备：`POST /api/v1/devices` 和 `DELETE /api/v1/devices/{id}`。
* 链接：`ghamon://proposal/<id>?hash=<64 个十六进制字符>`。

## ⚙️ 配置

所有可调参数都在 `app/src/main/assets/app_config.json` 中：路径、超时、页面大小、推送默认值和链接方案。监控器的地址、令牌和你的名字在应用的设置中输入，并保存在手机上。代码中没有任何可调参数。

## 🚀 快速开始

构建需要 JDK 21 和 Android SDK（Android Studio 两者都带）；轻量检查只需要 Python 3.10 或更高版本。打开应用，进入设置，输入监控器的地址和令牌并保存。

```bash
./gradlew testDebugUnitTest lintDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 测试

69 个单元测试在 JVM 上运行（`./gradlew testDebugUnitTest`），无需手机：针对模拟网络的 API 客户端、规则、链接、推送过滤和设置。11 个 Python 测试覆盖仓库和应用的轻量检查。CI 会运行全部测试；这里的任何内容都尚未在手机上运行过。

## 🏗️ 构建与发布

版本号保存在 `VERSION`、`electro-hobby-3d.project.json` 和 `app/build.gradle.kts` 中（不一致时构建会停止），每个版本在 [CHANGELOG.md](CHANGELOG.md) 中有一条记录。发布构建用 R8 压缩，之后用维护者自己的密钥签名，该密钥保存在仓库之外。第一个版本是预览版：尚未在真实手机上测试。APK 由 GitHub Actions 构建（工作流“APK build (manual)”，手动启动，其中没有签名密钥），维护者用自己的密钥签名，并连同 `SHA256SUMS` 作为预发布版本发布。**安装：** 从 Releases 页面的最新发布下载 `TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk`，用 `sha256sum -c SHA256SUMS` 校验，在手机设置中允许从该来源安装（Android 8.0 或更高版本）并打开文件，或运行 `adb install TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk`。**更新：** 在旧版本之上安装更新的 APK；由于签名相同且版本号更高，设置会保留，令牌仍封存在 Keystore 中。如果 Android 拒绝更新，说明签名不同：请先卸载旧应用（其令牌和设置会丢失）。

## 📂 仓库结构

<p align="center">
  <img src="images/repository-structure.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID 的仓库结构" width="100%">
</p>

<details>
<summary>目录树的文本版</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                构建和压缩规则
│   ├── src/main/AndroidManifest.xml                       权限、活动、ghamon:// 链接
│   ├── src/main/assets/app_config.json                    路径、超时、页面大小、推送默认值
│   ├── src/main/res/                                      八种语言的文本、图标和主题
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   在 JVM 上测试的规则
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        界面、视图模型、Keystore、推送
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   JVM 上的单元测试
├── tools/
│   ├── ci_validate.py                                     清单、版本、README 一致性
│   ├── check_app.py                                       应用的语言、资源、配置和机密
│   └── make_architecture.py make_structure.py             绘制 README 的两张图
├── docs/                                                  两张图的数据源
├── gradle/                                                Gradle 包装器文件
├── gradlew                                                Gradle 包装器
├── gradlew.bat                                            Gradle 包装器
├── build.gradle.kts                                       插件和构建名称
├── settings.gradle.kts                                    插件和构建名称
├── gradle.properties                                      构建的内存设置
├── images/                                                横幅图和两张图片
├── .github/                                               CI 工作流和模板
├── VERSION                                                仓库的版本
├── electro-hobby-3d.project.json                          项目清单
├── CHANGELOG.md                                           每个版本的变更
├── SECURITY.md SUPPORT.md                                 如何报告漏洞及在哪里提问
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             参与贡献、行为准则和许可证
```

</details>

## 🗺️ 路线图与已知限制

**目前已知的限制**

* 预览版：应用尚未在手机上运行，也尚未与真实监控器试用。
* 它调用的提案和设备路由目前尚未在监控器中。
* 没有推送服务：需要只有维护者才能创建的 Google 项目和密钥。
* 批准之前尚无生物识别确认。

**下一步**

* 运行首次 CI 并修复发现的问题，接通监控器的路由，待 Google 项目建立后再加入 Firebase 推送服务。

## 🌍 语言

README 和文本提供英语、西班牙语、法语、意大利语、德语、汉语、日语和欧洲葡萄牙语版本。

## 🔗 相关项目

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)：此应用所连接的监控器
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS)：约定与共享检查

## 📚 文档

* [套件的约定](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [变更日志](CHANGELOG.md)

## 🤝 参与贡献与社区

* 提交拉取请求前请运行 `python -I tools/ci_validate.py` 和 `python -I tools/check_app.py`；Gradle 构建、测试和 lint 在 CI 中运行。参见 [CONTRIBUTING.md](CONTRIBUTING.md) 和[行为准则](CODE_OF_CONDUCT.md)。
* README 有八种语言：修改文字时要同步修改全部八个文件，两张图片由 `tools/make_architecture.py` 和 `tools/make_structure.py` 根据 `docs/architecture.json` 和 `docs/structure.json` 绘制。
* 请私下报告漏洞：[SECURITY.md](SECURITY.md)。问题、想法和报告：electrohobby3d@gmail.com（[SUPPORT.md](SUPPORT.md)）。

## 👤 作者

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 许可证

软件：GPL-3.0-or-later。见 [LICENSE](LICENSE)。套件的硬件设计使用 CERN OHL-S。

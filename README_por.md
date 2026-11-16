<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | <a href="README_spa.md">🇪🇸 Español</a> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | 🇵🇹 <b>Português</b></p>

### Cliente Android nativo do GitHub Actions Monitor: resumo, alertas, repositórios e propostas no telemóvel, com notificações push apenas críticas e desativadas por predefinição.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-77-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Verificação de honestidade - o que funciona hoje:** as regras e o cliente da API estão escritos com testes unitários que correm na JVM no CI; a aplicação ainda não foi compilada num APK, não foi mostrada num telemóvel e nunca falou com um monitor real. As notificações push estão desativadas por predefinição e não há nenhum serviço push ligado.

---

> **Estado de segurança (safety) e de segurança informática:** esta aplicação lê um monitor e pode aprovar ou rejeitar propostas de ação que podem repetir workflows ou comentar no GitHub. Uma aprovação fica ligada ao conteúdo exato mostrado, tem de a aprovar outra pessoa (o monitor impõe-no) e uma ligação nunca aprova nada por si só. Não é um sistema de segurança (safety) de nenhuma máquina.

## 📋 Num relance

| | |
|---|---|
| Versão | `0.0.1` |
| Maturidade | Estrutura inicial |
| Testes | 77 |
| Tecnologias | Kotlin, Jetpack Compose (Android 8.0+) |
| Em hardware real | Ainda não foi executada num telemóvel |

**Ir para:** [Visão geral](#-visão-geral) · [Arquitetura](#-arquitetura) · [Início rápido](#-início-rápido) · [Testes](#-testes) · [Estrutura do repositório](#-estrutura-do-repositório) · [Projetos relacionados](#-projetos-relacionados)

## 🚦 Estado do projeto

Cada linha é uma parte deste repositório e a luz diz até onde chega hoje. É escrita com o que o repositório consegue demonstrar (testes, compilações, execuções), não com o que está previsto.

🟢 Feito e verificado · 🟡 Funciona, com limites conhecidos · 🟠 Feito a meio ou ainda por verificar · 🔴 Por fazer ou a não funcionar

| Área | Estado | O que significa aqui |
|---|---|---|
| Verificações do repositório | 🟡 | A verificação do repositório, a das imagens, as verificações leves da aplicação (oito idiomas, recursos, segredos) e os respetivos testes correm no CI. |
| Regras e cliente da API | 🟠 | Estão escritos 69 testes unitários na JVM (aprovação, ligações, filtro push, definições, o cliente contra uma rede simulada); a compilação do Gradle que os executa ainda não correu. |
| Ecrãs e Keystore | 🔴 | Escritos, mas nunca compilados, nunca mostrados num ecrã e nunca executados num telemóvel. |
| Notificações push | 🟠 | Apenas críticas e desativadas por predefinição; a parte do Firebase é uma implementação nula até existir um projeto Google. |
| Rotas do monitor | 🟠 | As rotas de leitura seguem o contrato da API v1 do monitor; as rotas de propostas e dispositivos que a aplicação chama ainda não estão no monitor. |

## 🎯 Visão geral

* **O monitor no bolso:** o resumo, os alertas, os repositórios e as propostas de um GitHub Actions Monitor, lidos com um token bearer através da sua API v1.
* **Decisões a quatro olhos:** aprove ou rejeite uma proposta depois de ver o seu conteúdo exato; a aprovação fica ligada ao hash desse conteúdo e o monitor recusa-a se a mesma pessoa criou a proposta.
* **Silenciosa por predefinição:** as notificações push estão desativadas, são apenas críticas, uma vez por alerta, com um título curto e mais nada.
* **Nada secreto no repositório:** o token é cifrado com uma chave do Keystore do Android; aqui não há nenhum ficheiro do Firebase, chave ou keystore.

## 🧩 Papel no EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID é a aplicação Android do GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). É um cliente: não calcula regras próprias sobre alertas ou propostas; o monitor decide o que é crítico e o que é uma proposta, e a aplicação mostra-o e pergunta a uma pessoa.

## 🏛️ Arquitetura

<p align="center">
  <img src="images/architecture.svg" alt="Arquitetura de TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: quem o usa, o que contém e de que depende" width="100%">
</p>

Como as partes deste repositório se encaixam: quem o usa, o que contém e de que depende. A imagem é desenhada a partir de `docs/architecture.json` e verificada com o código.

## ✨ Funcionalidades

* Cinco ecrãs: resumo, alertas (filtro por nível, paginação com cursores), repositórios (os que têm alertas e uma pesquisa de qualquer repositório), propostas (pendentes ou todas, com detalhe) e definições.
* Aprovar ou rejeitar uma proposta depois de uma confirmação que indica o hash do conteúdo; se indicar o seu nome nas definições, não lhe é oferecida a aprovação de uma proposta que criou.
* As ligações `ghamon://proposal/<id>?hash=<hex>` abrem uma proposta; se o seu conteúdo mudou desde que a ligação foi criada, a aprovação não é oferecida.
* Registo do dispositivo para notificações push apenas críticas, desativadas por predefinição.
* Oito idiomas que seguem o idioma do telemóvel: inglês, espanhol, alemão, francês, italiano, japonês, chinês e português de Portugal.

## 🔒 Segurança e proteção

* O token é cifrado com AES-GCM sob uma chave que vive no Keystore do Android, nunca é escrito num registo nem num texto de erro e só é enviado para o endereço escrito; os redirecionamentos nunca são seguidos.
* O `http://` sem cifra só é aceite para localhost, nomes `.local` e endereços IPv4 privados; qualquer outro anfitrião exige `https://`, verificado apenas contra as autoridades do sistema.
* Uma aprovação leva o hash do conteúdo que estava no ecrã; uma ligação só abre uma proposta e nunca aprova nem executa nada.
* As notificações push estão desativadas por predefinição, são apenas críticas e levam um título curto e limpo: sem registos nem segredos de repositórios.
* Os dados da aplicação ficam fora das cópias de segurança, e `python -I tools/check_app.py` falha se houver um ficheiro do Firebase, um keystore ou uma chave na árvore.

## 🔌 Interfaces

* Leituras do monitor: `GET /api/v1/health`, `/status`, `/accounts`, `/accounts/{id}/status`, `/alerts` (paginação por cursor, filtros) e `/repos/{owner}/{repo}/health`, com `Authorization: Bearer` e `X-API-Version: 1`.
* Propostas (rotas que a aplicação presume, todas no ficheiro de configuração): `GET /api/v1/actions/proposals`, `GET .../{id}`, `POST .../{id}/approve` com `expected_hash` e `POST .../{id}/reject`.
* Dispositivos: `POST /api/v1/devices` e `DELETE /api/v1/devices/{id}`.
* Ligação: `ghamon://proposal/<id>?hash=<64 carateres hexadecimais>`.

## ⚙️ Configuração

Todo o valor ajustável está em `app/src/main/assets/app_config.json`: os caminhos, os tempos limite, o tamanho da página, os valores de push e o esquema da ligação. O endereço do monitor, o seu token e o seu nome escrevem-se nas definições da aplicação e ficam guardados no telemóvel. Nada ajustável está no código.

## 🚀 Início rápido

Para compilar precisa do JDK 21 e do SDK do Android (o Android Studio traz ambos); as verificações leves só precisam do Python 3.10 ou posterior. Abra a aplicação, vá a Definições, escreva o endereço do seu monitor e o respetivo token e guarde.

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Testes

69 testes unitários correm na JVM (`./gradlew testDebugUnitTest`) sem telemóvel: o cliente da API contra uma rede simulada, as regras, as ligações, o filtro push e as definições. 8 testes de Python cobrem o repositório e as verificações leves da aplicação. O CI executa-os todos; nada aqui foi executado num telemóvel.

## 🏗️ Compilação e publicação

A versão vive em `VERSION`, em `electro-hobby-3d.project.json` e em `app/build.gradle.kts` (a compilação pára se diferirem), e cada versão tem a sua entrada em [CHANGELOG.md](CHANGELOG.md). A compilação de lançamento é reduzida com o R8 e assinada depois com a chave própria do mantenedor, guardada fora do repositório. Ainda não há nenhum APK publicado.

## 📂 Estrutura do repositório

<p align="center">
  <img src="images/repository-structure.svg" alt="Estrutura do repositório TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Versão em texto da árvore</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                a compilação e as regras de redução
│   ├── src/main/AndroidManifest.xml                       permissões, atividade, ligação ghamon://
│   ├── src/main/assets/app_config.json                    caminhos, tempos limite, página, valores de push
│   ├── src/main/res/                                      os textos em oito idiomas, ícones e temas
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   regras testadas na JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        ecrãs, view model, Keystore, push
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   os testes unitários na JVM
├── tools/
│   ├── ci_validate.py                                     manifesto, versões, paridade dos README
│   ├── check_app.py                                       idiomas, recursos, configuração e segredos da aplicação
│   └── make_architecture.py make_structure.py             desenham as duas imagens do README
├── docs/                                                  as fontes das duas imagens
├── gradle/                                                os ficheiros do wrapper do Gradle
├── gradlew gradlew.bat                                    o wrapper do Gradle
├── build.gradle.kts settings.gradle.kts                   os plugins e o nome da compilação
├── gradle.properties                                      a memória da compilação
├── images/                                                banner e as duas imagens
├── .github/                                               fluxos de CI e modelos
├── VERSION                                                a versão do repositório
├── CHANGELOG.md                                           o que mudou em cada versão
├── SECURITY.md SUPPORT.md                                 como comunicar uma vulnerabilidade e onde perguntar
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             como contribuir, o código de conduta e a licença
```

</details>

## 🗺️ Roteiro e limites conhecidos

**Limites conhecidos hoje**

* A aplicação não foi compilada num APK, nem executada num telemóvel, nem experimentada com um monitor real.
* As rotas de propostas e dispositivos que chama ainda não estão no monitor.
* Não há serviço push: precisa de um projeto Google e de uma chave que só o mantenedor pode criar.
* Ainda não há confirmação biométrica antes de uma aprovação.

**A seguir**

* Executar o primeiro CI e corrigir o que encontrar, ligar as rotas do monitor e depois acrescentar o serviço push do Firebase quando o projeto Google existir.

## 🌍 Idiomas

O README e os textos existem em inglês, espanhol, francês, italiano, alemão, chinês, japonês e português europeu.

## 🔗 Projetos relacionados

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): o monitor de que esta aplicação é cliente
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): as convenções e as verificações partilhadas

## 📚 Documentação

* [As convenções da suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Registo de alterações](CHANGELOG.md)

## 🤝 Contribuir e comunidade

* Antes de um pull request execute `python -I tools/ci_validate.py` e `python -I tools/check_app.py`; a compilação do Gradle, os testes e o lint correm no CI. Consulte [CONTRIBUTING.md](CONTRIBUTING.md) e o [código de conduta](CODE_OF_CONDUCT.md).
* O README existe em oito idiomas: uma alteração ao texto faz-se nos oito ficheiros, e as duas imagens são desenhadas a partir de `docs/architecture.json` e `docs/structure.json` com `tools/make_architecture.py` e `tools/make_structure.py`.
* Comunique uma vulnerabilidade em privado: [SECURITY.md](SECURITY.md). Perguntas, ideias e relatórios: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENÇA

Software: GPL-3.0-or-later. Ver [LICENSE](LICENSE). Os projetos de hardware da suite usam a CERN OHL-S.

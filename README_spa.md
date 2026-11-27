<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | 🇪🇸 <b>Español</b> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Cliente Android nativo de GitHub Actions Monitor: resumen, alertas, repositorios y las propuestas de su IA en el móvil, en solo lectura.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-75-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.1.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Preliminar: no probada en un teléfono real.** El cliente se comprueba contra monitores de demostración (datos inventados) en GitHub Actions y las pantallas se fotografían en un emulador, pero la app no se ha visto en un móvil ni ha hablado con un monitor de uso diario. No envía notificaciones push: el monitor aún no tiene servicio push.

---

> **Estado de seguridad (safety) y de seguridad informática:** esta app lee un monitor; salvo iniciar y cerrar sesión no cambia nada, y las propuestas se aprueban o rechazan solo en el ordenador del monitor. No es un sistema de seguridad (safety) de ninguna máquina.

## 📋 De un vistazo

| | |
|---|---|
| Versión | `0.1.1` |
| Madurez | Andamiaje |
| Pruebas | 75 |
| Pila tecnológica | Kotlin, Jetpack Compose (Android 8.0+) |
| En hardware real | No probada en un teléfono real (preliminar) |

**Ir a:** [Resumen](#-resumen) · [Arquitectura](#-arquitectura) · [Inicio rápido](#-inicio-rápido) · [Pruebas](#-pruebas) · [Estructura del repositorio](#-estructura-del-repositorio) · [Proyectos relacionados](#-proyectos-relacionados)

## 🚦 Estado del proyecto

Cada línea es una parte de este repositorio y la luz dice hasta dónde llega hoy. Se escribe con lo que el repositorio puede demostrar (pruebas, compilaciones, ejecuciones), no con lo previsto.

🟢 Hecho y verificado · 🟡 Funciona, con límites conocidos · 🟠 Hecho a medias o sin verificar · 🔴 Sin hacer o sin funcionar

| Área | Estado | Qué significa aquí |
|---|---|---|
| Comprobaciones del repositorio | 🟡 | La comprobación del repositorio, la de las imágenes, las comprobaciones ligeras de la app (ocho idiomas, recursos, secretos) y sus pruebas se ejecutan en el CI. |
| Reglas y cliente del monitor | 🟡 | Hay 64 pruebas unitarias en la JVM, entre ellas el cliente contra respuestas grabadas de un monitor de demostración; la CI las ejecuta con Gradle, comprueba cuántas se ejecutan y lanza el cliente contra monitores de demostración en marcha. |
| Pantallas y Keystore | 🟠 | Fotografiadas en un emulador contra un monitor de demostración; nunca ejecutadas en un móvil. |
| Notificaciones push | 🔴 | El monitor no tiene servicio push, así que la app no ofrece interruptor ni pide permiso. |
| Rutas del monitor | 🟡 | Comprobadas contra el monitor de demostración del commit fijado en `.github/workflows/ci.yml`; no contra un monitor de uso diario. |

## 🎯 Resumen

* **El monitor en el bolsillo:** el resumen, las alertas, los repositorios y las propuestas de un GitHub Actions Monitor en modo red.
* **Se identifica como el monitor lo pide:** el token de acceso del modo red y, si el monitor tiene usuarios, la sesión de una persona.
* **Solo lectura:** las propuestas muestran el comando exacto y su hash; aprobar se hace en el ordenador del monitor.
* **Nada secreto en el repositorio:** los tokens se cifran con una clave del Keystore de Android; aquí no hay ningún archivo de Firebase, clave ni keystore.

## 🧩 Papel en EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID es la app Android de GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). Es un cliente: no calcula reglas propias sobre alertas o propuestas; el monitor decide qué es crítico y qué es una propuesta, y la app lo muestra y pregunta a una persona.

## 🏛️ Arquitectura

<p align="center">
  <img src="images/architecture.svg" alt="Arquitectura de TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: quién lo usa, qué contiene y de qué depende" width="100%">
</p>

Cómo encajan las partes de este repositorio: quién lo usa, qué contiene y de qué depende. La imagen se dibuja desde `docs/architecture.json` y se comprueba contra el código.

## ✨ Funciones

* Cinco pantallas: resumen, alertas (filtro por nivel), repositorios (ordenados por riesgo, búsqueda por nombre), propuestas (pendientes o todas, con detalle) y ajustes.
* Iniciar y cerrar sesión cuando el monitor trabaja en modo equipo; cada rechazo del monitor tiene su propio mensaje.
* Los enlaces `ghamon://proposal/<id>?hash=<hex>` abren una propuesta y avisan si su contenido cambió desde que se hizo el enlace.
* Ocho idiomas que siguen el idioma del móvil: inglés, español, alemán, francés, italiano, japonés, chino y portugués de Portugal.

Las capturas son de la app en un emulador, contra un monitor de demostración con datos inventados:

<table><tr><td align="center"><img src="images/screenshots/sign-in.png" alt="Inicio de sesión" width="180"><br>Inicio de sesión</td><td align="center"><img src="images/screenshots/summary.png" alt="Resumen" width="180"><br>Resumen</td><td align="center"><img src="images/screenshots/alerts.png" alt="Alertas" width="180"><br>Alertas</td><td align="center"><img src="images/screenshots/repos.png" alt="Repositorios" width="180"><br>Repositorios</td><td align="center"><img src="images/screenshots/proposals.png" alt="Propuestas" width="180"><br>Propuestas</td></tr></table>

## 🔒 Seguridad y protección

* El token de acceso y la sesión se cifran con AES-GCM bajo una clave que vive en el Keystore de Android, nunca se escriben en un registro ni en un texto de error y solo se envían a la dirección escrita; al cambiar la dirección se borran, y nunca se siguen redirecciones.
* La contraseña se envía una vez para iniciar sesión y no se guarda.
* `http://` sin cifrar solo se acepta para localhost, nombres `.local` y direcciones IPv4 privadas; cualquier otro host necesita `https://`, comprobado solo contra las autoridades del sistema.
* Un enlace solo abre una propuesta; desde el móvil no se aprueba ni se ejecuta nada.
* Los datos de la app quedan fuera de las copias de seguridad, y `python -I tools/check_app.py` falla si hay un archivo de Firebase, un keystore o una clave en el árbol.

## 🔌 Interfaces

* Lecturas: `GET /api/auth/me`, `/api/health`, `/api/alerts`, `/api/team/health`, `/api/ai/actions` y `/api/accounts` (solo con varias cuentas).
* Iniciar y cerrar sesión: `POST /api/auth/login` (`client: "api"`) y `POST /api/auth/logout`.
* Cabeceras: `X-Monitor-Access` con el token de acceso y, en modo equipo, `Authorization: Bearer` con la sesión.
* Enlace: `ghamon://proposal/<id>?hash=<64 caracteres hexadecimales>`.

## ⚙️ Configuración

Todo valor ajustable está en `app/src/main/assets/app_config.json`: las rutas, los tiempos, el límite de tamaño, la regla de push y el esquema del enlace. La dirección del monitor, su token de acceso y tu usuario se escriben en los ajustes de la app y se guardan en el móvil.

El monitor necesita `listen.network` activado; un nombre (no una dirección) usado para llegar a él va en `listen.allowed_hosts`; pon el token de acceso en `MONITOR_ACCESS_TOKEN` para que no cambie en cada arranque; con usuarios (modo equipo) activa también `auth.allow_bearer`.

## 🚀 Inicio rápido

Para compilar necesitas JDK 21 y el SDK de Android (Android Studio trae ambos); las comprobaciones ligeras solo necesitan Python 3.10 o posterior. Abre la app, ve a Ajustes, escribe la dirección de tu monitor y su token, y guarda.

```bash
./gradlew testDebugUnitTest lintDebug
python -I tools/check_app.py
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Pruebas

64 pruebas unitarias se ejecutan en la JVM (`./gradlew testDebugUnitTest`) sin móvil: el cliente contra respuestas grabadas de un monitor de demostración, sus rechazos, los enlaces, los ajustes y la regla de push. Cinco de ellas hablan con monitores de demostración en marcha y se omiten salvo que el trabajo de CI "contract" los arranque. 11 pruebas de Python cubren el repositorio y las comprobaciones ligeras de la app. Nada de aquí se ha ejecutado en un móvil.

## 🏗️ Compilación y publicación

La versión vive en `VERSION`, en `electro-hobby-3d.project.json` y en `app/build.gradle.kts` (la compilación se detiene si difieren), y cada versión tiene su entrada en [CHANGELOG.md](CHANGELOG.md). La compilación de publicación se reduce con R8 y se firma después con la clave propia del mantenedor, guardada fuera del repositorio. La primera versión es preliminar: no se ha probado en un teléfono real. GitHub Actions compila el APK (el workflow "APK build (manual)", lanzado a mano y sin clave de firma) y el mantenedor lo firma con su propia clave y lo publica como versión preliminar junto con `SHA256SUMS`. **Para instalar:** descarga `TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk` de la última versión en la página de Releases, compruébalo con `sha256sum -c SHA256SUMS`, permite instalar desde esa fuente en los ajustes del teléfono (Android 8.0 o posterior) y abre el archivo, o ejecuta `adb install TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID-release.apk`. **Para actualizar:** instala el APK más nuevo sobre el anterior; los ajustes se conservan porque la firma es la misma y el código de versión es mayor, y el token sigue sellado en el Keystore. Si Android rechaza la actualización, la firma no es la misma: quita antes la app antigua (se pierden su token y sus ajustes).

## 📂 Estructura del repositorio

<p align="center">
  <img src="images/repository-structure.svg" alt="Estructura del repositorio TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Versión en texto del árbol</summary>

```text
MONITOR-ANDROID/
├── app/
│   ├── build.gradle.kts proguard-rules.pro                la compilación y las reglas de reducción
│   ├── src/main/AndroidManifest.xml                       permisos, actividad, enlace ghamon://
│   ├── src/main/assets/app_config.json                    rutas, tiempos, tamaño máximo, regla push
│   ├── src/main/res/                                      los textos en ocho idiomas, iconos y temas
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   reglas probadas en la JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        pantallas, view model, tema, Keystore
│   ├── src/test/java/es/electrohobby3d/ghamonitor/core/   las pruebas unitarias en la JVM
│   ├── src/test/resources/contract/                       respuestas grabadas de un monitor de demo
│   └── src/androidTest/                                   las capturas en un emulador
├── tools/
│   ├── ci_validate.py                                     manifiesto, versiones, paridad de README
│   ├── check_app.py                                       idiomas, recursos, configuración y secretos de la app
│   ├── make_architecture.py make_structure.py             dibujan las dos imágenes del README
│   └── contract/                                          monitores de demo y grabación de respuestas
├── docs/                                                  las fuentes de las dos imágenes
├── gradle/                                                los archivos del wrapper de Gradle
├── gradlew                                                el wrapper de Gradle
├── gradlew.bat                                            el wrapper de Gradle
├── build.gradle.kts                                       los plugins y el nombre de la compilación
├── settings.gradle.kts                                    los plugins y el nombre de la compilación
├── gradle.properties                                      la memoria de la compilación
├── images/                                                banner y las dos imágenes
├── .github/                                               flujos de CI y plantillas
├── VERSION                                                la versión del repositorio
├── electro-hobby-3d.project.json                          el manifiesto del proyecto
├── CHANGELOG.md                                           qué cambió en cada versión
├── SECURITY.md SUPPORT.md                                 cómo informar de una vulnerabilidad y dónde preguntar
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE             cómo contribuir, el código de conducta y la licencia
```

</details>

## 🗺️ Hoja de ruta y límites conocidos

**Límites conocidos hoy**

* Versión preliminar: la app no se ha ejecutado en un móvil.
* La edición Lite del monitor no tiene alertas, repositorios ni propuestas, y la app solo puede decirlo.
* No hay servicio push: el monitor todavía no envía mensajes push.
* No se puede emparejar el móvil con el código QR del monitor; el token de acceso se escribe.

**Siguiente**

* Probarla en un móvil contra un monitor real, después el emparejamiento por QR, y push cuando el monitor lo ofrezca.

## 🌍 Idiomas

El README y los textos existen en inglés, español, francés, italiano, alemán, chino, japonés y portugués de Portugal.

## 🔗 Proyectos relacionados

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): el monitor del que esta app es cliente
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): las convenciones y las comprobaciones compartidas

## 📚 Documentación

* [Las convenciones de la suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Registro de cambios](CHANGELOG.md)

## 🤝 Contribuir y comunidad

* Antes de una pull request ejecuta `python -I tools/ci_validate.py` y `python -I tools/check_app.py`; la compilación de Gradle, las pruebas y el lint se ejecutan en el CI. Consulta [CONTRIBUTING.md](CONTRIBUTING.md) y el [código de conducta](CODE_OF_CONDUCT.md).
* El README existe en ocho idiomas: un cambio en el texto se hace en los ocho archivos, y las dos imágenes se dibujan a partir de `docs/architecture.json` y `docs/structure.json` con `tools/make_architecture.py` y `tools/make_structure.py`.
* Informa de una vulnerabilidad en privado: [SECURITY.md](SECURITY.md). Preguntas, ideas e informes: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENCIA

Software: GPL-3.0-or-later. Véase [LICENSE](LICENSE). Los diseños de hardware de la suite usan CERN OHL-S.

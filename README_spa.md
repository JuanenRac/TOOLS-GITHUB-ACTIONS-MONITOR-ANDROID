<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | 🇪🇸 <b>Español</b> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### Cliente Android nativo de GitHub Actions Monitor: resumen, alertas, repositorios y propuestas en el móvil, con notificaciones push solo críticas desactivadas por defecto.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose-blue.svg" alt="Kotlin and Jetpack Compose">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-80-2EA043.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.1.0-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Preliminar: no probada en un teléfono real.** Las reglas y el cliente de la API están cubiertos por pruebas unitarias que se ejecutan en la JVM en el CI y el APK de la versión lo compila GitHub Actions, pero la app no se ha visto en un móvil y nunca ha hablado con un monitor real. Las notificaciones push están desactivadas por defecto y no hay ningún servicio push conectado.

---

> **Estado de seguridad (safety) y de seguridad informática:** esta app lee un monitor y puede aprobar o rechazar propuestas de acción que pueden repetir workflows o comentar en GitHub. Una aprobación queda ligada al contenido exacto mostrado, debe aprobarla otra persona (lo impone el monitor) y un enlace nunca aprueba nada por sí solo. No es un sistema de seguridad (safety) de ninguna máquina.

## 📋 De un vistazo

| | |
|---|---|
| Versión | `0.1.0` |
| Madurez | Andamiaje |
| Pruebas | 80 |
| Pila tecnológica | Kotlin, Jetpack Compose (Android 8.0+) |
| En hardware real | No probada en un teléfono real (preliminar) |

**Ir a:** [Resumen](#-resumen) · [Arquitectura](#-arquitectura) · [Inicio rápido](#-inicio-rápido) · [Pruebas](#-pruebas) · [Estructura del repositorio](#-estructura-del-repositorio) · [Proyectos relacionados](#-proyectos-relacionados)

## 🚦 Estado del proyecto

Cada línea es una parte de este repositorio y la luz dice hasta dónde llega hoy. Se escribe con lo que el repositorio puede demostrar (pruebas, compilaciones, ejecuciones), no con lo previsto.

🟢 Hecho y verificado · 🟡 Funciona, con límites conocidos · 🟠 Hecho a medias o sin verificar · 🔴 Sin hacer o sin funcionar

| Área | Estado | Qué significa aquí |
|---|---|---|
| Comprobaciones del repositorio | 🟡 | La comprobación del repositorio, la de las imágenes, las comprobaciones ligeras de la app (ocho idiomas, recursos, secretos) y sus pruebas se ejecutan en el CI. |
| Reglas y cliente de la API | 🟠 | Hay 69 pruebas unitarias en la JVM escritas (aprobación, enlaces, filtro push, ajustes, el cliente contra una red simulada); la CI las ejecuta con Gradle y comprueba cuántas se han ejecutado. |
| Pantallas y Keystore | 🔴 | Escritas y compiladas en CI, pero nunca mostradas en una pantalla y nunca ejecutadas en un móvil. |
| Notificaciones push | 🟠 | Solo críticas y desactivadas por defecto; la parte de Firebase es una implementación nula hasta que exista un proyecto de Google. |
| Rutas del monitor | 🟠 | Las rutas de lectura siguen el contrato de la API v1 del monitor; las rutas de propuestas y dispositivos que llama la app aún no están en el monitor. |

## 🎯 Resumen

* **El monitor en el bolsillo:** el resumen, las alertas, los repositorios y las propuestas de un GitHub Actions Monitor, leídos con un token bearer sobre su API v1.
* **Decisiones a cuatro ojos:** aprueba o rechaza una propuesta tras ver su contenido exacto; la aprobación queda ligada al hash de ese contenido y el monitor la rechaza si la misma persona creó la propuesta.
* **Silenciosa por defecto:** las notificaciones push están desactivadas, son solo críticas, una vez por alerta, con un título corto y nada más.
* **Nada secreto en el repositorio:** el token se cifra con una clave del Keystore de Android; aquí no hay ningún archivo de Firebase, clave ni keystore.

## 🧩 Papel en EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID es la app Android de GitHub Actions Monitor (TOOLS-GITHUB-ACTIONS-MONITOR). Es un cliente: no calcula reglas propias sobre alertas o propuestas; el monitor decide qué es crítico y qué es una propuesta, y la app lo muestra y pregunta a una persona.

## 🏛️ Arquitectura

<p align="center">
  <img src="images/architecture.svg" alt="Arquitectura de TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: quién lo usa, qué contiene y de qué depende" width="100%">
</p>

Cómo encajan las partes de este repositorio: quién lo usa, qué contiene y de qué depende. La imagen se dibuja desde `docs/architecture.json` y se comprueba contra el código.

## ✨ Funciones

* Cinco pantallas: resumen, alertas (filtro por nivel, paginación con cursores), repositorios (los que tienen alertas y una búsqueda de cualquier repositorio), propuestas (pendientes o todas, con detalle) y ajustes.
* Aprobar o rechazar una propuesta tras una confirmación que nombra el hash del contenido; si indicas tu nombre en los ajustes, no se te ofrece aprobar una propuesta que creaste tú.
* Los enlaces `ghamon://proposal/<id>?hash=<hex>` abren una propuesta; si su contenido cambió desde que se hizo el enlace, no se ofrece aprobar.
* Registro del dispositivo para notificaciones push solo críticas, desactivadas por defecto.
* Ocho idiomas que siguen el idioma del móvil: inglés, español, alemán, francés, italiano, japonés, chino y portugués de Portugal.

## 🔒 Seguridad y protección

* El token se cifra con AES-GCM bajo una clave que vive en el Keystore de Android, nunca se escribe en un registro ni en un texto de error y solo se envía a la dirección escrita; nunca se siguen redirecciones.
* `http://` sin cifrar solo se acepta para localhost, nombres `.local` y direcciones IPv4 privadas; cualquier otro host necesita `https://`, comprobado solo contra las autoridades del sistema.
* Una aprobación lleva el hash del contenido que había en pantalla; un enlace solo abre una propuesta y nunca aprueba ni ejecuta nada.
* Las notificaciones push están desactivadas por defecto, son solo críticas y llevan un título corto y limpio: sin registros ni secretos de repositorios.
* Los datos de la app quedan fuera de las copias de seguridad, y `python -I tools/check_app.py` falla si hay un archivo de Firebase, un keystore o una clave en el árbol.

## 🔌 Interfaces

* Lecturas del monitor: `GET /api/v1/health`, `/status`, `/accounts`, `/accounts/{id}/status`, `/alerts` (paginación por cursor, filtros) y `/repos/{owner}/{repo}/health`, con `Authorization: Bearer` y `X-API-Version: 1`.
* Propuestas (rutas que la app supone, todas en el archivo de configuración): `GET /api/v1/actions/proposals`, `GET .../{id}`, `POST .../{id}/approve` con `expected_hash` y `POST .../{id}/reject`.
* Dispositivos: `POST /api/v1/devices` y `DELETE /api/v1/devices/{id}`.
* Enlace: `ghamon://proposal/<id>?hash=<64 caracteres hexadecimales>`.

## ⚙️ Configuración

Todo valor ajustable está en `app/src/main/assets/app_config.json`: las rutas, los tiempos, el tamaño de página, los valores de push y el esquema del enlace. La dirección del monitor, su token y tu nombre se escriben en los ajustes de la app y se guardan en el móvil. Nada ajustable está en el código.

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

69 pruebas unitarias se ejecutan en la JVM (`./gradlew testDebugUnitTest`) sin móvil: el cliente de la API contra una red simulada, las reglas, los enlaces, el filtro push y los ajustes. 11 pruebas de Python cubren el repositorio y las comprobaciones ligeras de la app. El CI las ejecuta todas; nada de aquí se ha ejecutado en un móvil.

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
│   ├── src/main/assets/app_config.json                    rutas, tiempos, página, valores de push
│   ├── src/main/res/                                      los textos en ocho idiomas, iconos y temas
│   ├── src/main/java/es/electrohobby3d/ghamonitor/core/   reglas probadas en la JVM
│   ├── src/main/java/es/electrohobby3d/ghamonitor/        pantallas, view model, Keystore, push
│   └── src/test/java/es/electrohobby3d/ghamonitor/core/   las pruebas unitarias en la JVM
├── tools/
│   ├── ci_validate.py                                     manifiesto, versiones, paridad de README
│   ├── check_app.py                                       idiomas, recursos, configuración y secretos de la app
│   └── make_architecture.py make_structure.py             dibujan las dos imágenes del README
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

* Versión preliminar: la app no se ha ejecutado en un móvil ni probado con un monitor real.
* Las rutas de propuestas y dispositivos que llama aún no están en el monitor.
* No hay servicio push: necesita un proyecto de Google y una clave que solo puede crear el mantenedor.
* Aún no hay confirmación biométrica antes de aprobar.

**Siguiente**

* Ejecutar el primer CI y corregir lo que encuentre, conectar las rutas del monitor y añadir el servicio push de Firebase cuando exista el proyecto de Google.

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

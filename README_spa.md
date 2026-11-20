<p align="center">
  <img src="images/TOOLS_GITHUB_ACTIONS_MONITOR_ANDROID_BANNER.svg" alt="TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID banner" width="100%">
</p>

# 🛠️ TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

<p align="center"><a href="README.md">🇺🇸 English</a> | 🇪🇸 <b>Español</b> | <a href="README_fra.md">🇫🇷 Français</a> | <a href="README_ita.md">🇮🇹 Italiano</a> | <a href="README_deu.md">🇩🇪 Deutsch</a> | <a href="README_zho.md">🇨🇳 中文</a> | <a href="README_jpn.md">🇯🇵 日本語</a> | <a href="README_por.md">🇵🇹 Português</a></p>

### App Android prevista de GitHub Actions Monitor: el estado de tus workflows y las alertas de tu propio monitor en el móvil. Solo andamiaje: todavía no hay nada implementado.

<p align="center">
  <img src="https://img.shields.io/badge/License-GPL%203.0--or--later-blue.svg" alt="GPL 3.0 or later">
  <img src="https://img.shields.io/badge/Stack-Kotlin%20%2B%20Jetpack%20Compose%20(planned)-blue.svg" alt="Kotlin and Jetpack Compose (planned)">
  <img src="https://img.shields.io/badge/Languages-8-00E5FF.svg" alt="Languages">
  <img src="https://img.shields.io/badge/Tests-none%20yet-lightgrey.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Version-0.0.1-0f6cbd.svg" alt="Version">
  <img src="https://img.shields.io/badge/Maturity-scaffolding-FFB020.svg" alt="Maturity">
</p>

---

**Comprobación de honestidad - lo que se ejecuta hoy:** nada de la app. Este repositorio solo contiene su base: los documentos, el manifiesto, la licencia y las comprobaciones compartidas del repositorio. No hay código, ni APK, ni pantalla, ni servicio de notificaciones, ni pruebas de la app, y no se ha probado nada en un móvil.

---

## 📋 De un vistazo

| | |
|---|---|
| Versión | `0.0.1` |
| Madurez | Andamiaje |
| Pruebas | Ninguna todavía (solo las comprobaciones del repositorio) |
| Pila tecnológica | Kotlin y Jetpack Compose (previsto) |
| En hardware real | No se ha ejecutado en un móvil |

**Ir a:** [Resumen](#-resumen) · [Arquitectura](#-arquitectura) · [Inicio rápido](#-inicio-rápido) · [Pruebas](#-pruebas) · [Estructura del repositorio](#-estructura-del-repositorio) · [Proyectos relacionados](#-proyectos-relacionados)

## 🚦 Estado del proyecto

Cada línea es una parte de este repositorio y la luz dice hasta dónde llega hoy. Se escribe con lo que el repositorio puede demostrar (pruebas, compilaciones, ejecuciones), no con lo previsto.

🟢 Hecho y verificado · 🟡 Funciona, con límites conocidos · 🟠 Hecho a medias o sin verificar · 🔴 Sin hacer o sin funcionar

| Área | Estado | Qué significa aquí |
|---|---|---|
| Base del repositorio | 🟡 | El manifiesto, los README en ocho idiomas, la licencia y las comprobaciones compartidas del repositorio se ejecutan en el CI. |
| Código de la app | 🔴 | Sin empezar: este repositorio aún no tiene ningún proyecto Android. |
| Pruebas | 🔴 | No hay pruebas de la app; solo se ejecutan las comprobaciones del repositorio. |
| Conexión con el monitor | 🔴 | Sin empezar. El monitor ofrece una API de lectura que un cliente puede usar; esta app todavía no la usa. |
| Móvil y notificaciones | 🔴 | Sin empezar y nunca ejecutada en un móvil; no hay ningún servicio de notificaciones conectado. |

## 🎯 Resumen

* **Qué será:** una app Android que muestra, en tu móvil, el estado de los workflows y las alertas de tu propio GitHub Actions Monitor.
* **Solo lectura:** solo leerá de tu monitor. No escribirá en GitHub ni cambiará nada en el monitor.
* **Tu monitor, tus datos:** hablará con el monitor que ejecutas tú mismo, con un token de acceso que le des; no hay ningún servicio nuestro por en medio.
* **Qué no es (todavía):** no hay app que instalar, ni release, ni fecha. Este repositorio solo reserva su sitio y cuenta con honestidad lo que está previsto.

## 🧩 Papel en EH3D

TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID pretende ser el cliente Android de GitHub Actions Monitor ([TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR)). Mostraría lo que el monitor ya sabe y no decidiría nada por su cuenta. Está previsto que una versión mínima para reloj, [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH), siga a esta app.

## 🏛️ Arquitectura

<p align="center">
  <img src="images/architecture.svg" alt="Arquitectura de TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID: quién lo usa, qué contiene y de qué depende" width="100%">
</p>

Cómo encajan las partes de este repositorio: quién lo usa, qué contiene y de qué depende. La imagen se dibuja desde `docs/architecture.json` y se comprueba contra el código.

## ✨ Funciones

**Previsto, y nada de ello existe todavía:**
* El resumen de tus workflows (qué está en rojo, qué está atascado, qué está esperando) y las alertas del monitor.
* Una lista de repositorios con su estado.
* Notificaciones opcionales para alertas importantes, desactivadas por defecto.
* Textos en ocho idiomas.

**No previsto:** escribir en GitHub, repetir workflows desde el móvil ni enviar tus datos a ningún servicio que no sea tu propio monitor.

## 🔒 Seguridad y protección

* Previsto: la app solo lee, y el token de acceso se queda en el móvil y se envía únicamente a la dirección de tu monitor.
* Nada secreto tiene cabida en este repositorio: ni claves, ni tokens, ni archivos de servicios de notificaciones, ni claves de firma.
* Son intenciones de diseño. Nada de ello existe todavía, así que nadie ha podido revisarlo.

## 🚀 Inicio rápido

Todavía no hay nada que instalar ni ejecutar. Para comprobar la base de este repositorio necesitas Python 3.10 o posterior:

```bash
python -I tools/ci_validate.py
python -I tools/make_architecture.py --check
python -I tools/make_structure.py --check --readmes
python -I -m unittest discover -s tools -p "test_*.py"
```

## 🧪 Pruebas

El CI ejecuta las comprobaciones del repositorio y las pruebas de esas comprobaciones. La app no tiene pruebas propias porque aún no tiene código, y aquí no se afirma ningún contador de pruebas.

## 🏗️ Compilación y publicación

No hay compilación ni release, porque no existe ninguna versión de la app. La versión `0.0.1` de `VERSION`, del manifiesto y del registro de cambios es la de la base del repositorio.

## 📂 Estructura del repositorio

<p align="center">
  <img src="images/repository-structure.svg" alt="Estructura del repositorio TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID" width="100%">
</p>

<details>
<summary>Versión en texto del árbol</summary>

```text
MONITOR-ANDROID/
├── tools/
│   ├── ci_validate.py                           manifiesto, versiones, paridad de README
│   └── make_architecture.py make_structure.py   dibujan las dos imágenes del README
├── docs/                                        las fuentes de las dos imágenes
├── images/                                      banner y las dos imágenes
├── .github/                                     flujos de CI y plantillas
├── VERSION                                      la versión del repositorio
├── electro-hobby-3d.project.json                el manifiesto del proyecto
├── CHANGELOG.md                                 qué cambió en cada versión
├── SECURITY.md SUPPORT.md                       cómo informar de una vulnerabilidad y dónde preguntar
└── CONTRIBUTING.md CODE_OF_CONDUCT.md LICENSE   cómo contribuir, el código de conducta y la licencia
```

</details>

## 🗺️ Hoja de ruta y límites conocidos

**Límites conocidos hoy**

* No hay código: ni app, ni APK, ni pantalla.
* No se ha probado nada en un móvil ni con un monitor real.
* El alcance final depende de la API de lectura del monitor y puede cambiar.

**Siguiente**

* Decidir las primeras pantallas, construir la app paso a paso con sus pruebas en el CI y solo entonces describirla aquí como funcional.

## 🌍 Idiomas

El README y los textos existen en inglés, español, francés, italiano, alemán, chino, japonés y portugués de Portugal.

## 🔗 Proyectos relacionados

* [TOOLS-GITHUB-ACTIONS-MONITOR](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR): el monitor que esta app pretende leer
* [TOOLS-GITHUB-ACTIONS-MONITOR-WATCH](https://github.com/JuanenRac/TOOLS-GITHUB-ACTIONS-MONITOR-WATCH): la versión mínima para reloj prevista que sigue a esta app
* [EH3D-DOCS](https://github.com/JuanenRac/EH3D-DOCS): las convenciones y las comprobaciones compartidas

## 📚 Documentación

* [Las convenciones de la suite](https://github.com/JuanenRac/EH3D-DOCS/blob/main/docs/CONVENTIONS.md)
* [Registro de cambios](CHANGELOG.md)

## 🤝 Contribuir y comunidad

* Antes de una pull request ejecuta `python -I tools/ci_validate.py`; las demás comprobaciones se ejecutan en el CI. Consulta [CONTRIBUTING.md](CONTRIBUTING.md) y el [código de conducta](CODE_OF_CONDUCT.md).
* El README existe en ocho idiomas: un cambio en el texto se hace en los ocho archivos, y las dos imágenes se dibujan a partir de `docs/architecture.json` y `docs/structure.json` con `tools/make_architecture.py` y `tools/make_structure.py`.
* Informa de una vulnerabilidad en privado: [SECURITY.md](SECURITY.md). Preguntas, ideas e informes: electrohobby3d@gmail.com ([SUPPORT.md](SUPPORT.md)).

## 👤 AUTOR

**JuanenRac (Electro Hobby 3D)** · electrohobby3d@gmail.com

## 📜 LICENCIA

Software: GPL-3.0-or-later. Véase [LICENSE](LICENSE). Los diseños de hardware de la suite usan CERN OHL-S.

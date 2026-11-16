#!/usr/bin/env python3
# =============================================================================
# TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - tools/check_app.py
# Copyright (C) 2026 JuanenRac (Electro Hobby 3D) <electrohobby3d@gmail.com>
# GPL-3.0-or-later - see LICENSE
# =============================================================================
"""Light checks of the app that need no Gradle and no Android SDK (they run in a second on any machine):

  strings      the eight language folders (en, es, de, fr, it, ja, zh, pt) hold exactly the same keys, with the same %n$s placeholders, and every string is well-formed XML
  resources    every R.string.<name> the Kotlin code uses exists, and every string is used somewhere
  config       app/src/main/assets/app_config.json has every endpoint the code calls, under /api/v1/, and the push defaults are the conservative ones (off, critical only)
  secrets      no Firebase config, keystore or key file is in the tree, and no file under app/ carries a token-looking value
  manifest     the version in the manifest, in VERSION and in app/build.gradle.kts is the same

Prints one line per check and ends with APP_CHECK=PASS or APP_CHECK=FAIL. Exit code: 0 pass, 1 fail, 64 wrong use."""
from __future__ import annotations

import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "app" / "src" / "main" / "res"
KOTLIN = ROOT / "app" / "src" / "main" / "java"
LANG_FOLDERS = ("values", "values-es", "values-de", "values-fr", "values-it", "values-ja", "values-zh", "values-pt")
REQUIRED_ENDPOINTS = (
    "health", "status", "accounts", "account_status", "alerts", "repo_health",
    "proposals", "proposal", "proposal_approve", "proposal_reject", "devices", "device",
)
PLACEHOLDER = re.compile(r"%(\d+)\$[sd]")
FORBIDDEN_NAMES = ("google-services.json", "GoogleService-Info.plist")
FORBIDDEN_SUFFIXES = (".jks", ".keystore", ".p12", ".pem", ".key")


def load_strings(folder: str) -> dict[str, str]:
    path = RES / folder / "strings.xml"
    tree = ET.parse(path)  # raises ParseError on bad XML: the check reports it
    return {item.attrib["name"]: (item.text or "") for item in tree.getroot().findall("string")}


def check_strings(problems: list[str]) -> dict[str, str]:
    base: dict[str, str] = {}
    for folder in LANG_FOLDERS:
        try:
            current = load_strings(folder)
        except (OSError, ET.ParseError) as error:
            problems.append(f"strings: {folder}/strings.xml cannot be read: {error}")
            continue
        if folder == "values":
            base = current
            continue
        missing = sorted(set(base) - set(current))
        extra = sorted(set(current) - set(base))
        if missing:
            problems.append(f"strings: {folder} lacks {', '.join(missing[:8])}")
        if extra:
            problems.append(f"strings: {folder} has keys that values/ lacks: {', '.join(extra[:8])}")
        for key in sorted(set(base) & set(current)):
            if set(PLACEHOLDER.findall(base[key])) != set(PLACEHOLDER.findall(current[key])):
                problems.append(f"strings: {folder}/{key} has other placeholders than values/")
            if re.search(r"(?<!\\)'", current[key]):
                problems.append(f"strings: {folder}/{key} has an apostrophe that is not escaped")
            if not current[key].strip():
                problems.append(f"strings: {folder}/{key} is empty")
    if not base:
        problems.append("strings: values/strings.xml has no strings")
    return base


def check_resources(problems: list[str], names: set[str]) -> None:
    used: set[str] = set()
    for path in KOTLIN.rglob("*.kt"):
        used.update(re.findall(r"R\.string\.([A-Za-z0-9_]+)", path.read_text(encoding="utf-8")))
    for path in [ROOT / "app" / "src" / "main" / "AndroidManifest.xml", *RES.rglob("*.xml")]:
        if path.name != "strings.xml":
            used.update(re.findall(r"@string/([A-Za-z0-9_]+)", path.read_text(encoding="utf-8")))
    for name in sorted(used - names):
        problems.append(f"resources: R.string.{name} is used in the code but has no string")
    for name in sorted(names - used):
        problems.append(f"resources: string {name} is never used")


def check_config(problems: list[str]) -> None:
    path = ROOT / "app" / "src" / "main" / "assets" / "app_config.json"
    try:
        config = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        problems.append(f"config: app_config.json cannot be read: {error}")
        return
    endpoints = config.get("endpoints", {})
    for name in REQUIRED_ENDPOINTS:
        value = endpoints.get(name)
        if not isinstance(value, str) or not value.startswith("/api/v1/"):
            problems.append(f"config: endpoint {name} is missing or not under /api/v1/")
    push = config.get("push", {})
    if push.get("enabled_by_default") is not False:
        problems.append("config: push.enabled_by_default must be false")
    if push.get("only_level") != "critical":
        problems.append("config: push.only_level must be critical")


def check_secrets(problems: list[str]) -> None:
    for path in ROOT.rglob("*"):
        if not path.is_file() or ".git" in path.parts or "build" in path.parts or ".gradle" in path.parts:
            continue
        if path.name in FORBIDDEN_NAMES or path.suffix in FORBIDDEN_SUFFIXES:
            problems.append(f"secrets: {path.relative_to(ROOT).as_posix()} must not be in the repository")
    pattern = re.compile(r"(?i)(ghp_|github_pat_|AIza[0-9A-Za-z_-]{20,}|-----BEGIN [A-Z ]*PRIVATE KEY-----)")
    for base in (ROOT / "app", ROOT / "docs"):
        for path in base.rglob("*"):
            if path.is_file() and "build" not in path.parts and path.suffix in (".kt", ".xml", ".json", ".kts", ".md", ".pro"):
                if pattern.search(path.read_text(encoding="utf-8", errors="replace")):
                    problems.append(f"secrets: {path.relative_to(ROOT).as_posix()} looks like it holds a key or token")


def check_version(problems: list[str]) -> None:
    manifest = json.loads((ROOT / "electro-hobby-3d.project.json").read_text(encoding="utf-8"))["version"]
    version = (ROOT / "VERSION").read_text(encoding="utf-8").strip()
    gradle = (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
    match = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
    if not (match and match.group(1) == manifest == version):
        problems.append(f"manifest: versions differ (manifest {manifest}, VERSION {version}, gradle {match.group(1) if match else '?'})")


def main(argv: list[str]) -> int:
    if len(argv) > 1 and argv[1] in ("-h", "--help"):
        print(__doc__)
        return 0
    if len(argv) > 1:
        print("usage: check_app.py", file=sys.stderr)
        return 64
    problems: list[str] = []
    names = set(check_strings(problems))
    check_resources(problems, names)
    check_config(problems)
    check_secrets(problems)
    check_version(problems)
    for line in problems:
        print("  FAIL", line)
    print(f"APP_CHECK={'FAIL' if problems else 'PASS'} ({len(names)} strings x {len(LANG_FOLDERS)} languages)")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))

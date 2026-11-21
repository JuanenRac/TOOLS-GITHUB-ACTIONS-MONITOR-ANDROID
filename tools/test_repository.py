# =============================================================================
# TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - tools/test_repository.py
# Copyright (C) 2026 JuanenRac (Electro Hobby 3D) <electrohobby3d@gmail.com>
# GPL-3.0-or-later - see LICENSE
# =============================================================================
"""The base of the repository: the version is the same everywhere it is written, and the eight README files exist."""
from __future__ import annotations

import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
README_FILES = ("README.md", "README_spa.md", "README_fra.md", "README_ita.md", "README_deu.md", "README_zho.md", "README_jpn.md", "README_por.md")


class BaseTests(unittest.TestCase):
    def test_the_version_is_the_same_in_the_manifest_the_version_file_and_the_changelog(self) -> None:
        manifest = json.loads((ROOT / "electro-hobby-3d.project.json").read_text(encoding="utf-8"))
        self.assertEqual((ROOT / "VERSION").read_text(encoding="utf-8").strip(), manifest["version"])
        newest = re.search(r"(?m)^## \[(\d[^\]]*)\]", (ROOT / "CHANGELOG.md").read_text(encoding="utf-8"))
        self.assertIsNotNone(newest)
        self.assertEqual(newest.group(1), manifest["version"])

    def test_the_manifest_names_the_folder_of_the_repository(self) -> None:
        manifest = json.loads((ROOT / "electro-hobby-3d.project.json").read_text(encoding="utf-8"))
        self.assertEqual(manifest["name"], ROOT.name)

    def test_the_eight_readme_files_exist_and_show_the_version(self) -> None:
        version = (ROOT / "VERSION").read_text(encoding="utf-8").strip()
        for name in README_FILES:
            text = (ROOT / name).read_text(encoding="utf-8")
            self.assertIn(f"`{version}`", text, name)


class ApkWorkflowTests(unittest.TestCase):
    """The manual APK build: it names the real module, application id and manifest, holds no signing key and prepares its draft in the right repository."""

    def setUp(self) -> None:
        self.text = (ROOT / ".github" / "workflows" / "apk-build.yml").read_text(encoding="utf-8")

    def test_it_builds_the_app_module_and_names_the_application_id_of_the_gradle_file(self) -> None:
        gradle = (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        application_id = re.search(r'applicationId = "([^"]+)"', gradle)
        self.assertIsNotNone(application_id)
        self.assertIn(f"name='{application_id.group(1)}'", self.text)
        self.assertIn(":app:assembleRelease", self.text)
        self.assertIn("electro-hobby-3d.project.json", self.text)

    def test_it_is_manual_with_a_dry_run_by_default_and_has_no_signing_key(self) -> None:
        self.assertIn("workflow_dispatch:", self.text)
        self.assertNotIn("pull_request", self.text)
        self.assertRegex(self.text, r"(?s)dry_run:.*?default: true")
        self.assertNotRegex(self.text, r"(?i)secrets\.")
        self.assertNotIn("apksigner sign", self.text)

    def test_every_action_is_pinned_to_a_commit_and_the_draft_job_names_its_repository(self) -> None:
        for use in re.findall(r"(?m)^\s*uses:\s*(\S+)", self.text):
            self.assertRegex(use, r"@[0-9a-f]{40}$", use)
        draft = self.text.split("  draft:", 1)[1].split("  notify:", 1)[0]
        self.assertIn("GH_REPO: ${{ github.repository }}", draft)
        self.assertIn("environment: release", draft)


if __name__ == "__main__":
    unittest.main()

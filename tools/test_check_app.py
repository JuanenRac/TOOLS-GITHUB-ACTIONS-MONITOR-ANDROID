# =============================================================================
# TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - tools/test_check_app.py
# Copyright (C) 2026 JuanenRac (Electro Hobby 3D) <electrohobby3d@gmail.com>
# GPL-3.0-or-later - see LICENSE
# =============================================================================
"""The light checks of the app (tools/check_app.py) pass on this tree and fail on the mistakes they are meant to catch."""
from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import check_app  # noqa: E402


class CheckAppTests(unittest.TestCase):
    def test_the_tree_passes(self) -> None:
        self.assertEqual(check_app.main(["check_app.py"]), 0)

    def test_a_missing_language_key_is_reported(self) -> None:
        problems: list[str] = []
        original = check_app.load_strings

        def fake(folder: str) -> dict[str, str]:
            strings = original(folder)
            if folder == "values-ja":
                strings.pop("app_name")
            return strings

        check_app.load_strings = fake
        try:
            check_app.check_strings(problems)
        finally:
            check_app.load_strings = original
        self.assertTrue(any("values-ja lacks app_name" in line for line in problems), problems)

    def test_a_changed_placeholder_is_reported(self) -> None:
        problems: list[str] = []
        original = check_app.load_strings

        def fake(folder: str) -> dict[str, str]:
            strings = original(folder)
            if folder == "values-de":
                strings["summary_updated"] = "Aktualisiert"
            return strings

        check_app.load_strings = fake
        try:
            check_app.check_strings(problems)
        finally:
            check_app.load_strings = original
        self.assertTrue(any("values-de/summary_updated has other placeholders" in line for line in problems), problems)

    def test_an_unescaped_apostrophe_is_reported(self) -> None:
        problems: list[str] = []
        original = check_app.load_strings

        def fake(folder: str) -> dict[str, str]:
            strings = original(folder)
            if folder == "values-fr":
                strings["empty_list"] = "Rien d'affiché"
            return strings

        check_app.load_strings = fake
        try:
            check_app.check_strings(problems)
        finally:
            check_app.load_strings = original
        self.assertTrue(any("values-fr/empty_list has an apostrophe" in line for line in problems), problems)

    def test_the_push_defaults_are_the_conservative_ones(self) -> None:
        problems: list[str] = []
        check_app.check_config(problems)
        self.assertEqual(problems, [])


if __name__ == "__main__":
    unittest.main()

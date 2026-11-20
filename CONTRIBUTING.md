# Contributing to TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID

Thank you for looking. This repository is at its first steps, so a contribution is most useful when it says plainly what it does and what has been tried.

1. **Say what runs and what is only planned.** Something counts as working on a phone only when it has been run on one; a unit test that passes on the JVM is described as that.
2. **Before a pull request:** `python -I tools/ci_validate.py`, `python -I tools/check_app.py` and the tests of the tools (`python -I -m unittest discover -s tools -p "test_*.py"`) must pass. The Gradle build, the unit tests and lint run in the CI.
3. **Every text of the app exists in eight languages** (English, Spanish, German, French, Italian, Japanese, Chinese and European Portuguese) in `app/src/main/res/values*/strings.xml`, with the same keys and the same placeholders; the README files keep the same structure.
4. **Tuned values live in `app/src/main/assets/app_config.json`**, never in the code, and no secret, Firebase file or keystore is ever committed.
5. Commits are written in English, small and about one thing.

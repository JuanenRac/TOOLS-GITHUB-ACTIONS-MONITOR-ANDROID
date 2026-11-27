#!/usr/bin/env python3
# =============================================================================
# TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - tools/contract/record.py
# Copyright (C) 2026 JuanenRac (Electro Hobby 3D) <electrohobby3d@gmail.com>
# GPL-3.0-or-later - see LICENSE
# =============================================================================
"""Records what demo monitors answer to the calls of the app, or checks that the recorded answers still have the shape a monitor gives.

    python record.py OUT_FOLDER                 write one JSON file per call: {"request", "status", "body"}
    python record.py --check FOLDER --host 127.0.0.1   ask again and compare: same status, and every key of the recorded answer still there with the same kind of value

It expects four monitors on this computer (see .github/workflows/ci.yml): 19801 demo_server.py (individual), 19802 --team, 19803 --team --no-bearer, 19804 tools/demo_accounts.py
of the monitor (several accounts). Ends with CONTRACT=PASS or CONTRACT=FAIL."""
from __future__ import annotations

import json
import sys
import urllib.error
import urllib.request
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from demo_server import ACCESS_TOKEN, PASSPHRASE, USER  # noqa: E402

PORTS = (19801, 19802, 19803, 19804)
ONE = TEAM = NO_BEARER = MANY = ""
LONGEST = 8      # items of a list that are kept in a recorded answer


def call(base, path, token=ACCESS_TOKEN, session="", body=None, host=""):
    headers = {"Accept": "application/json"}
    if token:
        headers["X-Monitor-Access"] = token
    if session:
        headers["Authorization"] = "Bearer " + session
    if host:
        headers["Host"] = host
    data = None
    if body is not None:
        headers["Content-Type"] = "application/json"
        data = json.dumps(body).encode("utf-8")
    request = urllib.request.Request(base + path, data=data, headers=headers)
    try:
        with urllib.request.urlopen(request, timeout=30) as answer:
            status, raw = answer.status, answer.read()
    except urllib.error.HTTPError as error:
        status, raw = error.code, error.read()
    try:
        return status, json.loads(raw)
    except ValueError:
        return status, None


def answers():
    """{name: (request, status, body)} of every call, in the order the app makes them."""
    out = {}

    def take(name, base, path, **options):
        status, body = call(base, path, **options)
        out[name] = (("POST " if options.get("body") is not None else "GET ") + path, status, body)
        return body

    take("auth_me", ONE, "/api/auth/me")
    take("health", ONE, "/api/health")
    take("alerts", ONE, "/api/alerts")
    take("team_health", ONE, "/api/team/health")
    take("proposals", ONE, "/api/ai/actions")
    take("accounts_single", ONE, "/api/accounts")
    take("access_refused", ONE, "/api/alerts", token="not-the-token-of-the-demo")
    take("host_refused", ONE, "/api/alerts", host="other.example:19801")
    take("auth_me_team", TEAM, "/api/auth/me")
    take("sign_in_needed", TEAM, "/api/alerts")
    take("login_wrong", TEAM, "/api/auth/login", body={"username": USER, "password": "not the passphrase", "client": "api"})
    signed = take("login", TEAM, "/api/auth/login", body={"username": USER, "password": PASSPHRASE, "client": "api"})
    session = (signed or {}).get("token", "")
    take("auth_me_signed_in", TEAM, "/api/auth/me", session=session)
    status, _body = call(TEAM, "/api/alerts", session=session)
    if status != 200:
        raise SystemExit(f"CONTRACT=FAIL the signed-in session cannot read the alerts (status {status})")
    take("logout", TEAM, "/api/auth/logout", session=session, body={})
    take("login_bearer_off", NO_BEARER, "/api/auth/login", body={"username": USER, "password": PASSPHRASE, "client": "api"})
    take("health_accounts", MANY, "/api/health", token="")
    take("accounts", MANY, "/api/accounts", token="")
    return out


def trimmed(value):
    if isinstance(value, list):
        return [trimmed(v) for v in value[:LONGEST]]
    if isinstance(value, dict):
        return {k: trimmed(v) for k, v in value.items()}
    return value


def redacted(name, body):
    """A session token of the demo opens nothing, but a recorded answer carries a made-up one all the same."""
    if name == "login" and isinstance(body, dict):
        return {**body, "token": "msn_example-session-of-the-demo-000000", "csrf": "example"}
    if name == "auth_me_signed_in" and isinstance(body, dict):
        return {**body, "csrf": "example"}
    return body


def differences(label, kept, fresh):
    """The keys and kinds of `kept` that `fresh` does not have (a key may be added, never lost). A null on either side says nothing about the kind."""
    if kept is None or fresh is None:
        return []
    if isinstance(kept, dict):
        if not isinstance(fresh, dict):
            return [f"{label}: was an object, is {type(fresh).__name__}"]
        out = []
        for key, value in kept.items():
            # by_level, by_sentinel and by_ecosystem are counted by whatever there is: their keys are data, not shape
            if key not in fresh:
                if not label.endswith(("by_level", "by_sentinel", "by_ecosystem")):
                    out.append(f"{label}.{key}: the key is gone")
            else:
                out += differences(f"{label}.{key}", value, fresh[key])
        return out
    if isinstance(kept, list):
        if not isinstance(fresh, list):
            return [f"{label}: was a list, is {type(fresh).__name__}"]
        if kept and not fresh:
            return [f"{label}: had items, now empty"]
        return differences(label + "[0]", kept[0], fresh[0]) if kept else []
    number = (int, float)
    if isinstance(kept, bool) != isinstance(fresh, bool) or not (isinstance(fresh, type(kept)) or (isinstance(kept, number) and isinstance(fresh, number))):
        return [f"{label}: was {type(kept).__name__}, is {type(fresh).__name__}"]
    return []


def point_at(host):
    global ONE, TEAM, NO_BEARER, MANY
    ONE, TEAM, NO_BEARER, MANY = (f"http://{host}:{port}" for port in PORTS)


def main(argv):
    host = "127.0.0.1"
    if len(argv) > 3 and argv[-2] == "--host":
        host, argv = argv[-1], argv[:-2]
    point_at(host)
    check = len(argv) == 3 and argv[1] == "--check"
    if not check and len(argv) != 2:
        print(__doc__, file=sys.stderr)
        return 64
    folder = Path(argv[-1])
    fresh = answers()
    if not check:
        folder.mkdir(parents=True, exist_ok=True)
        for name, (request, status, body) in fresh.items():
            record = {"request": request, "status": status, "body": trimmed(redacted(name, body))}
            (folder / f"{name}.json").write_text(json.dumps(record, indent=1, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")
            print(name, status)
        print("CONTRACT=PASS recorded", len(fresh))
        return 0
    problems = []
    for name, (_request, status, body) in fresh.items():
        path = folder / f"{name}.json"
        if not path.is_file():
            problems.append(f"{name}: there is no recorded answer")
            continue
        kept = json.loads(path.read_text(encoding="utf-8"))
        if kept["status"] != status:
            problems.append(f"{name}: status {status}, recorded {kept['status']}")
        else:
            problems += differences(name, kept["body"], body)
    problems += [f"{path.stem}: recorded but no longer asked" for path in folder.glob("*.json") if path.stem not in fresh]
    for line in problems:
        print("  FAIL", line)
    print("CONTRACT=" + ("FAIL" if problems else "PASS") + f" ({len(fresh)} answers)")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))

#!/usr/bin/env python3
# =============================================================================
# TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - tools/contract/demo_server.py
# Copyright (C) 2026 JuanenRac (Electro Hobby 3D) <electrohobby3d@gmail.com>
# GPL-3.0-or-later - see LICENSE
# =============================================================================
"""The REAL demo monitor (tools/demo_monitor.py of TOOLS-GITHUB-ACTIONS-MONITOR, invented data only) switched to what a phone meets: the network mode with an access token, and
optionally the team mode with one user. For the CI of this app: nothing is read from GitHub and everything it writes goes to temporary folders. It listens on 127.0.0.1 only
(an Android emulator reaches it as 10.0.2.2).

    python demo_server.py MONITOR_CHECKOUT PORT [--team] [--no-bearer]

The token, the user and the password below are invented for the demo and open nothing anywhere else."""
from __future__ import annotations

import sys
import tempfile
from pathlib import Path

ACCESS_TOKEN = "demo-access-0123456789"
USER = "demo-admin"
PASSPHRASE = "demo-passphrase-2468"


def main() -> int:
    if len(sys.argv) < 3:
        print(__doc__, file=sys.stderr)
        return 64
    checkout, port = Path(sys.argv[1]).resolve(), int(sys.argv[2])
    team, bearer = "--team" in sys.argv, "--no-bearer" not in sys.argv
    sys.path.insert(0, str(checkout / "tools"))
    sys.argv = ["demo_monitor.py", str(port), "--parts"]
    import demo_monitor  # noqa: PLC0415 - found through the path above

    plain_server = demo_monitor.ThreadingHTTPServer

    class NetworkServer(plain_server):
        """Made at the very end of the setup of the demo: the access rules are put in place just before it listens."""

        def __init__(self, address, handler):
            from github_actions_monitor.security import auth, localguard
            am = demo_monitor.am
            am.LIVE_CONFIG["listen"] = {"network": True, "host": "127.0.0.1", "allowed_hosts": ["10.0.2.2"]}
            localguard.configure(True, "127.0.0.1", ACCESS_TOKEN, ["10.0.2.2"])
            am.LIVE_CONFIG["session_guard"] = True
            if team:
                from github_actions_monitor.storage import factory
                folder = Path(tempfile.mkdtemp(prefix="ghamon_contract_"))
                am.LIVE_CONFIG["auth"] = {"enabled": True, "allow_bearer": bearer}
                auth.AUDIT_FILE = folder / "auth_audit.jsonl"
                auth._storage["obj"] = factory.create_storage({"path": str(folder)})
                _user, code = auth.bootstrap_admin(auth.get_storage(), USER, PASSPHRASE)
                if code:
                    raise SystemExit("the demo user could not be made: " + code)
            super().__init__(address, handler)

    demo_monitor.ThreadingHTTPServer = NetworkServer
    demo_monitor.main()
    return 0


if __name__ == "__main__":
    sys.exit(main())

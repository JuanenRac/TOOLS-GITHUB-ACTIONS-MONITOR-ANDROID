#!/usr/bin/env bash
# TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - starts the four demo monitors of the contract (ports 19801 to 19804) from a checkout of the monitor and waits until they answer.
#   bash tools/contract/start.sh MONITOR_CHECKOUT LOG_FOLDER
set -euo pipefail
monitor="$1"
logs="$2"
here="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$logs"
nohup python3 "$here/demo_server.py" "$monitor" 19801 >"$logs/one.log" 2>&1 &
nohup python3 "$here/demo_server.py" "$monitor" 19802 --team >"$logs/team.log" 2>&1 &
nohup python3 "$here/demo_server.py" "$monitor" 19803 --team --no-bearer >"$logs/nobearer.log" 2>&1 &
nohup python3 "$monitor/tools/demo_accounts.py" 19804 >"$logs/many.log" 2>&1 &
for port in 19801 19802 19803 19804; do
  ok=0
  for _ in $(seq 1 90); do
    code="$(curl -s -o /dev/null -w '%{http_code}' -H 'X-Monitor-Access: demo-access-0123456789' "http://127.0.0.1:$port/api/health" || true)"
    if [ "$code" = "200" ]; then ok=1; break; fi
    sleep 1
  done
  if [ "$ok" != "1" ]; then
    echo "no monitor on port $port"
    tail -n 60 "$logs"/*.log
    exit 1
  fi
  echo "monitor on port $port answers"
done

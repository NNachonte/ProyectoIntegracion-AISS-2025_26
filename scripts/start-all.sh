#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT/logs"
PID_FILE="$ROOT/.service-pids.txt"

if [[ -z "${JAVA_HOME:-}" ]]; then
  java_bin="$(command -v java 2>/dev/null || true)"
  if [[ -n "$java_bin" ]]; then
    if command -v readlink >/dev/null 2>&1; then
      java_real="$(readlink -f "$java_bin" 2>/dev/null || true)"
    elif command -v realpath >/dev/null 2>&1; then
      java_real="$(realpath "$java_bin" 2>/dev/null || true)"
    else
      java_real="$java_bin"
    fi

    if [[ -n "$java_real" ]]; then
      JAVA_HOME="$(cd "$(dirname "$(dirname "$java_real")")" && pwd)"
      export JAVA_HOME
    fi
  fi
fi

SERVICES=("VideoMiner" "DailyMotionMiner" "PeerTubeMiner" "TwitchMiner")

mkdir -p "$LOG_DIR"
: > "$PID_FILE"

for svc in "${SERVICES[@]}"; do
  service_dir="$ROOT/$svc"
  mvnw="$service_dir/mvnw"

  if [[ ! -x "$mvnw" ]]; then
    echo "Missing mvnw in $service_dir" >&2
    exit 1
  fi

  out_log="$LOG_DIR/${svc}.out.log"
  err_log="$LOG_DIR/${svc}.err.log"

  (cd "$service_dir" && bash -c 'exec -a ./mvnw bash -s -- spring-boot:run' < <(tr -d '\r' < "$mvnw") >"$out_log" 2>"$err_log") &
  echo "$svc:$!" >> "$PID_FILE"
 done

echo "Started services. PIDs saved to $PID_FILE"
echo "Logs: $LOG_DIR"

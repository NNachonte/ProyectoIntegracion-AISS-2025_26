#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_FILE="$ROOT/.service-pids.txt"
UNAME_S="$(uname -s)"
PSH=""

if [[ "$UNAME_S" =~ (MINGW|MSYS|CYGWIN) ]]; then
  if [[ -x /c/Windows/System32/WindowsPowerShell/v1.0/powershell.exe ]]; then
    PSH="/c/Windows/System32/WindowsPowerShell/v1.0/powershell.exe"
  elif command -v powershell.exe >/dev/null 2>&1; then
    PSH="powershell.exe"
  elif command -v pwsh >/dev/null 2>&1; then
    PSH="pwsh"
  fi
fi

kill_by_pattern_windows() {
  local root_win
  if command -v cygpath >/dev/null 2>&1; then
    root_win="$(cygpath -w "$ROOT")"
  else
    root_win="$ROOT"
  fi

  if [[ -z "$PSH" ]]; then
    echo "PowerShell not found. Cannot stop services by pattern." >&2
    return 1
  fi

  cmd.exe /c "\"$PSH\" -NoProfile -Command \"\$root='$root_win'; \$repo=Split-Path -Leaf \$root; \$targets=@('VideoMiner','DailyMotionMiner','PeerTubeMiner','TwitchMiner'); \$procs=Get-CimInstance -ClassName Win32_Process -ErrorAction SilentlyContinue; foreach (\$p in \$procs) { if (-not \$p.CommandLine) { continue }; if (\$p.CommandLine -notlike ('*' + \$repo + '*')) { continue }; if (\$p.CommandLine -notlike '*spring-boot:run*') { continue }; foreach (\$t in \$targets) { if (\$p.CommandLine -like ('*' + \$t + '*')) { Stop-Process -Id \$p.ProcessId -Force -ErrorAction SilentlyContinue; break } } }\"" > /dev/null 2>&1 || true
  cmd.exe /c "\"$PSH\" -NoProfile -Command \"\$ports=@(8080,8081,8082,8083); \$pids=(Get-NetTCPConnection -LocalPort \$ports -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess) | Select-Object -Unique; foreach (\$pid in \$pids) { Stop-Process -Id \$pid -Force -ErrorAction SilentlyContinue }\"" > /dev/null 2>&1 || true
}

kill_by_pattern_unix() {
  local repo_name
  repo_name="$(basename "$ROOT")"
  pkill -f "${repo_name}.*/(VideoMiner|DailyMotionMiner|PeerTubeMiner|TwitchMiner).*spring-boot:run" 2>/dev/null || true
  for port in 8080 8081 8082 8083; do
    pid="$(lsof -ti tcp:"$port" 2>/dev/null || true)"
    if [[ -n "$pid" ]]; then
      kill -9 $pid 2>/dev/null || true
    fi
  done
}

if [[ ! -f "$PID_FILE" ]]; then
  echo "No PID file found. Stopping services by pattern."
  if [[ "$UNAME_S" =~ (MINGW|MSYS|CYGWIN) ]]; then
    kill_by_pattern_windows
  else
    kill_by_pattern_unix
  fi
  echo "All services stopped."
  exit 0
fi

while IFS= read -r line; do
  pid="${line#*:}"
  if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
    echo "Stopping PID $pid"
    kill "$pid" >/dev/null 2>&1 || true
  fi
 done < "$PID_FILE"

rm -f "$PID_FILE"
if [[ "$UNAME_S" =~ (MINGW|MSYS|CYGWIN) ]]; then
  kill_by_pattern_windows || true
else
  kill_by_pattern_unix
fi
echo "All services stopped."

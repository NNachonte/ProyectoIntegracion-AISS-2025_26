$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$pidFile = Join-Path $root ".service-pids.json"

if (!(Test-Path $pidFile)) {
    Write-Host "No PID file found. Nothing to stop."
    exit 0
}

$entries = Get-Content $pidFile -Raw | ConvertFrom-Json
$entries = @($entries)

foreach ($entry in $entries) {
    if (-not $entry.Pid) { continue }

    $running = Get-Process -Id $entry.Pid -ErrorAction SilentlyContinue
    if ($null -eq $running) {
        Write-Host "Process already stopped: $($entry.Name) (PID $($entry.Pid))"
        continue
    }

    Write-Host "Stopping $($entry.Name) (PID $($entry.Pid))"
    & taskkill /PID $entry.Pid /T /F | Out-Null
}

Remove-Item $pidFile -Force
Write-Host "All services stopped."

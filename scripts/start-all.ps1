$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$logDir = Join-Path $root "logs"
$pidFile = Join-Path $root ".service-pids.json"

if (-not $env:JAVA_HOME) {
    $javaHomeLine = (& java -XshowSettings:properties -version 2>&1 | Select-String '^\s*java\.home = ' | Select-Object -First 1).ToString()
    if ($javaHomeLine -match 'java\.home = (.+)$') {
        $env:JAVA_HOME = $Matches[1].Trim()
    }
}

$services = @(
    @{ Name = "VideoMiner"; Path = (Join-Path $root "VideoMiner") },
    @{ Name = "DailyMotionMiner"; Path = (Join-Path $root "DailyMotionMiner") },
    @{ Name = "PeerTubeMiner"; Path = (Join-Path $root "PeerTubeMiner") },
    @{ Name = "TwitchMiner"; Path = (Join-Path $root "TwitchMiner") }
)

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
if (Test-Path $pidFile) {
    Remove-Item $pidFile -Force
}

$processes = @()

foreach ($service in $services) {
    $mvnw = Join-Path $service.Path "mvnw.cmd"
    if (!(Test-Path $mvnw)) {
        Write-Error "Missing mvnw.cmd in $($service.Path)"
        exit 1
    }

    $outLog = Join-Path $logDir "$($service.Name).out.log"
    $errLog = Join-Path $logDir "$($service.Name).err.log"

    $proc = Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "mvnw.cmd spring-boot:run" -WorkingDirectory $service.Path -RedirectStandardOutput $outLog -RedirectStandardError $errLog -PassThru -WindowStyle Hidden

    $processes += [pscustomobject]@{
        Name = $service.Name
        Pid = $proc.Id
        Path = $service.Path
        OutLog = $outLog
        ErrLog = $errLog
        StartedAt = (Get-Date).ToString("o")
    }
}

$processes | ConvertTo-Json -Depth 3 | Set-Content -Path $pidFile -Encoding UTF8
Write-Host "Started services. PIDs saved to $pidFile"
Write-Host "Logs: $logDir"

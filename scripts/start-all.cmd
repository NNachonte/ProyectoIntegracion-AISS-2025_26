@echo off
setlocal

set "ROOT=%~dp0.."
set "LOG_DIR=%ROOT%\logs"
set "PID_FILE=%ROOT%\.service-pids.txt"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if exist "%PID_FILE%" del /f /q "%PID_FILE%"
type nul > "%PID_FILE%"

call :startService VideoMiner
call :startService DailyMotionMiner
call :startService PeerTubeMiner
call :startService TwitchMiner

echo Started services. PIDs saved to %PID_FILE%
echo Logs: %LOG_DIR%
exit /b 0

:startService
set "SERVICE=%~1"
set "SERVICE_DIR=%ROOT%\%SERVICE%"
set "MVNW=%SERVICE_DIR%\mvnw.cmd"

if not exist "%MVNW%" (
  echo Missing mvnw.cmd in %SERVICE_DIR%
  exit /b 1
)

set "OUT_LOG=%LOG_DIR%\%SERVICE%.out.log"
set "ERR_LOG=%LOG_DIR%\%SERVICE%.err.log"

powershell -NoProfile -Command "$ErrorActionPreference='Stop'; $pidFile='%PID_FILE%'; $proc=Start-Process -FilePath 'cmd.exe' -ArgumentList '/c','mvnw.cmd spring-boot:run' -WorkingDirectory '%SERVICE_DIR%' -RedirectStandardOutput '%OUT_LOG%' -RedirectStandardError '%ERR_LOG%' -WindowStyle Hidden -PassThru; Add-Content -Path $pidFile -Value ('%SERVICE%:' + $proc.Id)" 1>nul 2>nul
if errorlevel 1 (
  echo Failed to start %SERVICE%. Check logs in %LOG_DIR%.
)
exit /b 0

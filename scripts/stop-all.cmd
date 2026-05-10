@echo off
setlocal

set "ROOT=%~dp0.."
set "PID_FILE=%ROOT%\.service-pids.txt"

if not exist "%PID_FILE%" (
  echo No PID file found. Trying to stop running services by command line.
  call :killByPattern
  exit /b 0
)

set "PID_COUNT=0"
set "ZERO_PIDS=0"
for /f "usebackq tokens=1,2 delims=:" %%a in ("%PID_FILE%") do (
  set /a PID_COUNT+=1
  if "%%b"=="0" set "ZERO_PIDS=1"
)

for /f "usebackq tokens=1,2 delims=:" %%a in ("%PID_FILE%") do (
  if not "%%b"=="0" (
    echo Stopping %%a (PID %%b)
    taskkill /PID %%b /T /F >nul 2>&1
  )
)

del /f /q "%PID_FILE%"
if "%PID_COUNT%"=="3" if "%ZERO_PIDS%"=="0" exit /b 0
call :killByPattern
echo All services stopped.
exit /b 0

:killByPattern
setlocal
set "ROOT_ESC=%ROOT:\=\\%"
for /f "delims=" %%p in ('powershell -NoProfile -Command "$root='%ROOT_ESC%'; $repoName=Split-Path -Path $root -Leaf; $targets=@('VideoMiner','DailyMotionMiner','PeerTubeMiner'); $procs=Get-CimInstance -ClassName Win32_Process -ErrorAction SilentlyContinue; foreach ($p in $procs) { if (-not $p.CommandLine) { continue }; if ($p.CommandLine -notlike ('*' + $repoName + '*')) { continue }; if ($p.CommandLine -notlike '*spring-boot:run*') { continue }; foreach ($t in $targets) { if ($p.CommandLine -like ('*' + $t + '*')) { $p.ProcessId; break } } }"') do (
  echo Stopping PID %%p
  taskkill /PID %%p /T /F >nul 2>&1
)
endlocal
exit /b 0

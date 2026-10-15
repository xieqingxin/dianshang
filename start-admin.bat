@echo off
setlocal
cd /d "%~dp0admin"

echo Starting admin console on http://localhost:5174 ...
call npm run dev
endlocal
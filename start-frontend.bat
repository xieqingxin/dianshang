@echo off
setlocal
cd /d "%~dp0frontend"

echo Starting frontend mall on http://localhost:5173 ...
call npm run dev
endlocal
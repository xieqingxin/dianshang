@echo off
setlocal
set REDIS_DIR=D:\soft\Redis

if not exist "%REDIS_DIR%\redis-server.exe" (
    echo [ERROR] Redis not found at %REDIS_DIR%
    echo Please edit REDIS_DIR in this script to your Redis install path.
    pause
    exit /b 1
)

echo Starting Redis from %REDIS_DIR% ...
cd /d "%REDIS_DIR%"
redis-server.exe
endlocal
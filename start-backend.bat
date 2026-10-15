@echo off
setlocal
cd /d "%~dp0backend"

set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

echo Starting backend (Spring Boot) on http://localhost:8080/api ...
call mvn spring-boot:run
endlocal
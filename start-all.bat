@echo off
echo Launching all services in separate windows...
echo NOTE: Make sure MySQL is running first. Edit start-redis.bat if Redis path differs.

start "Redis" cmd /k "%~dp0start-redis.bat"
start "Backend" cmd /k "%~dp0start-backend.bat"
start "Frontend" cmd /k "%~dp0start-frontend.bat"
start "Admin" cmd /k "%~dp0start-admin.bat"

echo Done. Visit:
echo   Frontend: http://localhost:5173
echo   Admin:    http://localhost:5174
echo   API docs: http://localhost:8080/api/doc.html
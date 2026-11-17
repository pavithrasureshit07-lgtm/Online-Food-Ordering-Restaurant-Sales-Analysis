@echo off
setlocal
if "%DB_URL%"=="" set DB_URL=jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
if "%DB_USER%"=="" set DB_USER=root
if "%DB_PASSWORD%"=="" (
  echo Set DB_PASSWORD before starting. Example: set DB_PASSWORD=your_password
  exit /b 1
)
java -jar "%~dp0restaurant-management.jar"
endlocal

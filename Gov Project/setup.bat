@echo off
REM Janmanrega Dashboard Windows Setup Script
REM This script helps set up the development environment on Windows

echo 🚀 Starting Janmanrega Dashboard Setup...

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo Please install Java 17 or higher from https://adoptium.net/
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo ✅ Java and Maven are installed

REM Build the application
echo 📦 Building the application...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Build failed
    pause
    exit /b 1
)

echo ✅ Application built successfully

REM Check if PostgreSQL is running (optional check)
echo 📊 Checking PostgreSQL connection...
echo Note: Make sure PostgreSQL is installed and running
echo Default connection: localhost:5432/janmanrega

REM Create logs directory
if not exist "logs" mkdir logs

echo.
echo 🎉 Setup completed successfully!
echo.
echo 📋 Next steps:
echo 1. Install PostgreSQL if not already installed
echo 2. Create database 'janmanrega' in PostgreSQL
echo 3. Update application.yml with your database credentials
echo 4. Run: java -jar target/dashboard-0.0.1-SNAPSHOT.jar
echo.
echo 🌐 Application will be available at: http://localhost:8080
echo 📊 Health check: http://localhost:8080/actuator/health
echo.
pause

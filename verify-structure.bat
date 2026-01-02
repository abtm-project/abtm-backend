@echo off
REM ABTM Backend - Project Structure Verification Script
REM This script checks if all required files are in place

echo ===============================================================
echo    ABTM Backend - Structure Verification
echo ===============================================================
echo.

SET ERROR_COUNT=0

REM Check if we're in the project directory
echo [1/10] Checking project directory...
if exist pom.xml (
    echo [OK] Found pom.xml
) else (
    echo [ERROR] pom.xml not found! Are you in the project directory?
    SET /A ERROR_COUNT+=1
)
echo.

REM Check src directory
echo [2/10] Checking src directory structure...
if exist src\main\java (
    echo [OK] src\main\java exists
) else (
    echo [ERROR] src\main\java directory not found!
    SET /A ERROR_COUNT+=1
)
echo.

REM Check package structure
echo [3/10] Checking com.abtm package structure...
if exist src\main\java\com\abtm (
    echo [OK] Package structure exists
) else (
    echo [ERROR] src\main\java\com\abtm directory not found!
    SET /A ERROR_COUNT+=1
)
echo.

REM Check main application class
echo [4/10] Checking AbtmApplication.java...
if exist src\main\java\com\abtm\AbtmApplication.java (
    echo [OK] AbtmApplication.java found
) else (
    echo [ERROR] AbtmApplication.java not found!
    echo        Expected location: src\main\java\com\abtm\AbtmApplication.java
    SET /A ERROR_COUNT+=1
)
echo.

REM Check controller
echo [5/10] Checking controller package...
if exist src\main\java\com\abtm\controller\ScenarioController.java (
    echo [OK] ScenarioController.java found
) else (
    echo [WARN] ScenarioController.java not found
    echo       Location: src\main\java\com\abtm\controller\ScenarioController.java
)
echo.

REM Check model classes
echo [6/10] Checking model package...
if exist src\main\java\com\abtm\model\User.java (
    echo [OK] User.java found
) else (
    echo [WARN] User.java not found
)
if exist src\main\java\com\abtm\model\Scenario.java (
    echo [OK] Scenario.java found
) else (
    echo [WARN] Scenario.java not found
)
echo.

REM Check service
echo [7/10] Checking service package...
if exist src\main\java\com\abtm\service\ScenarioAnalyzer.java (
    echo [OK] ScenarioAnalyzer.java found
) else (
    echo [WARN] ScenarioAnalyzer.java not found
)
echo.

REM Check repository
echo [8/10] Checking repository package...
if exist src\main\java\com\abtm\repository (
    echo [OK] Repository package exists
) else (
    echo [WARN] Repository package not found
)
echo.

REM Check resources
echo [9/10] Checking resources...
if exist src\main\resources\application.properties (
    echo [OK] application.properties found
) else (
    echo [ERROR] application.properties not found!
    echo        Expected location: src\main\resources\application.properties
    SET /A ERROR_COUNT+=1
)
echo.

REM Check Java and Maven
echo [10/10] Checking Java and Maven installation...
java -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Java is installed
    java -version 2>&1 | findstr "version"
) else (
    echo [ERROR] Java not found! Please install Java 11.
    SET /A ERROR_COUNT+=1
)

mvn -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven is installed
    mvn -version 2>&1 | findstr "Apache Maven"
) else (
    echo [ERROR] Maven not found! Please install Maven.
    SET /A ERROR_COUNT+=1
)
echo.

REM Summary
echo ===============================================================
echo    Verification Summary
echo ===============================================================
if %ERROR_COUNT% EQU 0 (
    echo [SUCCESS] All critical files are in place!
    echo.
    echo Next steps:
    echo   1. Run: mvn clean install
    echo   2. Run: mvn spring-boot:run
    echo   3. Test: .\test-api.ps1
) else (
    echo [FAILED] Found %ERROR_COUNT% critical error(s)
    echo.
    echo Please fix the errors above before building.
    echo See TROUBLESHOOTING.md for detailed help.
)
echo ===============================================================
echo.
pause

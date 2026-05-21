@echo off
echo ============================================
echo   JavaFX Notepad - Build and Run
echo ============================================
echo Building...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo BUILD FAILED.
    pause
    exit /b 1
)
echo Build successful! Starting Notepad...
call mvn javafx:run
pause

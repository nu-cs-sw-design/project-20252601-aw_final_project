@echo off
REM Simple compilation script for Windows

echo Compiling Wardrobe Tracker...

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile the Java file
javac -d bin src\main\java\wardrobe\WardrobeApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the application, execute:
    echo   run.bat
) else (
    echo Compilation failed!
    exit /b 1
)


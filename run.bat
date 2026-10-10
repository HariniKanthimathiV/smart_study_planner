@echo off
cd /d "%~dp0"
echo Compiling Study Planner...
javac StudyPlanner.java
if errorlevel 1 (
  echo.
  echo Compilation failed. Make sure a JDK is installed and javac is on PATH.
  pause
  exit /b 1
)
echo Starting Study Planner...
java StudyPlanner
pause

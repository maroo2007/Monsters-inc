@echo off
cd /d "%~dp0"

:: ── Copy FXML and CSS resources to bin ───────────────────────────────────────
if not exist bin\game\gui\fxml md bin\game\gui\fxml
if not exist bin\game\gui\css  md bin\game\gui\css
copy /y src\game\gui\fxml\*.fxml bin\game\gui\fxml\ >nul
copy /y src\game\gui\css\*.css   bin\game\gui\css\  >nul

:: ── Locate Java runtime ───────────────────────────────────────────────────────
:: Prefer JAVA_HOME if set, otherwise fall back to whatever "java" is on PATH.
if defined JAVA_HOME (
    set JAVA="%JAVA_HOME%\bin\java.exe"
) else (
    set JAVA=java
)

:: ── Classpath ─────────────────────────────────────────────────────────────────
set CP=bin;lib\jfxrt.jar;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar

%JAVA% -cp "%CP%" game.gui.Main
pause

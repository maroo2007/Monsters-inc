@echo off
cd /d "%~dp0"

if not exist bin\game\gui\fxml md bin\game\gui\fxml
if not exist bin\game\gui\css  md bin\game\gui\css
copy /y src\game\gui\fxml\*.fxml bin\game\gui\fxml\ >nul
copy /y src\game\gui\css\*.css   bin\game\gui\css\  >nul

set NATIVE=lib\jfx-native

if exist "e:\bellsoft-jre8\jre8u432-full\bin\java.exe" (
    set JAVA="e:\bellsoft-jre8\jre8u432-full\bin\java.exe"
) else if defined JAVA_HOME (
    set JAVA="%JAVA_HOME%\bin\java.exe"
) else (
    set JAVA=java
)

set CP=bin;lib\jfxrt.jar;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar

%JAVA% -Djava.library.path="%NATIVE%" -cp "%CP%" game.gui.Main
pause

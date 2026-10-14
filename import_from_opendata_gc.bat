@echo off
setlocal
chcp 65001 >nul
pushd "%~dp0" || exit /b 1
"C:\java\software\jdk-21.0.11\bin\java.exe" -Xms14g -Xmx14g -jar "%~dp0import_from_opendata_gc.jar"
set "IMPORT_EXIT_CODE=%ERRORLEVEL%"
popd
if /I not "%~1"=="--no-pause" pause
exit /b %IMPORT_EXIT_CODE%

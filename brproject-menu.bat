@echo off
setlocal
cd /d "%~dp0"
title Brproject - Build

:menu
cls
echo.
echo   ========================================
echo      Brproject - Build
echo   ========================================
echo.
echo   [1] Compilar normal (Java + Kotlin)
echo   [2] Clean + compilar
echo   [3] Ant: Mount.xml dist-test
echo   [0] Sair
echo.
rem choice: teclas 1, 2, 3, 0  -  ERRORLEVEL 1..4 (testar do maior para o menor)
choice /C 1230 /N /M "Digite a opcao: "

if errorlevel 4 goto :sair
if errorlevel 3 goto :ant_dist
if errorlevel 2 goto :clean_build
if errorlevel 1 goto :compile

:compile
echo.
echo --- Compilando... ---
call "%~dp0gradlew.bat" br-compile
goto :apos

:clean_build
echo.
echo --- Clean + compilar... ---
call "%~dp0gradlew.bat" br-compile-clean
goto :apos

:ant_dist
echo.
echo --- Ant dist-test... ---
call "%~dp0gradlew.bat" br-ant-dist-test
goto :apos

:apos
echo.
pause
goto :menu

:sair
echo.
endlocal
exit /b 0

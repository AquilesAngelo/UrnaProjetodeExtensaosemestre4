@echo off
REM Gera uma pasta com UrnaV3.exe que roda SEM instalar Java.
REM Antes: no IntelliJ, Maven > Lifecycle > clean e depois package (gera target\urna.jar).
REM Requer JDK 14 ou superior no computador onde este script é executado.

if not exist target\urna.jar (
    echo ERRO: target\urna.jar nao encontrado. Rode "package" no Maven do IntelliJ primeiro.
    pause
    exit /b 1
)

if exist build-input rmdir /s /q build-input
if exist dist rmdir /s /q dist
mkdir build-input
copy target\urna.jar build-input\ >nul

jpackage --type app-image --input build-input --name UrnaV3 --main-jar urna.jar --main-class urna.Main --dest dist
if errorlevel 1 (
    echo ERRO ao executar o jpackage. Confira se o JDK e 14 ou superior ^(java -version^).
    pause
    exit /b 1
)

xcopy fotos dist\UrnaV3\fotos /E /I /Y >nul

echo.
echo Pronto: pasta dist\UrnaV3
echo Compacte essa pasta em .zip para levar ao outro computador.
echo LEMBRETE: coloque o firebase-chave.json ao lado do UrnaV3.exe (nao inclua no GitHub).
pause

@echo off
@REM Copyright 2018-present, Yudong (Dom) Wang
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.

@REM -----------------------------------------------------------------------------
@REM Start script for the Holer Client
@REM -----------------------------------------------------------------------------

setlocal enabledelayedexpansion
set errorlevel=
title holer-client

set HOLER_MAIN=org.holer.client.ClientContainer
set HOLER_LIB_JARS=""
set HOLER_HOME=%~dp0
set HOLER_LOG_DIR=!HOLER_HOME!\..\logs

cd "!HOLER_HOME!"
cd ..\lib
for %%i in (*) do set HOLER_LIB_JARS=!HOLER_LIB_JARS!;..\lib\%%i
cd ..\bin

if not exist "!HOLER_LOG_DIR!" (
    mkdir "!HOLER_LOG_DIR!"
)

@REM Check if Java is correctly installed and set
java -version 1>nul 2>nul
if !errorlevel! neq 0 (
    @echo.
    @echo Please install Java 1.7 or higher and make sure the Java is set correctly.
    @echo.
    @echo You can execute command [ java -version ] to check if Java is correctly installed and set.
    @echo.

    pause
    goto:eof
)

@echo.
@echo Started holer client.
@echo.
@echo The holer client is running.
@echo Please do not close the current window.
@echo.

java -Dapp.home=../ -Xms64m -Xmx1024m -classpath ..\conf;%HOLER_LIB_JARS% %HOLER_MAIN%

@echo Stopped holer client.
@echo.

pause
goto:eof

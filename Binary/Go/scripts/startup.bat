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
@REM Holer Startup
@REM -----------------------------------------------------------------------------
title holer-client
setlocal enabledelayedexpansion
set errorlevel=

set HOLER_OK=0
set HOLER_ERR=1

set HOLER_HOME=%~dp0
set HOLER_BIN=holer-windows-amd64.exe
set HOLER_LOG_DIR=!HOLER_HOME!\logs
set HOLER_LOG=!HOLER_LOG_DIR!\holer-client.log
set HOLER_LINE=------------------------------------------
set HOLER_CONF=!HOLER_HOME!\holer.conf

@REM Create logs directory
if not exist "!HOLER_LOG_DIR!" (
    mkdir "!HOLER_LOG_DIR!"
)

@REM Set HOLER ENV
if exist "!HOLER_CONF!" (
    for /f "usebackq eol=# delims== tokens=1,*" %%i in ("!HOLER_CONF!") do set %%i=%%j
)

@REM Asking for the HOLER_ACCESS_KEY
if "!HOLER_ACCESS_KEY!" equ "" (
    @echo !HOLER_LINE!
    set /p HOLER_ACCESS_KEY="Enter holer access key: "
    if "!HOLER_ACCESS_KEY!" == "" (
        @echo Please enter holer access key
        @echo !HOLER_LINE!
        pause
        exit /b !HOLER_ERR!
    )
    @echo HOLER_ACCESS_KEY=!HOLER_ACCESS_KEY!> "!HOLER_CONF!"
)

@REM Asking for the HOLER_SERVER_HOST
if "!HOLER_SERVER_HOST!" equ "" (
    @echo !HOLER_LINE!
    set /p HOLER_SERVER_HOST="Enter holer server host: "
    if "!HOLER_SERVER_HOST!" == "" (
        @echo Please enter holer server host
        @echo !HOLER_LINE!
        pause
        exit /b !HOLER_ERR!
    )
    @echo HOLER_SERVER_HOST=!HOLER_SERVER_HOST!>> "!HOLER_CONF!"
)

@echo !HOLER_LINE!
@echo Starting holer client...

start /b /min !HOLER_BIN! -k !HOLER_ACCESS_KEY! -s !HOLER_SERVER_HOST! >> "!HOLER_LOG!"
timeout /T 3 /NOBREAK

@echo !HOLER_LINE!
tasklist | findstr !HOLER_BIN!

if !errorlevel! equ 0 (
    @echo !HOLER_LINE!
    @echo Started holer client.
    @echo.
    @echo The holer client is running.
    @echo Please do not close the current window.
    @echo !HOLER_LINE!
) else (
    @echo Holer client is stopped.
    @echo Please check the log file for details [ !HOLER_LOG! ]
    @echo !HOLER_LINE!
)

pause
goto:eof

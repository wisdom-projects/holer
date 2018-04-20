@echo off
@rem Copyright 2018-present, Yudong (Dom) Wang
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.

@rem -----------------------------------------------------------------------------
@rem Start script for the Holer Client
@rem -----------------------------------------------------------------------------

setlocal enabledelayedexpansion
set errorlevel=

title holer-client
cd %~dp0

set HOLER_MAIN=org.holer.client.HolerClientContainer
set HOLER_LIB_JARS=""

cd ..\lib
for %%i in (*) do set HOLER_LIB_JARS=!HOLER_LIB_JARS!;..\lib\%%i
cd ..\bin

@echo.
@echo Started holer client.
@echo.
@echo The holer client is running.
@echo Please do not close the current window.
@echo.

java -Dapp.home=../ -Xms64m -Xmx1024m -classpath ..\conf;%HOLER_LIB_JARS% %HOLER_MAIN%

@echo Stopped holer client.
@echo.

goto:eof

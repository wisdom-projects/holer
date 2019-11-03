' Copyright 2018-present, Yudong (Dom) Wang
'
' Licensed under the Apache License, Version 2.0 (the "License");
' you may not use this file except in compliance with the License.
' You may obtain a copy of the License at
'
'      http://www.apache.org/licenses/LICENSE-2.0
'
' Unless required by applicable law or agreed to in writing, software
' distributed under the License is distributed on an "AS IS" BASIS,
' WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
' See the License for the specific language governing permissions and
' limitations under the License.

'---------------------------------------------------
' HOLER STARTUP.VBS
'---------------------------------------------------
Dim JAVA_BIN
Dim HOLER_WSH
Dim HOLER_FSO
Dim HOLER_CMD
Dim HOLER_LINE
Dim HOLER_HOME
Dim HOLER_LOG
Dim HOLER_LOG_DIR
Dim HOLER_CONTENTS

Dim HOLER_ACCESS_KEY
Dim HOLER_SERVER_HOST

Set HOLER_FSO = CreateObject("Scripting.FileSystemObject")
Set HOLER_WSH = CreateObject("WScript.Shell")
Set HOLER_ENV = HOLER_WSH.Environment("USER")

JAVA_BIN = "javaw"

HOLER_ACCESS_KEY = HOLER_ENV("HOLER_ACCESS_KEY")
HOLER_SERVER_HOST = HOLER_ENV("HOLER_SERVER_HOST")
HOLER_HOME = HOLER_ENV("HOLER_HOME")

If HOLER_HOME = Empty Then
    MsgBox "Please set HOLER_HOME"
    WScript.Quit
End If

HOLER_APP = HOLER_HOME & "holer-client.jar"
HOLER_LOG_DIR = HOLER_HOME & "logs"
HOLER_LOG = HOLER_LOG_DIR & "\holer-client.log"
HOLER_LINE = "------------------------------------------"
HOLER_CONF = HOLER_HOME & "conf\holer.conf"

'---------------------------------------------------
' Input parameters
'---------------------------------------------------
InputParam

'---------------------------------------------------
' Create logs directory
'---------------------------------------------------
If Not HOLER_FSO.folderExists(HOLER_LOG_DIR) Then
    HOLER_FSO.CreateFolder HOLER_LOG_DIR
End If

'---------------------------------------------------
' Launch holer program
'---------------------------------------------------
LaunchHoler

'---------------------------------------------------
' Function - Ask user to input parameters
'---------------------------------------------------
Function InputParam()
    If HOLER_ACCESS_KEY = Empty Then
        HOLER_ACCESS_KEY = InputBox("Enter holer access key")
        If HOLER_ACCESS_KEY = Empty Then
            MsgBox "Please enter holer access key"
            WScript.Quit
        End If
        HOLER_ENV("HOLER_ACCESS_KEY") = HOLER_ACCESS_KEY
        HOLER_CONTENTS = "HOLER_ACCESS_KEY=" & HOLER_ACCESS_KEY & vbCrLf
    End If

    If HOLER_SERVER_HOST = Empty Then
        HOLER_SERVER_HOST = InputBox("Enter holer server host")
        If HOLER_SERVER_HOST = Empty Then
            MsgBox "Please enter holer server host"
            WScript.Quit
        End If
        HOLER_ENV("HOLER_SERVER_HOST") = HOLER_SERVER_HOST
        HOLER_CONTENTS = HOLER_CONTENTS & "HOLER_SERVER_HOST=" & HOLER_SERVER_HOST
        WriteFile HOLER_CONF, HOLER_CONTENTS
    End If
End Function

'---------------------------------------------------
' Write contents to file
'---------------------------------------------------
Function WriteFile(file, contents)
    Dim OutStream, FSO

    on error resume Next
    Set FSO = CreateObject("Scripting.FileSystemObject")
    Set OutStream = FSO.OpenTextFile(file, 2, True)

    OutStream.Write contents
    OutStream.close
End Function

'---------------------------------------------------
' Function - Launch holer program
'---------------------------------------------------
Function LaunchHoler()
    '---------------------------------------------------
    ' Launch holer daemon
    '---------------------------------------------------
    HOLER_CMD = "cmd.exe /c " & JAVA_BIN & " -jar """ & HOLER_APP & """ >> """ & HOLER_LOG & """"
    HOLER_WSH.Run HOLER_CMD, 0, False

    '---------------------------------------------------
    ' Find holer daemon
    '---------------------------------------------------
    HOLER_CMD = "cmd.exe /c echo Starting holer client... & timeout /T 3 /NOBREAK & echo " & HOLER_LINE & " & echo The running holer client: & tasklist | findstr " & JAVA_BIN & " & echo " & HOLER_LINE & " & pause"
    HOLER_WSH.Run HOLER_CMD, 1, True
End Function

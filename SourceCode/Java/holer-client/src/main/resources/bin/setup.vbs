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
' SETUP.VBS
'---------------------------------------------------
Dim HOLER_FSO
Dim HOLER_WSH
Dim HOLER_ENV
Dim HOLER_HOME
Dim HOLER_ACCESS_KEY
Dim HOLER_SERVER_HOST
Dim HOLER_VBS_FILE
Dim HOLER_BOOT_DIR
Dim HOLER_CONTENTS

Set HOLER_FSO = CreateObject("Scripting.FileSystemObject")
Set HOLER_WSH = CreateObject("WScript.Shell")
Set HOLER_ENV = HOLER_WSH.Environment("USER")

HOLER_VBS_FILE = "holer.vbs"
HOLER_BOOT_DIR = "C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp\"
HOLER_HOME = HOLER_FSO.GetFolder("..\").Path & "\"
HOLER_CONF = HOLER_HOME & "conf\holer.conf"

'---------------------------------------------------
' Input parameters
'---------------------------------------------------
InputParam

'---------------------------------------------------
' Set HOLER ENV
'---------------------------------------------------
HOLER_ENV("HOLER_HOME") = HOLER_HOME
HOLER_ENV("HOLER_ACCESS_KEY") = HOLER_ACCESS_KEY
HOLER_ENV("HOLER_SERVER_HOST") = HOLER_SERVER_HOST

HOLER_CONTENTS = "HOLER_ACCESS_KEY=" & HOLER_ACCESS_KEY & vbCrLf & "HOLER_SERVER_HOST=" & HOLER_SERVER_HOST
WriteFile HOLER_CONF, HOLER_CONTENTS

'---------------------------------------------------
' Set startup
'---------------------------------------------------
HOLER_FSO.CopyFile HOLER_VBS_FILE, HOLER_BOOT_DIR

MsgBox("Done")
WScript.Quit

'---------------------------------------------------
' Ask user to input parameters
'---------------------------------------------------
Function InputParam()
    HOLER_ACCESS_KEY = InputBox("Enter holer access key")
    If HOLER_ACCESS_KEY = Empty Then
        MsgBox "Please enter holer access key"
        WScript.Quit
    End If

    HOLER_SERVER_HOST = InputBox("Enter holer server host")
    If HOLER_SERVER_HOST = Empty Then
        MsgBox "Please enter holer server host"
        WScript.Quit
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

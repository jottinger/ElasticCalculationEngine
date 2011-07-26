@ECHO OFF

echo %CD%
echo %~dp0

if not "%GSHOME%" == "" goto :gsdefined
echo Please define the GSHOME environment variable.
goto :exit

:gsdefined
set GSCLASSPATH=%GSHOME%\lib\required\*
set BASECLASSPATH=%~dp0..\ece-client\target\ece-client-1.0.jar
echo %BASECLASSPATH% 
java -cp "%GSCLASSPATH%;%BASECLASSPATH%" org.openspaces.ece.client.ConsoleClient %*

:exit

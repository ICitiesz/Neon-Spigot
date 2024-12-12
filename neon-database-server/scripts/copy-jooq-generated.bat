@echo off

set SOURCE_PATH=%CD%/../build/generated/jooq/com/islandstudio/neon/stable/core/database/schema
set DESTINATION_PATH=%CD%/../../neon-kotlin/src/main/com/islandstudio/neon/stable/core/database/schema

for %%I in ("%SOURCE_PATH%") do set "SOURCE_PATH=%%~fI"
for %%I in ("%DESTINATION_PATH%") do set "DESTINATION_PATH=%%~fI"

echo %SOURCE_PATH%

if exist "%DESTINATION_PATH%" rmdir /s /q "%DESTINATION_PATH%"

xcopy "%SOURCE_PATH%" "%DESTINATION_PATH%" /Y /E /I /Q
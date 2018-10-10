@echo off
cls
call mvn clean install
call mvn dependency:copy-dependencies
pause
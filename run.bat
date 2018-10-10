@echo off
cls
call java -cp "target/classes/;target/dependency/*" com.przemekm.coreservicesapp.Main
pause
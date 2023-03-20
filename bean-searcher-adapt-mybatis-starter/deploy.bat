@echo off
chcp 65001

cmd /k mvn clean deploy -Dmaven.test.skip=true
pause

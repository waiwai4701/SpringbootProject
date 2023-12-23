@echo off
rem ======================================================================
rem windows startup script
rem
rem author: geekidea
rem date: 2018-12-2
rem ======================================================================

rem Open in a browser
start "" "http://127.0.0.1:8081/login?username=admin&password=123456"

rem startup jar
java -jar ../boot/SpringbootProject.jar --spring.config.location=../config/

pause

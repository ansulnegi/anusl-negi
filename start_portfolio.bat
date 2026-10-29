@echo off
title Ansul - Full Stack Java Portfolio
echo ==========================================================
echo    Compiling and Launching Java Portfolio Server...
echo ==========================================================
echo.

javac PortfolioServer.java
if %errorlevel% neq 0 (
    echo [ERROR] Java compilation failed! Please make sure Java JDK is installed.
    pause
    exit /b %errorlevel%
)

echo [SUCCESS] Compiled PortfolioServer.java successfully!
echo Opening browser at http://localhost:8080 ...
start http://localhost:8080

echo.
echo Starting Java Server on port 8080...
echo (Keep this terminal window open during your interview / demo)
echo Press Ctrl+C to stop the server anytime.
echo.
java PortfolioServer
pause


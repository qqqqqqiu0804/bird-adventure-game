@echo off
echo ========================================
echo   飞翔小鸟冒险版 - 打包工具
echo ========================================
echo.

REM 创建输出目录
if not exist "dist" mkdir dist

REM 编译Java文件
echo [1/4] 正在编译Java文件...
javac -encoding UTF-8 cn/edu/huse/game/*.java
if errorlevel 1 (
    echo 编译失败！
    pause
    exit /b 1
)
echo 编译成功！

REM 复制资源文件
echo [2/4] 正在复制资源文件...
xcopy /E /Y cn\edu\huse\game\*.png dist\cn\edu\huse\game\ >nul
xcopy /E /Y cn\edu\huse\game\*.wav dist\cn\edu\huse\game\ >nul 2>nul

REM 创建MANIFEST.MF
echo [3/4] 正在创建MANIFEST.MF...
echo Main-Class: cn.edu.huse.game.BirdGame> dist\MANIFEST.MF
echo.>> dist\MANIFEST.MF

REM 创建JAR文件
echo [4/4] 正在创建JAR文件...
cd dist
jar cfm ../飞翔小鸟冒险版.jar MANIFEST.MF cn/
cd ..

echo.
echo ========================================
echo   打包完成！
echo   生成文件: 飞翔小鸟冒险版.jar
echo ========================================
echo.
echo 双击 飞翔小鸟冒险版.jar 即可运行游戏
echo.
pause

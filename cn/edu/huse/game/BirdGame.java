package cn.edu.huse.game;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * 主游戏类 - 飞翔小鸟冒险版
 */
public class BirdGame extends JPanel {
    Bird bird;
    Obstacle obstacle1;
    Obstacle obstacle2;
    List<Coin> coins;
    List<PowerUp> powerUps;
    BufferedImage backGround;
    BufferedImage ground;

    GameState state;
    Level currentLevel;
    ScoreManager scoreManager;
    SoundManager soundManager;
    ParticleSystem particles;
    Random random;

    // 新增管理器
    SkinManager skinManager;
    AchievementManager achievementManager;
    SceneManager sceneManager;
    UpgradeSystem upgradeSystem;
    ObstacleSkinManager obstacleSkinManager;  // 障碍物皮肤管理器
    SaveManager saveManager;                  // 存档管理器
    SettingsManager settingsManager;          // 设置管理器
    MusicManager musicManager;                // 背景音乐管理器
    TutorialManager tutorialManager;          // 教程管理器
    int screenAlpha = 0;                      // 屏幕透明度（用于淡入淡出）
    boolean showTutorial = false;             // 是否显示教程

    // 缓存字体
    Font scoreFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    Font levelFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
    Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);

    // 关卡选择相关
    int selectedLevel = 1;
    int skinScrollOffset = 0;
    int achievementScrollOffset = 0;
    int sceneScrollOffset = 0;

    // 按钮悬停检测
    int hoveredButton = -1;        // 当前悬停按钮索引
    Point lastMousePosition = null; // 最后鼠标位置

    // 生命值系统
    int currentHealth;
    int maxHealth;

    // 作弊系统
    boolean cheatMode = false;
    String cheatInput = "";
    boolean showCheatConsole = false;
    String cheatMessage = "";
    int cheatMessageTimer = 0;
    boolean godMode = false;
    boolean wasInfiniteMode = false;  // 记录死亡前是否在无限模式
    int savedCoins = 0;  // 作弊模式开启前的金币数

    /**
     * 构造方法
     */
    public BirdGame() throws Exception {
        bird = new Bird();
        obstacle1 = new Obstacle(1);
        obstacle2 = new Obstacle(2);
        coins = new ArrayList<>();
        powerUps = new ArrayList<>();
        backGround = ImageIO.read(getClass().getResource("bg.png"));
        ground = ImageIO.read(getClass().getResource("ground.png"));

        state = GameState.MAIN_MENU;
        currentLevel = new Level(1);
        scoreManager = new ScoreManager();
        particles = new ParticleSystem();
        random = new Random();

        // 初始化新增管理器
        skinManager = new SkinManager();
        achievementManager = new AchievementManager();
        sceneManager = new SceneManager();
        upgradeSystem = new UpgradeSystem();
        obstacleSkinManager = new ObstacleSkinManager();
        saveManager = new SaveManager();
        settingsManager = new SettingsManager();
        soundManager = new SoundManager(settingsManager);  // 传递设置管理器

        // 初始化新管理器
        musicManager = new MusicManager();
        tutorialManager = new TutorialManager();

        // 加载菜单背景音乐
        musicManager.loadMusic("menu_music.wav");

        // 加载教程完成状态
        tutorialManager.setCompleted(saveManager.isTutorialCompleted());

        // 检查是否首次启动
        if (tutorialManager.isFirstLaunch()) {
            showTutorial = true;
        } else {
            // 非首次启动，直接播放菜单音乐
            musicManager.play();
        }

        // 初始化生命值
        maxHealth = upgradeSystem.getMaxHealth();
        currentHealth = maxHealth;
    }

    /**
     * 游戏入口
     */
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("飞翔小鸟 - 冒险版");
        BirdGame game = new BirdGame();
        frame.add(game);
        frame.setSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        game.action();
    }

    /**
     * 绘制游戏画面
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.drawImage(backGround, 0, 0, null);

        // 绘制教程（在所有界面之上）
        if (showTutorial && !tutorialManager.isCompleted()) {
            tutorialManager.showTutorial(g);
            return; // 教程显示时，不绘制其他内容
        }

        // 根据游戏状态绘制界面
        switch (state) {
            case MAIN_MENU:
                drawMainMenu(g);
                break;
            case LEVEL_SELECT:
                drawLevelSelect(g);
                break;
            case SKIN_SELECT:
                drawSkinSelect(g);
                break;
            case SCENE_SELECT:
                drawSceneSelect(g);
                break;
            case ACHIEVEMENTS:
                drawAchievements(g);
                break;
            case UPGRADE:
                drawUpgrade(g);
                break;
            case START:
                drawStartScreen(g);
                break;
            case RUNNING:
            case INFINITE:
                drawGame(g);
                break;
            case GAME_OVER:
                drawGame(g);
                drawGameOverScreen(g);
                break;
            case REVIVE_CONFIRM:
                drawGame(g);
                drawReviveConfirmScreen(g);
                break;
            case LEVEL_COMPLETE:
                drawGame(g);
                drawLevelCompleteScreen(g);
                break;
            case PAUSED:
                drawGame(g);
                drawPausedScreen(g);
                break;
            case LEADERBOARD:
                drawLeaderboard(g);
                break;
            case OBSTACLE_SKIN:
                drawObstacleSkinSelect(g);
                break;
            case SETTINGS:
                drawSettings(g);
                break;
        }

        // 绘制淡入淡出效果
        drawFadeEffect(g);
    }

    /**
     * 绘制游戏画面
     */
    private void drawGame(Graphics g) {
        // 绘制障碍物（自适应高度）
        drawObstacle(g, obstacle1);
        drawObstacle(g, obstacle2);

        // 绘制金币
        for (Coin c : coins) {
            if (!c.isCollected()) {
                g.drawImage(c.image, c.x - c.size / 2, c.y - c.size / 2, null);
            }
        }

        // 绘制道具
        for (PowerUp p : powerUps) {
            if (!p.isCollected()) {
                g.drawImage(p.image, p.x - p.size / 2, p.y - p.size / 2, null);
            }
        }

        // 绘制地面
        g.drawImage(ground, 0, GameConfig.GROUND_Y, null);

        // 绘制小鸟（带旋转效果）
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(-bird.velocity * 0.05, bird.x, bird.y);
        g.drawImage(bird.image, bird.x - bird.width / 2, bird.y - bird.height / 2, null);
        g2.rotate(bird.velocity * 0.05, bird.x, bird.y);

        // 绘制护盾效果
        if (bird.hasShield) {
            drawShieldEffect(g);
        }

        // 绘制粒子特效
        particles.draw(g);

        // 绘制UI
        drawUI(g);
    }

    /**
     * 绘制护盾视觉效果
     */
    private void drawShieldEffect(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        long time = System.currentTimeMillis();

        // 护盾半径（比小鸟稍大）
        int shieldRadius = bird.size + 10;

        // 脉冲效果
        float pulse = (float)(Math.sin(time * 0.005) * 0.3 + 0.7);

        // 外层光晕（半透明紫色）
        g2.setColor(new Color(150, 50, 255, (int)(60 * pulse)));
        g2.fillOval(bird.x - shieldRadius - 5, bird.y - shieldRadius - 5,
                    (shieldRadius + 5) * 2, (shieldRadius + 5) * 2);

        // 内层护盾圈（更亮）
        g2.setColor(new Color(180, 100, 255, (int)(100 * pulse)));
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawOval(bird.x - shieldRadius, bird.y - shieldRadius,
                    shieldRadius * 2, shieldRadius * 2);

        // 重置画笔
        g2.setStroke(new java.awt.BasicStroke(1));
    }

    /**
     * 绘制自适应高度的障碍物（使用选中的皮肤）
     */
    private void drawObstacle(Graphics g, Obstacle obstacle) {
        int x = obstacle.x - obstacle.width / 2;
        int gapTop = obstacle.y - obstacle.gap / 2;
        int gapBottom = obstacle.y + obstacle.gap / 2;
        int w = obstacle.width;

        // 全局统一脉冲值（0~1），科技感更协调
        long time = System.currentTimeMillis();
        float pulse = (float)(Math.sin(time * 0.004) * 0.5 + 0.5);

        // 获取选中的障碍物皮肤
        ObstacleSkin skin = obstacleSkinManager.getSelectedSkin();

        // 绘制上障碍物
        skin.draw(g, x, 0, w, gapTop, pulse, true);

        // 绘制下障碍物
        skin.draw(g, x, gapBottom, w, GameConfig.GROUND_Y - gapBottom, pulse, false);
    }

    /**
     * 绘制单个太空站模块
     */
    private void drawStationModule(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 配色方案：上偏冷蓝灰，下偏深青灰
        int baseR = isTop ? 75 : 65;
        int baseG = isTop ? 85 : 80;
        int baseB = isTop ? 100 : 90;

        // 主体外壳（纯矩形，无波纹）
        g.setColor(new Color(baseR, baseG, baseB));
        g.fillRect(x, y, w, h);

        // 外壳边框 - 高光效果
        g.setColor(new Color(baseR + 70, baseG + 75, baseB + 80));
        g.drawLine(x, y, x, y + h);
        g.drawLine(x + w, y, x + w, y + h);
        g.setColor(new Color(baseR - 15, baseG - 15, baseB - 15));
        g.drawLine(x + 1, y, x + 1, y + h);
        g.drawLine(x + w - 1, y, x + w - 1, y + h);

        // 动态主光条（青蓝脉冲，最显眼）
        drawStationLightBar(g, x, y, w, h, pulse, isTop);

        // 窗户（弱脉冲发光）
        drawStationWindows(g, x, y, w, h, pulse);

        // 天线
        drawStationAntenna(g, x, y, w, isTop);
    }

    /**
     * 绘制动态主光条
     */
    private void drawStationLightBar(Graphics g, int x, int y, int w, int h, float pulse, boolean isTop) {
        // 光条位置：上模块在底部，下模块在顶部
        int lightY = isTop ? y + h - 12 : y;

        // 青蓝脉冲发光
        int glowG = (int)(200 * pulse);
        int glowB = (int)(255 * pulse);
        g.setColor(new Color(0, glowG, glowB));
        g.fillRect(x, lightY, w, 12);

        // 外层光晕
        g.setColor(new Color(0, glowG, glowB, 150));
        g.fillRect(x - 2, lightY - 2, w + 4, 16);
    }

    /**
     * 绘制窗户（整齐排列，弱脉冲）
     */
    private void drawStationWindows(Graphics g, int x, int y, int w, int h, float pulse) {
        // 窗户参数
        int winW = w / 2;
        int winH = 20;
        int winX = x + w / 4;
        int startY = y + 25;
        int spacing = 50;

        for (int wy = startY; wy < y + h - 30; wy += spacing) {
            // 窗户背景
            g.setColor(new Color(20, 30, 50));
            g.fillRect(winX, wy, winW, winH);

            // 窗户玻璃 - 统一脉冲，弱发光
            int glow = (int)(120 * pulse + 60);
            g.setColor(new Color(glow / 4, glow / 3, glow));
            g.fillRect(winX + 2, wy + 2, winW - 4, winH - 4);

            // 窗户边框
            g.setColor(new Color(180, 190, 210));
            g.drawRect(winX, wy, winW, winH);
        }
    }

    /**
     * 绘制天线
     */
    private void drawStationAntenna(Graphics g, int x, int y, int w, boolean isTop) {
        int antennaX = x + w / 2;

        if (isTop) {
            // 上天线（向上延伸）
            g.setColor(new Color(180, 180, 200));
            g.drawLine(antennaX, y, antennaX, y - 18);
            // 红色指示灯
            g.setColor(new Color(255, 50, 50));
            g.fillOval(antennaX - 4, y - 22, 8, 8);
        } else {
            // 下天线（向下延伸）
            g.setColor(new Color(180, 180, 200));
            g.drawLine(antennaX, y + 0, antennaX, y + 18);
            // 黄色指示灯
            g.setColor(new Color(255, 255, 50));
            g.fillOval(antennaX - 3, y + 14, 6, 6);
        }
    }

    /**
     * 绘制UI
     */
    private void drawUI(Graphics g) {
        // 关卡信息
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        if (state == GameState.INFINITE) {
            g.setColor(new Color(255, 100, 0));
            g.drawString("无限模式", 10, 30);
            g.setFont(smallFont);
            g.setColor(Color.CYAN);
            g.drawString("通过障碍: " + currentLevel.currentObstacles, 10, 55);
            g.setColor(Color.YELLOW);
            g.drawString("收集金币: " + currentLevel.coinsCollected, 10, 75);
            g.setColor(Color.WHITE);
            g.drawString("综合得分: " + (currentLevel.currentObstacles * 10 + currentLevel.coinsCollected * 5), 10, 95);
        } else {
            g.drawString("关卡: " + currentLevel.levelNumber, 10, 30);
            g.drawString(currentLevel.getProgress(), 10, 55);
            g.setFont(smallFont);
            g.drawString("本关得分: " + currentLevel.score, 10, 75);
            g.drawString("本关金币: " + currentLevel.coinsCollected, 10, 95);
        }

        // 金币信息
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("总金币: " + scoreManager.getTotalCoins(), 10, 120);

        // 生命值显示
        g.setColor(Color.RED);
        g.drawString("生命: " + currentHealth + "/" + maxHealth, 10, 140);

        // 速度加成显示
        g.setColor(Color.CYAN);
        g.drawString("速度加成: " + String.format("%.1f", upgradeSystem.getSpeedBonus()) + "x", 10, 160);

        // 道具状态
        int statusY = 180;
        if (bird.isInvincible) {
            g.setColor(Color.YELLOW);
            g.drawString("无敌: " + (bird.invincibleTimer / 60) + "秒", 10, statusY);
            statusY += 20;
        }
        if (bird.hasMagnet) {
            g.setColor(Color.CYAN);
            g.drawString("磁铁: " + (bird.magnetTimer / 60) + "秒", 10, statusY);
            statusY += 20;
        }
        if (bird.hasSlow) {
            g.setColor(Color.GREEN);
            g.drawString("减速: " + (bird.slowTimer / 60) + "秒", 10, statusY);
            statusY += 20;
        }
        if (bird.hasShield) {
            g.setColor(Color.MAGENTA);
            g.drawString("护盾: 已激活", 10, statusY);
            statusY += 20;
        }

        // 绘制作弊控制台
        if (showCheatConsole) {
            drawCheatConsole(g);
        }

        // 绘制作弊消息
        if (cheatMessageTimer > 0) {
            g.setFont(smallFont);
            g.setColor(Color.YELLOW);
            g.drawString(cheatMessage, GameConfig.WIDTH / 2 - 100, GameConfig.HEIGHT - 50);
            cheatMessageTimer--;
        }

        // 无敌模式提示
        if (godMode) {
            g.setColor(Color.YELLOW);
            g.drawString("无敌模式", GameConfig.WIDTH - 100, 30);
        }
    }

    /**
     * 绘制作弊控制台
     */
    private void drawCheatConsole(Graphics g) {
        // 控制台背景
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, GameConfig.HEIGHT - 150, GameConfig.WIDTH - 100, 100);

        // 控制台边框
        g.setColor(Color.GREEN);
        g.drawRect(50, GameConfig.HEIGHT - 150, GameConfig.WIDTH - 100, 100);

        // 标题
        g.setFont(smallFont);
        g.setColor(Color.GREEN);
        g.drawString("作弊控制台 (按 Enter 确认, ESC 关闭)", 60, GameConfig.HEIGHT - 130);

        // 输入框
        g.setColor(Color.WHITE);
        g.drawString("> " + cheatInput + "_", 60, GameConfig.HEIGHT - 100);

        // 可用指令提示
        g.setColor(Color.GRAY);
        g.drawString("指令: money, health, unlock, god, clear", 60, GameConfig.HEIGHT - 70);
    }

    /**
     * 绘制开始界面
     */
    private void drawStartScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.setColor(Color.WHITE);
        g.drawString("飞翔小鸟", GameConfig.WIDTH / 2 - 100, 200);

        g.setFont(levelFont);
        g.drawString("冒险版", GameConfig.WIDTH / 2 - 30, 250);

        g.setFont(smallFont);
        g.drawString(currentLevel.getDescription(), GameConfig.WIDTH / 2 - 100, 350);

        // 呼吸效果
        int alpha = (int)(128 + 127 * Math.sin(System.currentTimeMillis() / 500.0));
        g.setColor(new Color(255, 255, 255, alpha));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g.drawString("点击开始", GameConfig.WIDTH / 2 - 50, 450);
    }

    /**
     * 绘制游戏结束界面
     */
    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.setColor(Color.RED);
        g.drawString("游戏结束", GameConfig.WIDTH / 2 - 100, 180);

        // 无限模式显示
        if (wasInfiniteMode) {
            g.setFont(levelFont);
            g.setColor(new Color(255, 100, 0));
            g.drawString("无限模式", GameConfig.WIDTH / 2 - 50, 230);

            // 综合得分 = 障碍数*10 + 金币数*5
            int totalScore = currentLevel.currentObstacles * 10 + currentLevel.coinsCollected * 5;
            g.setFont(levelFont);
            g.setColor(Color.WHITE);
            g.drawString("综合得分: " + totalScore, GameConfig.WIDTH / 2 - 70, 270);

            g.setFont(smallFont);
            g.setColor(Color.CYAN);
            g.drawString("通过障碍: " + currentLevel.currentObstacles + " (x10)", GameConfig.WIDTH / 2 - 80, 300);
            g.setColor(Color.YELLOW);
            g.drawString("收集金币: " + currentLevel.coinsCollected + " (x5)", GameConfig.WIDTH / 2 - 80, 320);

            g.setColor(Color.WHITE);
            g.drawString("历史最高: " + scoreManager.getInfiniteHighScore(), GameConfig.WIDTH / 2 - 60, 350);

            // 判断是否新纪录
            if (totalScore > scoreManager.getInfiniteHighScore()) {
                g.setColor(new Color(255, 215, 0));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
                g.drawString("NEW HIGH SCORE!", GameConfig.WIDTH / 2 - 80, 380);
            }
        } else {
            // 普通模式
            g.setFont(levelFont);
            g.setColor(Color.WHITE);
            g.drawString("关卡 " + currentLevel.levelNumber + " 得分: " + currentLevel.score, GameConfig.WIDTH / 2 - 100, 250);
            g.drawString("最高分: " + scoreManager.getHighScore(), GameConfig.WIDTH / 2 - 60, 290);
        }

        // 复活选项
        g.setFont(smallFont);
        if (currentLevel.canReviveFree()) {
            g.setColor(Color.YELLOW);
            g.drawString("免费复活 (点击)", GameConfig.WIDTH / 2 - 60, 400);
        } else if (scoreManager.getTotalCoins() >= GameConfig.REVIVE_COST) {
            g.setColor(Color.CYAN);
            g.drawString("花费 " + GameConfig.REVIVE_COST + " 金币复活 (点击)", GameConfig.WIDTH / 2 - 100, 400);
        } else {
            g.setColor(Color.GRAY);
            g.drawString("无法复活", GameConfig.WIDTH / 2 - 40, 400);
        }

        // 返回主菜单按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, 450, 160, 40, Color.ORANGE);

        g.setColor(Color.WHITE);
        g.setFont(smallFont);
        if (wasInfiniteMode) {
            g.drawString("空格键重新开始无限模式", GameConfig.WIDTH / 2 - 80, 520);
        } else {
            g.drawString("空格键重新开始", GameConfig.WIDTH / 2 - 60, 520);
        }
    }

    /**
     * 绘制复活确认界面
     */
    private void drawReviveConfirmScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.setColor(Color.YELLOW);
        g.drawString("复活确认", GameConfig.WIDTH / 2 - 100, 200);

        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("得分: " + currentLevel.score, GameConfig.WIDTH / 2 - 50, 280);
        g.drawString("最高分: " + scoreManager.getHighScore(), GameConfig.WIDTH / 2 - 60, 320);

        // 复活选项
        g.setFont(smallFont);
        if (currentLevel.canReviveFree()) {
            g.setColor(Color.YELLOW);
            g.drawString("免费复活 (点击)", GameConfig.WIDTH / 2 - 60, 400);
            g.setColor(Color.WHITE);
            g.drawString("免费次数: 1次", GameConfig.WIDTH / 2 - 60, 430);
        } else if (scoreManager.getTotalCoins() >= GameConfig.REVIVE_COST) {
            g.setColor(Color.CYAN);
            g.drawString("花费 " + GameConfig.REVIVE_COST + " 金币复活 (点击)", GameConfig.WIDTH / 2 - 100, 400);
            g.setColor(Color.WHITE);
            g.drawString("当前金币: " + scoreManager.getTotalCoins(), GameConfig.WIDTH / 2 - 60, 430);
        } else {
            g.setColor(Color.GRAY);
            g.drawString("金币不足，无法复活", GameConfig.WIDTH / 2 - 80, 400);
            g.setColor(Color.WHITE);
            g.drawString("需要: " + GameConfig.REVIVE_COST + " 金币", GameConfig.WIDTH / 2 - 60, 430);
        }

        // 返回主菜单按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, 480, 160, 40, Color.ORANGE);

        // 重新开始提示
        g.setColor(Color.WHITE);
        g.setFont(smallFont);
        g.drawString("空格键重新开始", GameConfig.WIDTH / 2 - 60, 550);
    }

    /**
     * 绘制关卡完成界面
     */
    private void drawLevelCompleteScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.setColor(Color.GREEN);
        g.drawString("关卡完成!", GameConfig.WIDTH / 2 - 120, 200);

        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("得分: " + currentLevel.score, GameConfig.WIDTH / 2 - 50, 280);
        g.drawString("金币: " + currentLevel.coinsCollected, GameConfig.WIDTH / 2 - 50, 320);

        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("点击进入下一关", GameConfig.WIDTH / 2 - 70, 400);
    }

    /**
     * 绘制暂停界面
     */
    private void drawPausedScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 60));
        g.setColor(Color.WHITE);
        g.drawString("PAUSED", GameConfig.WIDTH / 2 - 100, GameConfig.HEIGHT / 2);

        // 无限模式显示
        if (state == GameState.INFINITE) {
            g.setFont(levelFont);
            g.setColor(new Color(255, 100, 0));
            g.drawString("无限模式", GameConfig.WIDTH / 2 - 40, GameConfig.HEIGHT / 2 + 50);
        }
    }

    /**
     * 绘制主菜单
     */
    private void drawMainMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题（调整位置避免与按钮重叠）
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("飞翔小鸟", GameConfig.WIDTH / 2 - 100, 80);
        g.setFont(levelFont);
        g.drawString("冒险版", GameConfig.WIDTH / 2 - 30, 120);

        // 按钮（使用悬停效果）
        drawButton(g, "开始游戏", GameConfig.WIDTH / 2 - 80, 150, 160, 38, Color.GREEN, hoveredButton == 0);
        drawButton(g, "关卡选择", GameConfig.WIDTH / 2 - 80, 195, 160, 38, Color.BLUE, hoveredButton == 1);
        drawButton(g, "皮肤选择", GameConfig.WIDTH / 2 - 80, 240, 160, 38, Color.ORANGE, hoveredButton == 2);
        drawButton(g, "场景选择", GameConfig.WIDTH / 2 - 80, 285, 160, 38, Color.CYAN, hoveredButton == 3);
        drawButton(g, "障碍物皮肤", GameConfig.WIDTH / 2 - 80, 330, 160, 38, new Color(139, 90, 43), hoveredButton == 4);
        drawButton(g, "升级系统", GameConfig.WIDTH / 2 - 80, 375, 160, 38, Color.YELLOW, hoveredButton == 5);
        drawButton(g, "成就系统", GameConfig.WIDTH / 2 - 80, 420, 160, 38, Color.MAGENTA, hoveredButton == 6);
        drawButton(g, "排行榜", GameConfig.WIDTH / 2 - 80, 465, 160, 38, new Color(255, 200, 0), hoveredButton == 7);
        drawButton(g, "设置", GameConfig.WIDTH / 2 - 80, 510, 160, 38, Color.GRAY, hoveredButton == 8);

        // 无限模式按钮（通关后显示）
        if (scoreManager.areAllLevelsCompleted()) {
            drawButton(g, "无限模式", GameConfig.WIDTH / 2 - 80, 555, 160, 38, new Color(255, 100, 0));
            drawButton(g, "退出游戏", GameConfig.WIDTH / 2 - 80, 600, 160, 38, Color.RED);
        } else {
            drawButton(g, "退出游戏", GameConfig.WIDTH / 2 - 80, 555, 160, 38, Color.RED);
        }

        // 作弊模式按钮（右下角上移）
        drawButton(g, "C", GameConfig.WIDTH - 50, GameConfig.HEIGHT - 100, 40, 40, new Color(80, 80, 80));

        // 作弊模式提示
        if (cheatMode) {
            g.setFont(smallFont);
            g.setColor(Color.GREEN);
            g.drawString("作弊模式已开启", GameConfig.WIDTH - 150, 30);
        }

        // 绘制作弊消息
        if (cheatMessageTimer > 0) {
            g.setFont(smallFont);
            g.setColor(Color.YELLOW);
            g.drawString(cheatMessage, GameConfig.WIDTH / 2 - 100, GameConfig.HEIGHT - 60);
            cheatMessageTimer--;
        }

        // 底部提示
        g.setFont(smallFont);
        g.setColor(Color.WHITE);
        g.drawString("点击按钮开始", GameConfig.WIDTH / 2 - 50, GameConfig.HEIGHT - 30);

        // 金币显示
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("金币: " + scoreManager.getTotalCoins(), 10, 30);
    }

    /**
     * 绘制排行榜界面
     */
    private void drawLeaderboard(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(new Color(255, 200, 0));
        g.drawString("排行榜", GameConfig.WIDTH / 2 - 80, 100);

        // 无限模式最高分
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("无限模式最高分", GameConfig.WIDTH / 2 - 80, 180);
        g.setColor(new Color(255, 100, 0));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
        g.drawString(String.valueOf(scoreManager.getInfiniteHighScore()), GameConfig.WIDTH / 2 - 30, 230);

        // 排行榜
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("TOP 5 排行榜", GameConfig.WIDTH / 2 - 70, 300);

        int[] leaderboard = scoreManager.getLeaderboard();
        int startY = 340;
        for (int i = 0; i < 5; i++) {
            int y = startY + i * 50;

            // 排名背景
            if (leaderboard[i] > 0) {
                g.setColor(new Color(50, 50, 50, 200));
                g.fillRect(100, y, GameConfig.WIDTH - 200, 40);

                // 排名
                g.setFont(levelFont);
                if (i == 0) {
                    g.setColor(new Color(255, 215, 0));  // 金色
                } else if (i == 1) {
                    g.setColor(new Color(192, 192, 192));  // 银色
                } else if (i == 2) {
                    g.setColor(new Color(205, 127, 50));  // 铜色
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString("#" + (i + 1), 120, y + 28);

                // 分数
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf(leaderboard[i]), GameConfig.WIDTH / 2 - 30, y + 28);
            } else {
                g.setColor(new Color(50, 50, 50, 100));
                g.fillRect(100, y, GameConfig.WIDTH - 200, 40);
                g.setFont(levelFont);
                g.setColor(Color.GRAY);
                g.drawString("#" + (i + 1), 120, y + 28);
                g.drawString("---", GameConfig.WIDTH / 2 - 10, y + 28);
            }
        }

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 100, 160, 50, Color.RED);
    }

    /**
     * 绘制障碍物皮肤选择界面
     */
    private void drawObstacleSkinSelect(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(new Color(139, 90, 43));
        g.drawString("障碍物皮肤", GameConfig.WIDTH / 2 - 120, 80);

        // 金币显示
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("金币: " + scoreManager.getTotalCoins(), 10, 30);

        // 皮肤列表
        List<ObstacleSkin> skins = obstacleSkinManager.getSkins();
        int startY = 120;
        int itemHeight = 100;

        for (int i = 0; i < skins.size(); i++) {
            ObstacleSkin skin = skins.get(i);
            int y = startY + i * itemHeight;

            // 绘制皮肤项背景
            g.setColor(new Color(50, 50, 50, 200));
            g.fillRect(50, y, GameConfig.WIDTH - 100, itemHeight - 10);

            // 绘制皮肤预览
            skin.draw(g, 70, y + 10, 60, 60, 0.5f, true);

            // 绘制皮肤信息
            g.setFont(levelFont);
            g.setColor(Color.WHITE);
            g.drawString(skin.getName(), 150, y + 30);

            g.setFont(smallFont);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(skin.getDescription(), 150, y + 50);

            // 绘制状态/价格
            if (skin.isUnlocked()) {
                if (i == obstacleSkinManager.getSelectedSkinIndex()) {
                    g.setColor(Color.GREEN);
                    g.drawString("已装备", GameConfig.WIDTH - 120, y + 40);
                } else {
                    g.setColor(Color.CYAN);
                    g.drawString("点击装备", GameConfig.WIDTH - 120, y + 40);
                }
            } else {
                g.setColor(Color.YELLOW);
                g.drawString("价格: " + skin.getPrice() + " 金币", GameConfig.WIDTH - 150, y + 30);
                g.setColor(Color.ORANGE);
                g.drawString("点击购买", GameConfig.WIDTH - 120, y + 50);
            }
        }

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 100, 160, 50, Color.RED);
    }

    /**
     * 绘制设置界面
     */
    private void drawSettings(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("设置", GameConfig.WIDTH / 2 - 60, 80);

        // 音量设置区域
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("音量设置", 50, 150);

        // 音效音量条
        g.setFont(smallFont);
        g.setColor(Color.WHITE);
        g.drawString("音效", 50, 190);
        drawVolumeBar(g, 180, 175, 300, 25, settingsManager.getSfxVolume());

        // 存档管理区域
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("存档管理", 50, 260);

        // 绘制3个存档槽位
        for (int i = 0; i < 3; i++) {
            drawSaveSlot(g, 50, 280 + i * 70, i);
        }

        // 导入导出按钮
        drawButton(g, "导入存档", 50, 500, 120, 35, Color.BLUE);
        drawButton(g, "导出存档", 200, 500, 120, 35, Color.CYAN);

        // 数据管理区域
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("数据管理", 50, 570);

        // 重置金币按钮
        drawButton(g, "重置金币", 50, 600, 120, 35, Color.ORANGE);

        // 恢复出厂设置按钮
        drawButton(g, "恢复出厂设置", 200, 600, 150, 35, Color.RED);

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50, Color.RED);
    }

    /**
     * 绘制音量条
     */
    private void drawVolumeBar(Graphics g, int x, int y, int width, int height, int volume) {
        // 背景
        g.setColor(new Color(50, 50, 50));
        g.fillRect(x, y, width, height);

        // 音量条
        int fillWidth = (int)(width * volume / 100.0);
        g.setColor(new Color(0, 200, 0));
        g.fillRect(x, y, fillWidth, height);

        // 边框
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);

        // 音量数值
        g.setFont(smallFont);
        g.setColor(Color.WHITE);
        g.drawString(volume + "%", x + width + 10, y + 18);

        // 滑块按钮
        drawButton(g, "-", x - 30, y, 25, height, Color.GRAY);
        drawButton(g, "+", x + width + 40, y, 25, height, Color.GRAY);
    }

    /**
     * 绘制存档槽位
     */
    private void drawSaveSlot(Graphics g, int x, int y, int slotIndex) {
        SaveManager.SaveSlot slot = saveManager.getSlotInfo(slotIndex);

        // 槽位背景
        g.setColor(new Color(50, 50, 50, 200));
        g.fillRect(x, y, GameConfig.WIDTH - 100, 60);

        // 槽位编号
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString("存档" + (slotIndex + 1), x + 10, y + 25);

        if (slot.isEmpty) {
            // 空槽位
            g.setFont(smallFont);
            g.setColor(Color.GRAY);
            g.drawString("空槽位", x + 100, y + 25);
            drawButton(g, "保存", GameConfig.WIDTH - 180, y + 10, 70, 40, Color.GREEN);
        } else {
            // 有存档
            g.setFont(smallFont);
            g.setColor(Color.WHITE);
            g.drawString(slot.name, x + 100, y + 20);
            g.setColor(Color.GRAY);
            g.drawString(slot.timestamp, x + 100, y + 40);

            // 操作按钮
            drawButton(g, "加载", GameConfig.WIDTH - 260, y + 10, 60, 40, Color.BLUE);
            drawButton(g, "删除", GameConfig.WIDTH - 190, y + 10, 60, 40, Color.RED);
            drawButton(g, "重命名", GameConfig.WIDTH - 120, y + 10, 70, 40, Color.YELLOW);
        }
    }

    /**
     * 绘制关卡选择界面
     */
    private void drawLevelSelect(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("关卡选择", GameConfig.WIDTH / 2 - 100, 80);

        // 关卡网格 (4列5行)
        int gridX = 50;
        int gridY = 120;
        int cellWidth = 80;
        int cellHeight = 80;
        int padding = 10;

        for (int i = 0; i < GameConfig.TOTAL_LEVELS; i++) {
            int row = i / 4;
            int col = i % 4;
            int x = gridX + col * (cellWidth + padding);
            int y = gridY + row * (cellHeight + padding);

            boolean isUnlocked = i < scoreManager.getCurrentLevel();
            boolean isCompleted = scoreManager.isLevelCompleted(i + 1);
            boolean isSelected = (i + 1) == selectedLevel;

            // 绘制关卡按钮
            if (isCompleted) {
                drawButton(g, String.valueOf(i + 1), x, y, cellWidth, cellHeight, Color.GREEN);
            } else if (isUnlocked) {
                drawButton(g, String.valueOf(i + 1), x, y, cellWidth, cellHeight, Color.BLUE);
            } else {
                drawButton(g, "🔒", x, y, cellWidth, cellHeight, Color.GRAY);
            }

            // 选中边框
            if (isSelected) {
                g.setColor(Color.YELLOW);
                g.drawRect(x - 2, y - 2, cellWidth + 4, cellHeight + 4);
                g.drawRect(x - 3, y - 3, cellWidth + 6, cellHeight + 6);
            }
        }

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 100, 160, 50, Color.RED);

        // 提示信息
        g.setFont(smallFont);
        g.setColor(Color.WHITE);
        g.drawString("点击关卡开始游戏", GameConfig.WIDTH / 2 - 60, GameConfig.HEIGHT - 30);
    }

    /**
     * 绘制皮肤选择界面
     */
    private void drawSkinSelect(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("皮肤选择", GameConfig.WIDTH / 2 - 100, 80);

        // 金币显示
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("金币: " + scoreManager.getTotalCoins(), 10, 30);

        // 皮肤列表
        List<Skin> skins = skinManager.getSkins();
        int startY = 120;
        int itemHeight = 100;

        for (int i = 0; i < skins.size(); i++) {
            Skin skin = skins.get(i);
            int y = startY + i * itemHeight;

            // 绘制皮肤项背景
            g.setColor(new Color(50, 50, 50, 200));
            g.fillRect(50, y, GameConfig.WIDTH - 100, itemHeight - 10);

            // 绘制皮肤图片
            g.drawImage(skin.getImage(), 70, y + 10, 60, 60, null);

            // 绘制皮肤信息
            g.setFont(levelFont);
            g.setColor(Color.WHITE);
            g.drawString(skin.getName(), 150, y + 30);

            g.setFont(smallFont);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(skin.getDescription(), 150, y + 50);

            // 绘制状态/价格
            if (skin.isUnlocked()) {
                if (i == scoreManager.getSelectedSkinIndex()) {
                    g.setColor(Color.GREEN);
                    g.drawString("已装备", GameConfig.WIDTH - 120, y + 40);
                } else {
                    g.setColor(Color.CYAN);
                    g.drawString("点击装备", GameConfig.WIDTH - 120, y + 40);
                }
            } else {
                g.setColor(Color.YELLOW);
                g.drawString("价格: " + skin.getPrice() + " 金币", GameConfig.WIDTH - 150, y + 30);
                g.setColor(Color.ORANGE);
                g.drawString("点击购买", GameConfig.WIDTH - 120, y + 50);
            }
        }

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50, Color.RED);
    }

    /**
     * 绘制成就系统界面
     */
    private void drawAchievements(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("成就系统", GameConfig.WIDTH / 2 - 100, 80);

        // 成就统计
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("已解锁: " + achievementManager.getUnlockedCount() + "/" + achievementManager.getAchievements().size(), 10, 30);

        // 成就列表
        List<Achievement> achievements = achievementManager.getAchievements();
        int startY = 120;
        int itemHeight = 80;

        for (int i = 0; i < achievements.size(); i++) {
            Achievement achievement = achievements.get(i);
            int y = startY + i * itemHeight;

            // 绘制成就项背景
            if (achievement.isUnlocked()) {
                g.setColor(new Color(50, 100, 50, 200));
            } else {
                g.setColor(new Color(50, 50, 50, 200));
            }
            g.fillRect(50, y, GameConfig.WIDTH - 100, itemHeight - 10);

            // 绘制成就信息
            g.setFont(levelFont);
            if (achievement.isUnlocked()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(achievement.getName(), 70, y + 25);

            g.setFont(smallFont);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(achievement.getDescription(), 70, y + 45);

            // 绘制金币奖励
            g.setColor(Color.YELLOW);
            g.drawString("奖励: " + achievement.getCoinReward() + " 金币", 70, y + 65);

            // 绘制进度条
            int progressBarWidth = 200;
            int progressBarHeight = 15;
            int progressBarX = GameConfig.WIDTH - 280;
            int progressBarY = y + 30;

            g.setColor(Color.DARK_GRAY);
            g.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

            int progressWidth = (int) ((double) achievement.getProgress() / achievement.getTarget() * progressBarWidth);
            if (achievement.isUnlocked()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillRect(progressBarX, progressBarY, progressWidth, progressBarHeight);

            g.setColor(Color.WHITE);
            g.setFont(smallFont);
            g.drawString(achievement.getProgress() + "/" + achievement.getTarget(), progressBarX + progressBarWidth + 10, progressBarY + 12);
        }

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50, Color.RED);
    }

    /**
     * 绘制场景选择界面
     */
    private void drawSceneSelect(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("场景选择", GameConfig.WIDTH / 2 - 100, 80);

        // 金币显示
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("金币: " + scoreManager.getTotalCoins(), 10, 30);

        // 场景列表
        List<Scene> scenes = sceneManager.getScenes();
        int startY = 120;
        int itemHeight = 100;

        for (int i = 0; i < scenes.size(); i++) {
            Scene scene = scenes.get(i);
            int y = startY + i * itemHeight;

            // 绘制场景项背景
            g.setColor(new Color(50, 50, 50, 200));
            g.fillRect(50, y, GameConfig.WIDTH - 100, itemHeight - 10);

            // 绘制场景图片
            g.drawImage(scene.getBackground(), 70, y + 10, 80, 60, null);

            // 绘制场景信息
            g.setFont(levelFont);
            g.setColor(Color.WHITE);
            g.drawString(scene.getName(), 170, y + 30);

            g.setFont(smallFont);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(scene.getDescription(), 170, y + 50);

            // 绘制状态/价格
            if (scene.isUnlocked()) {
                if (i == sceneManager.getSelectedSceneIndex()) {
                    g.setColor(Color.GREEN);
                    g.drawString("已装备", GameConfig.WIDTH - 120, y + 40);
                } else {
                    g.setColor(Color.CYAN);
                    g.drawString("点击装备", GameConfig.WIDTH - 120, y + 40);
                }
            } else {
                g.setColor(Color.YELLOW);
                g.drawString("价格: " + scene.getPrice() + " 金币", GameConfig.WIDTH - 150, y + 30);
                g.setColor(Color.ORANGE);
                g.drawString("点击购买", GameConfig.WIDTH - 120, y + 50);
            }
        }

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50, Color.RED);
    }

    /**
     * 绘制升级系统界面
     */
    private void drawUpgrade(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        // 标题
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("升级系统", GameConfig.WIDTH / 2 - 100, 80);

        // 金币显示
        g.setFont(smallFont);
        g.setColor(Color.YELLOW);
        g.drawString("金币: " + scoreManager.getTotalCoins(), 10, 30);

        // 升级项目
        int startY = 150;
        int itemHeight = 120;

        // 生命值升级
        drawUpgradeItem(g, "生命值", "增加最大生命值",
                upgradeSystem.getHealthLevel(), upgradeSystem.getMaxHealth(),
                upgradeSystem.getHealthUpgradeCost(), startY, true);

        // 速度升级
        drawUpgradeItem(g, "速度", "增加移动速度",
                upgradeSystem.getSpeedLevel(), upgradeSystem.getMaxSpeed(),
                upgradeSystem.getSpeedUpgradeCost(), startY + itemHeight, true);

        // 返回按钮
        drawButton(g, "返回主菜单", GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50, Color.RED);
    }

    /**
     * 绘制升级项目
     */
    private void drawUpgradeItem(Graphics g, String name, String description,
                                  int level, int maxLevel, int cost, int y, boolean canUpgrade) {
        // 背景
        g.setColor(new Color(50, 50, 50, 200));
        g.fillRect(50, y, GameConfig.WIDTH - 100, 110);

        // 名称和描述
        g.setFont(levelFont);
        g.setColor(Color.WHITE);
        g.drawString(name, 70, y + 30);

        g.setFont(smallFont);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(description, 70, y + 50);

        // 等级显示
        g.setColor(Color.CYAN);
        g.drawString("等级: " + level + "/" + maxLevel, 70, y + 70);

        // 升级按钮
        if (canUpgrade && scoreManager.getTotalCoins() >= cost) {
            drawButton(g, "升级 (" + cost + "金币)", GameConfig.WIDTH - 200, y + 20, 150, 40, Color.GREEN);
        } else {
            drawButton(g, "升级 (" + cost + "金币)", GameConfig.WIDTH - 200, y + 20, 150, 40, Color.GRAY);
        }
    }

    /**
     * 绘制按钮
     */
    private void drawButton(Graphics g, String text, int x, int y, int width, int height, Color color) {
        drawButton(g, text, x, y, width, height, color, false);
    }

    /**
     * 绘制按钮（支持悬停效果）
     */
    private void drawButton(Graphics g, String text, int x, int y, int width, int height, Color color, boolean hovered) {
        // 悬停时按钮变亮
        if (hovered) {
            g.setColor(new Color(255, 255, 255, 40));
            g.fillRoundRect(x, y, width, height, 10, 10);
        }

        // 按钮背景
        g.setColor(color);
        g.fillRoundRect(x, y, width, height, 10, 10);

        // 按钮边框（悬停时加粗高亮）
        if (hovered) {
            g.setColor(GameConfig.COLOR_ACCENT);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width, height, 10, 10);
            g2.setStroke(new BasicStroke(1));
        } else {
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, width, height, 10, 10);
        }

        // 按钮文字
        g.setFont(buttonFont);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - 5;

        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);
    }

    /**
     * 游戏主循环
     */
    public void action() {
        // 鼠标点击事件
        MouseListener l = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick();
            }
        };
        addMouseListener(l);

        // 鼠标移动事件
        MouseMotionListener motionListener = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                lastMousePosition = e.getPoint();
                updateHoveredButton(e.getX(), e.getY());
            }
        };
        addMouseMotionListener(motionListener);

        // 键盘事件
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 作弊控制台开关
                if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {  // ` 键
                    showCheatConsole = !showCheatConsole;
                    if (showCheatConsole) {
                        cheatInput = "";
                    }
                    return;
                }

                // 作弊控制台输入处理
                if (showCheatConsole) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        executeCheatCommand(cheatInput);
                        cheatInput = "";
                        showCheatConsole = false;
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        showCheatConsole = false;
                        cheatInput = "";
                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (cheatInput.length() > 0) {
                            cheatInput = cheatInput.substring(0, cheatInput.length() - 1);
                        }
                    } else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                        cheatInput += e.getKeyChar();
                    }
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    handleSpaceKey();
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (state == GameState.RUNNING || state == GameState.INFINITE) {
                        state = GameState.PAUSED;
                    } else if (state == GameState.PAUSED) {
                        state = GameState.RUNNING;
                    }
                }
            }
        });
        setFocusable(true);

        // 游戏主循环
        Timer timer = new Timer(GameConfig.FRAME_DELAY, e -> {
            switch (state) {
                case START:
                    bird.flappy();
                    break;
                case RUNNING:
                case INFINITE:
                    updateGame();
                    break;
                case GAME_OVER:
                case LEVEL_COMPLETE:
                case PAUSED:
                    break;
            }
            repaint();
        });
        timer.start();
    }

    /**
     * 处理点击事件
     */
    private void handleClick() {
        // 获取鼠标位置
        Point mousePoint = getMousePosition();
        if (mousePoint == null) return;

        int mouseX = mousePoint.x;
        int mouseY = mousePoint.y;

        // 处理教程点击
        if (showTutorial && !tutorialManager.isCompleted()) {
            if (tutorialManager.handleClick(mouseX, mouseY)) {
                if (tutorialManager.isCompleted()) {
                    showTutorial = false;
                    saveManager.setTutorialCompleted(true);
                    saveManager.saveSettings();
                    // 教程完成后播放菜单音乐
                    musicManager.play();
                }
                return;
            }
        }

        switch (state) {
            case MAIN_MENU:
                handleMainMenuClick(mouseX, mouseY);
                break;
            case LEVEL_SELECT:
                handleLevelSelectClick(mouseX, mouseY);
                break;
            case SKIN_SELECT:
                handleSkinSelectClick(mouseX, mouseY);
                break;
            case SCENE_SELECT:
                handleSceneSelectClick(mouseX, mouseY);
                break;
            case ACHIEVEMENTS:
                handleAchievementsClick(mouseX, mouseY);
                break;
            case UPGRADE:
                handleUpgradeClick(mouseX, mouseY);
                break;
            case START:
                state = GameState.RUNNING;
                bird.fly();
                soundManager.playClick();
                // 切换到游戏背景音乐
                musicManager.switchTrack("game_music.wav");
                break;
            case RUNNING:
            case INFINITE:
                bird.fly();
                soundManager.playClick();
                break;
            case GAME_OVER:
                // 检查是否点击了返回主菜单按钮
                if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 450, 160, 40)) {
                    state = GameState.MAIN_MENU;
                    wasInfiniteMode = false;
                    soundManager.playClick();
                    // 切换回菜单背景音乐
                    musicManager.switchTrack("menu_music.wav");
                }
                // 检查是否点击了复活选项（扩大点击区域）
                else if (mouseY >= 385 && mouseY <= 415) {
                    if (currentLevel.canReviveFree()) {
                        // 免费复活
                        currentLevel.useFreeRevive();
                        bird.revive();
                        state = wasInfiniteMode ? GameState.INFINITE : GameState.RUNNING;
                        soundManager.playClick();
                    } else if (scoreManager.getTotalCoins() >= GameConfig.REVIVE_COST) {
                        // 金币复活
                        if (scoreManager.spendCoins(GameConfig.REVIVE_COST)) {
                            bird.revive();
                            state = wasInfiniteMode ? GameState.INFINITE : GameState.RUNNING;
                            soundManager.playClick();
                        }
                    }
                }
                // 点击空格键重新开始
                break;
            case REVIVE_CONFIRM:
                // 检查是否点击了返回主菜单按钮
                if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 480, 160, 40)) {
                    state = GameState.MAIN_MENU;
                    soundManager.playClick();
                    musicManager.switchTrack("menu_music.wav");
                } else {
                    handleReviveConfirm();
                }
                break;
            case LEVEL_COMPLETE:
                nextLevel();
                break;
            case PAUSED:
                state = GameState.RUNNING;
                break;
            case LEADERBOARD:
                state = GameState.MAIN_MENU;
                soundManager.playClick();
                break;
            case OBSTACLE_SKIN:
                handleObstacleSkinSelectClick(mouseX, mouseY);
                break;
            case SETTINGS:
                handleSettingsClick(mouseX, mouseY);
                break;
        }
    }

    /**
     * 处理主菜单点击
     */
    private void handleMainMenuClick(int mouseX, int mouseY) {
        // 开始游戏按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 150, 160, 38)) {
            state = GameState.START;
            currentLevel = new Level(scoreManager.getCurrentLevel());
            resetGameObjects();
            soundManager.playClick();
        }
        // 关卡选择按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 195, 160, 38)) {
            state = GameState.LEVEL_SELECT;
            soundManager.playClick();
        }
        // 皮肤选择按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 240, 160, 38)) {
            state = GameState.SKIN_SELECT;
            soundManager.playClick();
        }
        // 场景选择按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 285, 160, 38)) {
            state = GameState.SCENE_SELECT;
            soundManager.playClick();
        }
        // 障碍物皮肤按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 330, 160, 38)) {
            state = GameState.OBSTACLE_SKIN;
            soundManager.playClick();
        }
        // 升级系统按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 375, 160, 38)) {
            state = GameState.UPGRADE;
            soundManager.playClick();
        }
        // 成就系统按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 420, 160, 38)) {
            state = GameState.ACHIEVEMENTS;
            soundManager.playClick();
        }
        // 排行榜按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 465, 160, 38)) {
            state = GameState.LEADERBOARD;
            soundManager.playClick();
        }
        // 设置按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 510, 160, 38)) {
            state = GameState.SETTINGS;
            soundManager.playClick();
        }
        // 无限模式按钮（通关后可用）
        else if (scoreManager.areAllLevelsCompleted() && isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 555, 160, 38)) {
            state = GameState.INFINITE;
            currentLevel = new Level(GameConfig.TOTAL_LEVELS);
            resetGameObjects();
            soundManager.playClick();
            // 切换到游戏背景音乐
            musicManager.switchTrack("game_music.wav");
        }
        // 作弊模式按钮（右下角上移）
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 50, GameConfig.HEIGHT - 100, 40, 40)) {
            cheatMode = !cheatMode;
            if (cheatMode) {
                // 开启作弊模式：保存当前金币，设置无限金币
                savedCoins = scoreManager.getTotalCoins();
                scoreManager.addCoins(99999);
                for (int i = 1; i <= GameConfig.TOTAL_LEVELS; i++) {
                    scoreManager.completeLevel(i);
                }
                for (int i = 0; i < skinManager.getSkins().size(); i++) {
                    skinManager.getSkins().get(i).unlock();
                    scoreManager.unlockSkin(i);
                }
                for (int i = 0; i < sceneManager.getScenes().size(); i++) {
                    sceneManager.getScenes().get(i).unlock();
                }
                cheatMessage = "作弊模式已开启！已解锁全部";
                cheatMessageTimer = 180;
            } else {
                // 关闭作弊模式：恢复原来的金币
                scoreManager.resetCoins(savedCoins);
                godMode = false;
                cheatMessage = "作弊模式已关闭，金币已恢复";
                cheatMessageTimer = 120;
            }
            soundManager.playClick();
        }
        // 退出游戏按钮
        else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, scoreManager.areAllLevelsCompleted() ? 620 : 560, 160, 40)) {
            System.exit(0);
        }
    }

    /**
     * 处理关卡选择点击
     */
    private void handleLevelSelectClick(int mouseX, int mouseY) {
        // 关卡网格
        int gridX = 50;
        int gridY = 120;
        int cellWidth = 80;
        int cellHeight = 80;
        int padding = 10;

        for (int i = 0; i < GameConfig.TOTAL_LEVELS; i++) {
            int row = i / 4;
            int col = i % 4;
            int x = gridX + col * (cellWidth + padding);
            int y = gridY + row * (cellHeight + padding);

            if (isButtonClick(mouseX, mouseY, x, y, cellWidth, cellHeight)) {
                // 关卡解锁条件：已到达该关卡 或 已完成该关卡
                boolean isUnlocked = i < scoreManager.getCurrentLevel() || scoreManager.isLevelCompleted(i + 1);
                if (isUnlocked) {
                    selectedLevel = i + 1;
                    currentLevel = new Level(selectedLevel);
                    resetGameObjects();
                    state = GameState.START;
                    soundManager.playClick();
                }
                break;
            }
        }

        // 返回按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 100, 160, 50)) {
            state = GameState.MAIN_MENU;
            soundManager.playClick();
        }
    }

    /**
     * 处理皮肤选择点击
     */
    private void handleSkinSelectClick(int mouseX, int mouseY) {
        List<Skin> skins = skinManager.getSkins();
        int startY = 120;
        int itemHeight = 100;

        for (int i = 0; i < skins.size(); i++) {
            int y = startY + i * itemHeight;
            if (isButtonClick(mouseX, mouseY, 50, y, GameConfig.WIDTH - 100, itemHeight - 10)) {
                Skin skin = skins.get(i);
                if (skin.isUnlocked()) {
                    // 装备皮肤 - 更新图片和动画帧
                    scoreManager.setSelectedSkinIndex(i);
                    bird.image = skin.getImage();
                    bird.images = skin.getAnimationFrames();
                } else {
                    // 购买皮肤
                    if (scoreManager.spendCoins(skin.getPrice())) {
                        skin.unlock();
                        scoreManager.unlockSkin(i);
                        scoreManager.setSelectedSkinIndex(i);
                        bird.image = skin.getImage();
                        bird.images = skin.getAnimationFrames();
                    }
                }
                soundManager.playClick();
                break;
            }
        }

        // 返回按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50)) {
            state = GameState.MAIN_MENU;
            soundManager.playClick();
        }
    }

    /**
     * 处理成就系统点击
     */
    private void handleAchievementsClick(int mouseX, int mouseY) {
        // 返回按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50)) {
            state = GameState.MAIN_MENU;
            soundManager.playClick();
        }
    }

    /**
     * 处理障碍物皮肤选择点击
     */
    private void handleObstacleSkinSelectClick(int mouseX, int mouseY) {
        List<ObstacleSkin> skins = obstacleSkinManager.getSkins();
        int startY = 120;
        int itemHeight = 100;

        for (int i = 0; i < skins.size(); i++) {
            int y = startY + i * itemHeight;
            if (isButtonClick(mouseX, mouseY, 50, y, GameConfig.WIDTH - 100, itemHeight - 10)) {
                ObstacleSkin skin = skins.get(i);
                if (skin.isUnlocked()) {
                    // 装备皮肤
                    obstacleSkinManager.selectSkin(i);
                } else {
                    // 购买皮肤
                    if (scoreManager.spendCoins(skin.getPrice())) {
                        skin.unlock();
                        obstacleSkinManager.selectSkin(i);
                    }
                }
                soundManager.playClick();
                break;
            }
        }

        // 返回按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 100, 160, 50)) {
            state = GameState.MAIN_MENU;
            soundManager.playClick();
        }
    }

    /**
     * 处理设置界面点击
     */
    private void handleSettingsClick(int mouseX, int mouseY) {
        // 音量控制 - 音效
        if (isButtonClick(mouseX, mouseY, 150, 175, 25, 25)) {
            settingsManager.setSfxVolume(Math.max(0, settingsManager.getSfxVolume() - 10));
            soundManager.playClick();
        } else if (isButtonClick(mouseX, mouseY, 520, 175, 25, 25)) {
            settingsManager.setSfxVolume(Math.min(100, settingsManager.getSfxVolume() + 10));
            soundManager.playClick();
        }
        // 存档槽位操作
        else {
            for (int i = 0; i < 3; i++) {
                int slotY = 280 + i * 70;
                SaveManager.SaveSlot slot = saveManager.getSlotInfo(i);

                if (slot.isEmpty) {
                    // 空槽位 - 保存按钮
                    if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 180, slotY + 10, 70, 40)) {
                        saveManager.saveGame(i, scoreManager, achievementManager,
                                skinManager.getSelectedSkinIndex(),
                                sceneManager.getSelectedSceneIndex(),
                                obstacleSkinManager.getSelectedSkinIndex());
                        soundManager.playClick();
                        break;
                    }
                } else {
                    // 有存档 - 加载按钮
                    if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 260, slotY + 10, 60, 40)) {
                        SaveManager.GameSaveData data = saveManager.loadGame(i);
                        if (data != null) {
                            applySaveData(data);
                        }
                        soundManager.playClick();
                        break;
                    }
                    // 删除按钮
                    else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 190, slotY + 10, 60, 40)) {
                        saveManager.deleteGame(i);
                        soundManager.playClick();
                        break;
                    }
                    // 重命名按钮
                    else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 120, slotY + 10, 70, 40)) {
                        // 简单重命名：使用时间戳
                        String newName = "存档" + (i + 1) + "-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
                        saveManager.renameGame(i, newName);
                        soundManager.playClick();
                        break;
                    }
                }
            }

            // 导入存档按钮
            if (isButtonClick(mouseX, mouseY, 50, 500, 120, 35)) {
                // 简化：导入到第一个空槽位
                for (int i = 0; i < 3; i++) {
                    if (saveManager.getSlotInfo(i).isEmpty) {
                        // 这里简化处理，实际应该打开文件选择器
                        soundManager.playClick();
                        break;
                    }
                }
            }
            // 导出存档按钮
            else if (isButtonClick(mouseX, mouseY, 200, 500, 120, 35)) {
                // 简化：导出当前槽位
                if (saveManager.getCurrentSlot() >= 0) {
                    String json = saveManager.exportSave(saveManager.getCurrentSlot());
                    if (json != null) {
                        // 这里简化处理，实际应该保存到文件
                        soundManager.playClick();
                    }
                }
            }
            // 重置金币按钮
            else if (isButtonClick(mouseX, mouseY, 50, 600, 120, 35)) {
                settingsManager.resetCoins(scoreManager);
                soundManager.playClick();
            }
            // 恢复出厂设置按钮
            else if (isButtonClick(mouseX, mouseY, 200, 600, 150, 35)) {
                settingsManager.factoryReset();
                // 重新加载游戏状态
                scoreManager = new ScoreManager();
                achievementManager = new AchievementManager();
                soundManager.playClick();
            }
            // 返回按钮
            else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50)) {
                state = GameState.MAIN_MENU;
                soundManager.playClick();
            }
        }
    }

    /**
     * 应用存档数据
     */
    private void applySaveData(SaveManager.GameSaveData data) {
        // 恢复游戏进度
        scoreManager.loadData(data.highScore, data.totalCoins, data.currentLevel,
                data.levelCompleted, data.skinUnlocked, data.infiniteHighScore,
                data.leaderboard, data.totalDeaths, data.totalRevives,
                data.totalPowerUps, data.totalCoinsCollected);

        // 恢复装备
        if (data.selectedSkin >= 0 && data.selectedSkin < skinManager.getSkins().size()) {
            skinManager.selectSkin(data.selectedSkin);
            Skin skin = skinManager.getSelectedSkin();
            bird.image = skin.getImage();
            bird.images = skin.getAnimationFrames();
        }
        if (data.selectedScene >= 0 && data.selectedScene < sceneManager.getScenes().size()) {
            sceneManager.selectScene(data.selectedScene);
            backGround = sceneManager.getSelectedScene().getBackground();
        }
        if (data.selectedObstacleSkin >= 0 && data.selectedObstacleSkin < obstacleSkinManager.getSkins().size()) {
            obstacleSkinManager.selectSkin(data.selectedObstacleSkin);
        }

        // 恢复成就
        if (data.achievementProgress != null && data.achievementUnlocked != null) {
            achievementManager.loadData(data.achievementProgress, data.achievementUnlocked);
        }
    }

    /**
     * 处理场景选择点击
     */
    private void handleSceneSelectClick(int mouseX, int mouseY) {
        List<Scene> scenes = sceneManager.getScenes();
        int startY = 120;
        int itemHeight = 100;

        for (int i = 0; i < scenes.size(); i++) {
            int y = startY + i * itemHeight;
            if (isButtonClick(mouseX, mouseY, 50, y, GameConfig.WIDTH - 100, itemHeight - 10)) {
                Scene scene = scenes.get(i);
                if (scene.isUnlocked()) {
                    // 装备场景
                    sceneManager.selectScene(i);
                    backGround = scene.getBackground();
                } else {
                    // 购买场景
                    if (scoreManager.spendCoins(scene.getPrice())) {
                        scene.unlock();
                        sceneManager.selectScene(i);
                        backGround = scene.getBackground();
                    }
                }
                soundManager.playClick();
                break;
            }
        }

        // 返回按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50)) {
            state = GameState.MAIN_MENU;
            soundManager.playClick();
        }
    }

    /**
     * 处理升级系统点击
     */
    private void handleUpgradeClick(int mouseX, int mouseY) {
        int startY = 150;
        int itemHeight = 120;

        // 生命值升级
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 200, startY + 20, 150, 40)) {
            int cost = upgradeSystem.upgradeHealth(scoreManager.getTotalCoins());
            if (cost >= 0) {
                scoreManager.spendCoins(cost);
                maxHealth = upgradeSystem.getMaxHealth();
                currentHealth = maxHealth;
                soundManager.playClick();
            }
        }

        // 速度升级
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 200, startY + itemHeight + 20, 150, 40)) {
            int cost = upgradeSystem.upgradeSpeed(scoreManager.getTotalCoins());
            if (cost >= 0) {
                scoreManager.spendCoins(cost);
                soundManager.playClick();
            }
        }

        // 返回按钮
        if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, GameConfig.HEIGHT - 80, 160, 50)) {
            state = GameState.MAIN_MENU;
            soundManager.playClick();
        }
    }

    /**
     * 绘制样式化按钮（带阴影、渐变、悬停效果）
     */
    private void drawStyledButton(Graphics g, String text, int x, int y,
                                   int width, int height, Color color,
                                   boolean hovered) {
        Graphics2D g2 = (Graphics2D) g;

        // 阴影
        g2.setColor(GameConfig.COLOR_SHADOW);
        g2.fillRoundRect(x + 3, y + 3, width, height, 15, 15);

        // 渐变背景
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter(),
            x, y + height, color
        );
        g2.setPaint(gradient);
        g2.fillRoundRect(x, y, width, height, 15, 15);

        // 悬停效果
        if (hovered) {
            g2.setColor(GameConfig.COLOR_BUTTON_HOVER);
            g2.fillRoundRect(x, y, width, height, 15, 15);
        }

        // 边框
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 15, 15);

        // 文字（居中，带阴影）
        g2.setFont(GameConfig.SUBTITLE_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height + fm.getHeight()) / 2 - 3;

        // 文字阴影
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(text, textX + 1, textY + 1);

        // 文字
        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);

        // 重置画笔
        g2.setStroke(new BasicStroke(1));
        g2.setPaint(null);
    }

    /**
     * 更新当前悬停的按钮
     */
    private void updateHoveredButton(int mouseX, int mouseY) {
        hoveredButton = -1;

        switch (state) {
            case MAIN_MENU:
                // 检测主菜单按钮
                if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 150, 160, 38)) hoveredButton = 0;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 195, 160, 38)) hoveredButton = 1;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 240, 160, 38)) hoveredButton = 2;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 285, 160, 38)) hoveredButton = 3;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 330, 160, 38)) hoveredButton = 4;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 375, 160, 38)) hoveredButton = 5;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 420, 160, 38)) hoveredButton = 6;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 465, 160, 38)) hoveredButton = 7;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH / 2 - 80, 510, 160, 38)) hoveredButton = 8;
                else if (isButtonClick(mouseX, mouseY, GameConfig.WIDTH - 50, GameConfig.HEIGHT - 100, 40, 40)) hoveredButton = 9;
                break;
            case LEVEL_SELECT:
                // 检测关卡选择按钮
                for (int i = 0; i < GameConfig.TOTAL_LEVELS; i++) {
                    int col = i % 5;
                    int row = i / 5;
                    int x = 50 + col * 105;
                    int y = 100 + row * 70;
                    if (isButtonClick(mouseX, mouseY, x, y, 90, 55)) {
                        hoveredButton = i;
                        break;
                    }
                }
                break;
        }
    }

    /**
     * 检查点击是否在按钮范围内
     */
    private boolean isButtonClick(int mouseX, int mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    /**
     * 处理空格键事件
     */
    private void handleSpaceKey() {
        switch (state) {
            case REVIVE_CONFIRM:
                restartGame();
                break;
            case GAME_OVER:
                restartGame();
                break;
            default:
                handleClick();
                break;
        }
    }

    /**
     * 执行作弊指令
     */
    private void executeCheatCommand(String command) {
        command = command.toLowerCase().trim();

        switch (command) {
            case "money":
                scoreManager.addCoins(99999);
                cheatMessage = "已获得 99999 金币！";
                cheatMessageTimer = 120;
                cheatMode = true;
                break;

            case "health":
                currentHealth = 999;
                maxHealth = 999;
                cheatMessage = "生命值已设为 999！";
                cheatMessageTimer = 120;
                cheatMode = true;
                break;

            case "unlock":
                // 解锁所有关卡
                for (int i = 1; i <= GameConfig.TOTAL_LEVELS; i++) {
                    scoreManager.completeLevel(i);
                }
                // 解锁所有皮肤
                for (int i = 0; i < skinManager.getSkins().size(); i++) {
                    skinManager.getSkins().get(i).unlock();
                    scoreManager.unlockSkin(i);
                }
                // 解锁所有场景
                for (int i = 0; i < sceneManager.getScenes().size(); i++) {
                    sceneManager.getScenes().get(i).unlock();
                }
                cheatMessage = "已解锁所有关卡、皮肤和场景！";
                cheatMessageTimer = 120;
                cheatMode = true;
                break;

            case "god":
                godMode = !godMode;
                if (godMode) {
                    cheatMessage = "无敌模式已开启！";
                } else {
                    cheatMessage = "无敌模式已关闭！";
                }
                cheatMessageTimer = 120;
                cheatMode = true;
                break;

            case "clear":
                cheatMode = false;
                godMode = false;
                currentHealth = upgradeSystem.getMaxHealth();
                maxHealth = currentLevel.levelNumber;
                cheatMessage = "已清除所有作弊效果！";
                cheatMessageTimer = 120;
                break;

            default:
                cheatMessage = "未知指令: " + command;
                cheatMessageTimer = 120;
                break;
        }
    }

    /**
     * 更新游戏状态
     */
    private void updateGame() {
        // 更新小鸟
        bird.step();
        bird.flappy();
        bird.updatePowerUps();

        // 更新障碍物
        int speed = bird.hasSlow ? GameConfig.OBSTACLE_SPEED / 2 : GameConfig.OBSTACLE_SPEED;
        obstacle1.step();
        obstacle2.step();

        // 更新金币
        updateCoins(speed);

        // 更新道具
        updatePowerUps(speed);

        // 碰撞检测
        checkCollisions();

        // 检查通过障碍物
        checkPassedObstacles();

        // 更新粒子
        particles.update();

        // 发射尾焰粒子
        particles.emitTailFlame(bird.x, bird.y + bird.height / 2);

        // 检查关卡完成
        if (currentLevel.isCompleted) {
            if (state == GameState.INFINITE) {
                // 无限模式：重置关卡但保持难度，继续游戏
                currentLevel.resetForInfinite();
                // 逐渐增加难度
                obstacle1.speed++;
                obstacle2.speed++;
                if (currentLevel.levelNumber < 20) {
                    currentLevel.levelNumber++;
                }
            } else {
                // 普通模式：关卡完成
                state = GameState.LEVEL_COMPLETE;
                scoreManager.completeLevel(currentLevel.levelNumber);
                scoreManager.addCoins(currentLevel.coinsCollected);
                soundManager.playLevelComplete();

                // 成就更新 - 关卡成就
                String[] levelAchievements = {"初出茅庐", "小有成就", "经验丰富", "关卡大师", "通关达人"};
                int[] levelTargets = {1, 5, 10, 15, 20};
                for (int i = 0; i < levelAchievements.length; i++) {
                    if (currentLevel.levelNumber >= levelTargets[i]) {
                        int reward = achievementManager.updateAchievement(levelAchievements[i], currentLevel.levelNumber);
                        if (reward > 0) {
                            scoreManager.addRewardCoins(reward);  // 使用奖励金币方法
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新金币
     */
    private void updateCoins(int speed) {
        // 移动现有金币
        Iterator<Coin> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Coin c = coinIt.next();

            // 已收集的金币直接移除
            if (c.isCollected()) {
                coinIt.remove();
                continue;
            }

            c.step(speed);

            // 检查金币是否进入柱子区域且不在空隙内
            boolean inObstacle = false;

            // 检查障碍物1
            if (c.x > obstacle1.x - obstacle1.width / 2 - c.size / 2
                    && c.x < obstacle1.x + obstacle1.width / 2 + c.size / 2) {
                int[] gap1 = obstacle1.getGapBounds();
                if (c.y < gap1[0] - c.size / 2 || c.y > gap1[1] + c.size / 2) {
                    inObstacle = true;
                }
            }

            // 检查障碍物2
            if (c.x > obstacle2.x - obstacle2.width / 2 - c.size / 2
                    && c.x < obstacle2.x + obstacle2.width / 2 + c.size / 2) {
                int[] gap2 = obstacle2.getGapBounds();
                if (c.y < gap2[0] - c.size / 2 || c.y > gap2[1] + c.size / 2) {
                    inObstacle = true;
                }
            }

            // 如果金币进入柱子区域且不在空隙内，或者移出屏幕，就移除
            if (inObstacle || !c.isOnScreen()) {
                coinIt.remove();
            }
        }

        // 随机生成新金币（只在柱子之间的空隙里）
        if (random.nextInt(100) < 5) {  // 5%概率生成
            try {
                int x = GameConfig.WIDTH + 50;

                // 随机选择一个障碍物的空隙
                Obstacle targetObstacle = random.nextBoolean() ? obstacle1 : obstacle2;
                int[] gapBounds = targetObstacle.getGapBounds();
                int top = gapBounds[0];
                int bottom = gapBounds[1];

                // 确保空隙足够大
                if (bottom - top > GameConfig.COIN_SIZE * 2) {
                    // 在空隙内随机生成金币
                    int y = random.nextInt(bottom - top - GameConfig.COIN_SIZE) + top + GameConfig.COIN_SIZE / 2;
                    coins.add(new Coin(x, y));
                }
            } catch (Exception e) {
                // 生成失败静默处理
            }
        }
    }

    /**
     * 更新道具
     */
    private void updatePowerUps(int speed) {
        // 移动现有道具
        Iterator<PowerUp> powerUpIt = powerUps.iterator();
        while (powerUpIt.hasNext()) {
            PowerUp p = powerUpIt.next();
            p.step(speed);
            if (!p.isOnScreen()) {
                powerUpIt.remove();
            }
        }

        // 随机生成新道具（确保不与障碍物重叠）
        if (random.nextInt(1000) < 2) {  // 0.2%概率生成
            try {
                int x = GameConfig.WIDTH + 50;
                int y = random.nextInt(GameConfig.GROUND_Y - 100) + 50;

                // 检查是否与障碍物重叠
                boolean overlap = false;
                if (Math.abs(x - obstacle1.x) < obstacle1.width / 2 + GameConfig.POWERUP_SIZE) {
                    if (y < obstacle1.y - obstacle1.gap / 2 || y > obstacle1.y + obstacle1.gap / 2) {
                        overlap = true;
                    }
                }
                if (Math.abs(x - obstacle2.x) < obstacle2.width / 2 + GameConfig.POWERUP_SIZE) {
                    if (y < obstacle2.y - obstacle2.gap / 2 || y > obstacle2.y + obstacle2.gap / 2) {
                        overlap = true;
                    }
                }

                if (!overlap) {
                    PowerUp.PowerUpType type = PowerUp.PowerUpType.values()[random.nextInt(4)];
                    powerUps.add(new PowerUp(x, y, type));
                }
            } catch (Exception e) {
                // 生成失败静默处理
            }
        }
    }

    /**
     * 碰撞检测
     */
    private void checkCollisions() {
        // 无敌模式下不检测碰撞
        if (godMode) {
            // 金币碰撞
            for (Coin c : coins) {
                if (!c.isCollected() && bird.hitCoin(c)) {
                    c.collect();
                    currentLevel.collectCoin();
                    soundManager.playCoin();
                    particles.emitCoinCollect(c.x, c.y);
                }
            }

            // 道具碰撞
            for (PowerUp p : powerUps) {
                if (!p.isCollected() && bird.hitPowerUp(p)) {
                    p.collect();
                    activatePowerUp(p);
                    soundManager.playPowerUp();
                    particles.emitPowerUpCollect(p.x, p.y);
                }
            }
            return;
        }

        // 地面碰撞
        if (bird.hitGround()) {
            if (bird.useShield()) {
                // 护盾挡住地面碰撞
                soundManager.playHit();
                particles.emitExplosion(bird.x, bird.y);
                bird.y = GameConfig.GROUND_Y - bird.size;
                bird.velocity = -3;  // 弹起
            } else {
                handleDeath();
            }
            return;
        }

        // 障碍物碰撞
        if (bird.hitObstacle(obstacle1) || bird.hitObstacle(obstacle2)) {
            if (bird.useShield()) {
                // 护盾被消耗，播放音效和粒子特效
                soundManager.playHit();
                particles.emitExplosion(bird.x, bird.y);
            } else if (currentHealth > 1) {
                // 还有生命，扣一条命，短暂无敌
                currentHealth--;
                bird.isInvincible = true;
                bird.invincibleTimer = 120;  // 2秒无敌
                soundManager.playHit();
                particles.emitExplosion(bird.x, bird.y);
            } else {
                handleDeath();
            }
            return;
        }

        // 金币碰撞（磁铁吸附效果）
        for (Coin c : coins) {
            if (!c.isCollected()) {
                // 磁铁效果：金币向小鸟移动
                if (bird.hasMagnet) {
                    double dx = bird.x - c.x;
                    double dy = bird.y - c.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < 150 && dist > 0) {
                        c.x += (int)(dx / dist * 5);
                        c.y += (int)(dy / dist * 5);
                    }
                }
                if (bird.hitCoin(c)) {
                    c.collect();
                    currentLevel.collectCoin();
                    soundManager.playCoin();
                    particles.emitCoinCollect(c.x, c.y);
                    // 成就更新
                    int reward = achievementManager.updateAchievement("金币收集者", scoreManager.getTotalCoinsCollected());
                    if (reward > 0) {
                        scoreManager.addRewardCoins(reward);  // 使用奖励金币方法
                        cheatMessage = "成就解锁！+" + reward + "金币";
                        cheatMessageTimer = 120;
                    }
                }
            }
        }

        // 道具碰撞
        for (PowerUp p : powerUps) {
            if (!p.isCollected() && bird.hitPowerUp(p)) {
                p.collect();
                activatePowerUp(p);
                soundManager.playPowerUp();
                particles.emitPowerUpCollect(p.x, p.y);
                // 无限模式道具加分
                if (state == GameState.INFINITE) {
                    currentLevel.score += 5;
                }
                // 成就更新
                scoreManager.recordPowerUp();
                int reward = achievementManager.updateAchievement("道具收集者", scoreManager.getTotalPowerUps());
                if (reward > 0) {
                    scoreManager.addRewardCoins(reward);  // 使用奖励金币方法
                    cheatMessage = "成就解锁！+" + reward + "金币";
                    cheatMessageTimer = 120;
                }
            }
        }
    }

    /**
     * 激活道具效果
     */
    private void activatePowerUp(PowerUp p) {
        switch (p.type) {
            case STAR:
                bird.activateInvincible();
                break;
            case MAGNET:
                bird.activateMagnet();
                break;
            case SLOW:
                bird.activateSlow();
                break;
            case SHIELD:
                bird.activateShield();
                break;
        }
    }

    /**
     * 检查通过障碍物
     */
    private void checkPassedObstacles() {
        if (obstacle1.isPassed(bird.x)) {
            currentLevel.passObstacle();
        }
        if (obstacle2.isPassed(bird.x)) {
            currentLevel.passObstacle();
        }
    }

    /**
     * 处理死亡
     */
    private void handleDeath() {
        // 记录是否在无限模式
        wasInfiniteMode = (state == GameState.INFINITE);
        state = GameState.GAME_OVER;
        soundManager.playHit();
        soundManager.playGameOver();
        particles.emitExplosion(bird.x, bird.y);

        if (wasInfiniteMode) {
            // 无限模式：保存综合得分
            int totalScore = currentLevel.currentObstacles * 10 + currentLevel.coinsCollected * 5;
            scoreManager.saveInfiniteScore(totalScore);
        } else {
            scoreManager.saveHighScore(currentLevel.score);
        }
    }

    /**
     * 处理复活确认
     */
    private void handleReviveConfirm() {
        if (currentLevel.canReviveFree()) {
            // 免费复活
            currentLevel.useFreeRevive();
            bird.revive();
            // 无限模式下保持无限模式状态
            if (state != GameState.INFINITE) {
                state = GameState.RUNNING;
            }
        } else if (scoreManager.getTotalCoins() >= GameConfig.REVIVE_COST) {
            // 金币复活 - 需要确认
            if (scoreManager.spendCoins(GameConfig.REVIVE_COST)) {
                bird.revive();
                // 无限模式下保持无限模式状态
                if (state != GameState.INFINITE) {
                    state = GameState.RUNNING;
                }
            }
        } else {
            // 无法复活，重新开始
            restartGame();
        }
    }

    /**
     * 处理复活
     */
    private void handleRevive() {
        if (currentLevel.canReviveFree()) {
            // 免费复活
            currentLevel.useFreeRevive();
            bird.revive();
            state = GameState.RUNNING;
        } else if (scoreManager.spendCoins(GameConfig.REVIVE_COST)) {
            // 金币复活
            bird.revive();
            state = GameState.RUNNING;
        } else {
            // 重新开始
            restartGame();
        }
    }

    /**
     * 进入下一关
     */
    private void nextLevel() {
        if (currentLevel.levelNumber < GameConfig.TOTAL_LEVELS) {
            currentLevel = new Level(currentLevel.levelNumber + 1);
            resetGameObjects();
            state = GameState.START;
        } else {
            // 游戏通关
            restartGame();
        }
    }

    /**
     * 重新开始游戏
     */
    private void restartGame() {
        // 无限模式下从第一关开始
        if (wasInfiniteMode) {
            currentLevel = new Level(1);
            state = GameState.START;
            wasInfiniteMode = false;
        } else {
            currentLevel = new Level(currentLevel.levelNumber);
            state = GameState.START;
        }
        resetGameObjects();
    }

    /**
     * 重置游戏对象
     */
    private void resetGameObjects() {
        bird.reset();
        obstacle1.reset(1);
        obstacle2.reset(2);
        coins.clear();
        powerUps.clear();
        // 重置生命值
        currentHealth = maxHealth;
    }

    /**
     * 绘制淡入淡出效果
     */
    private void drawFadeEffect(Graphics g) {
        if (screenAlpha > 0) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0, 0, 0, screenAlpha));
            g2.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        }
    }

    /**
     * 开始淡入效果
     */
    private void startFadeIn() {
        screenAlpha = 255;
        new Thread(() -> {
            while (screenAlpha > 0) {
                screenAlpha = AnimationUtils.fadeOut(screenAlpha, 0, 10);
                try {
                    Thread.sleep(16); // 约60FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
            screenAlpha = 0;
        }).start();
    }

    /**
     * 开始淡出效果
     */
    private void startFadeOut() {
        screenAlpha = 0;
        new Thread(() -> {
            while (screenAlpha < 255) {
                screenAlpha = AnimationUtils.fadeIn(screenAlpha, 255, 10);
                try {
                    Thread.sleep(16); // 约60FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
            screenAlpha = 255;
        }).start();
    }
}

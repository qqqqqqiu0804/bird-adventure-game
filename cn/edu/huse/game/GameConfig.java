package cn.edu.huse.game;

import java.awt.Color;
import java.awt.Font;

/**
 * 游戏配置常量 - 飞翔小鸟冒险版
 */
public class GameConfig {
    // 窗口
    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;

    // 小鸟物理
    public static final double GRAVITY = 0.3;
    public static final double JUMP_STRENGTH = -5.0;
    public static final double MAX_FALL_SPEED = 6.0;
    public static final int BIRD_START_X = 100;
    public static final int BIRD_START_Y = 300;
    public static final int BIRD_SIZE = 30;

    // 障碍物
    public static final int OBSTACLE_GAP = 150;
    public static final int OBSTACLE_DISTANCE = 200;
    public static final int OBSTACLE_SPEED = 2;
    public static final int OBSTACLE_MIN_Y = 200;
    public static final int OBSTACLE_MAX_Y = 600;
    public static final int OBSTACLE_Y_RANGE = 400;

    // 地面
    public static final int GROUND_Y = 750;
    public static final int GROUND_RESET_X = -109;

    // 帧率
    public static final int FPS = 60;
    public static final int FRAME_DELAY = 1000 / FPS;

    // 金币
    public static final int COIN_SIZE = 20;
    public static final int COIN_VALUE = 10;

    // 道具
    public static final int POWERUP_SIZE = 25;
    public static final int POWERUP_DURATION_STAR = 300;    // 5秒 (60帧/秒)
    public static final int POWERUP_DURATION_MAGNET = 480;  // 8秒
    public static final int POWERUP_DURATION_SLOW = 360;    // 6秒

    // 复活
    public static final int REVIVE_COST = 50;  // 金币花费
    public static final int REVIVE_INVINCIBLE_TIME = 180;  // 3秒无敌

    // 关卡
    public static final int TOTAL_LEVELS = 20;
    public static final int[] LEVEL_OBSTACLES = {10, 10, 10, 10, 10, 15, 15, 15, 15, 15, 20, 20, 20, 20, 20, 25, 25, 25, 25, 25};
    public static final int[] LEVEL_COINS = {5, 5, 5, 5, 5, 10, 10, 10, 10, 10, 15, 15, 15, 15, 15, 20, 20, 20, 20, 20};

    // 粒子系统
    public static final int MAX_PARTICLES = 200;

    // 配色方案
    public static final Color COLOR_PRIMARY = new Color(26, 26, 46);      // 深蓝
    public static final Color COLOR_ACCENT = new Color(0, 212, 255);     // 青色
    public static final Color COLOR_WARNING = new Color(255, 107, 53);   // 橙色
    public static final Color COLOR_SUCCESS = new Color(0, 255, 136);    // 绿色
    public static final Color COLOR_TEXT = new Color(255, 255, 255);     // 白色
    public static final Color COLOR_TEXT_SECONDARY = new Color(180, 180, 200); // 浅灰
    public static final Color COLOR_BUTTON_HOVER = new Color(255, 255, 255, 50); // 悬停效果
    public static final Color COLOR_SHADOW = new Color(0, 0, 0, 80);    // 阴影

    // 字体配置
    public static Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 48);
    public static Font SUBTITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 24);
    public static Font BODY_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    public static Font NUMBER_FONT = new Font("Consolas", Font.BOLD, 20);
}

package cn.edu.huse.game;

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
}

package cn.edu.huse.game;

/**
 * 游戏状态枚举
 */
public enum GameState {
    MAIN_MENU,      // 主菜单
    LEVEL_SELECT,   // 关卡选择
    SKIN_SELECT,    // 皮肤选择
    SCENE_SELECT,   // 场景选择
    ACHIEVEMENTS,   // 成就系统
    UPGRADE,        // 升级系统
    START,          // 开始界面
    RUNNING,        // 游戏进行中
    PAUSED,         // 暂停
    GAME_OVER,      // 游戏结束
    REVIVE_CONFIRM, // 复活确认
    REVIVING,       // 复活中
    LEVEL_COMPLETE, // 关卡完成
    SHOP,           // 商店
    INFINITE,       // 无限模式
    LEADERBOARD,    // 排行榜
    OBSTACLE_SKIN   // 障碍物皮肤选择
}

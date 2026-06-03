package cn.edu.huse.game;

/**
 * 关卡类 - 飞翔小鸟冒险版
 */
public class Level {
    int levelNumber;
    int targetObstacles;
    int targetCoins;
    int currentObstacles;
    int currentCoins;
    boolean isCompleted;
    boolean isFirstDeath;
    int score;
    int coinsCollected;

    /**
     * 构造方法
     * @param levelNumber 关卡编号
     */
    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        this.targetObstacles = GameConfig.LEVEL_OBSTACLES[levelNumber - 1];
        this.targetCoins = GameConfig.LEVEL_COINS[levelNumber - 1];
        this.currentObstacles = 0;
        this.currentCoins = 0;
        this.isCompleted = false;
        this.isFirstDeath = true;
        this.score = 0;
        this.coinsCollected = 0;
    }

    /**
     * 更新通过的障碍物数量
     */
    public void passObstacle() {
        currentObstacles++;
        score++;
        checkCompletion();
    }

    /**
     * 更新收集的金币数量
     */
    public void collectCoin() {
        currentCoins++;
        coinsCollected++;
        checkCompletion();
    }

    /**
     * 检查关卡是否完成
     */
    private void checkCompletion() {
        if (levelNumber <= 5) {
            // 基础关：飞过指定数量障碍物
            isCompleted = currentObstacles >= targetObstacles;
        } else if (levelNumber <= 10) {
            // 收集关：吃到指定数量金币
            isCompleted = currentCoins >= targetCoins;
        } else if (levelNumber <= 15) {
            // 挑战关：飞过指定数量障碍物且不能死亡
            isCompleted = currentObstacles >= targetObstacles;
        } else {
            // Boss关：飞过指定数量障碍物
            isCompleted = currentObstacles >= targetObstacles;
        }
    }

    /**
     * 检查是否可以免费复活
     * @return true 如果可以免费复活
     */
    public boolean canReviveFree() {
        return isFirstDeath;
    }

    /**
     * 使用免费复活
     */
    public void useFreeRevive() {
        isFirstDeath = false;
    }

    /**
     * 获取关卡描述
     * @return 关卡描述字符串
     */
    public String getDescription() {
        if (levelNumber <= 5) {
            return "基础关：飞过 " + targetObstacles + " 个障碍物";
        } else if (levelNumber <= 10) {
            return "收集关：吃到 " + targetCoins + " 个金币";
        } else if (levelNumber <= 15) {
            return "挑战关：飞过 " + targetObstacles + " 个障碍物且不能死亡";
        } else {
            return "Boss关：飞过 " + targetObstacles + " 个障碍物";
        }
    }

    /**
     * 获取进度信息
     * @return 进度信息字符串
     */
    public String getProgress() {
        if (levelNumber <= 5) {
            return "障碍物: " + currentObstacles + "/" + targetObstacles;
        } else if (levelNumber <= 10) {
            return "金币: " + currentCoins + "/" + targetCoins;
        } else if (levelNumber <= 15) {
            return "障碍物: " + currentObstacles + "/" + targetObstacles + " (不能死亡)";
        } else {
            return "障碍物: " + currentObstacles + "/" + targetObstacles;
        }
    }

    /**
     * 重置关卡状态
     */
    public void reset() {
        currentObstacles = 0;
        currentCoins = 0;
        isCompleted = false;
        isFirstDeath = true;
        score = 0;
        coinsCollected = 0;
    }

    /**
     * 无限模式重置（保持难度，重置进度）
     */
    public void resetForInfinite() {
        currentObstacles = 0;
        currentCoins = 0;
        isCompleted = false;
        // 不重置 score 和 coinsCollected，累计计算
    }
}

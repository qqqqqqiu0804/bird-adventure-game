package cn.edu.huse.game;

import java.io.*;

/**
 * 分数管理类 - 飞翔小鸟冒险版
 */
public class ScoreManager {
    private static final String SAVE_DIR = System.getProperty("user.home") + "/.bird_adventure";
    private static final String SAVE_FILE = SAVE_DIR + "/save.dat";

    private int highScore;
    private int totalCoins;
    private int currentLevel;
    private boolean[] levelCompleted;
    private boolean[] skinUnlocked;
    private int selectedSkinIndex;
    private int totalDeaths;
    private int totalRevives;
    private int totalPowerUps;
    private int totalCoinsCollected;

    // 无限模式
    private int infiniteHighScore;
    private int[] leaderboard;  // 排行榜前5名

    /**
     * 构造方法
     */
    public ScoreManager() {
        levelCompleted = new boolean[GameConfig.TOTAL_LEVELS];
        skinUnlocked = new boolean[4]; // 4个皮肤
        skinUnlocked[0] = true; // 默认皮肤解锁
        selectedSkinIndex = 0;
        totalDeaths = 0;
        totalRevives = 0;
        totalPowerUps = 0;
        totalCoinsCollected = 0;
        infiniteHighScore = 0;
        leaderboard = new int[5];  // 前5名
        loadSaveData();
    }

    /**
     * 获取最高分
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * 获取总金币数
     */
    public int getTotalCoins() {
        return totalCoins;
    }

    /**
     * 获取当前关卡
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * 检查关卡是否已完成
     * @param level 关卡编号
     */
    public boolean isLevelCompleted(int level) {
        return levelCompleted[level - 1];
    }

    /**
     * 检查是否所有关卡都已完成
     */
    public boolean areAllLevelsCompleted() {
        for (boolean completed : levelCompleted) {
            if (!completed) return false;
        }
        return true;
    }

    /**
     * 保存最高分
     * @param score 当前分数
     * @return true 如果创造了新纪录
     */
    public boolean saveHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            saveSaveData();
            return true;
        }
        return false;
    }

    /**
     * 获取无限模式最高分
     */
    public int getInfiniteHighScore() {
        return infiniteHighScore;
    }

    /**
     * 保存无限模式得分
     * @param score 综合得分
     * @return true 如果创造了新纪录
     */
    public boolean saveInfiniteScore(int score) {
        boolean isNewRecord = false;
        if (score > infiniteHighScore) {
            infiniteHighScore = score;
            isNewRecord = true;
        }
        // 更新排行榜
        updateLeaderboard(score);
        saveSaveData();
        return isNewRecord;
    }

    /**
     * 更新排行榜
     */
    private void updateLeaderboard(int score) {
        // 插入排序
        for (int i = 0; i < leaderboard.length; i++) {
            if (score > leaderboard[i]) {
                // 后移
                for (int j = leaderboard.length - 1; j > i; j--) {
                    leaderboard[j] = leaderboard[j - 1];
                }
                leaderboard[i] = score;
                break;
            }
        }
    }

    /**
     * 获取排行榜
     */
    public int[] getLeaderboard() {
        return leaderboard;
    }

    /**
     * 添加金币（游戏内收集）
     * @param coins 金币数量
     */
    public void addCoins(int coins) {
        totalCoins += coins;
        totalCoinsCollected += coins;
        saveSaveData();
    }

    /**
     * 添加奖励金币（成就奖励等，不计入总收集数）
     * @param coins 金币数量
     */
    public void addRewardCoins(int coins) {
        totalCoins += coins;
        saveSaveData();
    }

    /**
     * 花费金币
     * @param coins 金币数量
     * @return true 如果金币足够
     */
    public boolean spendCoins(int coins) {
        if (totalCoins >= coins) {
            totalCoins -= coins;
            saveSaveData();
            return true;
        }
        return false;
    }

    /**
     * 重置金币数量（作弊模式关闭时使用）
     * @param coins 要设置的金币数量
     */
    public void resetCoins(int coins) {
        totalCoins = coins;
        saveSaveData();
    }

    /**
     * 完成关卡
     * @param level 关卡编号
     */
    public void completeLevel(int level) {
        levelCompleted[level - 1] = true;
        if (level < GameConfig.TOTAL_LEVELS) {
            currentLevel = level + 1;
        }
        saveSaveData();
    }

    /**
     * 记录死亡
     */
    public void recordDeath() {
        totalDeaths++;
        saveSaveData();
    }

    /**
     * 记录复活
     */
    public void recordRevive() {
        totalRevives++;
        saveSaveData();
    }

    /**
     * 记录道具收集
     */
    public void recordPowerUp() {
        totalPowerUps++;
        saveSaveData();
    }

    /**
     * 获取总死亡次数
     */
    public int getTotalDeaths() {
        return totalDeaths;
    }

    /**
     * 获取总复活次数
     */
    public int getTotalRevives() {
        return totalRevives;
    }

    /**
     * 获取总道具收集数
     */
    public int getTotalPowerUps() {
        return totalPowerUps;
    }

    /**
     * 获取总金币收集数
     */
    public int getTotalCoinsCollected() {
        return totalCoinsCollected;
    }

    /**
     * 检查皮肤是否已解锁
     */
    public boolean isSkinUnlocked(int index) {
        if (index >= 0 && index < skinUnlocked.length) {
            return skinUnlocked[index];
        }
        return false;
    }

    /**
     * 解锁皮肤
     */
    public void unlockSkin(int index) {
        if (index >= 0 && index < skinUnlocked.length) {
            skinUnlocked[index] = true;
            saveSaveData();
        }
    }

    /**
     * 获取选中的皮肤索引
     */
    public int getSelectedSkinIndex() {
        return selectedSkinIndex;
    }

    /**
     * 设置选中的皮肤索引
     */
    public void setSelectedSkinIndex(int index) {
        if (index >= 0 && index < skinUnlocked.length && skinUnlocked[index]) {
            selectedSkinIndex = index;
            saveSaveData();
        }
    }

    /**
     * 加载存档数据
     */
    private void loadSaveData() {
        try {
            File file = new File(SAVE_FILE);
            if (!file.exists()) {
                // 初始化默认值
                highScore = 0;
                totalCoins = 100;  // 初始金币100
                currentLevel = 1;
                infiniteHighScore = 0;
                leaderboard = new int[5];
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            highScore = Integer.parseInt(reader.readLine().trim());
            totalCoins = Integer.parseInt(reader.readLine().trim());
            currentLevel = Integer.parseInt(reader.readLine().trim());

            // 读取关卡完成状态
            String levelStatus = reader.readLine().trim();
            for (int i = 0; i < Math.min(levelStatus.length(), GameConfig.TOTAL_LEVELS); i++) {
                levelCompleted[i] = levelStatus.charAt(i) == '1';
            }

            // 读取皮肤解锁状态
            String skinStatus = reader.readLine().trim();
            for (int i = 0; i < Math.min(skinStatus.length(), skinUnlocked.length); i++) {
                skinUnlocked[i] = skinStatus.charAt(i) == '1';
            }

            // 读取选中的皮肤索引
            selectedSkinIndex = Integer.parseInt(reader.readLine().trim());

            // 读取统计数据
            totalDeaths = Integer.parseInt(reader.readLine().trim());
            totalRevives = Integer.parseInt(reader.readLine().trim());
            totalPowerUps = Integer.parseInt(reader.readLine().trim());
            totalCoinsCollected = Integer.parseInt(reader.readLine().trim());

            // 读取无限模式最高分（兼容旧存档）
            String line = reader.readLine();
            if (line != null) {
                infiniteHighScore = Integer.parseInt(line.trim());
            }

            // 读取排行榜（兼容旧存档）
            line = reader.readLine();
            if (line != null) {
                String[] parts = line.trim().split(",");
                for (int i = 0; i < Math.min(parts.length, 5); i++) {
                    leaderboard[i] = Integer.parseInt(parts[i].trim());
                }
            }

            reader.close();
        } catch (Exception e) {
            // 文件损坏或不存在，使用默认值
            highScore = 0;
            totalCoins = 0;
            currentLevel = 1;
        }
    }

    /**
     * 保存存档数据
     */
    private void saveSaveData() {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) dir.mkdirs();

            PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE));
            writer.println(highScore);
            writer.println(totalCoins);
            writer.println(currentLevel);

            // 保存关卡完成状态
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < GameConfig.TOTAL_LEVELS; i++) {
                sb.append(levelCompleted[i] ? '1' : '0');
            }
            writer.println(sb.toString());

            // 保存皮肤解锁状态
            StringBuilder skinSb = new StringBuilder();
            for (int i = 0; i < skinUnlocked.length; i++) {
                skinSb.append(skinUnlocked[i] ? '1' : '0');
            }
            writer.println(skinSb.toString());

            // 保存选中的皮肤索引
            writer.println(selectedSkinIndex);

            // 保存统计数据
            writer.println(totalDeaths);
            writer.println(totalRevives);
            writer.println(totalPowerUps);
            writer.println(totalCoinsCollected);

            // 保存无限模式最高分
            writer.println(infiniteHighScore);

            // 保存排行榜
            StringBuilder lbSb = new StringBuilder();
            for (int i = 0; i < leaderboard.length; i++) {
                if (i > 0) lbSb.append(",");
                lbSb.append(leaderboard[i]);
            }
            writer.println(lbSb.toString());

            writer.close();
        } catch (Exception e) {
            // 保存失败静默处理
        }
    }
}

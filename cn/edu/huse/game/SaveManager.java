package cn.edu.huse.game;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 存档管理器 - 飞翔小鸟冒险版
 * 支持3个存档槽位、导入导出、重命名
 */
public class SaveManager {
    private static final String SAVE_DIR = System.getProperty("user.home") + "/.bird_adventure";
    private static final String SAVE_PREFIX = "/save_slot_";
    private static final String SAVE_SUFFIX = ".json";

    private SaveSlot[] slots;
    private int currentSlot;  // 当前使用的槽位，-1表示未选择

    /**
     * 存档槽位内部类
     */
    public static class SaveSlot {
        public String name;
        public String timestamp;
        public GameSaveData data;
        public boolean isEmpty;

        public SaveSlot() {
            this.isEmpty = true;
            this.name = "空槽位";
            this.timestamp = "";
            this.data = null;
        }
    }

    /**
     * 游戏存档数据
     */
    public static class GameSaveData {
        // 游戏进度
        public int highScore;
        public int totalCoins;
        public int currentLevel;
        public boolean[] levelCompleted;
        public boolean[] skinUnlocked;
        public int selectedSkin;
        public int selectedScene;
        public int selectedObstacleSkin;
        public int infiniteHighScore;
        public int[] leaderboard;

        // 统计数据
        public int totalDeaths;
        public int totalRevives;
        public int totalPowerUps;
        public int totalCoinsCollected;

        // 成就数据
        public int[] achievementProgress;
        public boolean[] achievementUnlocked;

        // 教程完成状态
        public boolean tutorialCompleted;
    }

    /**
     * 构造方法
     */
    public SaveManager() {
        slots = new SaveSlot[3];
        currentSlot = -1;
        for (int i = 0; i < 3; i++) {
            slots[i] = new SaveSlot();
        }
        loadAllSlots();
    }

    /**
     * 加载所有槽位信息
     */
    private void loadAllSlots() {
        for (int i = 0; i < 3; i++) {
            loadSlotInfo(i);
        }
    }

    /**
     * 加载槽位信息（不加载完整数据）
     */
    private void loadSlotInfo(int slotIndex) {
        String filePath = getSlotFilePath(slotIndex);
        File file = new File(filePath);

        if (!file.exists()) {
            slots[slotIndex] = new SaveSlot();
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            // 读取名称
            line = reader.readLine();
            if (line != null) {
                slots[slotIndex].name = line.trim();
            }

            // 读取时间戳
            line = reader.readLine();
            if (line != null) {
                slots[slotIndex].timestamp = line.trim();
            }

            slots[slotIndex].isEmpty = false;
            reader.close();
        } catch (Exception e) {
            slots[slotIndex] = new SaveSlot();
        }
    }

    /**
     * 获取槽位文件路径
     */
    private String getSlotFilePath(int slotIndex) {
        return SAVE_DIR + SAVE_PREFIX + (slotIndex + 1) + SAVE_SUFFIX;
    }

    /**
     * 获取槽位信息
     */
    public SaveSlot getSlotInfo(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            return slots[slotIndex];
        }
        return null;
    }

    /**
     * 获取所有槽位
     */
    public SaveSlot[] getAllSlots() {
        return slots;
    }

    /**
     * 获取当前槽位
     */
    public int getCurrentSlot() {
        return currentSlot;
    }

    /**
     * 设置当前槽位
     */
    public void setCurrentSlot(int slot) {
        this.currentSlot = slot;
    }

    /**
     * 保存游戏到指定槽位
     */
    public boolean saveGame(int slotIndex, ScoreManager scoreManager, AchievementManager achievementManager,
                            int selectedSkin, int selectedScene, int selectedObstacleSkin) {
        if (slotIndex < 0 || slotIndex >= 3) return false;

        try {
            // 创建存档目录
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) dir.mkdirs();

            // 准备存档数据
            GameSaveData data = new GameSaveData();
            data.highScore = scoreManager.getHighScore();
            data.totalCoins = scoreManager.getTotalCoins();
            data.currentLevel = scoreManager.getCurrentLevel();
            data.levelCompleted = scoreManager.getLevelCompleted();
            data.skinUnlocked = scoreManager.getSkinUnlocked();
            data.selectedSkin = selectedSkin;
            data.selectedScene = selectedScene;
            data.selectedObstacleSkin = selectedObstacleSkin;
            data.infiniteHighScore = scoreManager.getInfiniteHighScore();
            data.leaderboard = scoreManager.getLeaderboard();
            data.totalDeaths = scoreManager.getTotalDeaths();
            data.totalRevives = scoreManager.getTotalRevives();
            data.totalPowerUps = scoreManager.getTotalPowerUps();
            data.totalCoinsCollected = scoreManager.getTotalCoinsCollected();

            // 保存成就数据
            data.achievementProgress = achievementManager.getProgressArray();
            data.achievementUnlocked = achievementManager.getUnlockedArray();

            // 生成存档名称
            String name = generateSaveName(data);

            // 生成时间戳
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 写入文件
            PrintWriter writer = new PrintWriter(new FileWriter(getSlotFilePath(slotIndex)));
            writer.println(name);
            writer.println(timestamp);

            // 写入游戏数据（简单格式）
            writer.println(data.highScore);
            writer.println(data.totalCoins);
            writer.println(data.currentLevel);

            // 关卡完成状态
            StringBuilder levelSb = new StringBuilder();
            for (boolean b : data.levelCompleted) {
                levelSb.append(b ? '1' : '0');
            }
            writer.println(levelSb.toString());

            // 皮肤解锁状态
            StringBuilder skinSb = new StringBuilder();
            for (boolean b : data.skinUnlocked) {
                skinSb.append(b ? '1' : '0');
            }
            writer.println(skinSb.toString());

            writer.println(data.selectedSkin);
            writer.println(data.selectedScene);
            writer.println(data.selectedObstacleSkin);
            writer.println(data.infiniteHighScore);

            // 排行榜
            StringBuilder lbSb = new StringBuilder();
            for (int i = 0; i < data.leaderboard.length; i++) {
                if (i > 0) lbSb.append(",");
                lbSb.append(data.leaderboard[i]);
            }
            writer.println(lbSb.toString());

            writer.println(data.totalDeaths);
            writer.println(data.totalRevives);
            writer.println(data.totalPowerUps);
            writer.println(data.totalCoinsCollected);

            // 成就进度
            StringBuilder apSb = new StringBuilder();
            for (int i = 0; i < data.achievementProgress.length; i++) {
                if (i > 0) apSb.append(",");
                apSb.append(data.achievementProgress[i]);
            }
            writer.println(apSb.toString());

            // 成就解锁状态
            StringBuilder auSb = new StringBuilder();
            for (boolean b : data.achievementUnlocked) {
                auSb.append(b ? '1' : '0');
            }
            writer.println(auSb.toString());

            // 教程完成状态
            writer.println(data.tutorialCompleted ? "1" : "0");

            writer.close();

            // 更新槽位信息
            slots[slotIndex].name = name;
            slots[slotIndex].timestamp = timestamp;
            slots[slotIndex].isEmpty = false;
            slots[slotIndex].data = data;
            currentSlot = slotIndex;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从指定槽位加载游戏
     */
    public GameSaveData loadGame(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 3 || slots[slotIndex].isEmpty) {
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(getSlotFilePath(slotIndex)));

            // 跳过名称和时间戳
            reader.readLine();
            reader.readLine();

            GameSaveData data = new GameSaveData();

            data.highScore = Integer.parseInt(reader.readLine().trim());
            data.totalCoins = Integer.parseInt(reader.readLine().trim());
            data.currentLevel = Integer.parseInt(reader.readLine().trim());

            // 关卡完成状态
            String levelStatus = reader.readLine().trim();
            data.levelCompleted = new boolean[Math.min(levelStatus.length(), 20)];
            for (int i = 0; i < data.levelCompleted.length; i++) {
                data.levelCompleted[i] = levelStatus.charAt(i) == '1';
            }

            // 皮肤解锁状态
            String skinStatus = reader.readLine().trim();
            data.skinUnlocked = new boolean[Math.min(skinStatus.length(), 4)];
            for (int i = 0; i < data.skinUnlocked.length; i++) {
                data.skinUnlocked[i] = skinStatus.charAt(i) == '1';
            }

            data.selectedSkin = Integer.parseInt(reader.readLine().trim());
            data.selectedScene = Integer.parseInt(reader.readLine().trim());
            data.selectedObstacleSkin = Integer.parseInt(reader.readLine().trim());
            data.infiniteHighScore = Integer.parseInt(reader.readLine().trim());

            // 排行榜
            String[] lbParts = reader.readLine().trim().split(",");
            data.leaderboard = new int[5];
            for (int i = 0; i < Math.min(lbParts.length, 5); i++) {
                data.leaderboard[i] = Integer.parseInt(lbParts[i].trim());
            }

            data.totalDeaths = Integer.parseInt(reader.readLine().trim());
            data.totalRevives = Integer.parseInt(reader.readLine().trim());
            data.totalPowerUps = Integer.parseInt(reader.readLine().trim());
            data.totalCoinsCollected = Integer.parseInt(reader.readLine().trim());

            // 成就进度
            String[] apParts = reader.readLine().trim().split(",");
            data.achievementProgress = new int[apParts.length];
            for (int i = 0; i < apParts.length; i++) {
                data.achievementProgress[i] = Integer.parseInt(apParts[i].trim());
            }

            // 成就解锁状态
            String auStatus = reader.readLine().trim();
            data.achievementUnlocked = new boolean[auStatus.length()];
            for (int i = 0; i < auStatus.length(); i++) {
                data.achievementUnlocked[i] = auStatus.charAt(i) == '1';
            }

            // 教程完成状态
            String tutorialLine = reader.readLine();
            data.tutorialCompleted = tutorialLine != null && tutorialLine.trim().equals("1");

            reader.close();

            slots[slotIndex].data = data;
            currentSlot = slotIndex;
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除指定槽位
     */
    public boolean deleteGame(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 3) return false;

        File file = new File(getSlotFilePath(slotIndex));
        if (file.exists()) {
            file.delete();
        }

        slots[slotIndex] = new SaveSlot();
        if (currentSlot == slotIndex) {
            currentSlot = -1;
        }
        return true;
    }

    /**
     * 重命名存档
     */
    public boolean renameGame(int slotIndex, String newName) {
        if (slotIndex < 0 || slotIndex >= 3 || slots[slotIndex].isEmpty) return false;

        try {
            // 读取原文件内容
            String filePath = getSlotFilePath(slotIndex);
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String timestamp = reader.readLine(); // 跳过原名称
            timestamp = reader.readLine(); // 获取时间戳

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            // 重写文件
            PrintWriter writer = new PrintWriter(new FileWriter(filePath));
            writer.println(newName);
            writer.println(timestamp);
            writer.print(content.toString());
            writer.close();

            slots[slotIndex].name = newName;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 导出存档为JSON
     */
    public String exportSave(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 3 || slots[slotIndex].isEmpty) {
            return null;
        }

        GameSaveData data = slots[slotIndex].data;
        if (data == null) {
            data = loadGame(slotIndex);
            if (data == null) return null;
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"slot\": ").append(slotIndex + 1).append(",\n");
        json.append("  \"name\": \"").append(slots[slotIndex].name).append("\",\n");
        json.append("  \"timestamp\": \"").append(slots[slotIndex].timestamp).append("\",\n");
        json.append("  \"gameData\": {\n");
        json.append("    \"highScore\": ").append(data.highScore).append(",\n");
        json.append("    \"totalCoins\": ").append(data.totalCoins).append(",\n");
        json.append("    \"currentLevel\": ").append(data.currentLevel).append(",\n");
        json.append("    \"selectedSkin\": ").append(data.selectedSkin).append(",\n");
        json.append("    \"selectedScene\": ").append(data.selectedScene).append(",\n");
        json.append("    \"selectedObstacleSkin\": ").append(data.selectedObstacleSkin).append(",\n");
        json.append("    \"infiniteHighScore\": ").append(data.infiniteHighScore).append(",\n");
        json.append("    \"totalDeaths\": ").append(data.totalDeaths).append(",\n");
        json.append("    \"totalRevives\": ").append(data.totalRevives).append(",\n");
        json.append("    \"totalPowerUps\": ").append(data.totalPowerUps).append(",\n");
        json.append("    \"totalCoinsCollected\": ").append(data.totalCoinsCollected).append("\n");
        json.append("  }\n");
        json.append("}");

        return json.toString();
    }

    /**
     * 从JSON导入存档
     */
    public boolean importSave(int slotIndex, String json) {
        if (slotIndex < 0 || slotIndex >= 3) return false;

        try {
            // 简单解析JSON（实际项目应使用JSON库）
            // 这里简化处理，只提取关键数据
            GameSaveData data = new GameSaveData();

            // 提取highScore
            data.highScore = extractJsonInt(json, "highScore");
            data.totalCoins = extractJsonInt(json, "totalCoins");
            data.currentLevel = extractJsonInt(json, "currentLevel");
            data.selectedSkin = extractJsonInt(json, "selectedSkin");
            data.selectedScene = extractJsonInt(json, "selectedScene");
            data.selectedObstacleSkin = extractJsonInt(json, "selectedObstacleSkin");
            data.infiniteHighScore = extractJsonInt(json, "infiniteHighScore");
            data.totalDeaths = extractJsonInt(json, "totalDeaths");
            data.totalRevives = extractJsonInt(json, "totalRevives");
            data.totalPowerUps = extractJsonInt(json, "totalPowerUps");
            data.totalCoinsCollected = extractJsonInt(json, "totalCoinsCollected");

            // 初始化其他数据
            data.levelCompleted = new boolean[20];
            data.skinUnlocked = new boolean[4];
            data.skinUnlocked[0] = true;
            data.leaderboard = new int[5];
            data.achievementProgress = new int[15];
            data.achievementUnlocked = new boolean[15];

            // 保存到槽位
            slots[slotIndex].data = data;
            slots[slotIndex].name = extractJsonString(json, "name");
            slots[slotIndex].timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            slots[slotIndex].isEmpty = false;

            // 保存到文件
            saveSlotToFile(slotIndex);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 简单提取JSON整数值
     */
    private int extractJsonInt(String json, String key) {
        try {
            String search = "\"" + key + "\": ";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Integer.parseInt(json.substring(start, end).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 简单提取JSON字符串值
     */
    private String extractJsonString(String json, String key) {
        try {
            String search = "\"" + key + "\": \"";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "导入的存档";
        }
    }

    /**
     * 保存槽位到文件
     */
    private void saveSlotToFile(int slotIndex) {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) dir.mkdirs();

            SaveSlot slot = slots[slotIndex];
            PrintWriter writer = new PrintWriter(new FileWriter(getSlotFilePath(slotIndex)));
            writer.println(slot.name);
            writer.println(slot.timestamp);

            if (slot.data != null) {
                writer.println(slot.data.highScore);
                writer.println(slot.data.totalCoins);
                writer.println(slot.data.currentLevel);

                StringBuilder levelSb = new StringBuilder();
                for (boolean b : slot.data.levelCompleted) {
                    levelSb.append(b ? '1' : '0');
                }
                writer.println(levelSb.toString());

                StringBuilder skinSb = new StringBuilder();
                for (boolean b : slot.data.skinUnlocked) {
                    skinSb.append(b ? '1' : '0');
                }
                writer.println(skinSb.toString());

                writer.println(slot.data.selectedSkin);
                writer.println(slot.data.selectedScene);
                writer.println(slot.data.selectedObstacleSkin);
                writer.println(slot.data.infiniteHighScore);

                StringBuilder lbSb = new StringBuilder();
                for (int i = 0; i < slot.data.leaderboard.length; i++) {
                    if (i > 0) lbSb.append(",");
                    lbSb.append(slot.data.leaderboard[i]);
                }
                writer.println(lbSb.toString());

                writer.println(slot.data.totalDeaths);
                writer.println(slot.data.totalRevives);
                writer.println(slot.data.totalPowerUps);
                writer.println(slot.data.totalCoinsCollected);

                StringBuilder apSb = new StringBuilder();
                for (int i = 0; i < slot.data.achievementProgress.length; i++) {
                    if (i > 0) apSb.append(",");
                    apSb.append(slot.data.achievementProgress[i]);
                }
                writer.println(apSb.toString());

                StringBuilder auSb = new StringBuilder();
                for (boolean b : slot.data.achievementUnlocked) {
                    auSb.append(b ? '1' : '0');
                }
                writer.println(auSb.toString());

                // 教程完成状态
                writer.println(slot.data.tutorialCompleted ? "1" : "0");
            }

            writer.close();
        } catch (Exception e) {
            // 静默处理
        }
    }

    /**
     * 生成存档名称
     */
    private String generateSaveName(GameSaveData data) {
        return "关卡" + data.currentLevel + "-金币" + data.totalCoins;
    }

    /**
     * 自动保存到当前槽位
     */
    public void autoSave(ScoreManager scoreManager, AchievementManager achievementManager,
                         int selectedSkin, int selectedScene, int selectedObstacleSkin) {
        if (currentSlot >= 0 && currentSlot < 3) {
            saveGame(currentSlot, scoreManager, achievementManager, selectedSkin, selectedScene, selectedObstacleSkin);
        } else {
            // 查找第一个空槽位
            for (int i = 0; i < 3; i++) {
                if (slots[i].isEmpty) {
                    saveGame(i, scoreManager, achievementManager, selectedSkin, selectedScene, selectedObstacleSkin);
                    break;
                }
            }
        }
    }

    /**
     * 获取教程完成状态
     * @return 教程是否已完成
     */
    public boolean isTutorialCompleted() {
        if (currentSlot >= 0 && currentSlot < 3 && slots[currentSlot].data != null) {
            return slots[currentSlot].data.tutorialCompleted;
        }
        return false;
    }

    /**
     * 设置教程完成状态
     * @param completed 完成状态
     */
    public void setTutorialCompleted(boolean completed) {
        if (currentSlot >= 0 && currentSlot < 3 && slots[currentSlot].data != null) {
            slots[currentSlot].data.tutorialCompleted = completed;
        }
    }
}

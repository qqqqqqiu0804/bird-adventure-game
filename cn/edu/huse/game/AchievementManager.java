package cn.edu.huse.game;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 成就管理器 - 飞翔小鸟冒险版
 */
public class AchievementManager {
    List<Achievement> achievements;
    private static final String SAVE_DIR = System.getProperty("user.home") + "/.bird_adventure";
    private static final String ACHIEVEMENT_FILE = SAVE_DIR + "/achievements.dat";

    /**
     * 构造方法
     */
    public AchievementManager() {
        achievements = new ArrayList<>();
        initAchievements();
        loadAchievements();
    }

    /**
     * 初始化成就
     */
    private void initAchievements() {
        // 关卡成就
        achievements.add(new Achievement("初出茅庐", "完成第一关", 1, 50));
        achievements.add(new Achievement("小有成就", "完成第五关", 5, 100));
        achievements.add(new Achievement("经验丰富", "完成第十关", 10, 200));
        achievements.add(new Achievement("关卡大师", "完成第十五关", 15, 300));
        achievements.add(new Achievement("通关达人", "完成所有20关", 20, 500));

        // 金币成就
        achievements.add(new Achievement("金币收集者", "收集100个金币", 100, 50));
        achievements.add(new Achievement("金币富翁", "收集500个金币", 500, 150));
        achievements.add(new Achievement("金币大亨", "收集1000个金币", 1000, 300));

        // 得分成就
        achievements.add(new Achievement("得分新手", "获得10分", 10, 30));
        achievements.add(new Achievement("得分高手", "获得50分", 50, 100));
        achievements.add(new Achievement("得分大师", "获得100分", 100, 200));

        // 复活成就
        achievements.add(new Achievement("首次复活", "使用一次复活", 1, 20));
        achievements.add(new Achievement("复活专家", "使用10次复活", 10, 100));

        // 道具成就
        achievements.add(new Achievement("道具收集者", "收集10个道具", 10, 50));
        achievements.add(new Achievement("道具大师", "收集50个道具", 50, 150));
    }

    /**
     * 加载成就数据
     */
    private void loadAchievements() {
        try {
            File file = new File(ACHIEVEMENT_FILE);
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (Achievement a : achievements) {
                String line = reader.readLine();
                if (line != null) {
                    String[] parts = line.trim().split(",");
                    if (parts.length >= 2) {
                        a.setProgress(Integer.parseInt(parts[0]));
                        if (parts[1].equals("1")) {
                            a.unlock();
                        }
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            // 加载失败静默处理
        }
    }

    /**
     * 保存成就数据
     */
    public void saveAchievements() {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) dir.mkdirs();

            PrintWriter writer = new PrintWriter(new FileWriter(ACHIEVEMENT_FILE));
            for (Achievement a : achievements) {
                writer.println(a.getProgress() + "," + (a.isUnlocked() ? "1" : "0"));
            }
            writer.close();
        } catch (Exception e) {
            // 保存失败静默处理
        }
    }

    /**
     * 获取所有成就
     */
    public List<Achievement> getAchievements() {
        return achievements;
    }

    /**
     * 获取已解锁成就数量
     */
    public int getUnlockedCount() {
        int count = 0;
        for (Achievement a : achievements) {
            if (a.isUnlocked()) count++;
        }
        return count;
    }

    /**
     * 更新成就进度
     * @return 金币奖励，如果没有解锁则返回0
     */
    public int updateAchievement(String name, int progress) {
        for (Achievement a : achievements) {
            if (a.getName().equals(name)) {
                if (a.updateProgress(progress)) {
                    saveAchievements();  // 保存成就
                    return a.getCoinReward();
                }
                break;
            }
        }
        return 0;
    }

    /**
     * 增加成就进度
     * @return 金币奖励，如果没有解锁则返回0
     */
    public int addAchievementProgress(String name, int amount) {
        for (Achievement a : achievements) {
            if (a.getName().equals(name)) {
                if (a.addProgress(amount)) {
                    saveAchievements();  // 保存成就
                    return a.getCoinReward();
                }
                break;
            }
        }
        return 0;
    }

    /**
     * 检查成就是否解锁
     */
    public boolean isAchievementUnlocked(String name) {
        for (Achievement a : achievements) {
            if (a.getName().equals(name)) {
                return a.isUnlocked();
            }
        }
        return false;
    }

    /**
     * 获取成就进度数组
     */
    public int[] getProgressArray() {
        int[] progress = new int[achievements.size()];
        for (int i = 0; i < achievements.size(); i++) {
            progress[i] = achievements.get(i).getProgress();
        }
        return progress;
    }

    /**
     * 获取成就解锁状态数组
     */
    public boolean[] getUnlockedArray() {
        boolean[] unlocked = new boolean[achievements.size()];
        for (int i = 0; i < achievements.size(); i++) {
            unlocked[i] = achievements.get(i).isUnlocked();
        }
        return unlocked;
    }

    /**
     * 加载成就数据
     */
    public void loadData(int[] progress, boolean[] unlocked) {
        for (int i = 0; i < Math.min(progress.length, achievements.size()); i++) {
            achievements.get(i).setProgress(progress[i]);
            if (unlocked[i]) {
                achievements.get(i).unlock();
            }
        }
        saveAchievements();
    }
}

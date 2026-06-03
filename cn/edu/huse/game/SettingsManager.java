package cn.edu.huse.game;

import java.io.*;

/**
 * 设置管理器 - 飞翔小鸟冒险版
 * 管理音量、金币重置、恢复出厂设置
 */
public class SettingsManager {
    private static final String SETTINGS_DIR = System.getProperty("user.home") + "/.bird_adventure";
    private static final String SETTINGS_FILE = SETTINGS_DIR + "/settings.dat";

    private int sfxVolume;      // 音效音量 0-100
    private boolean dirty;      // 是否需要保存

    /**
     * 构造方法
     */
    public SettingsManager() {
        sfxVolume = 60;
        dirty = false;
        loadSettings();
    }

    /**
     * 加载设置
     */
    private void loadSettings() {
        try {
            File file = new File(SETTINGS_FILE);
            if (!file.exists()) {
                // 使用默认值
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            sfxVolume = Integer.parseInt(reader.readLine().trim());
            reader.close();

            // 确保值在有效范围内
            sfxVolume = Math.max(0, Math.min(100, sfxVolume));
        } catch (Exception e) {
            // 使用默认值
            sfxVolume = 60;
        }
    }

    /**
     * 保存设置
     */
    public void saveSettings() {
        if (!dirty) return;

        try {
            File dir = new File(SETTINGS_DIR);
            if (!dir.exists()) dir.mkdirs();

            PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE));
            writer.println(sfxVolume);
            writer.close();

            dirty = false;
        } catch (Exception e) {
            // 静默处理
        }
    }

    /**
     * 获取音效音量
     */
    public int getSfxVolume() {
        return sfxVolume;
    }

    /**
     * 设置音效音量
     */
    public void setSfxVolume(int volume) {
        this.sfxVolume = Math.max(0, Math.min(100, volume));
        dirty = true;
        saveSettings();
    }

    /**
     * 重置金币（保留其他进度）
     */
    public void resetCoins(ScoreManager scoreManager) {
        scoreManager.resetCoins(0);
    }

    /**
     * 恢复出厂设置
     */
    public void factoryReset() {
        // 删除所有存档
        File saveDir = new File(SETTINGS_DIR);
        if (saveDir.exists()) {
            File[] files = saveDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        // 重置设置
        sfxVolume = 60;
        dirty = true;
        saveSettings();
    }

    /**
     * 检查是否有未保存的设置
     */
    public boolean isDirty() {
        return dirty;
    }
}

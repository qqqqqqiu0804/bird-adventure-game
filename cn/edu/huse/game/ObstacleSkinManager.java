package cn.edu.huse.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 障碍物皮肤管理器 - 飞翔小鸟冒险版
 */
public class ObstacleSkinManager {
    List<ObstacleSkin> skins;
    int selectedSkinIndex;

    /**
     * 构造方法
     */
    public ObstacleSkinManager() {
        skins = new ArrayList<>();
        selectedSkinIndex = 0;
        initSkins();
    }

    /**
     * 初始化障碍物皮肤
     */
    private void initSkins() {
        // 默认皮肤 - 太空站
        skins.add(new ObstacleSkin("太空站", "经典太空站风格", true, 0,
                new Color(80, 85, 95), new Color(150, 160, 180),
                new Color(0, 200, 255), new Color(100, 150, 255), "spacestation"));

        // 解锁皮肤
        skins.add(new ObstacleSkin("竹林", "翠竹风格", false, 200,
                new Color(50, 120, 30), new Color(40, 100, 25),
                new Color(30, 150, 20), new Color(60, 180, 40), "bamboo"));

        skins.add(new ObstacleSkin("冰川", "冰雪世界风格", false, 300,
                new Color(200, 230, 255), new Color(100, 180, 230),
                new Color(150, 220, 255), new Color(180, 240, 255), "ice"));

        skins.add(new ObstacleSkin("水晶", "神秘水晶风格", false, 400,
                new Color(150, 50, 200), new Color(200, 100, 255),
                new Color(255, 150, 255), new Color(200, 100, 255), "crystal"));

        skins.add(new ObstacleSkin("木桩", "自然木桩风格", false, 500,
                new Color(139, 90, 43), new Color(100, 65, 30),
                new Color(180, 120, 60), new Color(160, 100, 50), "wood"));
    }

    /**
     * 获取所有皮肤
     */
    public List<ObstacleSkin> getSkins() {
        return skins;
    }

    /**
     * 获取选中的皮肤
     */
    public ObstacleSkin getSelectedSkin() {
        return skins.get(selectedSkinIndex);
    }

    /**
     * 获取选中的皮肤索引
     */
    public int getSelectedSkinIndex() {
        return selectedSkinIndex;
    }

    /**
     * 选择皮肤
     */
    public void selectSkin(int index) {
        if (index >= 0 && index < skins.size() && skins.get(index).isUnlocked()) {
            selectedSkinIndex = index;
        }
    }

    /**
     * 解锁皮肤
     */
    public boolean unlockSkin(int index, int coins) {
        if (index >= 0 && index < skins.size()) {
            ObstacleSkin skin = skins.get(index);
            if (!skin.isUnlocked() && coins >= skin.getPrice()) {
                skin.unlock();
                return true;
            }
        }
        return false;
    }

    /**
     * 检查皮肤是否已解锁
     */
    public boolean isSkinUnlocked(int index) {
        if (index >= 0 && index < skins.size()) {
            return skins.get(index).isUnlocked();
        }
        return false;
    }
}

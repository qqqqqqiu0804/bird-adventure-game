package cn.edu.huse.game;

import java.util.ArrayList;
import java.util.List;

/**
 * 皮肤管理器 - 飞翔小鸟冒险版
 */
public class SkinManager {
    List<Skin> skins;
    int selectedSkinIndex;

    /**
     * 构造方法
     */
    public SkinManager() throws Exception {
        skins = new ArrayList<>();
        selectedSkinIndex = 0;
        initSkins();
    }

    /**
     * 初始化皮肤
     */
    private void initSkins() throws Exception {
        // 默认皮肤 - UFO
        skins.add(new Skin("经典UFO", "默认的UFO皮肤", "bird0.png", true, 0));

        // 解锁皮肤
        skins.add(new Skin("愤怒小鸟", "红色愤怒小鸟", "bird_red0.png", false, 100));
        skins.add(new Skin("蜜蜂", "勤劳的小蜜蜂", "bee0.png", false, 200));
        skins.add(new Skin("幽灵", "神秘幽灵", "ghost0.png", false, 300));
    }

    /**
     * 获取所有皮肤
     */
    public List<Skin> getSkins() {
        return skins;
    }

    /**
     * 获取选中的皮肤
     */
    public Skin getSelectedSkin() {
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
            Skin skin = skins.get(index);
            if (!skin.isUnlocked() && coins >= skin.getPrice()) {
                skin.unlock();
                return true;
            }
        }
        return false;
    }

    /**
     * 获取解锁皮肤需要的金币
     */
    public int getSkinPrice(int index) {
        if (index >= 0 && index < skins.size()) {
            return skins.get(index).getPrice();
        }
        return 0;
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

package cn.edu.huse.game;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景管理器 - 飞翔小鸟冒险版
 */
public class SceneManager {
    List<Scene> scenes;
    int selectedSceneIndex;

    /**
     * 构造方法
     */
    public SceneManager() throws Exception {
        scenes = new ArrayList<>();
        selectedSceneIndex = 0;
        initScenes();
    }

    /**
     * 初始化场景 - 每个场景使用不同主题
     */
    private void initScenes() throws Exception {
        // 默认场景 - 星空（无主题）
        scenes.add(new Scene("星空", "默认的星空背景", "bg.png", true, 0, null));

        // 解锁场景 - 不同主题
        scenes.add(new Scene("深海", "深海世界背景", "bg.png", false, 200, "deepsea"));
        scenes.add(new Scene("森林", "神秘森林背景", "bg.png", false, 300, "forest"));
        scenes.add(new Scene("沙漠", "沙漠绿洲背景", "bg.png", false, 400, "desert"));
        scenes.add(new Scene("火山", "火山熔岩背景", "bg.png", false, 500, "volcano"));
    }

    /**
     * 获取所有场景
     */
    public List<Scene> getScenes() {
        return scenes;
    }

    /**
     * 获取选中的场景
     */
    public Scene getSelectedScene() {
        return scenes.get(selectedSceneIndex);
    }

    /**
     * 获取选中的场景索引
     */
    public int getSelectedSceneIndex() {
        return selectedSceneIndex;
    }

    /**
     * 选择场景
     */
    public void selectScene(int index) {
        if (index >= 0 && index < scenes.size() && scenes.get(index).isUnlocked()) {
            selectedSceneIndex = index;
        }
    }

    /**
     * 解锁场景
     */
    public boolean unlockScene(int index, int coins) {
        if (index >= 0 && index < scenes.size()) {
            Scene scene = scenes.get(index);
            if (!scene.isUnlocked() && coins >= scene.getPrice()) {
                scene.unlock();
                return true;
            }
        }
        return false;
    }

    /**
     * 获取解锁场景需要的金币
     */
    public int getScenePrice(int index) {
        if (index >= 0 && index < scenes.size()) {
            return scenes.get(index).getPrice();
        }
        return 0;
    }

    /**
     * 检查场景是否已解锁
     */
    public boolean isSceneUnlocked(int index) {
        if (index >= 0 && index < scenes.size()) {
            return scenes.get(index).isUnlocked();
        }
        return false;
    }
}

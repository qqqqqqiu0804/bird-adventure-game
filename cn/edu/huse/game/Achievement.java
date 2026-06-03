package cn.edu.huse.game;

/**
 * 成就类 - 飞翔小鸟冒险版
 */
public class Achievement {
    String name;
    String description;
    boolean unlocked;
    int progress;
    int target;
    int coinReward;

    /**
     * 构造方法
     */
    public Achievement(String name, String description, int target, int coinReward) {
        this.name = name;
        this.description = description;
        this.unlocked = false;
        this.progress = 0;
        this.target = target;
        this.coinReward = coinReward;
    }

    /**
     * 获取成就名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取成就描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 检查是否已解锁
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * 获取进度
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 获取目标
     */
    public int getTarget() {
        return target;
    }

    /**
     * 获取金币奖励
     */
    public int getCoinReward() {
        return coinReward;
    }

    /**
     * 更新进度
     * @return true 如果成就刚解锁
     */
    public boolean updateProgress(int value) {
        if (!unlocked) {
            progress = Math.min(value, target);
            if (progress >= target) {
                unlocked = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 增加进度
     * @return true 如果成就刚解锁
     */
    public boolean addProgress(int amount) {
        if (!unlocked) {
            progress = Math.min(progress + amount, target);
            if (progress >= target) {
                unlocked = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 设置进度（用于加载存档）
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     * 解锁成就（用于加载存档）
     */
    public void unlock() {
        this.unlocked = true;
        this.progress = target;
    }

    /**
     * 获取进度百分比
     */
    public int getProgressPercent() {
        return (int) ((double) progress / target * 100);
    }
}

package cn.edu.huse.game;

/**
 * 升级系统 - 飞翔小鸟冒险版
 */
public class UpgradeSystem {
    int healthLevel;
    int speedLevel;

    int maxHealthLevel;
    int maxSpeedLevel;

    int healthUpgradeCost;
    int speedUpgradeCost;

    /**
     * 构造方法
     */
    public UpgradeSystem() {
        healthLevel = 1;
        speedLevel = 1;

        maxHealthLevel = 10;
        maxSpeedLevel = 10;

        healthUpgradeCost = 50;
        speedUpgradeCost = 50;
    }

    /**
     * 获取生命值等级
     */
    public int getHealthLevel() {
        return healthLevel;
    }

    /**
     * 获取速度等级
     */
    public int getSpeedLevel() {
        return speedLevel;
    }

    /**
     * 获取最大生命值
     */
    public int getMaxHealth() {
        return healthLevel * 2;
    }

    /**
     * 获取最大速度等级
     */
    public int getMaxSpeed() {
        return maxSpeedLevel;
    }

    /**
     * 获取速度加成
     */
    public double getSpeedBonus() {
        return 1.0 + (speedLevel - 1) * 0.1;
    }

    /**
     * 获取生命值升级费用
     */
    public int getHealthUpgradeCost() {
        return healthUpgradeCost * healthLevel;
    }

    /**
     * 获取速度升级费用
     */
    public int getSpeedUpgradeCost() {
        return speedUpgradeCost * speedLevel;
    }

    /**
     * 升级生命值
     * @return 升级消耗的金币数，失败返回-1
     */
    public int upgradeHealth(int coins) {
        int cost = getHealthUpgradeCost();
        if (healthLevel < maxHealthLevel && coins >= cost) {
            healthLevel++;
            return cost;
        }
        return -1;
    }

    /**
     * 升级速度
     * @return 升级消耗的金币数，失败返回-1
     */
    public int upgradeSpeed(int coins) {
        int cost = getSpeedUpgradeCost();
        if (speedLevel < maxSpeedLevel && coins >= cost) {
            speedLevel++;
            return cost;
        }
        return -1;
    }

    /**
     * 检查是否可以升级生命值
     */
    public boolean canUpgradeHealth(int coins) {
        return healthLevel < maxHealthLevel && coins >= getHealthUpgradeCost();
    }

    /**
     * 检查是否可以升级速度
     */
    public boolean canUpgradeSpeed(int coins) {
        return speedLevel < maxSpeedLevel && coins >= getSpeedUpgradeCost();
    }
}

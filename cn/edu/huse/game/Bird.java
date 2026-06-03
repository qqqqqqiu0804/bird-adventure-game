package cn.edu.huse.game;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * 小鸟类 - 飞翔小鸟冒险版
 */
public class Bird {
    BufferedImage image;
    int x, y;
    int width, height;
    int size;

    // 物理参数
    double velocity;
    double gravity;
    double jumpStrength;
    double maxFallSpeed;

    // 动画
    BufferedImage[] images;
    int index;

    // 状态
    boolean isInvincible;
    int invincibleTimer;
    boolean hasMagnet;
    int magnetTimer;
    boolean hasSlow;
    int slowTimer;
    boolean hasShield;

    public Bird() throws Exception {
        image = ImageIO.read(getClass().getResource("bird0.png"));
        width = image.getWidth();
        height = image.getHeight();
        x = GameConfig.BIRD_START_X;
        y = GameConfig.BIRD_START_Y;
        size = GameConfig.BIRD_SIZE;

        gravity = GameConfig.GRAVITY;
        jumpStrength = GameConfig.JUMP_STRENGTH;
        maxFallSpeed = GameConfig.MAX_FALL_SPEED;
        velocity = 0;

        images = new BufferedImage[3];
        for (int i = 0; i < images.length; i++) {
            images[i] = ImageIO.read(getClass().getResource("bird" + i + ".png"));
        }
        index = 0;

        isInvincible = false;
        invincibleTimer = 0;
        hasMagnet = false;
        magnetTimer = 0;
        hasSlow = false;
        slowTimer = 0;
        hasShield = false;
    }

    /**
     * 跳跃
     */
    public void fly() {
        velocity = jumpStrength;
    }

    /**
     * 更新位置
     */
    public void step() {
        velocity += gravity;
        if (velocity > maxFallSpeed) {
            velocity = maxFallSpeed;
        }
        y += velocity;

        // 边界检测
        if (y < 0) {
            y = 0;
            velocity = 0;
        }
        if (y > GameConfig.GROUND_Y - size) {
            y = GameConfig.GROUND_Y - size;
            velocity = 0;
        }
    }

    /**
     * 动画更新
     */
    public void flappy() {
        index++;
        int animSpeed = velocity < 0 ? 6 : 12;
        image = images[(index / animSpeed) % 3];
    }

    /**
     * 更新道具效果
     */
    public void updatePowerUps() {
        if (isInvincible) {
            invincibleTimer--;
            if (invincibleTimer <= 0) {
                isInvincible = false;
            }
        }
        if (hasMagnet) {
            magnetTimer--;
            if (magnetTimer <= 0) {
                hasMagnet = false;
            }
        }
        if (hasSlow) {
            slowTimer--;
            if (slowTimer <= 0) {
                hasSlow = false;
            }
        }
    }

    /**
     * 激活无敌效果
     */
    public void activateInvincible() {
        isInvincible = true;
        invincibleTimer = GameConfig.POWERUP_DURATION_STAR;
    }

    /**
     * 激活磁铁效果
     */
    public void activateMagnet() {
        hasMagnet = true;
        magnetTimer = GameConfig.POWERUP_DURATION_MAGNET;
    }

    /**
     * 激活减速效果
     */
    public void activateSlow() {
        hasSlow = true;
        slowTimer = GameConfig.POWERUP_DURATION_SLOW;
    }

    /**
     * 激活护盾
     */
    public void activateShield() {
        hasShield = true;
    }

    /**
     * 使用护盾
     * @return true 如果护盾被使用
     */
    public boolean useShield() {
        if (hasShield) {
            hasShield = false;
            return true;
        }
        return false;
    }

    /**
     * 碰撞检测 - 地面
     */
    public boolean hitGround() {
        return y >= GameConfig.GROUND_Y - size;
    }

    /**
     * 碰撞检测 - 障碍物
     */
    public boolean hitObstacle(Obstacle o) {
        if (isInvincible) return false;

        int hitSize = (int)(size * 0.75);
        if (x > o.x - o.width / 2 - hitSize / 2
                && x < o.x + o.width / 2 + hitSize / 2) {
            if (y > o.y - o.gap / 2 + hitSize / 2
                    && y < o.y + o.gap / 2 - hitSize / 2) {
                return false;  // 在安全区域
            }
            return true;  // 碰撞
        }
        return false;
    }

    /**
     * 碰撞检测 - 金币
     */
    public boolean hitCoin(Coin c) {
        int hitSize = size + (hasMagnet ? 50 : 0);  // 磁铁增加吸附范围
        double dx = x - c.x;
        double dy = y - c.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (hitSize + c.size) / 2;
    }

    /**
     * 碰撞检测 - 道具
     */
    public boolean hitPowerUp(PowerUp p) {
        int hitSize = size;
        double dx = x - p.x;
        double dy = y - p.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (hitSize + p.size) / 2;
    }

    /**
     * 重置小鸟状态
     */
    public void reset() {
        x = GameConfig.BIRD_START_X;
        y = GameConfig.BIRD_START_Y;
        velocity = 0;
        index = 0;
        image = images[0];
        isInvincible = false;
        invincibleTimer = 0;
        hasMagnet = false;
        magnetTimer = 0;
        hasSlow = false;
        slowTimer = 0;
        hasShield = false;
    }

    /**
     * 复活
     */
    public void revive() {
        y = GameConfig.BIRD_START_Y;
        velocity = 0;
        isInvincible = true;
        invincibleTimer = GameConfig.REVIVE_INVINCIBLE_TIME;
    }

    /**
     * 更新皮肤图片
     * @param skinImage 皮肤图片
     */
    public void setSkinImage(BufferedImage skinImage) {
        this.image = skinImage;
        // 根据皮肤图片生成动画帧
        String baseName = getBaseName(skinImage);
        try {
            for (int i = 0; i < images.length; i++) {
                images[i] = ImageIO.read(getClass().getResource(baseName + i + ".png"));
            }
        } catch (Exception e) {
            // 如果找不到动画帧，使用静态图片
            for (int i = 0; i < images.length; i++) {
                images[i] = skinImage;
            }
        }
    }

    /**
     * 获取图片基础名称
     */
    private String getBaseName(BufferedImage img) {
        // 根据图片内容判断基础名称
        if (img == images[0]) return "bird";  // 默认
        // 尝试从资源名推断
        return "bird";  // 默认返回
    }
}

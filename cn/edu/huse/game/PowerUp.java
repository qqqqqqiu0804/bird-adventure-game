package cn.edu.huse.game;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * 道具类 - 飞翔小鸟冒险版
 */
public class PowerUp {
    BufferedImage image;
    int x, y;
    int size;
    PowerUpType type;
    boolean collected;

    /**
     * 道具类型枚举
     */
    public enum PowerUpType {
        STAR,    // 无敌星星
        MAGNET,  // 磁铁
        SLOW,    // 减速器
        SHIELD   // 护盾
    }

    /**
     * 构造方法
     * @param x 道具x坐标
     * @param y 道具y坐标
     * @param type 道具类型
     */
    public PowerUp(int x, int y, PowerUpType type) throws Exception {
        this.x = x;
        this.y = y;
        this.type = type;
        size = GameConfig.POWERUP_SIZE;
        collected = false;

        // 根据类型加载不同图片
        switch (type) {
            case STAR:
                image = ImageIO.read(getClass().getResource("star.png"));
                break;
            case MAGNET:
                image = ImageIO.read(getClass().getResource("magnet.png"));
                break;
            case SLOW:
                image = ImageIO.read(getClass().getResource("slow.png"));
                break;
            case SHIELD:
                image = ImageIO.read(getClass().getResource("shield.png"));
                break;
        }
    }

    /**
     * 更新道具位置
     * @param speed 移动速度
     */
    public void step(int speed) {
        x -= speed;
    }

    /**
     * 检查道具是否在屏幕内
     * @return true 如果道具在屏幕内
     */
    public boolean isOnScreen() {
        return x > -size;
    }

    /**
     * 收集道具
     */
    public void collect() {
        collected = true;
    }

    /**
     * 检查道具是否被收集
     * @return true 如果道具被收集
     */
    public boolean isCollected() {
        return collected;
    }
}

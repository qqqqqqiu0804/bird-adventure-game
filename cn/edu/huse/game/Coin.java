package cn.edu.huse.game;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * 金币类 - 飞翔小鸟冒险版
 */
public class Coin {
    BufferedImage image;
    int x, y;
    int size;
    int value;
    boolean collected;

    /**
     * 构造方法
     * @param x 金币x坐标
     * @param y 金币y坐标
     */
    public Coin(int x, int y) throws Exception {
        image = ImageIO.read(getClass().getResource("coin.png"));
        this.x = x;
        this.y = y;
        size = GameConfig.COIN_SIZE;
        value = GameConfig.COIN_VALUE;
        collected = false;
    }

    /**
     * 更新金币位置
     * @param speed 移动速度
     */
    public void step(int speed) {
        x -= speed;
    }

    /**
     * 检查金币是否在屏幕内
     * @return true 如果金币在屏幕内
     */
    public boolean isOnScreen() {
        return x > -size;
    }

    /**
     * 收集金币
     */
    public void collect() {
        collected = true;
    }

    /**
     * 检查金币是否被收集
     * @return true 如果金币被收集
     */
    public boolean isCollected() {
        return collected;
    }
}

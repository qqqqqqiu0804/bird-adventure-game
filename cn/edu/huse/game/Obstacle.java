package cn.edu.huse.game;

import java.awt.image.BufferedImage;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * 障碍物类 - 飞翔小鸟冒险版
 */
public class Obstacle {
    BufferedImage image;
    int x, y;
    int width, height;
    int gap;
    int distance;
    int speed;
    boolean passed;  // 标记是否已通过

    Random ran = new Random();

    /**
     * 构造方法
     * @param n 障碍物编号
     */
    public Obstacle(int n) throws Exception {
        image = ImageIO.read(getClass().getResource("obstacle.png"));
        width = image.getWidth();
        height = image.getHeight();
        gap = GameConfig.OBSTACLE_GAP;
        distance = GameConfig.OBSTACLE_DISTANCE;
        speed = GameConfig.OBSTACLE_SPEED;
        x = 550 + (n - 1) * distance;
        y = ran.nextInt(GameConfig.OBSTACLE_Y_RANGE) + GameConfig.OBSTACLE_MIN_Y;
        passed = false;
    }

    /**
     * 移动障碍物
     */
    public void step() {
        x -= speed;
        // 当障碍物移出屏幕左侧时，重置到右侧
        if (x <= -width / 2) {
            x = distance * 2 - width / 2;
            y = ran.nextInt(GameConfig.OBSTACLE_Y_RANGE) + GameConfig.OBSTACLE_MIN_Y;
            passed = false;  // 重置通过标记
        }
    }

    /**
     * 重置障碍物状态
     * @param n 障碍物编号
     */
    public void reset(int n) {
        x = 550 + (n - 1) * distance;
        y = ran.nextInt(GameConfig.OBSTACLE_Y_RANGE) + GameConfig.OBSTACLE_MIN_Y;
        speed = GameConfig.OBSTACLE_SPEED;
        gap = GameConfig.OBSTACLE_GAP;
        passed = false;
    }

    /**
     * 检查是否通过
     * @param birdX 小鸟x坐标
     * @return true 如果小鸟通过了障碍物
     */
    public boolean isPassed(int birdX) {
        if (!passed && birdX > x + width / 2) {
            passed = true;
            return true;
        }
        return false;
    }

    /**
     * 获取空隙的上下边界
     * @return 空隙的上下边界数组 [top, bottom]
     */
    public int[] getGapBounds() {
        int top = y - gap / 2;
        int bottom = y + gap / 2;
        return new int[]{top, bottom};
    }
}

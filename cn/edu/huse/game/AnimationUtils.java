package cn.edu.huse.game;

/**
 * 动画工具类 - 提供各种动画效果
 */
public class AnimationUtils {

    /**
     * 线性插值
     * @param start 起始值
     * @param end 结束值
     * @param t 进度 (0.0 - 1.0)
     * @return 插值结果
     */
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    /**
     * 缓动函数 - 先慢后快再慢
     * @param t 进度 (0.0 - 1.0)
     * @return 缓动后的进度
     */
    public static float easeInOut(float t) {
        return t < 0.5f ? 2 * t * t : 1 - (float)Math.pow(-2 * t + 2, 2) / 2;
    }

    /**
     * 缓动函数 - 先慢后快
     * @param t 进度 (0.0 - 1.0)
     * @return 缓动后的进度
     */
    public static float easeIn(float t) {
        return t * t;
    }

    /**
     * 缓动函数 - 先快后慢
     * @param t 进度 (0.0 - 1.0)
     * @return 缓动后的进度
     */
    public static float easeOut(float t) {
        return 1 - (1 - t) * (1 - t);
    }

    /**
     * 淡入效果
     * @param currentAlpha 当前透明度
     * @param targetAlpha 目标透明度
     * @param speed 速度
     * @return 新的透明度
     */
    public static int fadeIn(int currentAlpha, int targetAlpha, int speed) {
        if (currentAlpha < targetAlpha) {
            return Math.min(currentAlpha + speed, targetAlpha);
        }
        return currentAlpha;
    }

    /**
     * 淡出效果
     * @param currentAlpha 当前透明度
     * @param targetAlpha 目标透明度
     * @param speed 速度
     * @return 新的透明度
     */
    public static int fadeOut(int currentAlpha, int targetAlpha, int speed) {
        if (currentAlpha > targetAlpha) {
            return Math.max(currentAlpha - speed, targetAlpha);
        }
        return currentAlpha;
    }

    /**
     * 脉冲效果
     * @param time 时间
     * @param speed 速度
     * @return 脉冲值 (0.0 - 1.0)
     */
    public static float pulse(float time, float speed) {
        return (float)(Math.sin(time * speed) * 0.5 + 0.5);
    }

    /**
     * 弹跳效果
     * @param time 时间
     * @param height 高度
     * @return 弹跳偏移量
     */
    public static float bounce(float time, float height) {
        return (float)(height * Math.abs(Math.sin(time * 2)));
    }

    /**
     * 缩放脉冲效果
     * @param time 时间
     * @param speed 速度
     * @param minScale 最小缩放
     * @param maxScale 最大缩放
     * @return 缩放值
     */
    public static float scalePulse(float time, float speed, float minScale, float maxScale) {
        float t = pulse(time, speed);
        return lerp(minScale, maxScale, t);
    }
}

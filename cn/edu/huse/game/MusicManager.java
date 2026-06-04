package cn.edu.huse.game;

import javax.sound.sampled.*;
import java.io.InputStream;

/**
 * 背景音乐管理器 - 飞翔小鸟冒险版
 */
public class MusicManager {
    private Clip currentClip;
    private String currentTrack;
    private int volume;          // 0-100
    private boolean isPlaying;
    private boolean loop;

    /**
     * 构造方法
     */
    public MusicManager() {
        volume = 60;
        isPlaying = false;
        loop = true;
    }

    /**
     * 加载音乐文件
     * @param filename 音乐文件名
     */
    public void loadMusic(String filename) {
        try {
            // 如果正在播放，先停止
            if (currentClip != null) {
                stop();
                currentClip.close();
            }

            InputStream is = getClass().getResourceAsStream(filename);
            if (is == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            currentClip = AudioSystem.getClip();
            currentClip.open(ais);
            currentTrack = filename;

            // 应用音量
            applyVolume();

            // 添加循环播放监听
            if (loop) {
                currentClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                        currentClip.setFramePosition(0);
                        currentClip.start();
                    }
                });
            }
        } catch (Exception e) {
            // 加载失败静默处理
            currentClip = null;
        }
    }

    /**
     * 播放音乐
     */
    public void play() {
        if (currentClip == null) return;
        try {
            applyVolume();
            currentClip.setFramePosition(0);
            currentClip.start();
            isPlaying = true;
        } catch (Exception e) {
            // 播放失败静默处理
        }
    }

    /**
     * 暂停音乐
     */
    public void pause() {
        if (currentClip == null || !isPlaying) return;
        try {
            currentClip.stop();
            isPlaying = false;
        } catch (Exception e) {
            // 暂停失败静默处理
        }
    }

    /**
     * 停止音乐
     */
    public void stop() {
        if (currentClip == null) return;
        try {
            currentClip.stop();
            currentClip.setFramePosition(0);
            isPlaying = false;
        } catch (Exception e) {
            // 停止失败静默处理
        }
    }

    /**
     * 设置音量
     * @param volume 音量值 (0-100)
     */
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        applyVolume();
    }

    /**
     * 获取当前音量
     * @return 音量值 (0-100)
     */
    public int getVolume() {
        return volume;
    }

    /**
     * 切换音乐
     * @param filename 新的音乐文件名
     */
    public void switchTrack(String filename) {
        if (filename.equals(currentTrack)) return;

        // 淡出当前音乐
        fadeOut(500);

        // 加载并播放新音乐
        loadMusic(filename);
        fadeIn(500);
    }

    /**
     * 淡入效果
     * @param duration 淡入时长（毫秒）
     */
    public void fadeIn(int duration) {
        if (currentClip == null) return;

        // 在新线程中执行淡入
        new Thread(() -> {
            try {
                int steps = 20;
                int stepDuration = duration / steps;
                float stepVolume = (float) volume / steps;

                // 从0音量开始
                for (int i = 0; i < steps; i++) {
                    int currentVol = (int)(stepVolume * i);
                    applyVolume(currentVol);
                    Thread.sleep(stepDuration);
                }

                // 恢复目标音量
                applyVolume();
            } catch (InterruptedException e) {
                // 线程中断静默处理
            }
        }).start();
    }

    /**
     * 淡出效果
     * @param duration 淡出时长（毫秒）
     */
    public void fadeOut(int duration) {
        if (currentClip == null || !isPlaying) return;

        // 在新线程中执行淡出
        new Thread(() -> {
            try {
                int steps = 20;
                int stepDuration = duration / steps;
                float stepVolume = (float) volume / steps;

                // 逐渐降低音量
                for (int i = steps; i > 0; i--) {
                    int currentVol = (int)(stepVolume * i);
                    applyVolume(currentVol);
                    Thread.sleep(stepDuration);
                }

                // 停止播放
                stop();
            } catch (InterruptedException e) {
                // 线程中断静默处理
            }
        }).start();
    }

    /**
     * 应用音量设置
     */
    private void applyVolume() {
        applyVolume(volume);
    }

    /**
     * 应用指定音量
     * @param vol 音量值 (0-100)
     */
    private void applyVolume(int vol) {
        if (currentClip == null) return;

        try {
            if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);

                // 将 0-100 转换为分贝值
                float dB;
                if (vol == 0) {
                    dB = -80.0f;
                } else {
                    dB = (float) (20.0 * Math.log10(vol / 100.0));
                }

                // 确保在有效范围内
                dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            // 音量设置失败静默处理
        }
    }

    /**
     * 检查是否正在播放
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 获取当前曲目
     * @return 当前曲目文件名
     */
    public String getCurrentTrack() {
        return currentTrack;
    }

    /**
     * 设置是否循环播放
     * @param loop 是否循环
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}

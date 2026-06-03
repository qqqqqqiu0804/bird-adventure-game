package cn.edu.huse.game;

import javax.sound.sampled.*;
import java.io.File;

/**
 * 音效管理类 - 飞翔小鸟冒险版
 */
public class SoundManager {
    private Clip clickClip;
    private Clip scoreClip;
    private Clip hitClip;
    private Clip gameOverClip;
    private Clip levelCompleteClip;
    private Clip powerUpClip;
    private Clip coinClip;

    /**
     * 构造方法
     */
    public SoundManager() {
        // 预加载音效，失败时静默降级
        clickClip = loadClip("click.wav");
        scoreClip = loadClip("score.wav");
        hitClip = loadClip("hit.wav");
        gameOverClip = loadClip("gameover.wav");
        levelCompleteClip = loadClip("levelcomplete.wav");
        powerUpClip = loadClip("powerup.wav");
        coinClip = loadClip("coin.wav");
    }

    /**
     * 加载音效文件
     */
    private Clip loadClip(String filename) {
        try {
            File file = new File(getClass().getResource(filename).toURI());
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            return null;  // 加载失败，静默降级
        }
    }

    /**
     * 播放音效
     */
    private void play(Clip clip) {
        if (clip == null) return;  // 容错
        try {
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            // 播放失败静默处理
        }
    }

    /**
     * 播放点击音效
     */
    public void playClick() {
        play(clickClip);
    }

    /**
     * 播放得分音效
     */
    public void playScore() {
        play(scoreClip);
    }

    /**
     * 播放碰撞音效
     */
    public void playHit() {
        play(hitClip);
    }

    /**
     * 播放游戏结束音效
     */
    public void playGameOver() {
        play(gameOverClip);
    }

    /**
     * 播放关卡完成音效
     */
    public void playLevelComplete() {
        play(levelCompleteClip);
    }

    /**
     * 播放道具音效
     */
    public void playPowerUp() {
        play(powerUpClip);
    }

    /**
     * 播放金币音效
     */
    public void playCoin() {
        play(coinClip);
    }
}

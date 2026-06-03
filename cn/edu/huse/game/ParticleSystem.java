package cn.edu.huse.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 粒子特效系统 - 飞翔小鸟冒险版
 */
public class ParticleSystem {
    private List<Particle> particles = new ArrayList<>();
    private Random random = new Random();

    /**
     * 粒子内部类
     */
    private static class Particle {
        double x, y;
        double vx, vy;
        Color color;
        int life;
        int maxLife;
        int size;
        String text;

        public Particle(double x, double y, double vx, double vy,
                        Color color, int life, int size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.life = life;
            this.maxLife = life;
            this.size = size;
            this.text = null;
        }

        public boolean isDead() {
            return life <= 0;
        }

        public void update() {
            x += vx;
            y += vy;
            life--;
        }

        public float getAlpha() {
            return (float) life / maxLife;
        }
    }

    /**
     * 发射尾焰粒子
     */
    public void emitTailFlame(int x, int y) {
        for (int i = 0; i < 3; i++) {
            double vx = (random.nextDouble() - 0.5) * 2;
            double vy = random.nextDouble() * 3 + 1;
            Color color = new Color(255, 150 + random.nextInt(100), 50);
            addParticle(new Particle(x, y, vx, vy, color, 15 + random.nextInt(10), 3));
        }
    }

    /**
     * 发射爆炸粒子
     */
    public void emitExplosion(int x, int y) {
        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 5 + 2;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            Color color = new Color(255, random.nextInt(100), 0);
            addParticle(new Particle(x, y, vx, vy, color, 30 + random.nextInt(20), 4));
        }
    }

    /**
     * 发射得分闪光粒子
     */
    public void emitScoreFlash(int x, int y) {
        Particle p = new Particle(x, y, 0, -1.5, new Color(255, 255, 255), 40, 0);
        p.text = "+1";
        addParticle(p);
    }

    /**
     * 发射金币收集粒子
     */
    public void emitCoinCollect(int x, int y) {
        for (int i = 0; i < 10; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 3 + 1;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            Color color = new Color(255, 215, 0);  // 金色
            addParticle(new Particle(x, y, vx, vy, color, 20 + random.nextInt(10), 3));
        }
    }

    /**
     * 发射道具收集粒子
     */
    public void emitPowerUpCollect(int x, int y) {
        for (int i = 0; i < 15; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = random.nextDouble() * 4 + 2;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            addParticle(new Particle(x, y, vx, vy, color, 25 + random.nextInt(15), 4));
        }
    }

    /**
     * 添加粒子
     */
    private void addParticle(Particle p) {
        if (particles.size() >= GameConfig.MAX_PARTICLES) {
            particles.remove(0);  // 淘汰最旧的粒子
        }
        particles.add(p);
    }

    /**
     * 更新所有粒子
     */
    public void update() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.isDead()) it.remove();
        }
    }

    /**
     * 绘制所有粒子
     */
    public void draw(Graphics g) {
        for (Particle p : particles) {
            float alpha = p.getAlpha();
            if (p.text != null) {
                // 绘制飘字
                g.setColor(new Color(1f, 1f, 1f, alpha));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                g.drawString(p.text, (int) p.x, (int) p.y);
            } else {
                // 绘制圆点粒子
                g.setColor(new Color(
                    p.color.getRed() / 255f,
                    p.color.getGreen() / 255f,
                    p.color.getBlue() / 255f,
                    alpha));
                g.fillOval((int) p.x - p.size / 2, (int) p.y - p.size / 2,
                           p.size, p.size);
            }
        }
    }
}

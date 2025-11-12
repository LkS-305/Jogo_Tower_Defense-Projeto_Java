package com.neondf.entities;

import com.neondf.systems.WaveManager;
import java.awt.*;

public class Enemy {

    private double x, y;
    private double speed = 1.5;
    private boolean alive = true;
    private final WaveManager waveManager;

    public Enemy(double x, double y, WaveManager waveManager) {
        this.x = x;
        this.y = y;
        this.waveManager = waveManager;
    }

    public void tick(double targetX, double targetY) {
        if (!alive) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist != 0) {
            x += dx / dist * speed;
            y += dy / dist * speed;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillOval((int) x, (int) y, 20, 20);
    }

    public void kill() {
        if (alive) {
            alive = false;
            waveManager.enemyDied(); // informa ao WaveManager
        }
    }

    public boolean isAlive() { return alive; }
    public double getX() { return x; }
    public double getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 20, 20);
    }
}

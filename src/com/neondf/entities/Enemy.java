package com.neondf.entities;

import java.awt.*;

/**
 * Inimigo básico que se move em direção à torre.
 */
public class Enemy {
    private double x, y;
    private double speed;
    private final int size = 25;
    private boolean alive = true;

    public Enemy(double x, double y, double speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void tick(double targetX, double targetY) {
        // Calcula direção até a torre
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Move o inimigo em direção à torre
        if (dist > 5) { // evita pular muito
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }

        // Se estiver muito próximo da torre, marca como morto
        if (dist < 10) {
            alive = false;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.PINK);
        g.fillOval((int) x, (int) y, size, size);
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}

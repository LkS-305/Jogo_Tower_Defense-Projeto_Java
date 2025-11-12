package com.neondf.entities;

import java.awt.*;

public class Bullet {

    private double x, y;
    private double angle;
    private double speed = 6;
    private boolean alive = true;

    public Bullet(double x, double y, double angle) { // ✅ Construtor adicionado
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void tick() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        // Remove se sair da tela
        if (x < -10 || x > 820 || y < -10 || y > 620) {
            alive = false;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fillOval((int) x, (int) y, 6, 6);
    }

    public boolean isAlive() {
        return alive;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

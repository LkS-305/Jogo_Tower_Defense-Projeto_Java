package com.neondf.entities;

import java.awt.*;

/**
 * Representa uma bala disparada pela torre.
 * Move-se em linha reta na direção do cursor e desaparece ao sair da tela.
 */
public class Bullet {
    private double x, y;      // posição atual
    private double velX, velY; // velocidade
    private final int size = 6;
    private boolean alive = true; // se ainda está na tela

    public Bullet(double x, double y, double velX, double velY) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
    }

    public void tick() {
        x += velX;
        y += velY;

        // Se sair da tela, morre
        if (x < -50 || y < -50 || x > 850 || y > 650) {
            alive = false;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fillOval((int) x, (int) y, size, size);
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

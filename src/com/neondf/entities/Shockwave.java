package com.neondf.entities;

import java.awt.*;

public class Shockwave {
    private double x, y;
    private double radius = 10;
    private double maxRadius = 500; // Tamanho máximo da explosão
    private double speed = 15;      // Velocidade da expansão
    private boolean active = true;

    public Shockwave(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void tick() {
        radius += speed;
        if (radius >= maxRadius) {
            active = false;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(new Color(255, 255, 0, 150)); // Amarelo transparente
        g.setStroke(new BasicStroke(5)); // Borda grossa
        g.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
        g.setStroke(new BasicStroke(1)); // Reseta a borda
    }

    public boolean isActive() { return active; }

    // Verifica colisão circular (Apenas na borda da onda)
    public boolean collidesWith(Enemy e) {
        double dx = e.getX() - x;
        double dy = e.getY() - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        // Colide se o inimigo estiver sendo "atropelado" pela borda da onda
        return dist < radius && dist > (radius - speed - 20);
    }
}
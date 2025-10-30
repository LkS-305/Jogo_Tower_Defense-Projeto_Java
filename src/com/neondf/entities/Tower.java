package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Representa a torre central controlada pelo jogador.
 * Ela vai ficar fixa no centro da tela e poderá rotacionar em direção ao mouse.
 */
public class Tower {

    private double x, y; // posição
    private double angle; // rotação em radianos
    private final int size = 60; // tamanho da torre
    private long lastShotTime = 0;
private long shootCooldown = 300; // tempo entre tiros em ms


    public Tower(double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getCenterX() {
        return x + size / 2.0;
    }

    public double getCenterY() {
        return y + size / 2.0;
    }

    public void tick() {
        // aqui depois adicionaremos tiro, upgrades etc.
    }

    public Bullet tryShoot() {
    long now = System.currentTimeMillis();
    if (now - lastShotTime < shootCooldown) {
        return null; // ainda em cooldown
    }
    lastShotTime = now;

    // posição inicial da bala (na ponta do canhão)
    double startX = getCenterX() + Math.cos(angle) * (size / 2.0 + 8);
    double startY = getCenterY() + Math.sin(angle) * (size / 2.0 + 8);

    // velocidade da bala
    double speed = 8.0;
    double vx = Math.cos(angle) * speed;
    double vy = Math.sin(angle) * speed;

    return new Bullet(startX, startY, vx, vy);
}


    public void render(Graphics2D g) {
        // salva o estado gráfico atual
        AffineTransform old = g.getTransform();

        // move o sistema de coordenadas pro centro da torre e aplica rotação
        g.translate(getCenterX(), getCenterY());
        g.rotate(angle);

        // desenha o corpo principal (círculo)
        g.setColor(Color.CYAN);
        g.fillOval(-size / 2, -size / 2, size, size);

        // desenha o canhão (um retângulo apontando pra direita)
        g.setColor(Color.MAGENTA);
        g.fillRect(10, -5, 40, 10);

        // restaura a matriz original
        g.setTransform(old);
    }
}

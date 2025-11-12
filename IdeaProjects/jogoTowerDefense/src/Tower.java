package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tower {

    private double x, y;           // posição top-left da base da torre
    private double angle = 0;      // ângulo atual (radianos)
    private long lastShot = 0;
    private int hp = 100;
    private final int size = 50;   // largura/altura do corpo (círculo)

    // distância do centro até a ponta do cano (ajuste fino conforme seu sprite)
    private final double muzzleDist = 35;

    public Tower(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // ==============================
    // ATUALIZAÇÃO
    // ==============================

    public void tick() {
        // Sem necessidade de lógica extra aqui por enquanto
    }

    // Mantém o apontamento cardeal/diagonal por WASD:
    public void updateDirection(boolean up, boolean down, boolean left, boolean right ) {
        if (up && !down && !left && !right)  angle = -Math.PI / 2;  // cima
        if (down && !up && !left && !right)  angle =  Math.PI / 2;  // baixo
        if (left && !right && !up && !down)  angle =  Math.PI;      // esquerda
        if (right && !left && !up && !down)  angle =  0;            // direita

        // diagonais
        if (up && right)        angle = -Math.PI / 4;               // nordeste
        if (up && left)         angle = -3 * Math.PI / 4;           // noroeste
        if (down && right)      angle =  Math.PI / 4;               // sudeste
        if (down && left)       angle =  3 * Math.PI / 4;           // sudoeste

        normalizeAngle();
    }

    // NOVO: rotação incremental para Q/E
    public void rotateBy(double deltaAngle) {
        angle += deltaAngle;
        normalizeAngle();
    }

    private void normalizeAngle() {
        // normaliza para o intervalo (-π, π]
        if (angle <= -Math.PI || angle > Math.PI) {
            angle = Math.atan2(Math.sin(angle), Math.cos(angle));
        }
    }

    // ==============================
    // COMBATE
    // ==============================

    public Bullet tryShoot() {
        long now = System.currentTimeMillis();
        if (now - lastShot >= 300) {
            lastShot = now;

            // Origem do tiro: ponta do cano
            double ox = getCenterX() + Math.cos(angle) * muzzleDist;
            double oy = getCenterY() + Math.sin(angle) * muzzleDist;

            // Seu Bullet(x, y, angle) já usa o ângulo para velocidade/direção
            return new Bullet(ox, oy, angle);
        }
        return null;
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public int getHp() { return hp; }

    // ==============================
    // GEOMETRIA
    // ==============================

    public double getCenterX() { return x + size / 2.0; }
    public double getCenterY() { return y + size / 2.0; }

    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; normalizeAngle(); }

    // ==============================
    // RENDER
    // ==============================

    public void render(Graphics2D g) {
        AffineTransform old = g.getTransform();

        // Desenha a torre rotacionada em torno do centro
        g.translate(getCenterX(), getCenterY());
        g.rotate(angle);

        // Corpo
        g.setColor(Color.CYAN);
        g.fillOval(-size / 2, -size / 2, size, size);

        // Cano (apontando para +X)
        g.setColor(Color.MAGENTA);
        g.fillRect(0, -5, 35, 10);

        g.setTransform(old);

        // Barra de vida abaixo da torre (screen-space)
        int barW = 50;
        int barH = 8;
        int bx = (int) (getCenterX() - barW / 2.0);
        int by = (int) (getCenterY() + 40);

        g.setColor(Color.GRAY);
        g.fillRect(bx, by, barW, barH);

        if (hp > 60) g.setColor(Color.CYAN);
        else if (hp > 30) g.setColor(Color.ORANGE);
        else g.setColor(Color.RED);

        int filled = (int) ((hp / 100.0) * barW);
        g.fillRect(bx, by, filled, barH);

        g.setColor(Color.WHITE);
        g.drawRect(bx, by, barW, barH);
    }
}

package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tower {

    private double x, y;           // posição top-left
    private double angle = 0;      // ângulo atual (radianos)
    private long lastShot = 0;
    private int hp = 100;
    private final int size = 50;   // largura/altura

    public Tower(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // mantém lógica do jogo (pode ficar vazia se não houver mais comportamento)
    public void tick() {
        // por enquanto não precisamos atualizar center aqui
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Bullet tryShoot() {
        long now = System.currentTimeMillis();
        if (now - lastShot >= 300) {
            lastShot = now;
            // cria a bala usando o centro calculado dinamicamente
            return new Bullet(getCenterX(), getCenterY(), angle);
        }
        return null;
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public int getHp() {
        return hp;
    }

    // calcula o centro dinamicamente -> evita depender de tick()
    public double getCenterX() {
        return x + size / 2.0;
    }

    public double getCenterY() {
        return y + size / 2.0;
    }

    public void render(Graphics2D g) {
        AffineTransform old = g.getTransform();

        // desenha torre centrada
        g.translate(getCenterX(), getCenterY());
        g.rotate(angle);

        g.setColor(Color.CYAN);
        g.fillOval(-size / 2, -size / 2, size, size);

        g.setColor(Color.MAGENTA);
        g.fillRect(0, -5, 35, 10);

        g.setTransform(old);

        // barra de vida abaixo da torre (usa getCenterX/Y)
        int barW = 50;
        int barH = 8;
        int bx = (int) (getCenterX() - barW / 2.0);
        int by = (int) (getCenterY() + 40);

        g.setColor(Color.GRAY);
        g.fillRect(bx, by, barW, barH);

        // cor dinâmica opcional
        if (hp > 60) g.setColor(Color.CYAN);
        else if (hp > 30) g.setColor(Color.ORANGE);
        else g.setColor(Color.RED);

        int filled = (int) ((hp / 100.0) * barW);
        g.fillRect(bx, by, filled, barH);

        g.setColor(Color.WHITE);
        g.drawRect(bx, by, barW, barH);
    }
    public void updateDirection(boolean up, boolean down, boolean left, boolean right) {
    if (up && !down && !left && !right) angle = -Math.PI / 2;       // cima
    if (down && !up && !left && !right) angle = Math.PI / 2;        // baixo
    if (left && !right && !up && !down) angle = Math.PI;            // esquerda
    if (right && !left && !up && !down) angle = 0;                  // direita

    // diagonais
    if (up && right) angle = -Math.PI / 4;
    if (up && left)  angle = -3 * Math.PI / 4;
    if (down && right) angle = Math.PI / 4;
    if (down && left)  angle = 3 * Math.PI / 4;
}

}

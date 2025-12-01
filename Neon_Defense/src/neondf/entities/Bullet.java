package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {

    private double x, y;
    private final double angle;
    private final double speed;
    private final int dmg;
    private int hits;
    private boolean alive = true;

    private BufferedImage sprite;
    private int width = 20;
    private int height = 20;

    // --- NOVO ARGUMENTO: double scale ---
    public Bullet(double x, double y, double angle, double speed, int dmg, int maxHits, BufferedImage sprite, double scale) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.dmg = dmg;
        this.hits = maxHits;
        this.sprite = sprite;

        // Aplica a escala no tamanho da imagem
        if (sprite != null) {
            this.width = (int) (sprite.getWidth() * scale);
            this.height = (int) (sprite.getHeight() * scale);
        }
    }

    public void tick() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        if (hits <= 0){
            alive = false;
        }
    }

    public void render(Graphics2D g) {
        if (sprite != null) {
            AffineTransform old = g.getTransform();
            g.translate(x, y);
            g.rotate(angle);
            // Desenha com o tamanho reajustado pela escala
            g.drawImage(sprite, -width / 2, -height / 2, width, height, null);
            g.setTransform(old);
        } else {
            g.setColor(Color.CYAN);
            g.fillOval((int) x, (int) y, 6, 6);
        }
    }

    public int getBaseDmg(){ return dmg; }
    public boolean isAlive() { return alive; }

    public void hitEnemy(){
        this.hits--;
        if (this.hits <= 0) {
            this.alive = false;
        }
    }

    public boolean statusBullet(){
        return this.alive && (!(this.getX() > 1000)) && (!(this.getX() < -100)) && (!(this.getY() > 800)) && (!(this.getY() < -100));
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
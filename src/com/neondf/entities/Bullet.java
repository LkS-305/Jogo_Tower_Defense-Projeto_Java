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

    // Imagem da bala
    private BufferedImage sprite;
    private int width = 20;  // Tamanho padrão visual
    private int height = 20;

    // Novo Construtor recebendo a Imagem
    public Bullet(double x, double y, double angle, double speed, int dmg, int maxHits, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.dmg = dmg;
        this.hits = maxHits;
        this.sprite = sprite;

        // Ajusta o tamanho se a imagem existir
        if (sprite != null) {
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
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
        // Se tiver imagem, desenha girando
        if (sprite != null) {
            AffineTransform old = g.getTransform();
            g.translate(x, y);
            g.rotate(angle); // Gira a bala na direção que está andando

            // Desenha centralizado
            g.drawImage(sprite, -width / 2, -height / 2, width, height, null);

            g.setTransform(old);
        } else {
            // Fallback (bolinha ciano antiga) se a imagem falhar
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
        // Remove se sair muito longe da tela
        return this.alive && (!(this.getX() > 1000)) && (!(this.getX() < -100)) && (!(this.getY() > 800)) && (!(this.getY() < -100));
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
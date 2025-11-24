package com.neondf.entities;

import java.awt.*;

public class Bullet {

    private double x, y;
    private final double angle;
    private final double speed;
    private final int dmg;
    private int hits; // Quantos inimigos pode perfurar
    private boolean alive = true;

    // O construtor agora recebe os atributos da Torre
    public Bullet(double x, double y, double angle, double speed, int dmg, int maxHits) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.dmg = dmg;
        this.hits = maxHits;
    }

    public void tick() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        // Remove se "morrer" (acabaram os hits)
        if (hits <= 0){
            alive = false;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fillOval((int) x, (int) y, 6, 6);
    }

    public int getBaseDmg(){
        return dmg;
    }

    public boolean isAlive() {
        return alive;
    }

    public void hitEnemy(){
        this.hits--; // Reduz um hit de perfuração
        if (this.hits <= 0) {
            this.alive = false;
        }
    }

    public boolean statusBullet(){
        return this.alive && (!(this.getX() > 900)) && (!(this.getX() < 0)) && (!(this.getY() > 700)) && (!(this.getY() < 0));
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
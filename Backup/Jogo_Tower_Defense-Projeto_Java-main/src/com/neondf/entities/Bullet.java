package com.neondf.entities;

import java.awt.*;

public class Bullet {

    private double x, y;
    private final double angle;
    private final double speed;
    private int dmg, hits;
    private static int maxHits = 1, baseDmg = 10, baseSpeed = 6;
    private boolean alive = true;

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.hits = maxHits;
        this.dmg = baseDmg;
        this.speed = baseSpeed;
    }

    public void tick() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        // Remove se sair da tela
        if (hits == 0){
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
    public void setBaseDmg(int Dmg) {
        this.dmg = Dmg;
    }

    public boolean isAlive() {
        return alive;
    }

    public void hitEnemy(){
        this.hits--;
    }

    //Esse metodo retorna true se o tiro não tiver atingido ninguem e se estiver dentro de uma área
    public boolean statusBullet(){
        return this.alive && (!(this.getX() > 900)) && (!(this.getX() < 0)) && (!(this.getY() > 700)) && (!(this.getY() < 0));
    }

    public void upgradePierce(){
        maxHits++;
    }
    public void upgradeSpeed(){
        baseSpeed++;
    }
    public void upgradeDamage(){
        baseDmg+=10;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}

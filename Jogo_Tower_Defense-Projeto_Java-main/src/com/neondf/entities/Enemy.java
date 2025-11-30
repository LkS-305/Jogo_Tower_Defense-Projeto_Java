package com.neondf.entities;

import java.awt.*;

public class Enemy {

    // baseSpeed deve ser double para aceitar 0.5, 0.8, etc.
    private double x, y, speed;
    private int dmg, hp, score, shield;
    private boolean alive = true;
    private static int baseDmg = 10, baseHP = 10, baseScore = 10000;
    private static double baseSpeed = 1.0;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        this.speed = baseSpeed; // Garante que comece como double
        this.dmg = baseDmg;
        this.hp = baseHP;
        this.score = baseScore;
        this.shield = 0;
    }

    public void tick(double targetX, double targetY) {
        if (!alive) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Se a distância for muito pequena, evita erro de divisão por zero
        if (dist > 1) {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }

    // --- Getters e Setters ---

    public double getSpeed(){ return speed; }
    public void setSpeed(double speed){ this.speed = speed; }

    public int getDmg(){ return dmg; }
    public void setDmg(int damage){ this.dmg = damage; }

    public int getHp(){ return hp; }
    public void setHp(int hp){ this.hp = hp; }

    public int getScore(){ return score; }
    public void setScore(int score){ this.score = score; }

    public int getShield() {
        return shield;
    }
    public void setShield(int shield) {
        this.shield = shield;
    }

    public int getBaseDmg(){
        return baseDmg;
    }
    public int getBaseHp(){
        return baseHP;
    }
    public int getBaseScore(){
        return baseScore;
    }
    public double getBaseSpeed(){
        return baseSpeed;
    }

    public void render(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillOval((int) (x-10), (int) (y-10), 20, 20);
    }

    public boolean isAlive() { return alive; }
    public double getX() { return x; }
    public double getY() { return y; }

    public void changeDmg(double multiplier){
        this.dmg = (int) (multiplier * this.dmg);
    }
    public void changeHP(double multiplier){
        this.hp = (int) (multiplier * this.hp);
    }
    public void changeScore(double multiplier){ this.score = (int) (multiplier * this.score); }
    public void changeSpeed(double multiplier){
        // --- O ERRO ESTAVA AQUI ---
        // Antes estava: (int) (multiplier * this.baseSpeed);
        // Agora deixamos como double:
        this.speed = multiplier * this.speed;
    }

    public int calculateCoin(){
        return this.score/10;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 20, 20);
    }

    public void kill() {
        if (this.alive) {
            this.alive = false;
        }
    }

    public void takeDamage(int damage) {
        if(this.shield > 0){
            this.shield -= damage;
            if(this.shield <= 0){
                this.shield = 0;
            }
        } else{
            this.setHp(this.getHp() - damage);
            if(this.getHp() <= 0){
                this.kill();
            }
        }
    }

    public static void upgradeEnemies(){
        baseSpeed = 1.2 * baseSpeed;
        baseHP = (int) (1.2 * baseHP);
        baseDmg = (int) (1.2 * baseDmg);
        baseScore *= 2;
    }
}
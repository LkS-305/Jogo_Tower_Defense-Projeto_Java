package com.neondf.entities;

import java.awt.*;
/*
    ALTEREI INIMIGO PARA TER ARMADURA E IGUALEI A 0, ASSIM OS INIMIGOS QUE TIVEREM ARMADURA, ALTERA O VALOR NA SUBCLASSE
* */
public class Enemy {

    private double x, y, baseSpeed;
    private int baseDamage, baseHP, score, shield;
    private boolean alive = true;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        this.baseSpeed = 1;
        this.baseDamage = 10;
        this.baseHP = 10;
        this.score = 10;
        this.shield = 0;
    }

    public void tick(double targetX, double targetY) {
        if (!alive) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist != 0) {
            x += dx / dist * baseSpeed;
            y += dy / dist * baseSpeed;
        }
    }

    public double getBaseSpeed(){
        return baseSpeed;
    }
    public void setBaseSpeed(double baseSpeed){
        this.baseSpeed = baseSpeed;
    }

    public int getBaseDamage(){
        return baseDamage;
    }
    public void setBaseDamage(int damage){
        this.baseDamage = damage;
    }

    public int getBaseHP(){
        return baseHP;
    }
    public void setBaseHP(int baseHP){
        this.baseHP = baseHP;
    }

    public int getScore(){
        return score;
    }
    public void setScore(int score){
        this.score = score;
    }

    public void render(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillOval((int) x, (int) y, 20, 20);
    }

    public boolean isAlive() { return alive; }
    public double getX() { return x; }
    public double getY() { return y; }

    public void incSpeed(){
        this.baseSpeed = (int) (1.1 * this.baseSpeed);
    }
    public void incDmg(){
        this.baseDamage = (int) (1.1 * this.baseDamage);
    }
    public void incHP(){
        this.baseHP = (int) (1.1 * this.baseHP);
    }

    public void changeSpeed(double multiplier){
        this.baseSpeed = (int) (multiplier * this.baseSpeed);
    }
    public void changeDmg(double multiplier){
        this.baseDamage = (int) (multiplier * this.baseDamage);
    }
    public void changeHP(double multiplier){
        this.baseHP = (int) (multiplier * this.baseHP);
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
            this.setBaseHP(this.getBaseHP() - damage);
            if(this.getBaseHP() <= 0){
                this.kill();
            }
        }
    }

}

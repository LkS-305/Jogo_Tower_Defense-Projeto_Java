package com.neondf.entities;

/*
    Adicionei upgrades para reduzir o delay dos tiros
*/

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Shooter extends Tower {

    private double angle = 0.0;      // ângulo atual (radianos)
    private long lastShot = 0;
    private int hp;
    private static int upgradeDirection = 0, shotDelay = 400, maxHP = 100;
    public Shooter(double x, double y) {
        super(x,y, 50);
        this.hp = maxHP;
    }

    // mantém lógica do jogo (pode ficar vazia se não houver mais comportamento)

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void tryShoot(ArrayList<Bullet> bullets) {
        long now = System.currentTimeMillis();
        if (now - lastShot >= shotDelay) {
            lastShot = now;
            // cria a bala usando o centro calculado dinamicamente
            bullets.add(new Bullet(getCenterX(), getCenterY(), angle));
            if(upgradeDirection > 0){
                bullets.add(new Bullet(getCenterX(), getCenterY(), angle + Math.PI));
                if(upgradeDirection > 1){
                    bullets.add(new Bullet(getCenterX(), getCenterY(), angle + Math.PI/2));
                    if(upgradeDirection > 2){
                        bullets.add(new Bullet(getCenterX(), getCenterY(), angle - Math.PI/2));
                        if(upgradeDirection > 3){
                            bullets.add(new Bullet(getCenterX(), getCenterY(), angle + Math.PI/4));
                            if(upgradeDirection > 4){
                                bullets.add(new Bullet(getCenterX(), getCenterY(), angle - Math.PI/4));
                                if(upgradeDirection > 5){
                                    bullets.add(new Bullet(getCenterX(), getCenterY(), angle + 3*Math.PI/4));
                                    if(upgradeDirection > 6){
                                        bullets.add(new Bullet(getCenterX(), getCenterY(), angle - 3*Math.PI/4));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setUpgradeDirection(){
        if(upgradeDirection < 7){
            upgradeDirection++;
        }
    }

    public void upgradeShotDelay(){
        if(shotDelay > 150){
            shotDelay -= 50;
        }
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
        if (hp < 0) hp = 0;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    //Essa função é chamada realiza a cura
    public void healTower(double healFactor) {
        int hpHealed = (int) (this.getHp() * healFactor);
        if(hpHealed + this.hp < maxHP){
            this.hp += hpHealed;
        } else{
            this.hp = maxHP;
        }
    }

    @Override
    public void render(Graphics2D g) {
        AffineTransform old = g.getTransform();

        // desenha torre centrada
        g.translate(getCenterX(), getCenterY());
        g.rotate(angle);

        g.setColor(Color.CYAN);
        g.fillOval(-this.getSize() / 2, -this.getSize() / 2, this.getSize(), this.getSize());

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

        normalizeAngle();
    }

    private void normalizeAngle() {
        // normaliza para o intervalo (-π, π]
        if (angle <= -Math.PI || angle > Math.PI) {
            angle = Math.atan2(Math.sin(angle), Math.cos(angle));
        }
    }

    public void rotateBy(double deltaAngle) {
        angle += deltaAngle;
        normalizeAngle();
    }
}

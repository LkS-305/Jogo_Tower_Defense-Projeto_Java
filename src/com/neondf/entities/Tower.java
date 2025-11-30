package com.neondf.entities;

import com.neondf.systems.HUD;
import com.neondf.systems.SpriteSheet;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tower {
    private final double x, y;
    private double angle = 0.0;
    private long lastShot = 0;

    private BufferedImage sprite; // Imagem da Torre

    // --- IMAGENS DAS BALAS ---
    private BufferedImage bulletSprite1;
    private BufferedImage bulletSprite2;
    private BufferedImage bulletSprite3;

    private int hp = 100;
    private int maxHp = 100;
    private int shield = 0;
    private int maxShield = 0;
    private final int size = 64;

    private int energy = 0;
    private int maxEnergy = 100;

    // Status
    private int currentDmg = 10;
    private double currentSpeed = 6.0;
    private int currentPierce = 1;
    private int shotDelay = 400;

    // Upgrades
    private int totalUpgrades = 0; // <--- CONTADOR DE EVOLUÇÃO

    private int multiShotLevel = 0;
    private int costMultiShot = 50;
    private int costDmg = 5;
    private int costSpeed = 5;
    private int costPierce = 10;

    public Tower(double x, double y) {
        this.x = x;
        this.y = y;

        // Carrega Sprite da Torre
        SpriteSheet sheet = new SpriteSheet("/sprites/torre.png");
        this.sprite = sheet.getSprite();

        // --- CARREGA SPRITES DAS BALAS ---
        this.bulletSprite1 = new SpriteSheet("/sprites/bullet1.png").getSprite();
        this.bulletSprite2 = new SpriteSheet("/sprites/bullet2.png").getSprite();
        this.bulletSprite3 = new SpriteSheet("/sprites/bullet3.png").getSprite();
    }

    public void tick() {}

    public void tryShoot(ArrayList<Bullet> bullets) {
        long now = System.currentTimeMillis();
        if (now - lastShot >= shotDelay) {
            lastShot = now;

            // --- LÓGICA DE EVOLUÇÃO DA BALA ---
            BufferedImage currentBulletSprite;

            if (totalUpgrades < 5) {
                currentBulletSprite = bulletSprite1;
            } else if (totalUpgrades < 15) {
                currentBulletSprite = bulletSprite2;
            } else {
                currentBulletSprite = bulletSprite3;
            }

            // Criação das balas passando a sprite escolhida
            bullets.add(new Bullet(getCenterX(), getCenterY(), angle, currentSpeed, currentDmg, currentPierce, currentBulletSprite));

            if (multiShotLevel >= 1) bullets.add(new Bullet(getCenterX(), getCenterY(), angle + Math.PI, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
            if (multiShotLevel >= 2) bullets.add(new Bullet(getCenterX(), getCenterY(), angle - Math.PI/2, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
            if (multiShotLevel >= 3) bullets.add(new Bullet(getCenterX(), getCenterY(), angle + Math.PI/2, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
            if (multiShotLevel >= 4) bullets.add(new Bullet(getCenterX(), getCenterY(), angle + Math.PI/4, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
            if (multiShotLevel >= 5) bullets.add(new Bullet(getCenterX(), getCenterY(), angle - Math.PI/4, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
            if (multiShotLevel >= 6) bullets.add(new Bullet(getCenterX(), getCenterY(), angle + 3*Math.PI/4, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
            if (multiShotLevel >= 7) bullets.add(new Bullet(getCenterX(), getCenterY(), angle - 3*Math.PI/4, currentSpeed, currentDmg, currentPierce, currentBulletSprite));
        }
    }

    // --- UPGRADES (Agora incrementam o totalUpgrades) ---

    public void buyUpgradeDamage(HUD hud) {
        if (hud.getCoins() >= costDmg) {
            hud.addCoin(-costDmg);
            currentDmg += 5;
            costDmg *= 2;
            totalUpgrades++; // <--- Evolui
        }
    }
    public void buyUpgradeSpeed(HUD hud) {
        if (hud.getCoins() >= costSpeed) {
            hud.addCoin(-costSpeed);
            currentSpeed += 2.0;
            costSpeed *= 2;
            totalUpgrades++; // <--- Evolui
        }
    }
    public void buyUpgradePierce(HUD hud) {
        if (hud.getCoins() >= costPierce) {
            hud.addCoin(-costPierce);
            currentPierce += 1;
            costPierce *= 2;
            totalUpgrades++; // <--- Evolui
        }
    }
    public void buyUpgradeMultiShot(HUD hud) {
        if (multiShotLevel < 7 && hud.getCoins() >= costMultiShot) {
            hud.addCoin(-costMultiShot);
            multiShotLevel++;
            costMultiShot *= 2;
            totalUpgrades++; // <--- Evolui
        }
    }

    // ... (O resto do código é idêntico ao anterior) ...
    public void render(Graphics2D g) {
        AffineTransform old = g.getTransform();
        g.translate(getCenterX(), getCenterY());
        g.rotate(angle);
        if (sprite != null) {
            g.drawImage(sprite, -size / 2, -size / 2, size, size, null);
        } else {
            g.setColor(Color.CYAN); g.fillOval(-size / 2, -size / 2, size, size);
            g.setColor(Color.MAGENTA); g.fillRect(0, -5, 35, 10);
        }
        g.setTransform(old);

        // Barra de Vida
        int barW = 50; int barH = 8;
        int bx = (int) (getCenterX() - barW / 2.0); int by = (int) (getCenterY() + 40);
        g.setColor(Color.GRAY); g.fillRect(bx, by, barW, barH);
        if (hp > 60) g.setColor(Color.CYAN); else if (hp > 30) g.setColor(Color.ORANGE); else g.setColor(Color.RED);
        int filled = (int) ((hp / 100.0) * barW); g.fillRect(bx, by, filled, barH);
        if (shield > 0 && maxShield > 0) {
            g.setColor(new Color(100, 100, 255, 180));
            int shieldFill = (int) (( (double)shield / maxShield) * barW);
            if (shieldFill > barW) shieldFill = barW;
            g.fillRect(bx, by, shieldFill, barH);
            g.setColor(Color.BLUE); g.drawRect(bx-1, by-1, barW+2, barH+2);
        } else {
            g.setColor(Color.WHITE); g.drawRect(bx, by, barW, barH);
        }
    }

    public void addEnergy(int amount) { this.energy += amount; if (this.energy > maxEnergy) this.energy = maxEnergy; }
    public boolean isUltimateReady() { return energy >= maxEnergy; }
    public void resetEnergy() { this.energy = 0; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public void takeDamage(int amount) { if (shield > 0) { shield -= amount; if (shield < 0) { hp += shield; shield = 0; } } else { this.hp -= amount; } if (hp < 0) hp = 0; }
    public void heal(int amount) { this.hp += amount; if (this.hp > maxHp) this.hp = maxHp; }
    public void addShield(int amount) { this.shield += amount; if (this.shield > maxShield) this.shield = maxShield; }
    public void setMaxShield(int max) { this.maxShield = max; if (this.shield < max) this.shield += (max/10); }
    public int getCostDmg() { return costDmg; }
    public int getCostSpeed() { return costSpeed; }
    public int getCostPierce() { return costPierce; }
    public int getCostMultiShot() { return costMultiShot; }
    public int getMultiShotLevel() { return multiShotLevel; }
    public int getHp() { return hp; }
    public double getCenterX() { return x + size / 2.0; }
    public double getCenterY() { return y + size / 2.0; }
    public void updateDirection(boolean up, boolean down, boolean left, boolean right) {
        if (up && !down && !left && !right) angle = -Math.PI / 2;
        if (down && !up && !left && !right) angle = Math.PI / 2;
        if (left && !right && !up && !down) angle = Math.PI;
        if (right && !left && !up && !down) angle = 0;
        if (up && right) angle = -Math.PI / 4;
        if (up && left)  angle = -3 * Math.PI / 4;
        if (down && right) angle = Math.PI / 4;
        if (down && left)  angle = 3 * Math.PI / 4;
        if (angle <= -Math.PI || angle > Math.PI) angle = Math.atan2(Math.sin(angle), Math.cos(angle));
    }
    public void setAngle(double angle) { this.angle = angle; }
}
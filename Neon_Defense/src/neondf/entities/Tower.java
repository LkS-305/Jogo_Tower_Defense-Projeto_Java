package com.neondf.entities;

import com.neondf.systems.AudioPlayer;
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

    // Imagens das Balas
    private BufferedImage bulletSprite1;
    private BufferedImage bulletSprite2;
    private BufferedImage bulletSprite3;

    private int hp = 100;
    private int maxHp = 100;
    private int shield = 0;
    private int maxShield = 0;
    private final int size = 70;

    private int energy = 0;
    private int maxEnergy = 100;

    // Status
    private int currentDmg = 10;
    private double currentSpeed = 6.0;
    private int currentPierce = 1;
    private int shotDelay = 400;

    private int totalUpgrades = 0;
    private int multiShotLevel = 0;

    // Custos
    private int costMultiShot = 500, costDmg = 5, costSpeed = 5, costPierce = 10;

    public Tower(double x, double y) {
        this.x = x;
        this.y = y;

        try {
            this.bulletSprite1 = new SpriteSheet("/bullet1.png").getSprite();
            this.bulletSprite2 = new SpriteSheet("/bullet2.png").getSprite();
            this.bulletSprite3 = new SpriteSheet("/bullet3.png").getSprite();
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprites das balas.");
        }
    }

    public void tick() {}

    public void tryShoot(ArrayList<Bullet> bullets) {
        long now = System.currentTimeMillis();
        if (now - lastShot >= shotDelay) {
            lastShot = now;

            BufferedImage currentBulletSprite = bulletSprite1;
            double currentScale = 1.0;

            if (totalUpgrades >= 15) {
                currentBulletSprite = bulletSprite3;
                currentScale = 0.5;
            } else if (totalUpgrades >= 5) {
                currentBulletSprite = bulletSprite2;
                currentScale = 0.6;
            }

            shootBullet(bullets, angle, currentBulletSprite, currentScale);

            if (multiShotLevel >= 1) shootBullet(bullets, angle + Math.PI, currentBulletSprite, currentScale);
            if (multiShotLevel >= 2) shootBullet(bullets, angle - Math.PI/2, currentBulletSprite, currentScale);
            if (multiShotLevel >= 3) shootBullet(bullets, angle + Math.PI/2, currentBulletSprite, currentScale);
        }
    }

    private void shootBullet(ArrayList<Bullet> bullets, double ang, BufferedImage img, double scale) {
        bullets.add(new Bullet(getCenterX(), getCenterY(), ang, currentSpeed, currentDmg, currentPierce, img, scale));
    }

    // --- O RENDER RENOVADO ---
    public void render(Graphics2D g) {
        AffineTransform old = g.getTransform();
        g.translate(getCenterX(), getCenterY());

        long time = System.currentTimeMillis();

        // 1. BASE GIRATÓRIA (Fundo)
        AffineTransform baseTransform = g.getTransform();
        g.rotate(time * 0.002); // Gira devagar
        g.setColor(new Color(20, 20, 40));
        g.fillOval(-35, -35, 70, 70);
        g.setColor(new Color(0, 100, 100)); // Ciano Escuro
        g.setStroke(new BasicStroke(2));
        g.drawOval(-30, -30, 60, 60);
        // Detalhes da base
        g.drawLine(0, -30, 0, 30);
        g.drawLine(-30, 0, 30, 0);
        g.setTransform(baseTransform);

        // 2. CORPO DA ARMA (Rotaciona com o mouse)
        g.rotate(angle);

        // --- OS NOVOS TRILHOS (RAILGUN) ---
        // Desenhamos dois "dentes" flutuantes em vez de um quadrado sólido

        // Trilho Superior
        int[] railX = {15, 50, 50, 20}; // Formato angular
        int[] railY_Top = {-18, -14, -6, -10};

        // Trilho Inferior (Espelhado)
        int[] railY_Bot = {18, 14, 6, 10};

        // Cor Metálica Escura
        g.setColor(new Color(40, 40, 50));
        g.fillPolygon(railX, railY_Top, 4);
        g.fillPolygon(railX, railY_Bot, 4);

        // Borda Neon Magenta (Para destacar)
        g.setColor(new Color(255, 0, 255));
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(railX, railY_Top, 4);
        g.drawPolygon(railX, railY_Bot, 4);

        // --- NÚCLEO DE ENERGIA (Entre os trilhos) ---
        // Uma linha pulsante que conecta os trilhos
        float pulse = (float) (Math.sin(time * 0.02) + 1) / 2;
        int alpha = 100 + (int)(pulse * 155);

        g.setColor(new Color(0, 255, 255, alpha)); // Ciano brilhante
        g.setStroke(new BasicStroke(3));
        g.drawLine(20, 0, 45, 0); // O "Raio" dentro da arma

        // Brilho na ponta (Muzzle)
        g.setColor(new Color(255, 255, 255, 200));
        g.fillOval(45, -3, 6, 6);

        // --- CORPO CENTRAL (Cúpula) ---
        g.setColor(new Color(10, 10, 20)); // Preto quase total
        g.fillOval(-20, -20, 40, 40);

        // Borda da Cúpula (Ciano)
        g.setColor(Color.CYAN);
        g.setStroke(new BasicStroke(2));
        g.drawOval(-20, -20, 40, 40);

        // Luz Central (O "Olho" da torre)
        g.setColor(new Color(255, 0, 255)); // Magenta
        g.fillOval(-8, -8, 16, 16);
        g.setColor(Color.WHITE);
        g.fillOval(-3, -3, 6, 6);

        // --- MULTI-SHOT DRONES ---
        if (multiShotLevel > 0) {
            g.setColor(Color.ORANGE);
            g.fillRect(-10, -32, 4, 8); // Drone Esq
            g.fillRect(-10, 24, 4, 8);  // Drone Dir

            // Linha de conexão
            g.setColor(new Color(255, 200, 0, 100));
            g.setStroke(new BasicStroke(1));
            g.drawLine(-8, -20, -8, -32);
            g.drawLine(-8, 20, -8, 32);
        }

        g.setTransform(old);

        renderBars(g);
    }

    private void renderBars(Graphics2D g) {
        int barW = 60; int barH = 6;
        int bx = (int) (getCenterX() - barW / 2.0); int by = (int) (getCenterY() + 50);

        g.setColor(new Color(0,0,0, 180));
        g.fillRect(bx, by, barW, barH);

        if (hp > 60) g.setColor(Color.CYAN);
        else if (hp > 30) g.setColor(Color.ORANGE);
        else g.setColor(Color.RED);
        int filled = (int) ((hp / 100.0) * barW);
        g.fillRect(bx, by, filled, barH);

        if (shield > 0 && maxShield > 0) {
            g.setColor(new Color(0, 100, 255));
            int shieldFill = (int) (( (double)shield / maxShield) * barW);
            if (shieldFill > barW) shieldFill = barW;
            g.fillRect(bx, by - 4, shieldFill, 3);
        }

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawRect(bx, by, barW, barH);
    }

    // --- GETTERS E SETTERS ---
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getShield() { return shield; }
    public int getMaxShield() { return maxShield; }
    public int getCurrentDmg() { return currentDmg; }

    public void addEnergy(int amount) {
        this.energy += amount;
        if (this.energy > maxEnergy) this.energy = maxEnergy;
    }

    public boolean isUltimateReady() { return energy >= maxEnergy; }

    public void resetEnergy() { this.energy = 0; }

    public void takeDamage(int amount) {
        if (shield > 0) {
            shield -= amount;
            if (shield < 0) {
                hp += shield;
                shield = 0;
            }
        } else {
            this.hp -= amount;
        }
        if (hp < 0) hp = 0;
    }

    public void heal(int amount) {
        this.hp += amount;
        if (this.hp > maxHp) this.hp = maxHp;
    }

    public void addShield(int amount) {
        this.shield += amount;
        if (this.shield > maxShield) this.shield = maxShield;
    }
    public void setMaxShield(int max) {
        this.maxShield = max;
        if (this.shield < max) this.shield += (max / 10);
    }

    public void buyUpgradeDamage(HUD hud, AudioPlayer upgradeSound) {
        if (hud.getCoins() >= costDmg) {
            hud.addCoin(-costDmg);
            currentDmg += 5;
            costDmg = (int) (costDmg * 1.5);
            totalUpgrades++;
            upgradeSound.play();
        }
    }
    public void buyUpgradeSpeed(HUD hud, AudioPlayer upgradeSound) {
        if (hud.getCoins() >= costSpeed) {
            hud.addCoin(-costSpeed);
            currentSpeed += 2.0;
            costSpeed = (int) (costSpeed * 1.5);
            totalUpgrades++;
            upgradeSound.play();
        }
    }
    public void buyUpgradePierce(HUD hud, AudioPlayer upgradeSound) {
        if (hud.getCoins() >= costPierce && currentPierce <= 11) {
            hud.addCoin(-costPierce);
            currentPierce += 1;
            costPierce *= 4;
            totalUpgrades++;
            upgradeSound.play();
        }
    }
    public void buyUpgradeMultiShot(HUD hud, AudioPlayer upgradeSound) {
        if (multiShotLevel < 3 && hud.getCoins() >= costMultiShot) {
            hud.addCoin(-costMultiShot);
            multiShotLevel++;
            costMultiShot *= 50;
            totalUpgrades++;
            upgradeSound.play();
        }
    }

    public int getCostDmg() { return costDmg; }
    public int getCostSpeed() { return costSpeed; }
    public int getCostPierce() { return costPierce; }
    public int getCostMultiShot() { return costMultiShot; }

    public int getMultiShotLevel() { return multiShotLevel; }
    public int getPierceLevel() { return (currentPierce - 1); }

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
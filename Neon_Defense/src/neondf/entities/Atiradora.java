package com.neondf.entities;

import com.neondf.systems.SpriteSheet; // <--- Importante
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;   // <--- Importante
import java.util.ArrayList;

public class Atiradora extends Suporte {
    private long lastShot = 0;
    private int delay = 1000;
    private int dmg = 5;

    // Ângulo atual
    private double currentAngle = 0;
    private Enemy currentTarget = null;

    // --- A IMAGEM DA BALA QUE ESTAVA FALTANDO ---
    private BufferedImage bulletSprite;

    public Atiradora() {
        super("Atiradora", new Color(255, 0, 50));
        this.range = 250;
        this.baseColor = new Color(255, 30, 30);

        // --- CARREGA A IMAGEM DA BALA ---
        try {
            SpriteSheet sheet = new SpriteSheet("/bullet1.png");
            this.bulletSprite = sheet.getSprite();
        } catch (Exception e) {
            System.err.println("Erro ao carregar bala da Atiradora");
        }
    }

    @Override
    public void onUpgrade() {
        delay = Math.max(200, delay - 100);
        dmg += 3;
        range += 30;
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        super.tick(tower, enemies, bullets);
        if (level == 0) return;

        setPosition(tower.getCenterX() - 60, tower.getCenterY());

        // Lógica de Mira
        if (currentTarget == null || !currentTarget.isAlive() || getDistanceTo(currentTarget) > range) {
            currentTarget = findNearestEnemy(enemies);
        }

        if (currentTarget != null) {
            double dx = currentTarget.getX() - x;
            double dy = currentTarget.getY() - y;
            this.currentAngle = Math.atan2(dy, dx);
        }

        // Lógica de Tiro
        long now = System.currentTimeMillis();
        if (now - lastShot > delay && currentTarget != null) {
            shootAt(currentTarget, bullets);
            lastShot = now;
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (level == 0) return;

        AffineTransform old = g.getTransform();
        g.translate(x, y);

        // 1. BASE ESPINHADA
        g.setColor(new Color(50, 0, 0, 200));
        int[] spikeX = {-25, -15, 0, 15, 25, 15, 0, -15};
        int[] spikeY = {0, 15, 25, 15, 0, -15, -25, -15};
        g.fillPolygon(spikeX, spikeY, 8);
        g.setColor(baseColor);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(spikeX, spikeY, 8);

        // 2. TORRETA GIRATÓRIA
        g.rotate(currentAngle);

        g.setColor(new Color(100, 0, 20));
        g.fillRect(0, -12, 35, 8);
        g.fillRect(0, 4, 35, 8);

        g.setColor(Color.ORANGE);
        g.setStroke(new BasicStroke(1f));
        g.drawRect(0, -12, 35, 8);
        g.drawRect(0, 4, 35, 8);

        // 3. MIRA PULSANTE
        float pulse = (float) (Math.sin(pulseTimer*2) + 1) / 2;
        int alpha = (int) (pulse * 155) + 100;
        int size = 16 + (int)(pulse * 4);

        g.setColor(new Color(255, 50, 0, alpha));
        g.setStroke(new BasicStroke(2));
        g.drawOval(-size/2, -size/2, size, size);
        g.drawLine(-size, 0, size, 0);
        g.drawLine(0, -size, 0, size);
        g.setColor(Color.WHITE);
        g.fillOval(-2, -2, 4, 4);

        g.setTransform(old);

        renderLevelDots(g);
    }

    private void renderLevelDots(Graphics2D g) {
        int dotSize = 4; int spacing = 6;
        int startX = (int)x -((level * (dotSize + spacing)) / 2) + spacing/2;
        for (int i = 0; i < level; i++) {
            g.setColor(baseColor); g.fillOval(startX + (i * (dotSize + spacing)), (int)y-30, dotSize, dotSize);
            g.setColor(Color.WHITE); g.fillOval(startX + (i * (dotSize + spacing)) + 1, (int)y-29, dotSize-2, dotSize-2);
        }
    }

    private double getDistanceTo(Enemy e) {
        return Math.sqrt(Math.pow(e.getX() - x, 2) + Math.pow(e.getY() - y, 2));
    }

    private Enemy findNearestEnemy(ArrayList<Enemy> enemies) {
        Enemy target = null;
        double minDist = range;
        for (Enemy e : enemies) {
            double dist = getDistanceTo(e);
            if (dist < minDist && e.isAlive()) {
                minDist = dist;
                target = e;
            }
        }
        return target;
    }

    private void shootAt(Enemy target, ArrayList<Bullet> bullets) {
        // --- AQUI ESTAVA O ERRO ---
        // Agora passamos 'this.bulletSprite' e escala '1.0'
        bullets.add(new Bullet(x, y, currentAngle, 5.0, dmg, 1, this.bulletSprite, 1.0));
    }
}
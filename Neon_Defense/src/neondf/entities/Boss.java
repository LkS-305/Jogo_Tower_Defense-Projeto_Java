package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Boss extends Enemy {

    // Variáveis de animação
    private long animationTimer = 0;
    private double rotateAngleOuter = 0;
    private double rotateAngleInner = 0;
    private double auraAngle = 0; // Nova variável para a aura

    public Boss(double x, double y) {
        super(x, y);

        // --- STATUS DE BOSS ---
        this.hp = 3000;
        this.baseHP = 3000;
        this.dmg = 50;
        this.speed = 0.4;
        this.score = 50000;

        // Tamanho da Hitbox
        this.width = 120;
        this.height = 120;

        // Importante: scale deve ser compatível com a classe mãe
        // Se der erro aqui, certifique-se que mudou 'scale' para protected em Enemy.java
        this.scale = 1;
    }

    @Override
    public void tick(double targetX, double targetY) {
        super.tick(targetX, targetY);

        if (!alive) return;

        animationTimer++;
        rotateAngleOuter += 0.01;
        rotateAngleInner -= 0.02;
        auraAngle -= 0.05; // A aura gira rápido
    }

    @Override
    public void render(Graphics2D g) {
        if (!alive) return;

        AffineTransform old = g.getTransform();
        g.translate(x, y);

        // --- CAMADA 0: AURA DE ENERGIA (NOVO!) ---
        // Desenha isso ANTES do corpo para ficar "atrás"
        AffineTransform auraTx = g.getTransform();
        g.rotate(auraAngle);

        float pulse = (float) (Math.sin(animationTimer * 0.1) + 1) / 2; // 0.0 a 1.0
        int auraSize = 140 + (int)(pulse * 20); // Cresce e diminui

        // 1. Círculo de energia fraca
        g.setColor(new Color(100, 0, 50, 50)); // Roxo transparente
        g.fillOval(-auraSize/2, -auraSize/2, auraSize, auraSize);

        // 2. Arcos de eletricidade girando
        g.setStroke(new BasicStroke(4));
        g.setColor(new Color(255, 0, 100, 100)); // Magenta Neon
        g.drawArc(-auraSize/2, -auraSize/2, auraSize, auraSize, 0, 60);
        g.drawArc(-auraSize/2, -auraSize/2, auraSize, auraSize, 120, 60);
        g.drawArc(-auraSize/2, -auraSize/2, auraSize, auraSize, 240, 60);

        // 3. Partículas "Glitch" (Quadrados aleatórios tremendo)
        if (animationTimer % 5 == 0) {
            g.setColor(Color.RED);
            g.fillRect(60, -10, 10, 20);
            g.fillRect(-70, 20, 5, 5);
        }

        g.setTransform(auraTx); // Restaura para desenhar o corpo

        // --- CAMADA 1: ARMADURA EXTERNA ---
        AffineTransform outerTx = g.getTransform();
        g.rotate(rotateAngleOuter);

        int[] xPointsOut = {-60, -30, 30, 60, 60, 30, -30, -60};
        int[] yPointsOut = {-30, -60, -60, -30, 30, 60, 60, 30};
        Polygon outerArmor = new Polygon(xPointsOut, yPointsOut, 8);

        g.setColor(new Color(50, 0, 20, 200));
        g.fillPolygon(outerArmor);

        g.setColor(new Color(255, 0, 50));
        g.setStroke(new BasicStroke(6));
        g.drawPolygon(outerArmor);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(outerArmor);

        g.setTransform(outerTx);

        // --- CAMADA 2: MECANISMO INTERNO ---
        AffineTransform innerTx = g.getTransform();
        g.rotate(rotateAngleInner);

        int[] xPointsStar = {-40, -15, 0, 15, 40, 15, 0, -15};
        int[] yPointsStar = {0, -15, -40, -15, 0, 15, 40, 15};
        Polygon innerStar = new Polygon(xPointsStar, yPointsStar, 8);

        g.setColor(new Color(255, 100, 0));
        g.setStroke(new BasicStroke(3));
        g.drawPolygon(innerStar);

        g.setTransform(innerTx);

        // --- CAMADA 3: O NÚCLEO ---
        int coreSize = 30 + (int)(pulse * 15);
        int alpha = 150 + (int)(pulse * 105);

        g.setColor(new Color(255, 0, 0, alpha));
        g.fillOval(-coreSize/2, -coreSize/2, coreSize, coreSize);
        g.setColor(new Color(255, 255, 200));
        g.fillOval(-10, -10, 20, 20);

        g.setTransform(old);

        renderBossHealthBar(g);
    }

    private void renderBossHealthBar(Graphics2D g) {
        int barW = 100;
        int barH = 8;
        int bx = (int)x - barW/2;
        int by = (int)y - 80; // Subi um pouco por causa da aura

        g.setColor(Color.BLACK);
        g.fillRect(bx, by, barW, barH);

        g.setColor(Color.RED);
        int fill = (int)((hp / (double)baseHP) * barW);
        g.fillRect(bx, by, fill, barH);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawRect(bx, by, barW, barH);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x - width/2, (int)y - height/2, width, height);
    }
}
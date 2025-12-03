package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class Shockwave {
    private double x, y;
    private double radius = 10;
    private double maxRadius = 650; // Um pouco maior
    private double speed = 25;      // Explosão mais rápida
    private boolean active = true;

    // Gerador de aleatoriedade para o efeito "doidão"
    private Random rand = new Random();

    public Shockwave(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void tick() {
        radius += speed;
        if (radius >= maxRadius) {
            active = false;
        }
    }

    public void render(Graphics2D g) {
        if (!active) return;

        // Cálculo de transparência (Alpha)
        float progress = (float) (radius / maxRadius);
        // O alpha cai ao quadrado para sumir mais rápido no final
        int alpha = (int) (255 * (1 - progress * progress));
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;

        AffineTransform old = g.getTransform();
        g.translate(x, y); // Move o ponto zero para o centro da explosão

        // --- CAMADA 1: ANÉIS DE ENERGIA (BASE ROXA/MAGENTA) ---
        // A espessura diminui conforme expande
        float strokeWidth = 30f * (1 - progress);
        if (strokeWidth < 1) strokeWidth = 1;

        // Anel Externo Roxo
        g.setColor(new Color(100, 0, 200, alpha / 2));
        g.setStroke(new BasicStroke(strokeWidth));
        g.drawOval((int)(-radius), (int)(-radius), (int)(radius * 2), (int)(radius * 2));

        // Anel Médio Magenta Neon
        g.setColor(new Color(255, 0, 255, alpha));
        g.setStroke(new BasicStroke(strokeWidth / 2));
        int rMid = (int)(radius * 0.9);
        g.drawOval(-rMid, -rMid, rMid * 2, rMid * 2);

        // Anel Interno Ciano (O brilho da frente)
        g.setColor(new Color(0, 255, 255, alpha));
        g.setStroke(new BasicStroke(4));
        int rInner = (int)(radius * 0.95);
        g.drawOval(-rInner, -rInner, rInner * 2, rInner * 2);


        // --- CAMADA 2: RAIOS ELÉTRICOS DOIDÕES ---
        int numBolts = 12; // Quantidade de raios
        g.setStroke(new BasicStroke(3));

        for (int i = 0; i < numBolts; i++) {
            // Gira o canvas para desenhar o raio
            // Adiciona um "jitter" (tremor) aleatório no ângulo
            g.rotate(Math.toRadians((360.0 / numBolts) + rand.nextInt(15)-7));

            // Cor aleatória entre branco e roxo elétrico
            if (rand.nextBoolean()) g.setColor(new Color(255, 255, 255, alpha));
            else g.setColor(new Color(180, 50, 255, alpha));

            // Desenha um raio "torto" usando 2 segmentos de linha
            int midR = (int)(radius / 2);
            // Deslocamento aleatório no meio e no fim do raio para ele ficar zig-zag
            int midJitter = rand.nextInt(40) - 20;
            int endJitterX = rand.nextInt(30);
            int endJitterY = rand.nextInt(40) - 20;

            // Segmento 1 (Centro até o meio torto)
            g.drawLine(0, 0, midR, midJitter);
            // Segmento 2 (Meio torto até a borda torta)
            g.drawLine(midR, midJitter, (int)radius + endJitterX, endJitterY);

            // Reseta a rotação deste raio para o próximo
            g.rotate(-Math.toRadians((360.0 / numBolts) + rand.nextInt(15)-7));
        }

        // --- CAMADA 3: FAÍSCAS/PARTÍCULAS ---
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(255, 255, 100, alpha)); // Amarelo/Branco
        for(int i = 0; i < 15; i++) {
            // Posição aleatória dentro do raio atual
            double rSpark = radius * (0.6 + rand.nextDouble() * 0.4);
            double angleSpark = Math.toRadians(rand.nextInt(360));
            int sx = (int)(Math.cos(angleSpark) * rSpark);
            int sy = (int)(Math.sin(angleSpark) * rSpark);
            // Desenha um pequeno risco aleatório
            g.drawLine(sx, sy, sx+rand.nextInt(14)-7, sy+rand.nextInt(14)-7);
        }

        g.setTransform(old); // Restaura a posição original do Graphics
        g.setStroke(new BasicStroke(1)); // Reseta o pincel
    }

    public boolean isActive() { return active; }

    public boolean collidesWith(Enemy e) {
        double dx = e.getX() - x;
        double dy = e.getY() - y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        // Área de colisão
        return dist < radius && dist > (radius - speed - 50);
    }
}
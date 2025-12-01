package com.neondf.entities;

import com.neondf.systems.HUD;
import java.awt.*;
import java.awt.geom.AffineTransform; // <--- FALTAVA ESSA LINHA!
import java.util.ArrayList;

// Classe base para torres auxiliares (Atiradora, Médica, Escudeira)
public abstract class Suporte {

    protected String name;
    protected double x, y;
    protected int level = 0;
    protected int maxLevel = 5;
    protected int cost = 50;
    protected int range = 100;

    // Cor padrão agora é Verde Neon para aspecto de suporte
    protected Color baseColor = new Color(0, 255, 100);

    // Variável para animar o pulsar
    protected float pulseTimer = 0;

    public Suporte(String name, Color colorIgnorada) {
        this.name = name;
        // Ignoramos a cor passada no construtor e forçamos o verde neon
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Metodo tick padrão para atualizar a animação
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        if (level > 0) {
            pulseTimer += 0.1f; // Faz o núcleo pulsar
        }
    }

    // O NOVO RENDER TECH
    public void render(Graphics2D g) {
        if (level == 0) return; // Se não comprou, não desenha

        AffineTransform old = g.getTransform();
        g.translate(x, y); // Move o ponto zero para o centro da torre

        // --- 1. BASE HEXAGONAL ---
        int[] hexX = {-20, -10, 10, 20, 10, -10};
        int[] hexY = {0, -15, -15, 0, 15, 15};
        Polygon hexagon = new Polygon(hexX, hexY, 6);

        // Preenchimento escuro
        g.setColor(new Color(0, 100, 50, 200));
        g.fillPolygon(hexagon);
        // Borda brilhante
        g.setColor(baseColor);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(hexagon);

        // --- 2. NÚCLEO DE ENERGIA PULSANTE ---
        // Calcula um brilho que vai e volta (seno)
        float pulse = (float) (Math.sin(pulseTimer) + 1) / 2; // varia de 0.0 a 1.0
        int alpha = (int) (pulse * 155) + 100; // varia de 100 a 255 (transparência)
        int coreSize = 14 + (int)(pulse * 4); // Tamanho varia de 14 a 18

        // Brilho externo do núcleo
        g.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha));
        g.fillOval(-coreSize/2, -coreSize/2, coreSize, coreSize);

        // Centro branco intenso
        g.setColor(Color.WHITE);
        g.fillOval(-4, -4, 8, 8);

        // --- 3. INDICADORES DE NÍVEL (Pequenos pontos flutuando acima) ---
        int dotSize = 4;
        int spacing = 6;
        int startX = -((level * (dotSize + spacing)) / 2) + spacing/2;

        for (int i = 0; i < level; i++) {
            g.setColor(baseColor); // Verde neon
            g.fillOval(startX + (i * (dotSize + spacing)), -25, dotSize, dotSize);
            g.setColor(Color.WHITE); // Brilho central no ponto
            g.fillOval(startX + (i * (dotSize + spacing)) + 1, -24, dotSize-2, dotSize-2);
        }

        g.setTransform(old); // Restaura a posição normal
    }

    public abstract void onUpgrade();

    public void upgrade(HUD hud) {
        if (level < maxLevel && hud.getCoins() >= cost) {
            hud.addCoin(-cost);
            level++;
            cost *= 2;
            onUpgrade();
        }
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getCost() { return cost; }
    public boolean isMaxLevel() { return level >= maxLevel; }
}
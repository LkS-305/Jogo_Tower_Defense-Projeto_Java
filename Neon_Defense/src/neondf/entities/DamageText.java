package com.neondf.entities;

import java.awt.*;

public class DamageText {

    private double x, y;
    private String text;
    private int life = 40; // Dura 40 frames (menos de 1 segundo)
    private boolean active = true;

    // Cor do texto (Branco ou Amarelo dependendo do dano)
    private Color color;

    public DamageText(double x, double y, int damage) {
        this.x = x;
        this.y = y;
        this.text = String.valueOf(damage);

        // Se o dano for alto (crítico/alto nível), fica Amarelo, senão Branco
        if (damage > 50) this.color = Color.YELLOW;
        else this.color = Color.WHITE;

        // Pequena variação aleatória na posição X para não ficarem encavalados
        this.x += (Math.random() * 20) - 10;
    }

    public void tick() {
        life--;
        y -= 1.5; // O texto sobe

        if (life <= 0) {
            active = false;
        }
    }

    public void render(Graphics2D g) {
        if (!active) return;

        // Fonte pequena e negrito
        g.setFont(new Font("Arial", Font.BOLD, 14));

        // Efeito de sombra preta para ler melhor
        g.setColor(Color.BLACK);
        g.drawString(text, (int)x + 1, (int)y + 1);

        // Texto principal com transparência (fade out)
        // O Alpha diminui conforme a vida acaba
        int alpha = (int) ((life / 40.0) * 255);
        if (alpha < 0) alpha = 0; if (alpha > 255) alpha = 255;

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.drawString(text, (int)x, (int)y);
    }

    public boolean isActive() { return active; }
}
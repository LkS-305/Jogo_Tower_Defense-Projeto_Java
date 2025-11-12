package com.neondf.systems;

import java.awt.*;

public class HUD {

    private int health = 100;
    private int score = 0;
    private int wave = 1;
    private int neonCoins = 0;

    public void tick() {
        if (health < 0) health = 0;
        if (health > 100) health = 100;
    }

    public void render(Graphics2D g) {
        // HP
        g.setColor(Color.DARK_GRAY);
        g.fillRect(20, 540, 200, 20);

        g.setColor(Color.CYAN);
        g.fillRect(20, 540, health * 2, 20);

        g.setColor(Color.WHITE);
        g.drawRect(20, 540, 200, 20);

        // Textos
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.drawString("HP: " + health, 230, 555);
        g.drawString("Score: " + score, 340, 555);
        g.drawString("Wave: " + wave, 470, 555);
        g.drawString("💠 NeonCoins: " + neonCoins, 600, 555);
    }

    public void damage(int amount) { health -= amount; }
    public void addScore(int points) { score += points; }
    public void addCoin() { neonCoins++; }
    public void setWave(int wave) { this.wave = wave; }
    public int getHealth() { return health; }
    public int getCoins() { return neonCoins; }
}

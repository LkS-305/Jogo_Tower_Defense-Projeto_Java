package com.neondf.systems;

import com.neondf.entities.*;
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

    public void render(Graphics2D g, Tower tower, Atiradora shot, Medica heal, Escudeira shield) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(20, 540, 200, 20);
        g.setColor(Color.CYAN);
        g.fillRect(20, 540, health * 2, 20);
        g.setColor(Color.WHITE);
        g.drawRect(20, 540, 200, 20);

        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString("HP: " + health, 230, 555);
        g.drawString("Score: " + score, 340, 555);
        g.drawString("Wave: " + wave, 470, 555);
        g.setColor(Color.YELLOW);
        g.drawString("💠 NeonCoins: " + neonCoins, 600, 555);

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(10, 10, 240, 90);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.drawString("TORRE PRINCIPAL", 20, 25);
        g.setColor(Color.WHITE);
        g.drawString("[1] Dano ($" + tower.getCostDmg() + ")", 20, 45);
        g.drawString("[2] Velocidade ($" + tower.getCostSpeed() + ")", 20, 65);
        g.drawString("[3] Perfuração ($" + tower.getCostPierce() + ")", 20, 85);

        g.setColor(Color.GREEN);
        if(neonCoins >= tower.getCostDmg()) g.fillOval(220, 37, 8, 8);
        if(neonCoins >= tower.getCostSpeed()) g.fillOval(220, 57, 8, 8);
        if(neonCoins >= tower.getCostPierce()) g.fillOval(220, 77, 8, 8);

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(540, 10, 240, 90);
        g.setColor(Color.MAGENTA);

        if (shot != null) {
            g.drawString("AUXILIARES (Lv." + shot.getLevel() + "/" + heal.getLevel() + "/" + shield.getLevel() + ")", 550, 25);
            g.setColor(Color.WHITE);
            g.drawString("[4] Atiradora ($" + shot.getCost() + ")", 550, 45);
            g.drawString("[5] Médica    ($" + heal.getCost() + ")", 550, 65);
            g.drawString("[6] Escudo    ($" + shield.getCost() + ")", 550, 85);

            g.setColor(Color.GREEN);
            if(neonCoins >= shot.getCost()) g.fillOval(750, 37, 8, 8);
            if(neonCoins >= heal.getCost()) g.fillOval(750, 57, 8, 8);
            if(neonCoins >= shield.getCost()) g.fillOval(750, 77, 8, 8);
        }
    }
    public void damage(int amount) { health -= amount; }
    public void addScore(int points) { score += points; }
    public void addCoin(int coin) { neonCoins += coin; }
    public void setWave(int wave) { this.wave = wave; }
    public int getHealth() { return health; }
    public int getCoins() { return neonCoins; }
}
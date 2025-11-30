package com.neondf.systems;

import com.neondf.entities.*;
import java.awt.*;

public class HUD {
    private int health = 100;
    private int score = 0;
    private int wave = 1;
    private int neonCoins = 0;

    public void tick() {
        // Mantemos a variavel health aqui só para não quebrar o Game.java que chama hud.damage()
        if (health < 0) health = 0;
        if (health > 100) health = 100;
    }

    public void render(Graphics2D g, Tower tower, Atiradora shot, Medica heal, Escudeira shield) {

        // --- NOVA BARRA DE ESPECIAL (Substituindo a Vida) ---
        int barX = 20;
        int barY = 540;
        int barWidth = 200;
        int barHeight = 20;

        // Fundo da Barra
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Cálculo da largura baseado na Energia da Torre
        // (Usa tower.getEnergy() em vez da variável local health)
        int energyFill = (int) (( (double)tower.getEnergy() / tower.getMaxEnergy()) * barWidth);

        // Cor: Amarelo se carregando, Piscando Branco/Amarelo se cheia
        if (tower.isUltimateReady()) {
            if (System.currentTimeMillis() % 200 > 100) g.setColor(Color.WHITE);
            else g.setColor(Color.YELLOW);
        } else {
            g.setColor(new Color(200, 200, 0)); // Amarelo mais escuro
        }

        g.fillRect(barX, barY, energyFill, barHeight);

        // Borda
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        // --- TEXTO AO LADO DA BARRA ---
        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.setColor(Color.WHITE);

        if (tower.isUltimateReady()) {
            g.drawString("ULTIMATE PRONTA! [E]", barX + 210, barY + 15);
        } else {
            g.drawString("CARREGANDO: " + tower.getEnergy() + "%", barX + 210, barY + 15);
        }

        // --- RESTO DO HUD (Score, Wave, Coins) ---
        // Ajustei a posição X para não bater no texto da Ultimate
        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.drawString("Score: " + score, 450, 555);
        g.drawString("Wave: " + wave, 600, 555);

        g.setColor(Color.YELLOW);
        g.drawString("💠 Coins: " + neonCoins, 650, 530); // Subi um pouco para separar

        // --- LOJA PRINCIPAL ---
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(10, 10, 240, 110);

        g.setColor(Color.CYAN);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.drawString("TORRE PRINCIPAL", 20, 25);
        g.setColor(Color.WHITE);
        g.drawString("[1] Dano ($" + tower.getCostDmg() + ")", 20, 45);
        g.drawString("[2] Velocidade ($" + tower.getCostSpeed() + ")", 20, 65);
        g.drawString("[3] Perfuração ($" + tower.getCostPierce() + ")", 20, 85);

        if (tower.getMultiShotLevel() < 7) {
            g.drawString("[7] Multi-Tiro ($" + tower.getCostMultiShot() + ")", 20, 105);
        } else {
            g.setColor(Color.RED); g.drawString("[7] Multi-Tiro (MAX)", 20, 105);
        }

        // Indicadores (Bolinhas verdes)
        g.setColor(Color.GREEN);
        if(neonCoins >= tower.getCostDmg()) g.fillOval(220, 37, 8, 8);
        if(neonCoins >= tower.getCostSpeed()) g.fillOval(220, 57, 8, 8);
        if(neonCoins >= tower.getCostPierce()) g.fillOval(220, 77, 8, 8);
        if(neonCoins >= tower.getCostMultiShot() && tower.getMultiShotLevel() < 7) g.fillOval(220, 97, 8, 8);

        // --- LOJA AUXILIAR ---
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

    // Métodos mantidos para compatibilidade
    public void damage(int amount) { health -= amount; }
    public void addScore(int points) { score += points; }
    public void addCoin(int coin) { neonCoins += coin; }
    public void setWave(int wave) { this.wave = wave; }
    public int getHealth() { return health; }
    public int getCoins() { return neonCoins; }
    public int getScore() { return score; }
}
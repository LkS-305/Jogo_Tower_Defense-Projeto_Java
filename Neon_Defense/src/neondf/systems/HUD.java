package com.neondf.systems;

import com.neondf.entities.*;
import java.awt.*;

public class HUD {
    private int health = 100;
    private int score = 0;
    private int wave = 1;
    private int neonCoins = 0;

    // --- CORES DA INTERFACE ---
    private final Color GLASS_BG = new Color(10, 10, 30, 200);
    private final Color NEON_CYAN = new Color(0, 255, 255);
    private final Color NEON_MAGENTA = new Color(255, 0, 255);

    // NOVA COR DO DINHEIRO (Verde Matrix/Neon)
    private final Color MONEY_COLOR = new Color(50, 255, 100);

    private final Color TEXT_SHADOW = new Color(0, 0, 0, 180);

    public void tick() {
        if (health < 0) health = 0;
        if (health > 100) health = 100;
    }

    public void render(Graphics2D g, Tower tower, Atiradora shot, Medica heal, Escudeira shield) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. BARRA DE ULTIMATE
        renderUltimateBar(g, tower);

        // 2. STATS
        renderStats(g, tower);

        // 3. LOJA ESQUERDA
        renderMainShop(g, tower);

        // 4. LOJA DIREITA
        renderAuxShop(g, tower, shot, heal, shield);
    }

    private void renderUltimateBar(Graphics2D g, Tower tower) {
        int x = 20;
        int y = 520;
        int w = 250;
        int h = 25;

        // Fundo
        g.setColor(new Color(20, 20, 20));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 10, 10);

        // Preenchimento (Gradiente Laranja/Amarelo Tech)
        double pct = (double)tower.getEnergy() / tower.getMaxEnergy();
        int fillW = (int)(w * pct);

        if (fillW > 0) {
            GradientPaint gp;
            if (tower.isUltimateReady()) {
                int pulse = (int)(Math.sin(System.currentTimeMillis() * 0.01) * 50) + 200;
                gp = new GradientPaint(x, y, new Color(255, 255, 0), x + w, y, new Color(255, pulse, 0));
            } else {
                gp = new GradientPaint(x, y, new Color(200, 200, 0), x + w, y, new Color(255, 100, 0));
            }
            g.setPaint(gp);
            g.fillRoundRect(x+2, y+2, fillW-4, h-4, 8, 8);
        }

        // Texto
        g.setFont(new Font("Verdana", Font.BOLD, 12));
        String text = tower.isUltimateReady() ? "ULTIMATE PRONTA [E]" : "CARREGANDO: " + (int)(pct*100) + "%";

        g.setColor(TEXT_SHADOW);
        g.drawString(text, x + 12, y + 17);
        g.setColor(Color.WHITE);
        g.drawString(text, x + 10, y + 17);

        // HP da Base
        g.setFont(new Font("Verdana", Font.BOLD, 20));
        g.setColor(NEON_CYAN);
        g.drawString("HP TOTAL: " + (tower.getHp()+tower.getShield()), x, y - 10);
    }

    private void renderStats(Graphics2D g, Tower tower) {
        int x = 450;
        int y = 500;

        g.setFont(new Font("Verdana", Font.BOLD, 14));

        // DINHEIRO (Agora Verde Neon Tech)
        g.setColor(MONEY_COLOR);
        g.drawString(" $ " + neonCoins, x + 230, y + 25);

        // WAVE (Branco)
        g.setColor(Color.WHITE);
        g.drawString("WAVE: " + wave, x + 130, y + 25);

        // SCORE (Roxo para diferenciar)
        g.setColor(NEON_MAGENTA);
        g.drawString("SCORE: " + score, x - 100, y + 25);
    }

    private void renderMainShop(Graphics2D g, Tower tower) {
        int x = 10;
        int y = 10;
        int w = 260;
        int h = 130;

        drawGlassPanel(g, x, y, w, h, NEON_CYAN);

        g.setFont(new Font("Verdana", Font.BOLD, 14));
        g.setColor(NEON_CYAN);
        g.drawString("SISTEMAS DA TORRE", x + 15, y + 25);

        g.setFont(new Font("Verdana", Font.PLAIN, 12));
        int rowY = y + 50;
        int spacing = 20;

        drawUpgradeRow(g, "[1] Dano", tower.getCostDmg(), tower.getCostDmg() <= neonCoins, x+15, rowY);
        drawUpgradeRow(g, "[2] Velocidade", tower.getCostSpeed(), tower.getCostSpeed() <= neonCoins, x+15, rowY + spacing);
        if (tower.getPierceLevel() < 11) {
            drawUpgradeRow(g, "[3] Perfuração", tower.getCostPierce(), tower.getCostPierce() <= neonCoins, x+15, rowY + spacing*2);
        } else {
            g.setColor(Color.RED);
            g.drawString("[3] Perfuração (MAX)", x+15, rowY + spacing*2);
        }
        if (tower.getMultiShotLevel() < 7) {
            drawUpgradeRow(g, "[4] Multi-Tiro", tower.getCostMultiShot(), tower.getCostMultiShot() <= neonCoins, x+15, rowY + spacing*3);
        } else {
            g.setColor(Color.RED);
            g.drawString("[4] Multi-Tiro (MAX)", x+15, rowY + spacing*3);
        }
    }

    private void renderAuxShop(Graphics2D g, Tower tower, Atiradora shot, Medica heal, Escudeira shield) {
        if (shot == null) return;

        int w = 260;
        int h = 110;
        int x = 800 - w - 10;
        int y = 10;

        drawGlassPanel(g, x, y, w, h, NEON_MAGENTA);

        g.setFont(new Font("Verdana", Font.BOLD, 14));
        g.setColor(NEON_MAGENTA);
        String lvls = "(Lv." + shot.getLevel() + "/" + heal.getLevel() + "/" + shield.getLevel() + ")";
        g.drawString("DRONES " + lvls, x + 15, y + 25);

        g.setFont(new Font("Verdana", Font.PLAIN, 12));
        int rowY = y + 50;
        int spacing = 20;

        if (shot.getLevel() < 5) {
            drawUpgradeRow(g, "[5] Atiradora", shot.getCost(), shot.getCost() <= neonCoins, x+15, rowY);

        } else {
            g.setColor(Color.RED);
            g.drawString("[5] Atiradora (MAX)", x+15, rowY);
        }

        if (heal.getLevel() < 5) {
            drawUpgradeRow(g, "[6] Médica", heal.getCost(), heal.getCost() <= neonCoins, x+15, rowY + spacing);
        } else {
            g.setColor(Color.RED);
            g.drawString("[6] Médica (MAX)", x+15, rowY + spacing);
        }
        if (shield.getLevel() < 5) {
            drawUpgradeRow(g, "[7] Escudeira", shield.getCost(), shield.getCost() <= neonCoins, x+15, rowY + spacing*2);
        } else {
            g.setColor(Color.RED);
            g.drawString("[7] Escudeira (MAX)", x+15, rowY + spacing*2);
        }

    }

    private void drawUpgradeRow(Graphics2D g, String name, int price, boolean canBuy, int x, int y) {
        g.setColor(Color.WHITE);
        g.drawString(name, x, y);

        // Se pode comprar, o preço fica verde brilhante
        g.setColor(canBuy ? MONEY_COLOR : Color.GRAY);
        g.drawString("$" + price, x + 140, y);

        if (canBuy) {
            g.fillOval(x + 200, y - 8, 8, 8);
            g.setColor(new Color(50, 255, 100, 100)); // Glow verde
            g.drawOval(x + 198, y - 10, 12, 12);
        }
    }

    private void drawGlassPanel(Graphics2D g, int x, int y, int w, int h, Color borderColor) {
        g.setColor(GLASS_BG);
        g.fillRect(x, y, w, h);
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(1));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(3));
        int corner = 10;
        g.drawLine(x, y, x + corner, y);
        g.drawLine(x, y, x, y + corner);
        g.drawLine(x + w, y + h, x + w - corner, y + h);
        g.drawLine(x + w, y + h, x + w, y + h - corner);
    }

    public void damage(int amount) { health -= amount; }
    public void addScore(int points) { score += points; }
    public void addCoin(int coin) { neonCoins += coin; }
    public void setWave(int wave) { this.wave = wave; }
    public int getHealth() { return health; }
    public int getCoins() { return neonCoins; }
    public int getScore() { return score; }
}
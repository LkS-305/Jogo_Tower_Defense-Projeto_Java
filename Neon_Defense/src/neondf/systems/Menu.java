package com.neondf.systems;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Menu {

    public Rectangle playBtn;
    public Rectangle settingsBtn;
    public Rectangle controlsBtn;
    public Rectangle nameBox;

    public boolean inSettings = false;
    public boolean inControls = false;

    public String playerName = "Player";
    private int maxChars = 10;

    // --- NOVO: Variável para guardar o Recorde ---
    public int highScore = 0;

    // Variáveis Visuais
    private long timer = 0;
    private float gridOffset = 0;
    private Random rand = new Random();
    private ArrayList<Point> stars = new ArrayList<>();
    private ArrayList<Float> starSpeeds = new ArrayList<>();

    private final Color DEEP_BG = new Color(10, 5, 20);
    private final Color NEON_PURPLE = new Color(180, 0, 255);
    private final Color NEON_CYAN = new Color(0, 255, 255);
    private final Color GRID_COLOR = new Color(200, 0, 255, 50);

    public Menu() {
        int centerX = 800 / 2 - 100;

        nameBox = new Rectangle(centerX, 240, 200, 40);
        playBtn = new Rectangle(centerX, 320, 200, 50);
        settingsBtn = new Rectangle(centerX, 400, 200, 50);
        controlsBtn = new Rectangle(centerX, 400, 200, 50);

        for (int i = 0; i < 50; i++) {
            stars.add(new Point(rand.nextInt(800), rand.nextInt(600)));
            starSpeeds.add(0.5f + rand.nextFloat() * 2.0f);
        }

        // --- CARREGA O RECORDE AO INICIAR ---
        updateHighScore();
    }

    // Método para recarregar o recorde (chamado quando volta do Game Over)
    public void updateHighScore() {
        this.highScore = ScoreManager.loadHighScore();
    }

    public void tick() {
        timer++;
        gridOffset += 1.5;
        if (gridOffset >= 40) gridOffset = 0;

        for (int i = 0; i < stars.size(); i++) {
            Point p = stars.get(i);
            p.y -= starSpeeds.get(i);
            if (p.y < 0) {
                p.y = 600;
                p.x = rand.nextInt(800);
            }
        }
    }

    public void render(Graphics2D g, int mouseX, int mouseY) {
        // Fundo
        GradientPaint bgGradient = new GradientPaint(0, 0, Color.BLACK, 0, 600, DEEP_BG);
        g.setPaint(bgGradient);
        g.fillRect(0, 0, 800, 600);

        // Estrelas
        g.setColor(new Color(255, 255, 255, 100));
        for (Point p : stars) g.fillRect(p.x, p.y, 2, 2);

        // Grade
        int horizon = 300;
        g.setColor(GRID_COLOR);
        for (int i = -400; i <= 1200; i += 80) g.drawLine(400 + (i-400)/4, horizon, i, 600);
        for (int i = 0; i < 300; i += 40) {
            int y = horizon + i + (int)gridOffset;
            if (y <= 600) g.drawLine(0, y, 800, y);
        }
        g.setColor(NEON_PURPLE);
        g.fillRect(0, horizon - 1, 800, 3);

        if (inControls) {
            renderControls(g);
        } else if (inSettings) {
            renderSettings(g, mouseX, mouseY);
        } else {
            renderMainMenu(g, mouseX, mouseY);
        }

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("NEON SYSTEM v2.1 // SAVE_ENABLED", 10, 590);
    }

    private void renderMainMenu(Graphics2D g, int mouseX, int mouseY) {
        // --- MOSTRAR HIGH SCORE NO TOPO ---
        g.setFont(new Font("Consolas", Font.BOLD, 20));
        g.setColor(Color.YELLOW);
        String hsText = "BEST RECORD: " + highScore;
        int hsW = g.getFontMetrics().stringWidth(hsText);
        g.drawString(hsText, (800 - hsW)/2, 40);

        // Título
        g.setFont(new Font("Consolas", Font.BOLD, 70));
        String title = "NEON DEFENSE";
        int titleW = g.getFontMetrics().stringWidth(title);
        int titleX = (800 - titleW) / 2;
        int titleY = 150;

        g.setColor(new Color(255, 0, 255, 180));
        g.drawString(title, titleX + 4, titleY);
        g.setColor(new Color(0, 255, 255, 180));
        g.drawString(title, titleX - 4, titleY);

        if (rand.nextInt(100) > 95) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(title, titleX + rand.nextInt(5)-2, titleY + rand.nextInt(5)-2);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(title, titleX, titleY);
        }

        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(NEON_CYAN);
        drawCenteredText(g, "- PRESS START TO DEFEND -", 180);

        // Input Nome
        g.setColor(new Color(20, 20, 40, 200));
        g.fill(nameBox);
        g.setColor(NEON_PURPLE);
        g.draw(nameBox);
        g.setFont(new Font("Consolas", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        String cursor = (timer % 40 > 20) ? "_" : "";
        g.drawString("CAPT: " + playerName + cursor, nameBox.x + 10, nameBox.y + 28);

        // Botões
        drawNeonButton(g, playBtn, "INICIAR MISSÃO", mouseX, mouseY, NEON_CYAN);
        drawNeonButton(g, settingsBtn, "SISTEMA", mouseX, mouseY, NEON_PURPLE);
    }

    private void renderSettings(Graphics2D g, int mouseX, int mouseY) {
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(100, 100, 600, 400);
        g.setColor(NEON_PURPLE);
        g.drawRect(100, 100, 600, 400);

        g.setFont(new Font("Consolas", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        drawCenteredText(g, "/// CONFIGURAÇÕES DO SISTEMA ///", 150);

        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        g.setColor(Color.GRAY);
        drawCenteredText(g, "Dificuldade: [ NORMAL ]", 220);
        drawCenteredText(g, "Som: [ DESATIVADO ]", 260);

        drawNeonButton(g, controlsBtn, "CONTROLES", mouseX, mouseY, Color.ORANGE);

        g.setColor(Color.YELLOW);
        drawCenteredText(g, "[ESC] RETORNAR", 480);
    }

    private void renderControls(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 240));
        g.fillRect(0, 0, 800, 600);

        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.setColor(NEON_CYAN);
        drawCenteredText(g, "MANUAL DE DEFESA", 80);

        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.setColor(Color.WHITE);

        int leftX = 150;
        int rightX = 450;
        int y = 150;

        drawKey(g, "W", leftX, y); drawKey(g, "A", leftX+40, y); drawKey(g, "S", leftX+80, y); drawKey(g, "D", leftX+120, y);
        g.drawString("-> MOVER MIRA", leftX + 170, y+20);

        y += 60;
        drawKey(g, "SPACE", leftX, y, 100);
        g.drawString("-> DISPARAR", leftX + 120, y+20);

        y += 60;
        drawKey(g, "E", leftX, y);
        g.setColor(Color.YELLOW);
        g.drawString("-> ULTIMATE (Choque)", leftX + 50, y+20);

        y = 150;
        g.setColor(NEON_PURPLE);
        g.drawString("UPGRADES (Teclado Numérico):", rightX, y);
        y += 30;
        g.setColor(Color.WHITE);
        g.drawString("[1] Dano", rightX, y); y+=25;
        g.drawString("[2] Velocidade", rightX, y); y+=25;
        g.drawString("[3] Perfuração", rightX, y); y+=25;
        g.drawString("[7] Multi-Tiro", rightX, y); y+=40;

        g.setColor(NEON_CYAN);
        g.drawString("SUPORTE:", rightX, y); y+=30;
        g.setColor(Color.WHITE);
        g.drawString("[4] Atiradora", rightX, y); y+=25;
        g.drawString("[5] Médica", rightX, y); y+=25;
        g.drawString("[6] Escudeira", rightX, y);

        g.setColor(Color.YELLOW);
        drawCenteredText(g, "[ESC] FECHAR MANUAL", 550);
    }

    private void drawNeonButton(Graphics2D g, Rectangle btn, String text, int mx, int my, Color color) {
        boolean hover = btn.contains(mx, my);
        if (hover) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            g.fillRect(btn.x, btn.y, btn.width, btn.height);
            g.setColor(color);
            g.setStroke(new BasicStroke(3));
            g.drawRect(btn.x, btn.y, btn.width, btn.height);
            g.setColor(Color.WHITE);
        } else {
            g.setColor(color);
            g.setStroke(new BasicStroke(1));
            g.drawRect(btn.x, btn.y, btn.width, btn.height);
        }
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getHeight();
        g.drawString(text, btn.x + (btn.width - tw) / 2, btn.y + (btn.height + th/2) / 2 - 4);
    }

    private void drawKey(Graphics2D g, String key, int x, int y) { drawKey(g, key, x, y, 30); }
    private void drawKey(Graphics2D g, String key, int x, int y, int w) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, w, 30);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, w, 30);
        g.drawString(key, x + 5, y + 20);
    }

    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (800 - textWidth) / 2, y);
    }

    public void handleTyping(int key, char keyChar) {
        if (inSettings || inControls) return;
        if (key == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        } else if (playerName.length() < maxChars && Character.isLetterOrDigit(keyChar)) {
            playerName += keyChar;
        }
    }
}
package com.neondf.systems;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Menu {

    // Botões Principais
    public Rectangle playBtn;
    public Rectangle settingsBtn;
    public Rectangle nameBox;

    // Botão dentro de Configurações
    public Rectangle controlsBtn;

    // Estados do Menu
    public boolean inSettings = false;
    public boolean inControls = false; // Nova tela

    public String playerName = "Player";
    private int maxChars = 10;

    public Menu() {
        int centerX = 800 / 2 - 100;

        nameBox = new Rectangle(centerX, 200, 200, 40);
        playBtn = new Rectangle(centerX, 300, 200, 50);
        settingsBtn = new Rectangle(centerX, 380, 200, 50);

        // Botão que fica dentro da tela de configurações
        controlsBtn = new Rectangle(centerX, 400, 200, 50);
    }

    public void tick() { }

    public void render(Graphics2D g, int mouseX, int mouseY) {
        // Fundo
        g.setColor(new Color(10, 10, 20));
        g.fillRect(0, 0, 800, 600);

        // Título Geral
        if (!inControls) { // Só mostra o título grande se não estiver lendo os controles
            g.setFont(new Font("Consolas", Font.BOLD, 60));
            g.setColor(Color.MAGENTA);
            drawCenteredText(g, "NEON DEFENSE", 100);
            g.setColor(Color.CYAN);
            drawCenteredText(g, "NEON DEFENSE", 97);
        }

        // Lógica de qual tela mostrar
        if (inControls) {
            renderControls(g);
        } else if (inSettings) {
            renderSettings(g, mouseX, mouseY);
        } else {
            renderMainMenu(g, mouseX, mouseY);
        }

        // Rodapé
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.GRAY);
        g.drawString("v1.2 - Controls Update", 10, 580);
    }

    private void renderMainMenu(Graphics2D g, int mouseX, int mouseY) {
        g.setFont(new Font("Consolas", Font.BOLD, 20));

        // Caixa Nome
        g.setColor(Color.DARK_GRAY);
        g.fill(nameBox);
        g.setColor(Color.CYAN);
        g.draw(nameBox);
        g.setColor(Color.WHITE);
        g.drawString("Nome: " + playerName + (System.currentTimeMillis() % 1000 > 500 ? "_" : ""), nameBox.x + 10, nameBox.y + 27);

        // Botão Jogar
        drawButton(g, playBtn, "JOGAR", mouseX, mouseY, Color.CYAN);

        // Botão Config
        drawButton(g, settingsBtn, "CONFIGURAÇÕES", mouseX, mouseY, Color.MAGENTA);
    }

    private void renderSettings(Graphics2D g, int mouseX, int mouseY) {
        g.setFont(new Font("Consolas", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        drawCenteredText(g, "--- CONFIGURAÇÕES ---", 200);

        drawCenteredText(g, "Dificuldade: Normal", 260);
        drawCenteredText(g, "Som: Imaginário (Por enquanto)", 300);

        // Botão para ver Controles
        drawButton(g, controlsBtn, "VER CONTROLES", mouseX, mouseY, Color.ORANGE);

        g.setColor(Color.YELLOW);
        drawCenteredText(g, "[ESC] Voltar", 500);
    }

    private void renderControls(Graphics2D g) {
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.setColor(Color.ORANGE);
        drawCenteredText(g, "GUIA DE CONTROLES", 60);

        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.setColor(Color.WHITE);

        int leftX = 200;
        int rightX = 500;
        int y = 150;

        // --- MOVIMENTAÇÃO ---
        g.setColor(Color.CYAN);
        g.drawString("MIRA / MOVIMENTO", leftX, y);
        g.setColor(Color.WHITE);
        g.drawString("[ W ][ A ][ S ][ D ]", leftX, y + 30);
        g.drawString("ou Setas para girar", leftX, y + 50);

        // --- TIRO ---
        g.setColor(Color.CYAN);
        g.drawString("COMBATE", rightX, y);
        g.setColor(Color.WHITE);
        g.drawString("[ ESPAÇO ] Segure para Atirar", rightX, y + 30);

        y += 120;

        // --- UPGRADES ---
        g.setColor(Color.MAGENTA);
        g.drawString("LOJA - TORRE PRINCIPAL", leftX, y);
        g.setColor(Color.WHITE);
        g.drawString("[ 1 ] Dano", leftX, y + 30);
        g.drawString("[ 2 ] Velocidade de Tiro", leftX, y + 50);
        g.drawString("[ 3 ] Perfuração", leftX, y + 70);

        g.setColor(Color.MAGENTA);
        g.drawString("LOJA - AUXILIARES", rightX, y);
        g.setColor(Color.WHITE);
        g.drawString("[ 4 ] Torre Atiradora", rightX, y + 30);
        g.drawString("[ 5 ] Torre Médica", rightX, y + 50);
        g.drawString("[ 6 ] Torre de Escudo", rightX, y + 70);

        // Voltar
        g.setColor(Color.YELLOW);
        drawCenteredText(g, "Pressione [ESC] para voltar", 550);
    }

    // Método auxiliar para desenhar botões bonitos
    private void drawButton(Graphics2D g, Rectangle btn, String text, int mx, int my, Color hoverColor) {
        if (btn.contains(mx, my)) {
            g.setColor(hoverColor);
            g.fillRect(btn.x, btn.y, btn.width, btn.height);
            g.setColor(Color.BLACK);
            g.drawRect(btn.x, btn.y, btn.width, btn.height);
        } else {
            g.setColor(Color.WHITE);
            g.draw(btn);
        }

        // Centraliza texto no botão
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getHeight();
        g.drawString(text, btn.x + (btn.width - tw) / 2, btn.y + (btn.height + th/2) / 2 - 3);
    }

    public void handleTyping(int key, char keyChar) {
        if (inSettings || inControls) return; // Não digita se estiver nos menus
        if (key == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        } else if (playerName.length() < maxChars && Character.isLetterOrDigit(keyChar)) {
            playerName += keyChar;
        }
    }

    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (800 - textWidth) / 2, y);
    }
}
package com.neondf.main;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal do jogo Neon Defense.
 * Responsável por exibir o Canvas e registrar os listeners globais.
 */
public class Window {

    public Window(int width, int height, String title, Game game) {
        JFrame frame = new JFrame(title);

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Adiciona o Canvas do jogo
        frame.add(game);
        frame.pack();
        frame.setVisible(true);

        // Garante foco de input no Canvas
        game.setFocusable(true);
        game.requestFocusInWindow();
        game.requestFocus();

        // 🚀 Registra o listener de mouse DIRETAMENTE NA JANELA
        InputHandler handler = new InputHandler(game.getTower(), game);
        frame.addMouseMotionListener(handler);

        // Inicia o loop do jogo
        game.start();
    }
}

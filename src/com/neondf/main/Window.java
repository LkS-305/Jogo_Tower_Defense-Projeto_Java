package com.neondf.main;

import javax.swing.*;
import java.awt.*;

/**
 * Classe responsável por criar e configurar a janela principal do jogo.
 * Ela recebe o objeto Game (que é o Canvas onde desenhamos) e o exibe na tela.
 */
public class Window {

    public Window(int width, int height, String title, Game game) {
        JFrame frame = new JFrame(title); // cria a janela
        frame.setPreferredSize(new Dimension(width, height)); // define tamanho
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fecha ao clicar no X
        frame.setResizable(false); // não deixa redimensionar
        frame.setLocationRelativeTo(null); // centraliza na tela
        frame.add(game); // adiciona o Canvas (onde o jogo desenha)
        frame.pack(); // ajusta tudo direitinho
        frame.setVisible(true); // mostra a janela

        game.start(); // inicia o loop do jogo
    }
}

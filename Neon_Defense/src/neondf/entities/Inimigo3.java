package com.neondf.entities;

public class Inimigo3 extends Enemy {
    private final double hpMultiplier = 0.8, dmgMultiplier = 2, speedMultiplier = 1.5, scoreMultiplier = 5;
    public Inimigo3(double x, double y) {
        // --- CONFIGURAÇÃO DO REAPER (INIMIGO 3) ---
        // Caminho: "/sprites/inim3.png"

        // --- ATENÇÃO AOS NÚMEROS ---
        // Você precisa calcular e trocar o 100 pelo valor real!
        // Frames: 6 (A primeira linha tem 6 bonecos)
        // Linha: 0 (A primeira linha parece ser ele andando/flutuando)

        super(x, y, "/inim3.png", 100, 100, 6, 0);

        // Ajuste de tamanho (Ele parece magro, talvez escala 2 fique bom)
        this.scale = 2;

        // --- STATUS (Rápido e Frágil) ---
        this.changeScore(scoreMultiplier);
        this.changeSpeed(speedMultiplier);   // Mais rápido que o normal (1.0)
        this.changeHP(hpMultiplier);      // Menos vida (morre rápido)
        this.changeDmg(dmgMultiplier);     // Dano baixo
        this.setShield(0);       // Sem escudo
    }
}
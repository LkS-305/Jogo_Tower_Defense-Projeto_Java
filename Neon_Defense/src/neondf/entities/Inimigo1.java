package com.neondf.entities;

public class Inimigo1 extends Enemy {
    private final double hpMultiplier = 2.0, dmgMultiplier = 2.0, speedMultiplier = 0.8, scoreMultiplier = 2;

    public Inimigo1(double x, double y) {
        // --- CONFIGURAÇÃO DO NIGHTBORNE ---
        // Caminho: "/sprites/NightBorne.png"
        // Largura do Frame: 80 (Padrão desse asset)
        // Altura do Frame: 80 (Padrão desse asset)
        // Quantidade de Frames: 6 (São 6 bonecos correndo na segunda linha)
        // Linha (Row): 1 (A linha 0 é parado, a linha 1 é correndo)

        super(x, y, "/inim1.png", 80, 80, 6, 1);

        this.scale = 1;

        // Status do Inimigo
        this.changeScore(scoreMultiplier);
        this.changeSpeed(speedMultiplier);
        this.changeHP(hpMultiplier);
        this.changeDmg(dmgMultiplier);
        this.setShield(this.getHp()/2);
    }
}
package com.neondf.entities;

public class Inimigo1 extends Enemy {

    public Inimigo1(double x, double y) {
        // --- CONFIGURAÇÃO DO NIGHTBORNE ---
        // Caminho: "/sprites/NightBorne.png"
        // Largura do Frame: 80 (Padrão desse asset)
        // Altura do Frame: 80 (Padrão desse asset)
        // Quantidade de Frames: 6 (São 6 bonecos correndo na segunda linha)
        // Linha (Row): 1 (A linha 0 é parado, a linha 1 é correndo)

        super(x, y, "/sprites/inim1.png", 80, 80, 6, 1);

        // Status do Inimigo
        this.changeScore(3.0);
        this.changeSpeed(0.8);
        this.changeHP(2);
        this.changeDmg(2);
        this.setShield(this.getHp()/2);
    }
}
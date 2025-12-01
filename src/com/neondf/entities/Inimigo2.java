package com.neondf.entities;

public class Inimigo2 extends Enemy {

    public Inimigo2(double x, double y) {
        // --- CONFIGURAÇÃO DO NECROMANCER (INIMIGO 2) ---
        // Caminho: "/sprites/inim2.png"
        // Largura: 160
        // Altura: 128
        // Frames: 8 (Tem 8 bonecos na linha de andar)
        // Linha: 1 (A linha 1 é a animação de "Walk/Run")

        super(x, y, "/sprites/inim2.png", 160, 128, 8, 1);

        // --- AJUSTE DE TAMANHO ---
        // Agora está em 2x. Se quiser maior, coloque 3.
        this.scale = 2;

        // --- STATUS DO INIMIGO 2 ---
        this.changeScore(5.0);   // Mais pontos
        this.changeSpeed(1.0);   // Velocidade normal
        this.changeHP(3.0);      // Mais vida
        this.changeDmg(3.0);     // Mais dano
        this.setShield(this.getHp() / 3); // Escudo
    }
}
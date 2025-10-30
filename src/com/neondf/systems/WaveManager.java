package com.neondf.systems;

import com.neondf.entities.Enemy;
import java.util.ArrayList;
import java.util.Random;

/**
 * Gerencia as waves (fases) do jogo.
 * Controla a quantidade e velocidade dos inimigos que aparecem.
 */
public class WaveManager {

    private int currentWave = 1;
    private int enemiesToSpawn = 5;
    private Random random = new Random();
    private ArrayList<Enemy> enemies;

    private int spawnTimer = 0;

    public WaveManager(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void tick() {
        spawnTimer++;

        // Spawn a cada 60 ticks (~1 segundo)
        if (spawnTimer >= 60) {
            if (enemiesToSpawn > 0) {
                spawnEnemy();
                enemiesToSpawn--;
            }
            spawnTimer = 0;
        }

        // Quando todos morrerem e nenhum restar -> próxima wave
        if (enemies.isEmpty() && enemiesToSpawn == 0) {
            nextWave();
        }
    }

    private void spawnEnemy() {
    int side = random.nextInt(4);
    double x = 0, y = 0;

    switch (side) {
        case 0 -> { x = random.nextInt(780); y = -30; }     // topo
        case 1 -> { x = 830; y = random.nextInt(580); }     // direita
        case 2 -> { x = random.nextInt(780); y = 630; }     // baixo
        case 3 -> { x = -30; y = random.nextInt(580); }     // esquerda
    }

    double speed = 1.0 + (currentWave * 0.2);
    Enemy e = new Enemy(x, y, speed);
    enemies.add(e);

    // debug (pra testar)
    System.out.println("Inimigo spawnado em (" + x + ", " + y + ")");
}


    private void nextWave() {
        currentWave++;
        enemiesToSpawn = 5 + (currentWave * 2); // mais inimigos por wave
        System.out.println("Nova wave: " + currentWave);
    }

    public int getCurrentWave() {
        return currentWave;
    }
}

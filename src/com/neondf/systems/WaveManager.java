package com.neondf.systems;

import com.neondf.entities.Enemy;
import java.util.ArrayList;
import java.util.Random;

public class WaveManager {

    private int currentWave = 1;
    private int enemiesToSpawn;
    private int enemiesSpawned = 0;
    private int enemiesAlive = 0;
    private long lastSpawnTime = 0;
    private long spawnInterval = 1200; // ms entre inimigos
    private final ArrayList<Enemy> enemies;
    private final Random random = new Random();

    public WaveManager(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
        enemiesToSpawn = 5;
    }

    public void tick() {
        long now = System.currentTimeMillis();

        // Spawna enquanto ainda há inimigos pra nascer
        if (enemiesSpawned < enemiesToSpawn && now - lastSpawnTime >= spawnInterval) {
            spawnEnemy();
            enemiesSpawned++;
            enemiesAlive++;
            lastSpawnTime = now;
        }

        // Se acabou a wave
        if (enemiesAlive <= 0 && enemiesSpawned >= enemiesToSpawn) {
            nextWave();
        }
    }

    private void spawnEnemy() {
        int x = 0, y = 0;

        // Até a wave 5 → direções fixas N S L O
        if (currentWave <= 5) {
            int dir = enemiesSpawned % 4; // alterna direções
            switch (dir) {
                case 0 -> { x = 400; y = -30; } // Norte
                case 1 -> { x = 400; y = 630; } // Sul
                case 2 -> { x = 830; y = 300; } // Leste
                case 3 -> { x = -30; y = 300; } // Oeste
            }
        } else {
            // Depois da wave 5 → posições aleatórias nas bordas
            int side = random.nextInt(4);
            switch (side) {
                case 0 -> { x = random.nextInt(800); y = -30; } // topo
                case 1 -> { x = random.nextInt(800); y = 630; } // baixo
                case 2 -> { x = -30; y = random.nextInt(600); } // esquerda
                case 3 -> { x = 830; y = random.nextInt(600); } // direita
            }
        }

        enemies.add(new Enemy(x, y, this));
    }

    private void nextWave() {
        currentWave++;
        enemiesToSpawn += 3; // aumenta progressivamente
        enemiesSpawned = 0;
        enemiesAlive = 0;
    }

    public void enemyDied() {
        enemiesAlive--;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}

package com.neondf.systems;

import com.neondf.entities.*; // Importa todos os inimigos
import java.util.ArrayList;
import java.util.Random;

public class WaveManager { // <--- Veja, o nome bate com o arquivo!
    private int currentWave = 1;
    private int enemiesToSpawn;
    private int bossNumber = 1;
    private int enemiesSpawned = 0;
    private int enemiesAlive = 0;

    private long waveStartTime;
    private long lastSpawnTime = System.currentTimeMillis();
    private long spawnInterval = 1200L;

    private final ArrayList<Enemy> enemies;
    private final ArrayList<Integer> spawnType;
    private final Random random = new Random();

    public WaveManager(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
        this.spawnType = new ArrayList<>();
        this.enemiesToSpawn = 5;
        this.enemySpawner();
        this.waveStartTime = System.currentTimeMillis();
    }

    public void tick() {
        long now = System.currentTimeMillis();

        if (now - waveStartTime < 3000) {
            return;
        }

        if (spawnType.isEmpty() && enemiesSpawned < enemiesToSpawn) {
            enemiesSpawned = enemiesToSpawn;
        }

        if (this.enemiesSpawned < this.enemiesToSpawn && now - this.lastSpawnTime >= this.spawnInterval) {
            this.spawnEnemy();
        }

        if (this.enemiesAlive <= 0 && this.enemiesSpawned >= this.enemiesToSpawn) {
            this.nextWave();
        }
    }

    private void spawnEnemy() {
        if (this.spawnType.isEmpty()) return;

        int tipoInimigo = this.spawnType.remove(0); // remove(0) é seguro para ArrayList
        int x = 0;
        int y = 0;

        if (this.currentWave <= 5) {
            int dir = this.enemiesSpawned % 4;
            switch (dir) {
                case 0 -> { x = 400; y = -30; }
                case 1 -> { x = 400; y = 630; }
                case 2 -> { x = 830; y = 300; }
                case 3 -> { x = -30; y = 300; }
            }
        } else {
            int side = this.random.nextInt(8);
            switch (side) {
                case 0 -> { x = 400; y = -30; }
                case 1 -> { x = 730; y = -30; }
                case 2 -> { x = 800; y = 300; }
                case 3 -> { x = 730; y = 630; }
                case 4 -> { x = 400; y = 630; }
                case 5 -> { x = 70; y = 630; }
                case 6 -> { x = -30; y = 300; }
                case 7 -> { x = 70; y = -30; }
            }
        }

        // Adiciona o inimigo correto na lista
        switch (tipoInimigo) {
            case 0 -> this.enemies.add(new Boss(x, y));
            case 1 -> this.enemies.add(new Enemy(x, y)); // Usa o construtor padrão (bolinha ou sprite padrão)
            case 2 -> this.enemies.add(new Inimigo1(x, y));
            case 3 -> this.enemies.add(new Inimigo2(x, y));
            case 4 -> this.enemies.add(new Inimigo3(x, y));
        }

        ++this.enemiesSpawned;
        ++this.enemiesAlive;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void enemySpawner() {
        int qtdParaAdicionar = 0;

        if (this.currentWave <= 3) {
            qtdParaAdicionar = 5 + (currentWave * 2);
            for(int i = 0; i < qtdParaAdicionar; ++i) this.spawnType.add(1);
        } else if (this.currentWave <= 7) {
            qtdParaAdicionar = 8 + currentWave;
            for(int i = 0; i < qtdParaAdicionar; i++) this.spawnType.add(this.random.nextBoolean() ? 1 : 2);
        } else {
            qtdParaAdicionar = 10 + (currentWave * 2);
            for(int i = 0; i < qtdParaAdicionar; ++i) this.spawnType.add(this.random.nextInt(5));
        }

        if (this.currentWave % 5 == 0) {
            for(int i = 0; i < this.bossNumber; ++i) {
                this.spawnType.add(0);
                qtdParaAdicionar++;
            }
            this.bossNumber++;
        }
        this.enemiesToSpawn = this.spawnType.size();
    }

    private void nextWave() {
        ++this.currentWave;
        this.enemiesSpawned = 0;
        this.enemiesAlive = 0;
        this.waveStartTime = System.currentTimeMillis();
        this.enemySpawner();

        if (this.currentWave >= 5 && this.currentWave % 5 == 0) {
            Enemy.upgradeEnemies();
            this.spawnInterval -= 200L;
        }
    }

    public boolean isWaveStarting() { return System.currentTimeMillis() - waveStartTime < 3000; }
    public void enemyDied() { --this.enemiesAlive; }
    public int getCurrentWave() { return this.currentWave; }
}
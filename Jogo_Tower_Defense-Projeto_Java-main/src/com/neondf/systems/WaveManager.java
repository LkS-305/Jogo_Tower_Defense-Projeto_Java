package com.neondf.systems;

import com.neondf.entities.Boss;
import com.neondf.entities.Enemy;
import com.neondf.entities.Inimigo1;
import com.neondf.entities.Inimigo2;
import com.neondf.entities.Inimigo3;
import java.util.ArrayList;
import java.util.Random;

public class WaveManager {
    private int currentWave = 1;
    private int enemiesToSpawn;
    private int bossNumber = 1;
    private int enemiesSpawned = 0;
    private int enemiesAlive = 0;
    private long lastSpawnTime = System.currentTimeMillis();
    private long spawnInterval = 1200L;
    private final ArrayList<Enemy> enemies;
    private final ArrayList<Integer> spawnType;
    private final Random random = new Random();

    public WaveManager(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
        this.spawnType = new ArrayList<>();

        // Configuração inicial
        this.enemiesToSpawn = 5;
        this.enemySpawner(); // Preenche a primeira leva
    }

    public void tick() {
        long now = System.currentTimeMillis();

        // Só tenta spawnar se ainda houver inimigos planejados E se a lista não estiver vazia
        if (this.enemiesSpawned < this.enemiesToSpawn && now - this.lastSpawnTime >= this.spawnInterval) {
            this.spawnEnemy();
            // Se spawnEnemy falhar (lista vazia), não incrementamos nada para não travar
        }

        // Checa fim de wave
        if (this.enemiesAlive <= 0 && this.enemiesSpawned >= this.enemiesToSpawn) {
            this.nextWave();
        }
    }

    private void spawnEnemy() {
        // --- TRAVA DE SEGURANÇA ---
        // Se a lista estiver vazia, cancela o spawn. Isso impede o jogo de travar.
        if (this.spawnType.isEmpty()) {
            // Força o fim do spawn para evitar loop infinito tentando spawnar fantasma
            this.enemiesSpawned = this.enemiesToSpawn;
            return;
        }

        // Remove o primeiro da fila (Java seguro)
        int tipoInimigo = this.spawnType.removeFirst();

        int x = 0;
        int y = 0;

        // Lógica de Posição (Mantive igual)
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

        if (this.currentWave % 5 == 0 && enemiesSpawned == 0 && this.spawnInterval > 300L) {
            this.spawnInterval -= 200L;
        }

        // Cria o inimigo
        switch (tipoInimigo) {
            case 0 -> this.enemies.add(new Boss(x, y));
            case 1 -> this.enemies.add(new Enemy(x, y));
            case 2 -> this.enemies.add(new Inimigo1(x, y));
            case 3 -> this.enemies.add(new Inimigo2(x, y));
            case 4 -> this.enemies.add(new Inimigo3(x, y));
        }

        // Atualiza contadores APÓS o sucesso
        ++this.enemiesSpawned;
        ++this.enemiesAlive;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void enemySpawner() {
        // Aqui estava o erro: você aumentava o enemiesToSpawn DEPOIS do loop e também no nextWave.
        // Vamos usar variaveis temporarias para calcular quantos adicionar.

        int qtdParaAdicionar = 0;

        if (this.currentWave <= 3) {
            qtdParaAdicionar = 5 + (currentWave * 2); // Exemplo de escalonamento
            for(int i = 0; i < qtdParaAdicionar; ++i) {
                this.spawnType.add(1);
            }
        } else if (this.currentWave <= 7) {
            qtdParaAdicionar = 8 + currentWave;
            for(int i = 0; i < qtdParaAdicionar; i++) {
                this.spawnType.add(this.random.nextBoolean() ? 1 : 2);
            }
        } else {
            qtdParaAdicionar = 10 + (currentWave * 2);
            for(int i = 0; i < qtdParaAdicionar; ++i) {
                this.spawnType.add(this.random.nextInt(5));
            }
        }

        if (this.currentWave % 5 == 0) {
            for(int i = 0; i < this.bossNumber; ++i) {
                this.spawnType.add(0);
                qtdParaAdicionar++;
            }
            this.bossNumber++;
        }

        // O PULO DO GATO:
        // Sincronizamos o numero total EXATAMENTE com o tamanho da lista.
        // Assim é impossível dar erro de falta de inimigo.
        this.enemiesToSpawn = this.spawnType.size();
    }

    private void nextWave() {
        ++this.currentWave;
        this.enemiesSpawned = 0;
        this.enemiesAlive = 0;

        // Chama o spawner para preencher a lista da nova onda
        this.enemySpawner();

        if (this.currentWave >= 5 && this.currentWave % 5 == 0) {
            this.updateEnemies(this.enemies);
        }
    }

    // Getters e outros métodos mantidos...
    public void enemyDied() { --this.enemiesAlive; }

    public void updateEnemies(ArrayList<Enemy> enemies) {
        // Buffa os inimigos a cada 5 ondas
    }

    public int getCurrentWave() { return this.currentWave; }
    // Remova getters não usados se quiser limpar o código
}
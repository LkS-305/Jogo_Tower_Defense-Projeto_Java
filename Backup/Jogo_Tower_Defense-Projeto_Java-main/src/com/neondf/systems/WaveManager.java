package com.neondf.systems;

/*
    CRIEI METODO ENEMYSPAWNER() QUE IRÁ PREENCHER UM ARRAYLIST COM ÍNDICES DOS INIMIGOS
    NO METODO SPAWN ENEMY, ADICIONAR UM SWITCH QUE IRÁ:
        PEGAR O PRIMEIRO NUMERO DO spawnType e ADICIONA O INIMIGO CORRESPONDENTE NO ARRAYLIST enemies
        REMOVER ESSE PRIMEIRO NUMERO DO ARRAYLIST spawnType
    atribui os valores das variáveis ao objeto da classe, assim a cada vez que cria uma nova waveManager, reinicia os valores

    ALTEREI UM POUCO O METODO DE SPAWNENEMY PARA QUE A CADA
    FALTA:
        CRIAR O ALGORITMO QUE IRÁ PREENCHER O ARRAYLIST
        CRIAR O ALGORITMO PARA O BOSS
            CRIAR CLASSE PARA O BOSS
            QUAL O DIFERENCIAL DESSE BOSS?

*/

import com.neondf.entities.Enemy;
import com.neondf.entities.Inimigo3;
import com.neondf.entities.Inimigo1;
import com.neondf.entities.Inimigo2;
import com.neondf.entities.Boss;

import java.util.ArrayList;
import java.util.Random;

public class WaveManager {

    private int currentWave;
    private int enemiesToSpawn, bossNumber;
    private int enemiesSpawned;
    private int enemiesAlive;
    private long lastSpawnTime;
    private long spawnInterval; // ms entre inimigos
    private final ArrayList<Enemy> enemies;
    private final ArrayList<Integer> spawnType;
    private final Random random = new Random();

    public WaveManager(ArrayList<Enemy> enemies) {
        this.currentWave = 1;
        this.bossNumber = 1;
        this.enemiesSpawned = 0;
        this.enemiesAlive = 0;
        this.lastSpawnTime = System.currentTimeMillis();
        this.spawnInterval = 1200;
        this.enemies = enemies;
        this.spawnType = new ArrayList<>();
        enemiesToSpawn = 5;
    }


    public void tick() {
        long now = System.currentTimeMillis();
        if(currentWave == 1 && enemiesSpawned == 0){
            enemySpawner();
        }

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
            enemySpawner();
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
            int side = random.nextInt(8);
            switch (side) {
                case 0 -> { x = 400; y = -30; } // Norte
                case 1 -> { x = 730; y = -30; } // Nordeste
                case 2 -> { x = 800; y = 300; } // Leste
                case 3 -> { x = 730; y = 630; } // Sudeste
                case 4 -> { x = 400; y = 630; } // Sul
                case 5 -> { x = 70; y = 630; } // Sudoeste
                case 6 -> { x = -30; y = 300; } // Oeste
                case 7 -> { x = 70; y = -30; }// Noroeste
            }
            if((currentWave % 5 == 1) && spawnInterval > 100){
                spawnInterval -= 100;
            }
        }
        if(!spawnType.isEmpty()){
            switch(spawnType.getFirst()){
                case 0: enemies.add(new Boss(x,y)); break;
                case 1: enemies.add(new Enemy(x,y)); break;
                case 2: enemies.add(new Inimigo1(x,y)); break;
                case 3: enemies.add(new Inimigo2(x,y)); break;
                case 4: enemies.add(new Inimigo3(x,y)); break;
            }
        }
    }

    //ATRIBUI VALORES INT PARA O TIPO DE INIMIGO A SER CRIADO
    //0 -->     BOSS
    //1 -->     INIMIGO BÁSICO
    //2 -->     INIMIGO1 COM ESCUDO
    //3 -->     INIMIGO2 QUE ACELERA
    //4 -->     INIMIGO3 PEQUENO
    public void enemySpawner(){
        if(currentWave <= 3){
            for(int i = 0; i < enemiesToSpawn; i++){
                spawnType.add(1);
            }
            enemiesToSpawn += 4;
        } else{
            if(currentWave <= 7){
                for(int i = 0; i < enemiesToSpawn; i+=2){
                    spawnType.add(1);
                    spawnType.add(2);
                }
                enemiesToSpawn += 5;
            } else{
                if(currentWave <= 12){
                    for(int i = 0; i < enemiesToSpawn; i+=3){
                        spawnType.add((i % 3) + 1);
                    }
                    enemiesToSpawn += 6;
                } else{
                    for(int i = 0; i < enemiesToSpawn; i++){
                        spawnType.add(random.nextInt(5));
                    }
                    enemiesToSpawn += 10;
                }
            }
        }
        if(currentWave % 5 == 0){
            for(int i = 0; i <= bossNumber; i++){
                spawnType.add(0);   //SPAWN BOSS A CADA 5 WAVES (incrementa o número de boss spawnado a cada 5 waves);
            }
            bossNumber *= 2;
        }
    }

    public ArrayList<Integer> getSpawnType() {
        return spawnType;
    }

    private void nextWave() {
        currentWave++;
        enemiesToSpawn += 3; // aumenta progressivamente
        enemiesSpawned = 0;
        enemiesAlive = 0;
        if(currentWave >= 5 && ((currentWave % 5) == 0)){  //aumenta velocidade dos inimigos a partir da wave 10
            updateEnemies(enemies);
        }
    }

    public void enemyDied() {
        enemiesAlive--;
    }

    //Esse metodo atualiza a velocidade dos inimigos em 10%
    public void updateEnemies(ArrayList<Enemy> enemies){
        for(Enemy  e: enemies){
            e.incSpeed();
            e.incHP();
            e.incDmg();
        }
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getBossNumber(){
        return bossNumber;
    }

    public int getEnemiesToSpawn() {
        return enemiesToSpawn;
    }

    public int getEnemiesSpawned() {
        return enemiesSpawned;
    }
    public int getEnemiesAlive() {
        return enemiesAlive;
    }
    public long getLastSpawnTime() {
        return lastSpawnTime;
    }
    public long getSpawnInterval() {
        return spawnInterval;
    }
}

package com.neondf.entities;

import com.neondf.systems.SpriteSheet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Atiradora extends Suporte {
    private long lastShot = 0;
    private int delay = 1000;
    private int dmg = 5;
    private BufferedImage bulletSprite;

    public Atiradora() {
        super("Atiradora", Color.RED);
        SpriteSheet sheet = new SpriteSheet("/sprites/bullet1.png");
        this.bulletSprite = sheet.getSprite();
    }

    @Override
    public void onUpgrade() {
        delay = Math.max(200, delay - 100);
        dmg += 3;
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        if (level == 0) return;

        // Posiciona a torre auxiliar à esquerda da principal
        setPosition(tower.getCenterX() - 60, tower.getCenterY());

        long now = System.currentTimeMillis();
        if (now - lastShot > delay) {
            Enemy target = null;
            double minDist = 400.0; // Aumentei um pouco o alcance (era 300)

            // 1. Encontra o alvo mais próximo
            for (Enemy e : enemies) {
                double dx = e.getX() - x;
                double dy = e.getY() - y;
                double dist = Math.sqrt(dx*dx + dy*dy);

                // Só atira se o inimigo estiver vivo e dentro do alcance
                if (dist < minDist && e.isAlive()) {
                    minDist = dist;
                    target = e;
                }
            }

            if (target != null) {
                // --- LÓGICA DE MIRA PREDITIVA ---
                double bulletSpeed = 5.0; // Velocidade da bala (tem que ser igual a da classe Bullet)

                // Calcula quanto tempo a bala vai demorar para chegar no alvo atual
                double distanceToTarget = Math.sqrt(Math.pow(target.getX() - x, 2) + Math.pow(target.getY() - y, 2));
                double timeToHit = distanceToTarget / bulletSpeed;

                // Estima onde o inimigo estará nesse tempo
                // (Pega a posição atual + velocidade * tempo)
                // Nota: Assumindo que o inimigo anda em linha reta na direção da torre principal

                double dx = tower.getCenterX() - target.getX();
                double dy = tower.getCenterY() - target.getY();
                double distToTower = Math.sqrt(dx*dx + dy*dy);

                // Vetor de direção do inimigo normalizado
                double dirX = dx / distToTower;
                double dirY = dy / distToTower;

                // Posição futura prevista
                double futureX = target.getX() + (dirX * target.getSpeed() * timeToHit);
                double futureY = target.getY() + (dirY * target.getSpeed() * timeToHit);

                // Calcula o ângulo para essa posição futura
                double angle = Math.atan2(futureY - y, futureX - x);

                bullets.add(new Bullet(x, y, angle, bulletSpeed, dmg, 1, bulletSprite));
                lastShot = now;
            }
        }
    }
}
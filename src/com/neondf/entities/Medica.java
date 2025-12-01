package com.neondf.entities;

import java.awt.*;
import java.util.ArrayList;

public class Medica extends Suporte {

    private long lastHeal = 0;
    private int healDelay = 2000;
    private int healAmount = 5;

    public Medica() {
        // Passamos qualquer cor, pois a classe Suporte agora força o Verde Neon
        super("Médica", Color.PINK);
        this.range = 150;
    }

    @Override
    public void onUpgrade() {
        healAmount += 5;
        healDelay = Math.max(500, healDelay - 300);
        range += 20;
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        // Chama o tick da superclasse para animar o pulsar
        super.tick(tower, enemies, bullets);

        if (level == 0) return;
        setPosition(tower.getCenterX() + 60, tower.getCenterY());

        long now = System.currentTimeMillis();
        if (now - lastHeal > healDelay) {
            if (tower.getHp() < tower.getMaxHp()) {
                double dist = Math.sqrt(Math.pow(tower.getCenterX() - x, 2) + Math.pow(tower.getCenterY() - y, 2));
                if (dist <= range) {
                    tower.heal(healAmount);
                    lastHeal = now;
                }
            }
        }
    }

    // --- IMPORTANTE: REMOVEMOS O MÉTODO RENDER DAQUI ---
    // Assim ela usa o render estiloso da classe Suporte.java
}
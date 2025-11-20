package com.neondf.entities;

import java.awt.*;
import java.util.ArrayList;

public class Medica extends Suporte {
    private long lastHeal = 0;
    private int healInterval = 5000;
    private int healAmount = 5;

    public Medica() { super("Médica", Color.GREEN); }

    @Override
    public void onUpgrade() {
        healAmount += 5;
        healInterval = Math.max(1000, healInterval - 500);
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        if (level == 0) return;
        setPosition(tower.getCenterX() + 60, tower.getCenterY());

        long now = System.currentTimeMillis();
        if (now - lastHeal > healInterval) {
            if (tower.getHp() < 100) {
                tower.heal(healAmount);
                lastHeal = now;
            }
        }
    }
}
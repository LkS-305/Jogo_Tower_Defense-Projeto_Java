package com.neondf.entities;

import java.awt.*;
import java.util.ArrayList;

public class Escudeira extends Suporte {
    private int maxShield = 0;
    private long lastRegen = 0;

    public Escudeira() { super("Escudeira", Color.BLUE); }

    @Override
    public void onUpgrade() {
        maxShield += 3;
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        if (level == 0) return;
        setPosition(tower.getCenterX(), tower.getCenterY() - 60);

        tower.setMaxShield(maxShield);

        long now = System.currentTimeMillis();
        if (now - lastRegen > 2000) {
            tower.addShield(level * 2);
            lastRegen = now;
        }
    }
}
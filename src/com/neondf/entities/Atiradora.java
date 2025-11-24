package com.neondf.entities;

import java.awt.*;
import java.util.ArrayList;

public class Atiradora extends Suporte {
    private long lastShot = 0;
    private int delay = 1000;
    private int dmg = 5;

    public Atiradora() { super("Atiradora", Color.RED); }

    @Override
    public void onUpgrade() {
        delay = Math.max(200, delay - 100);
        dmg += 3;
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        if (level == 0) return;
        setPosition(tower.getCenterX() - 60, tower.getCenterY());

        long now = System.currentTimeMillis();
        if (now - lastShot > delay) {
            Enemy target = null;
            double minDist = 300.0;
            for (Enemy e : enemies) {
                double dx = e.getX() - x;
                double dy = e.getY() - y;
                double dist = Math.sqrt(dx*dx + dy*dy);
                if (dist < minDist) {
                    minDist = dist;
                    target = e;
                }
            }
            if (target != null) {
                double angle = Math.atan2(target.getY() - y, target.getX() - x);
                bullets.add(new Bullet(x, y, angle, 5, dmg, 1));
                lastShot = now;
            }
        }
    }
}
package com.neondf.entities;

import com.neondf.systems.HUD;
import java.awt.*;
import java.util.ArrayList;

public abstract class Suporte {
    protected double x, y;
    protected int level = 0;
    protected int cost = 50;
    protected String name;
    protected Color color;

    public Suporte(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void setPosition(double tx, double ty) { this.x = tx; this.y = ty; }

    public void upgrade(HUD hud) {
        if (hud.getCoins() >= cost) {
            hud.addCoin(-cost);
            level++;
            cost *= 2;
            onUpgrade();
        }
    }

    public abstract void onUpgrade();
    public abstract void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets);

    public void render(Graphics2D g) {
        if (level > 0) {
            g.setColor(color);
            g.fillRect((int)x - 10, (int)y - 10, 20, 20);
            g.setColor(Color.WHITE);
            g.drawRect((int)x - 10, (int)y - 10, 20, 20);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("Lvl " + level, (int)x-10, (int)y-15);
        }
    }

    public int getCost() { return cost; }
    public int getLevel() { return level; }
}
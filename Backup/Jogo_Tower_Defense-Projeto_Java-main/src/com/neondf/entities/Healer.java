package com.neondf.entities;

public class Healer extends Tower {
    private double healFactor;
    public Healer(double x, double y) {
        super(x, y, 30);
        this.healFactor = 1.1;
    }

    public void heal(Shooter shooter) {
        shooter.healTower(this.healFactor);
    }

    public void upgradeHeal(){
        this.healFactor += 0.1;
    }


}

package com.neondf.entities;

public class Inimigo1 extends Enemy {
    public Inimigo1(double x, double y){
        super(x, y);
        this.changeScore(3.0);
        this.changeSpeed(0.8);
        this.changeHP(2);
        this.changeDmg(2);
        this.setShield(this.getHp()/2);
    }
}
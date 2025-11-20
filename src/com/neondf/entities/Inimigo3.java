package com.neondf.entities;

public class Inimigo3 extends Enemy{
    public Inimigo3(double x, double y){
        super(x, y);
        this.changeSpeed(2);
        this.changeHP(0.8);
        this.changeDmg(0.5);
    }
}

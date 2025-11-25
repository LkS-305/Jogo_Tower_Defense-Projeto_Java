package com.neondf.entities;

public class Inimigo1 extends Enemy{
    //precisa de mais uma variável, para a imagem do inimigo
    private int shield;
    public Inimigo1(double x, double y){
        super(x, y);
        this.setScore(30);
        this.changeSpeed(0.8);
        this.changeHP(2);
        this.changeDmg(2);
        this.shield = (this.getBaseHP())/2;
    }

    public int getShield() {
        return shield;
    }
    public void setShield(int shield) {
        this.shield = shield;
    }
}
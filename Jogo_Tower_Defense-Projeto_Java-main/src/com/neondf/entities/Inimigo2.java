package com.neondf.entities;

//Esse inimigo acelera quando perder metade da vida
public class Inimigo2 extends Enemy{
    public Inimigo2(double x, double y){
        super(x, y);
        this.setScore(30);
        this.changeSpeed(1);
        this.changeHP(2);
        this.changeDmg(1.5);
    }

    @Override
    public void takeDamage(int damage) {
        if(damage >= (this.getBaseHP()/2)){
            this.setBaseSpeed(this.getBaseSpeed() * 2);
        }
        this.setBaseHP(this.getBaseHP() - damage);
        if(this.getBaseHP() <= 0){
            this.kill();
        }
    }
}

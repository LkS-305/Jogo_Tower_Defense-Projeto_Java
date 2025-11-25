package com.neondf.entities;

    //TEM MUITA VIDA E TOMA METADE DO DANO

public class Boss extends Enemy{
    public Boss(double x, double y){
        super(x, y);
        this.setScore(100);
        this.changeSpeed(0.5);
        this.changeHP(10);
        this.changeDmg(5);
        this.setShield(this.getHp()/2);
        //FALTA PREENCHER O BOSS
    }

    @Override
    public void takeDamage(int damage) {
        if(this.getShield() > 0){
            this.setShield(this.getShield()/2);
            if(this.getShield() <= 0){
                this.setShield(0);
            }
        } else{
            this.setHp(this.getHp() - (damage/2));
            if(this.getHp() <= 0){
                this.kill();
            }
        }
    }

}

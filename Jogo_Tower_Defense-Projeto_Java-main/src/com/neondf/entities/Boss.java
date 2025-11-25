package com.neondf.entities;

    //TEM MUITA VIDA E TOMA METADE DO DANO

public class Boss extends Enemy{
    private int shield;
    public Boss(double x, double y){
        super(x, y);
        this.setScore(100);
        this.changeSpeed(0.5);
        this.changeHP(10);
        this.changeDmg(5);
        this.shield = (this.getBaseHP())/2;
        //FALTA PREENCHER O BOSS
    }

    @Override
    public void takeDamage(int damage) {
        if(this.shield > 0){
            this.shield -= damage/2;
            if(this.shield <= 0){
                this.shield = 0;
            }
        } else{
            this.setBaseHP(this.getBaseHP() - (damage/2));
            if(this.getBaseHP() <= 0){
                this.kill();
            }
        }
    }

}

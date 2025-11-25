package com.neondf.entities;

/*
* REALIZEI UMA GRANDE ALTERAÇÃO NO SISTEMA DE TORRE QUE ESTÁVAMOS UTILIZANDO
*       - A CLASSE TOWER AGORA NÃO É UTILIZADA DIRETAMENTE
*       - TEMOS 3 SUBCLASSES DE TOWER
*           - SHOOTER
*           - SHIELD
*           - HEALER
*       - A SUBCLASSE SHOOTER É A TORRE PRINCIPAL
*
* */

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tower {
    private final double x, y;           // posição top-left
    private final int size;   // largura/altura

    public Tower(double x, double y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void tick() {
        // por enquanto não precisamos atualizar center aqui
    }

    public int getSize() {
        return size;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // calcula o centro dinamicamente -> evita depender de tick()
    public double getCenterX() {
        return this.getX() + this.getSize() / 2.0;
    }

    public double getCenterY() {
        return this.getY() + this.getSize() / 2.0;
    }

    public void render(Graphics2D g){}

}

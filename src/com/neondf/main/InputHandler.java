package com.neondf.main;

import com.neondf.entities.Tower;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Captura o movimento do mouse e atualiza o ângulo da torre.
 */
public class InputHandler implements MouseMotionListener {

    private Tower tower;

    public InputHandler(Tower tower) {
        this.tower = tower;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double dx = e.getX() - tower.getCenterX();
        double dy = e.getY() - tower.getCenterY();
        double angle = Math.atan2(dy, dx);
        tower.setAngle(angle);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e); // funciona igual enquanto arrasta
    }
}

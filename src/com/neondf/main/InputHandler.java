package com.neondf.main;

import com.neondf.entities.Tower;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class InputHandler implements MouseMotionListener {

    private final Tower tower;
    private final Component canvas;

    public InputHandler(Tower tower, Component canvas) {
        this.tower = tower;
        this.canvas = canvas;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Corrige a posição do mouse para o Canvas
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), canvas);

        double dx = p.getX() - tower.getCenterX();
        double dy = p.getY() - tower.getCenterY();

        double angle = Math.atan2(dy, dx);
        tower.setAngle(angle);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
}

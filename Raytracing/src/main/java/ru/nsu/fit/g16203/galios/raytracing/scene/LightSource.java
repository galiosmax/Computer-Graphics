package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;

import java.awt.*;

public class LightSource {

    private Point3D position;
    private Color color;

    public LightSource(Point3D position, Color color) {
        this.position = position;
        this.color = color;
    }


    public Point3D getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }
}

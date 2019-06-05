package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

public class Quadrangle extends Figure {

    private Point3D first;
    private Point3D second;
    private Point3D third;
    private Point3D fourth;

    private Triangle triangleFirst;
    private Triangle triangleSecond;

    public Quadrangle(Point3D first, Point3D second, Point3D third, Point3D fourth) {
        super();
        this.type = FigureType.QUADRANGLE;
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;

        triangleFirst = new Triangle(second, third, first);
        triangleSecond = new Triangle(first, third, fourth);

        calculatePoints();
    }

    private void calculatePoints() {
        points.add(first);
        points.add(second);
        points.add(third);
        points.add(fourth);

        segments.add(new Pair<>(first, second));
        segments.add(new Pair<>(second, third));
        segments.add(new Pair<>(third, fourth));
        segments.add(new Pair<>(fourth, first));
    }

    @Override
    Pair<Point3D, Vector> intersect(Point3D from, Vector vector) {

        Pair<Point3D, Vector> intersection = triangleFirst.intersect(from, vector);
        if (intersection != null && intersection.getKey() != null) {
            return intersection;
        }
        intersection = triangleSecond.intersect(from, vector);
        if (intersection != null && intersection.getKey() != null) {
            return intersection;
        }
        return null;
    }
}

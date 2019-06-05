package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

public class Triangle extends Figure {

    private Point3D first;
    private Point3D second;
    private Point3D third;
    private Plane plane;

    public Triangle(Point3D first, Point3D second, Point3D third) {
        super();
        this.type = FigureType.TRIANGLE;
        this.first = first;
        this.second = second;
        this.third = third;
        Point3D normal = ((second.subtract(first)).crossProduct(third.subtract(first))).normalize();

        double a = normal.getX();
        double b = normal.getY();
        double c = normal.getZ();
        double d = new Vector(first).scalar(new Vector(normal));
        this.plane = new Plane(a, b, c, -d);
        calculatePoints();
    }

    private void calculatePoints() {
        points.add(first);
        points.add(second);
        points.add(third);

        segments.add(new Pair<>(first, second));
        segments.add(new Pair<>(second, third));
        segments.add(new Pair<>(third, first));
    }

    @Override
    Pair<Point3D, Vector> intersect(Point3D from, Vector vector) {

        Pair<Point3D, Vector> intersection = plane.intersect(from, vector);
        Point3D point;
        if (intersection != null && intersection.getKey() != null) {
            point = intersection.getKey();
        } else {
            return null;
        }

        double deltaX = Math.max(Math.max(first.getX(), second.getX()), third.getX()) - Math.min(Math.min(first.getX(), second.getX()), third.getX());
        double deltaY = Math.max(Math.max(first.getY(), second.getY()), third.getY()) - Math.min(Math.min(first.getY(), second.getY()), third.getY());
        double deltaZ = Math.max(Math.max(first.getZ(), second.getZ()), third.getZ()) - Math.min(Math.min(first.getZ(), second.getZ()), third.getZ());

        double min = Math.min(Math.min(deltaX, deltaY), deltaZ);

        double area, area1, area2, area3;

        if (min == deltaX) {
            area = calculateArea(first.getZ(), first.getY(), second.getZ(), second.getY(), third.getZ(), third.getY());
            area1 = calculateArea(first.getZ(), first.getY(), second.getZ(), second.getY(), point.getZ(), point.getY());
            area2 = calculateArea(first.getZ(), first.getY(), point.getZ(), point.getY(), third.getZ(), third.getY());
            area3 = calculateArea(point.getZ(), point.getY(), second.getZ(), second.getY(), third.getZ(), third.getY());
        } else if (min == deltaY) {
            area = calculateArea(first.getX(), first.getZ(), second.getX(), second.getZ(), third.getX(), third.getZ());
            area1 = calculateArea(first.getX(), first.getZ(), second.getX(), second.getZ(), point.getX(), point.getZ());
            area2 = calculateArea(first.getX(), first.getZ(), point.getX(), point.getZ(), third.getX(), third.getZ());
            area3 = calculateArea(point.getX(), point.getZ(), second.getX(), second.getZ(), third.getX(), third.getZ());
        } else if (min == deltaZ) {
            area = calculateArea(first.getX(), first.getY(), second.getX(), second.getY(), third.getX(), third.getY());
            area1 = calculateArea(first.getX(), first.getY(), second.getX(), second.getY(), point.getX(), point.getY());
            area2 = calculateArea(first.getX(), first.getY(), point.getX(), point.getY(), third.getX(), third.getY());
            area3 = calculateArea(point.getX(), point.getY(), second.getX(), second.getY(), third.getX(), third.getY());
        } else {
            return null;
        }

        double alpha = area1 / area;
        double beta = area2 / area;
        double gamma = area3 / area;
        double sum = alpha + beta + gamma;

        if (!(alpha <= 1 && beta <= 1 && gamma <= 1 && alpha >= 0 && beta >= 0 && gamma >= 0 && Math.abs(sum - 1d) < 0.0001)) {
            return null;
        }
        return intersection;
    }

    private double calculateArea(double ax, double ay, double bx, double by, double cx, double cy) {
        return Math.abs(((bx - ax) * (cy - ay) - (cx - ax) * (by - ay)) / 2d);
    }
}

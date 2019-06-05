package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

public class Plane extends Figure {

    private double a, b, c, d;
    private Vector normal;

    Plane(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.normal = new Vector(new Point3D(a, b ,c)).normalize();
    }

    @Override
    Pair<Point3D, Vector> intersect(Point3D from, Vector vector) {

        double Vd = normal.scalar(vector);
        if (Vd >= 0) {
            return null;
        }
        double V0 = -normal.scalar(new Vector(from)) - d;
        double t = V0 / Vd;
        if (t < 0) {
            return null;
        }
        Point3D point = new Vector(from).add(vector.multiply(t)).getPoint3D();
        return new Pair<>(point, normal);
    }
}

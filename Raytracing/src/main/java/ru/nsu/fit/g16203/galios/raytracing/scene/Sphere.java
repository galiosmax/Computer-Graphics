package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

public class Sphere extends Figure {

    private Point3D centre;
    private double radius;

    public Sphere(Point3D centre, double radius) {
        super();
        this.type = FigureType.SPHERE;
        this.centre = centre;
        this.radius = radius;
        calculatePoints();
    }

    private void calculatePoints() {

        int n = 10;
        int k = 5;
        int nk = n * k;
        Point3D[][] tempPoints = new Point3D[nk + 1][nk + 1];

        double step = 2 * Math.PI / (nk + 1);
        double u, v;

        for (int i = 0; i <= nk; ++i) {
            u = i * step;

            for (int j = 0; j <= nk; ++j) {
                v = j * step;
                tempPoints[i][j] = new Point3D(centre.getX() + radius * Math.sin(u) * Math.cos(v), centre.getY() + radius * Math.sin(u) * Math.sin(v), centre.getZ() + radius * Math.cos(u));
                points.add(tempPoints[i][j]);
            }
        }

        for (int i = 0; i < nk; ++i) {
            for (int j = 0; j <= nk; j += k) {
                segments.add(new Pair<>(tempPoints[i][j], tempPoints[i + 1][j]));
            }
        }

        for (int i = 0; i <= nk; i += k) {
            for (int j = 0; j < nk; ++j) {
                segments.add(new Pair<>(tempPoints[i][j], tempPoints[i][j + 1]));
            }
        }
    }

    @Override
    Pair<Point3D, Vector> intersect(Point3D from, Vector vector) {

        Vector sub = new Vector(centre).sub(new Vector(from));
        double OC = sub.scalar(sub);
        double rad = Math.pow(radius, 2);
        if (OC  - rad < 0) {
            return null;
        }
        double tCA = sub.scalar(vector);
        double squareDist = OC - Math.pow(tCA, 2);

        double tHC = rad - squareDist;
        if (tHC < 0) {
            return null;
        }
        double t = tCA - Math.sqrt(tHC);
        Point3D point = new Vector(from).add(vector.multiply(t)).getPoint3D();

        Vector normal = new Vector(new Point3D((point.getX() - centre.getX()) / radius, (point.getY() - centre.getY()) / radius, (point.getZ() - centre.getZ()) / radius)).normalize();
        return new Pair<>(point, normal);
    }
}

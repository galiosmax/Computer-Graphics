package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

public class Box extends Figure {

    private Point3D min;
    private Point3D max;

    public Box(Point3D min, Point3D max) {
        super();
        this.type = FigureType.BOX;
        this.min = min;
        this.max = max;
        calculatePoints();
    }

    private void calculatePoints() {

        Point3D xyZ = new Point3D(min.getX(), min.getY(), max.getZ());
        Point3D xYz = new Point3D(min.getX(), max.getY(), min.getZ());
        Point3D Xyz = new Point3D(max.getX(), min.getY(), min.getZ());
        Point3D xYZ = new Point3D(min.getX(), max.getY(), max.getZ());
        Point3D XyZ = new Point3D(max.getX(), min.getY(), max.getZ());
        Point3D XYz = new Point3D(max.getX(), max.getY(), min.getZ());

        points.add(min);
        points.add(xyZ);
        points.add(xYz);
        points.add(Xyz);
        points.add(xYZ);
        points.add(XyZ);
        points.add(XYz);
        points.add(max);

        segments.add(new Pair<>(min, xyZ));
        segments.add(new Pair<>(min, xYz));
        segments.add(new Pair<>(min, Xyz));
        segments.add(new Pair<>(max, XYz));
        segments.add(new Pair<>(max, XyZ));
        segments.add(new Pair<>(max, xYZ));
        segments.add(new Pair<>(xyZ, XyZ));
        segments.add(new Pair<>(xyZ, xYZ));
        segments.add(new Pair<>(xYz, XYz));
        segments.add(new Pair<>(xYz, xYZ));
        segments.add(new Pair<>(Xyz, XYz));
        segments.add(new Pair<>(Xyz, XyZ));
    }

    @Override
    Pair<Point3D, Vector> intersect(Point3D from, Vector vector) {

        double tNear = Double.NEGATIVE_INFINITY;
        double tFar = Double.POSITIVE_INFINITY;

        double x0 = from.getX();
        double xMin = min.getX();
        double xMax = max.getX();
        double xd = vector.getPoint3D().getX();

        if (Math.abs(xd) < 0.00001 && (x0 < xMin || x0 > xMax)) {
            return null;
        }
        double t1 = (xMin - x0) / xd;
        double t2 = (xMax - x0) / xd;

        if (t2 < t1) {
            t1 += t2;
            t2 = t1 - t2;
            t1 -= t2;
        }
        if (t1 > tNear) {
            tNear = t1;
        }
        if (t2 < tFar) {
            tFar = t2;
        }
        if (tNear > tFar || tFar < 0 || tNear < 0) {
            return null;
        }

        double y0 = from.getY();
        double yMin = min.getY();
        double yMax = max.getY();
        double yd = vector.getPoint3D().getY();

        if (Math.abs(yd) < 0.00001 && (y0 < yMin || y0 > yMax)) {
            return null;
        }
        t1 = (yMin - y0) / yd;
        t2 = (yMax - y0) / yd;

        if (t2 < t1) {
            t1 += t2;
            t2 = t1 - t2;
            t1 -= t2;
        }
        if (t1 > tNear) {
            tNear = t1;
        }
        if (t2 < tFar) {
            tFar = t2;
        }
        if (tNear > tFar || tFar < 0 || tNear < 0) {
            return null;
        }

        double z0 = from.getZ();
        double zMin = min.getZ();
        double zMax = max.getZ();
        double zd = vector.getPoint3D().getZ();

        if (Math.abs(zd) < 0.00001 && (z0 < zMin || z0 > zMax)) {
            return null;
        }
        t1 = (zMin - z0) / zd;
        t2 = (zMax - z0) / zd;

        if (t2 < t1) {
            t1 += t2;
            t2 = t1 - t2;
            t1 -= t2;
        }
        if (t1 > tNear) {
            tNear = t1;
        }
        if (t2 < tFar) {
            tFar = t2;
        }
        if (tNear > tFar || tFar < 0 || tNear < 0) {
            return null;
        }

        Point3D point = new Vector(from).add(vector.multiply(tNear)).getPoint3D();

        Vector normal = getNormal(point);
        if (normal != null) {
            return new Pair<>(point, normal);
        }
        return null;
    }

    private Vector getNormal(Point3D point) {
        if (Math.abs(point.getX() - min.getX()) < 0.00001) {
            return new Vector(new Point3D(-1, 0, 0));
        }
        if (Math.abs(point.getY() - min.getY()) < 0.00001) {
            return new Vector(new Point3D(0, -1, 0));
        }
        if (Math.abs(point.getZ() - min.getZ()) < 0.00001) {
            return new Vector(new Point3D(0, 0, -1));
        }
        if (Math.abs(point.getX() - max.getX()) < 0.00001) {
            return new Vector(new Point3D(1, 0, 0));
        }
        if (Math.abs(point.getY() - max.getY()) < 0.00001) {
            return new Vector(new Point3D(0, 1, 0));
        }
        if (Math.abs(point.getZ() - max.getZ()) < 0.00001) {
            return new Vector(new Point3D(0, 0, 1));
        }
        return null;
    }
}

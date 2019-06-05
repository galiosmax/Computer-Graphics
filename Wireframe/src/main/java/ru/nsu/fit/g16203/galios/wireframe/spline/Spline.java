package ru.nsu.fit.g16203.galios.wireframe.spline;

import javafx.geometry.Point2D;
import ru.nsu.fit.g16203.galios.wireframe.matrix.Matrix;

import java.util.ArrayList;

public class Spline {

    private ArrayList<Point2D> points;
    private ArrayList<Point2D> splinePoints;
    private double length;

    private final int parts = 100;

    public Spline() {
        points = new ArrayList<>();
        splinePoints = new ArrayList<>();
    }

    public void calcSpline() {

        ArrayList<Point2D> splinePoints = new ArrayList<>();

        for (int i = 1; i < points.size() - 2; ++i) {
            double[][] matrixX = {{points.get(i - 1).getX()}, {points.get(i).getX()}, {points.get(i + 1).getX()}, {points.get(i + 2).getX()}};
            double[][] matrixY = {{points.get(i - 1).getY()}, {points.get(i).getY()}, {points.get(i + 1).getY()}, {points.get(i + 2).getY()}};

            Matrix Gx = new Matrix(matrixX);
            Matrix Gy = new Matrix(matrixY);

            splinePoints.addAll(calcPoints(Gx, Gy));
        }
        this.splinePoints = splinePoints;

        length = 0;
        Point2D point1 = splinePoints.get(0);
        Point2D point2;
        for (int i = 1; i < splinePoints.size(); ++i) {
            point2 = splinePoints.get(i);
            length += point1.distance(point2);
            point1 = point2;
        }
    }

    private ArrayList<Point2D> calcPoints(Matrix Gx, Matrix Gy) {
        ArrayList<Point2D> points = new ArrayList<>();
        Matrix M = Matrix.M;

        Matrix X = M.multiply(Gx);
        Matrix Y = M.multiply(Gy);

        Matrix T;
        double x, y;

        double incr = 1d / (double) parts;

        for (double t = 0; t < 1; t += incr) {
            double[][] matrixT = {{Math.pow(t, 3), Math.pow(t, 2), t, 1}};
            T = new Matrix(matrixT);

            x = T.multiply(X).matrix[0][0];
            y = T.multiply(Y).matrix[0][0];
            points.add(new Point2D(x, y));
        }
        return points;
    }

    public Point2D getSplinePoint(double u) {

        double currentLength = 0;
        int i = 1;

        Point2D point1 = splinePoints.get(0), point2;

        while (currentLength < u) {
            if (i < splinePoints.size()) {
                point2 = splinePoints.get(i);
                currentLength += point1.distance(point2);
                if (currentLength > u) {
                    break;
                }
                point1 = point2;
                ++i;
            } else {
                break;
            }
        }
        return point1;
    }

    public double getMax() {
        double max = 0;

        for (Point2D point : points) {
            double x = Math.abs(point.getX());
            double y = Math.abs(point.getY());
            if (x > max) {
                max = x;
            }
            if (y > max) {
                max = y;
            }
        }
        return max;
    }

    public ArrayList<Point2D> getPoints() {
        return points;
    }

    public ArrayList<Point2D> getSplinePoints() {
        return splinePoints;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}

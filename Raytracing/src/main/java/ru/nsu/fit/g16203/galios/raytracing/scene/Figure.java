package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Matrix;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;
import ru.nsu.fit.g16203.galios.raytracing.panels.OpticalParameters;

import java.awt.*;
import java.util.ArrayList;

public abstract class Figure {

    FigureType type;
    ArrayList<Pair<Point3D, Point3D>> segments;
    ArrayList<Point3D> points;
    private OpticalParameters opticalParameters;

    Figure() {
        segments = new ArrayList<>();
        points = new ArrayList<>();
    }

    ArrayList<Pair<Point, Point>> applyMatrix(Matrix finalMatrix, int width, int height) {

        ArrayList<Pair<Point, Point>> result = new ArrayList<>();

        for (Pair<Point3D, Point3D> pair : segments) {
            result.add(calcPair(pair, finalMatrix, width, height));
        }
        return result;
    }

    private Pair<Point, Point> calcPair(Pair<Point3D, Point3D> pair, Matrix matrix, int width, int height) {
        Point3D first = Matrix.calcTempPoint(pair.getKey(), matrix, width, height);
        Point3D second = Matrix.calcTempPoint(pair.getValue(), matrix, width, height);

        return clipPair(first, second);
    }

    private Pair<Point, Point> clipPair(Point3D first, Point3D second) {

        double x1 = first.getX();
        double x2 = second.getX();
        double y1 = first.getY();
        double y2 = second.getY();
        double z1 = first.getZ();
        double z2 = second.getZ();

        if (z1 > 0 && z1 < 1 && z2 < 0) {
            double proportion = z1 / (z1 - z2);
            x2 = x1 + (x2 - x1) * proportion;
            y2 = y1 + (y2 - y1) * proportion;
        } else if (z2 > 0 && z2 < 1 && z1 < 0) {
            double proportion = z2 / (z2 - z1);
            x1 = x2 + (x1 - x2) * proportion;
            y1 = y2 + (y1 - y2) * proportion;
        } else if (z1 > 1 && z2 > 0 && z2 < 1) {
            double proportion = (1 - z2) / (z1 - z2);
            x1 = x2 + (x1 - x2) * proportion;
            y1 = y2 + (y1 - y2) * proportion;
        } else if (z2 > 1 && z2 > 0 && z2 < 1) {
            double proportion = (1 - z1) / (z2 - z1);
            x2 = x1 + (x2 - x1) * proportion;
            y2 = y1 + (y2 - y1) * proportion;
        } else if (!(z1 > 0 && z2 > 0 && z1 < 1 && z2 < 1)) {
            return null;
        }
        return new Pair<>(new Point((int) x1, (int) y1), new Point((int) x2, (int) y2));
    }

    abstract Pair<Point3D, Vector> intersect(Point3D from, Vector vector);

    double getXMin() {

        double xMin = Double.MAX_VALUE;
        for (Point3D point : points) {

            double currentXMin = point.getX();
            if (currentXMin < xMin) {
                xMin = currentXMin;
            }
        }
        return xMin;
    }

    double getXMax() {

        double xMax = -Double.MAX_VALUE;
        for (Point3D point : points) {

            double currentXMax = point.getX();
            if (currentXMax > xMax) {
                xMax = currentXMax;
            }
        }
        return xMax;
    }

    double getYMin() {

        double yMin = Double.MAX_VALUE;
        for (Point3D point : points) {

            double currentYMin = point.getY();
            if (currentYMin < yMin) {
                yMin = currentYMin;
            }
        }
        return yMin;
    }

    double getYMax() {

        double yMax = -Double.MAX_VALUE;
        for (Point3D point : points) {

            double currentYMax = point.getY();
            if (currentYMax > yMax) {
                yMax = currentYMax;
            }
        }
        return yMax;
    }

    double getZMin() {

        double zMin = Double.MAX_VALUE;
        for (Point3D point : points) {

            double currentZMin = point.getZ();
            if (currentZMin < zMin) {
                zMin = currentZMin;
            }
        }
        return zMin;
    }

    double getZMax() {

        double zMax = -Double.MAX_VALUE;
        for (Point3D point : points) {

            double currentZMax = point.getZ();
            if (currentZMax > zMax) {
                zMax = currentZMax;
            }
        }
        return zMax;
    }

    public OpticalParameters getOpticalParameters() {
        return opticalParameters;
    }

    public void setOpticalParameters(OpticalParameters parameters) {
        opticalParameters = parameters;
    }
}

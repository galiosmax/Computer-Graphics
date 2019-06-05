package ru.nsu.fit.g16203.galios.raytracing.matrix;

import javafx.geometry.Point3D;

public class Vector {

    private int size;
    private double[] values;

    public Vector(double[] values) {
        this.values = values;
        size = values.length;
    }

    public Vector(Point3D point) {
        size = 3;

        values = new double[]{point.getX(), point.getY(), point.getZ()};
    }

    public Vector multiply(Vector vector) {
        double[] values2 = vector.getValues();
        return new Vector(new double[]{values[1] * values2[2] - values[2] * values2[1], values[2] * values2[0] - values[0] * values2[2], values[0] * values2[1] - values[1] * values2[0]});
    }

    public Vector multiply(double factor) {
        double[] newValues = new double[size];

        for (int i = 0; i < size; ++i) {
            newValues[i] = values[i] * factor;
        }
        return new Vector(newValues);
    }

    public double scalar(Vector vector) {
        return values[0] * vector.getValues()[0] + values[1] * vector.getValues()[1] + values[2] * vector.getValues()[2];
    }

    public Vector getInverted() {
        return new Vector(new double[]{-values[0], -values[1], -values[2]});
    }

    public Vector add(Vector vector) {

        double[] newValues = new double[size];
        double[] values2 = vector.getValues();

        for (int i = 0; i < size; ++i) {
            newValues[i] = values[i] + values2[i];
        }
        return new Vector(newValues);
    }

    public Vector sub(Vector vector) {

        double[] newValues = new double[size];
        double[] values2 = vector.getValues();

        for (int i = 0; i < size; ++i) {
            newValues[i] = values[i] - values2[i];
        }
        return new Vector(newValues);
    }

    public double norm() {
        double norm = 0;

        for (int i = 0; i < size; ++i) {
            norm += Math.pow(values[i], 2);
        }
        return Math.sqrt(norm);
    }

    public Vector normalize() {
        double norm = norm();
        double[] newVector = new double[size];
        for (int i = 0; i < size; ++i) {
            newVector[i] = values[i] / norm;
        }
        return new Vector(newVector);
    }

    public Matrix getMatrix() {
        return new Matrix(new double[][]{{values[0]}, {values[1]}, {values[2]}, {1}});
    }

    public int getSize() {
        return size;
    }

    public double[] getValues() {
        return values;
    }

    public Point3D getPoint3D() {
        return new Point3D(values[0], values[1], values[2]);
    }
}

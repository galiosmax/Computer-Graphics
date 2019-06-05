package ru.nsu.fit.g16203.galios.raytracing.matrix;

import javafx.geometry.Point3D;

import java.awt.*;

public class Matrix {

    private int n, m;
    public double[][] matrix;

    public Matrix(double[][] matrix) {
        this.n = matrix.length;
        this.m = matrix[0].length;
        this.matrix = matrix;
    }

    public Matrix(Point3D point) {
        n = 4;
        m = 1;
        matrix = new double[][]{{point.getX()}, {point.getY()}, {point.getZ()}, {1}};
    }

    public Matrix multiply(Matrix matrix) {

        double[][] newMatrix = new double[n][matrix.m];

        double[][] matrix2 = matrix.matrix;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < matrix.m; ++j) {
                for (int k = 0; k < m; ++k) {
                    newMatrix[i][j] += this.matrix[i][k] * matrix2[k][j];
                }
            }
        }
        return new Matrix(newMatrix);
    }

    public Matrix multiply(double factor) {

        double[][] newMatrix = new double[n][m];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                newMatrix[i][j] = matrix[i][j] * factor;
            }
        }
        return new Matrix(newMatrix);
    }

    public Matrix add(Matrix matrix) {

        double[][] newMatrix = new double[n][m];

        double[][] matrix1 = this.matrix;
        double[][] matrix2 = matrix.matrix;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                newMatrix[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return new Matrix(newMatrix);
    }

    public Matrix sub(Matrix matrix) {

        double[][] newMatrix = new double[n][m];

        double[][] matrix1 = this.matrix;
        double[][] matrix2 = matrix.matrix;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                newMatrix[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        return new Matrix(newMatrix);
    }

    public double norm() {

        double norm = 0;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                norm += Math.pow(matrix[i][j], 2);
            }
        }
        return Math.sqrt(norm);
    }

    public Matrix transpose() {

        double[][] newMatrixArray = new double[m][n];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                newMatrixArray[j][i] = matrix[i][j];
            }
        }

        return new Matrix(newMatrixArray);
    }

    public static Matrix getI() {
        double[][] IMatrix = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        return new Matrix(IMatrix);
    }

    public static Matrix getRotX(double rot) {
        double[][] rotMatrix = {{1, 0, 0, 0}, {0, Math.cos(rot), -Math.sin(rot), 0}, {0, Math.sin(rot), Math.cos(rot), 0}, {0, 0, 0, 1}};

        return new Matrix(rotMatrix);
    }

    public static Matrix getRotY(double rot) {
        double[][] rotMatrix = {{Math.cos(rot), 0, Math.sin(rot), 0}, {0, 1, 0, 0}, {-Math.sin(rot), 0, Math.cos(rot), 0}, {0, 0, 0, 1}};

        return new Matrix(rotMatrix);
    }

    public static Matrix getRotZ(double rot) {
        double[][] rotMatrix = {{Math.cos(rot), -Math.sin(rot), 0, 0}, {Math.sin(rot), Math.cos(rot), 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};

        return new Matrix(rotMatrix);
    }


    public static Point3D calcTempPoint(Point3D point, Matrix matrix, int width, int height) {
        Matrix vector = new Matrix(point);

        Matrix newVector = matrix.multiply(vector);
        newVector = newVector.multiply(1d / newVector.matrix[3][0]);
        double[][] newVectorArray = newVector.matrix;
        double z = newVectorArray[2][0];

        int x = (int) Math.round(((newVectorArray[0][0] + 1) / 2d) * (double) width);
        int y = (int) Math.round(((newVectorArray[1][0] + 1) / 2d) * (double) height);
        return new Point3D(x, y, z);
    }

    public static Point calcPoint(Point3D point, Matrix matrix, int width, int height) {
        Point3D point3D = calcTempPoint(point, matrix, width, height);
        return new Point((int) point3D.getX(), (int) point3D.getY());
    }
}

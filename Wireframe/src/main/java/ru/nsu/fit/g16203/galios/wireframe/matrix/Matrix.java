package ru.nsu.fit.g16203.galios.wireframe.matrix;

public class Matrix {

    private int n, m;
    public double[][] matrix;

    public static Matrix M = getDefaultM();
    public static Matrix MCam = getMCam();

    public Matrix(double[][] matrix) {
        this.n = matrix.length;
        this.m = matrix[0].length;
        this.matrix = matrix;
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

    private Matrix sub(Matrix matrix) {

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

    private double norm() {

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

    private Matrix vectorMultiply(Matrix matr) {
        double[][] matrix2 = matr.matrix;

        double[][] vector = {{matrix[0][1] * matrix2[0][2] - matrix[0][2] * matrix2[0][1], matrix[0][2] * matrix2[0][0] - matrix[0][0] * matrix2[0][2], matrix[0][0] * matrix2[0][1] - matrix[0][1] * matrix2[0][0]}};
        return new Matrix(vector);
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

    private static Matrix getMCam() {
        final double[][] PCamArray = {{-10, 0, 0}};
        final double[][] PViewArray = {{10, 0, 0}};
        final double[][] VUpArray = {{0, 1, 0}};

        Matrix PCam = new Matrix(PCamArray);
        Matrix PView = new Matrix(PViewArray);
        Matrix VUp = new Matrix(VUpArray);

        Matrix k = PCam.sub(PView).multiply(1d / (PView.sub(PCam)).norm());
        Matrix I = VUp.vectorMultiply(k);
        Matrix i = I.multiply(1d / I.norm());
        Matrix j = k.vectorMultiply(i);

        double[][] iArray = i.matrix;
        double[][] jArray = j.matrix;
        double[][] kArray = k.matrix;

        double[][] matrix1Array = {{iArray[0][0], iArray[0][1], iArray[0][2], 0}, {jArray[0][0], jArray[0][1], jArray[0][2], 0}, {kArray[0][0], kArray[0][1], kArray[0][2], 0}, {0, 0, 0, 1}};
        double[][] matrix2Array = {{1, 0, 0, PCamArray[0][0]}, {0, 1, 0, PCamArray[0][1]}, {0, 0, 1, PCamArray[0][2]}, {0, 0, 0, 1}};

        return new Matrix(matrix1Array).multiply(new Matrix(matrix2Array));
    }

    private static Matrix getDefaultM() {
        double[][] defaultM = {{-1d / 6d, 3 / 6d, -3 / 6d, 1 / 6d}, {3 / 6d, -6 / 6d, 3 / 6d, 0}, {-3 / 6d, 0, 3 / 6d, 0}, {1 / 6d, 4 / 6d, 1 / 6d, 0}};
        return new Matrix(defaultM);
    }
}

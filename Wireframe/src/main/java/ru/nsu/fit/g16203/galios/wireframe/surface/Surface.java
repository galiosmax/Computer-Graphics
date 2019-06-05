package ru.nsu.fit.g16203.galios.wireframe.surface;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import ru.nsu.fit.g16203.galios.wireframe.matrix.Matrix;
import ru.nsu.fit.g16203.galios.wireframe.parameters.Parameters;
import ru.nsu.fit.g16203.galios.wireframe.spline.Spline;

import java.awt.*;

public class Surface {

    private Point3D[][] surfacePoints;
    private Parameters parameters;

    private Color bodyColor;
    private Point3D centre;
    private Matrix rotationMatrix;
    private Matrix centreMatrix;
    private Spline spline;

    private final double xMin = -1;
    private final double xMax = 1;
    private final double yMin = -1;
    private final double yMax = 1;

    public Surface(Parameters parameters) {
        this.parameters = parameters;

        centre = new Point3D(0, 0, 0);
        rotationMatrix = Matrix.getI();
        centreMatrix = Matrix.getI();
    }

    public void calcPoints() {
        if (spline.getPoints().size() >= 4) {
            surfacePoints = new Point3D[parameters.k * parameters.n + 1][parameters.k * parameters.m + 1];

            double length = spline.getLength();
            double start = parameters.a * length;
            double finish = parameters.b * length;

            int n = parameters.n;
            int m = parameters.m;
            int k = parameters.k;
            int mk = m * k;
            int nk = n * k;

            double stepN = (finish - start) / (nk + 1);
            double stepM = (parameters.d - parameters.c) / mk;
            double x, y, z, u, v;
            Point2D splinePoint;

            for (int i = 0; i <= nk; ++i) {
                u = start + i * stepN;
                splinePoint = spline.getSplinePoint(u);

                x = splinePoint.getY();
                y = splinePoint.getY();
                z = splinePoint.getX();

                for (int j = 0; j <= mk; ++j) {
                    v = parameters.c + j * stepM;
                    surfacePoints[i][j] = new Point3D(x * Math.cos(v), y * Math.sin(v), z);
                }
            }

            for (int i = 0; i <= nk; ++i) {

                for (int j = 0; j <= mk; ++j) {

                    surfacePoints[i][j] = calcPoint3D(surfacePoints[i][j]);
                }
            }
        }
    }

    public void moveSurfaceTo(Point3D C) {
        centre = C;
        double[][] centreMatrixArray = {
                {1, 0, 0, -centre.getX()},
                {0, 1, 0, -centre.getY()},
                {0, 0, 1, -centre.getZ()},
                {0, 0, 0, 1}
        };
        centreMatrix = new Matrix(centreMatrixArray);
    }

    public void rotateSurface(Matrix matrix) {
        rotationMatrix = rotationMatrix.multiply(matrix);
    }

    public Point[][] applyMatrix(Matrix matrix, int width, int height) {

        int n = parameters.n;
        int m = parameters.m;
        int k = parameters.k;
        int mk = m * k;
        int nk = n * k;

        Point3D[][] tempPoints = new Point3D[nk + 1][mk + 1];

        for (int i = 0; i <= nk; ++i) {

            for (int j = 0; j <= mk; ++j) {
                tempPoints[i][j] = calcTempPoint(surfacePoints[i][j], matrix, width, height);
            }
        }
        return clipSurface(tempPoints);
    }

    private Point3D calcTempPoint(Point3D point, Matrix matrix, int width, int height) {
        double[][] vectorArray = {{point.getX()}, {point.getY()}, {point.getZ()}, {1}};
        Matrix vector = new Matrix(vectorArray);

        Matrix newVector = matrix.multiply(vector);
        newVector = newVector.multiply(1d / newVector.matrix[3][0]);
        double[][] newVectorArray = newVector.matrix;
        double z = newVectorArray[2][0];

        int x = (int) Math.round(((newVectorArray[0][0] - xMin) / (xMax - xMin)) * (double) width);
        int y = (int) Math.round(((newVectorArray[1][0] - yMin) / (yMax - yMin)) * (double) height);
        return new Point3D(x, y, z);
    }

    public Point calcPoint(Point3D point, Matrix matrix, int width, int height) {
        Point3D point3D = calcTempPoint(point, matrix, width, height);
        return new Point((int) point3D.getX(), (int) point3D.getY());
    }

    private Point[][] clipSurface(Point3D[][] tempPoints) {
        int n = parameters.n;
        int m = parameters.m;
        int k = parameters.k;
        int mk = m * k;
        int nk = n * k;

        Point[][] newSurfacePoints = new Point[nk + 1][mk + 1];

        for (int i = 0; i < nk; ++i) {
            for (int j = 0; j <= mk; ++j) {

                double z1 = tempPoints[i][j].getZ();
                double z2 = tempPoints[i + 1][j].getZ();

                if (z1 > 0 && z2 > 0 && z1 < 1 && z2 < 1) {
                    newSurfacePoints[i][j] = new Point((int) (tempPoints[i][j].getX()), (int) (tempPoints[i][j].getY()));
                }
            }
        }

        for (int i = 0; i < mk; ++i) {
            for (int j = 0; j <= nk; ++j) {
                double z1 = tempPoints[j][i].getZ();
                double z2 = tempPoints[j][i + 1].getZ();

                if (z1 > 0 && z2 >  0 && z1 <  1 && z2 <  1) {
                    newSurfacePoints[j][i] = new Point((int) (tempPoints[j][i].getX()), (int) (tempPoints[j][i].getY()));
                }
            }
        }

        for (int i = 0; i <= nk; ++i) {
            double z1 = tempPoints[i][mk].getZ();
            double z2 = tempPoints[i][0].getZ();

            if (z1 >  0 && z2 >  0 && z1 <  1 && z2 <  1) {
                newSurfacePoints[i][mk] = new Point((int) (tempPoints[i][mk].getX()), (int) (tempPoints[i][mk].getY()));
            }
        }
        return newSurfacePoints;
    }

    private Point3D calcPoint3D(Point3D point) {
        double[][] vectorArray = {{point.getX()}, {point.getY()}, {point.getZ()}, {1}};
        Matrix vector = new Matrix(vectorArray);

        Matrix newVector = centreMatrix.multiply(rotationMatrix.multiply(vector));
        double[][] newVectorArray = newVector.matrix;

        return new Point3D(newVectorArray[0][0], newVectorArray[1][0], newVectorArray[2][0]);
    }

    public double getXMin() {

        double xMin = Double.MAX_VALUE;

        for (Point3D[] points : surfacePoints) {
            for (Point3D point : points) {
                if (point.getX() < xMin) {
                    xMin = point.getX();
                }
            }
        }

        return xMin;
    }

    public double getYMin() {
        double yMin = Double.MAX_VALUE;

        for (Point3D[] points : surfacePoints) {
            for (Point3D point : points) {
                if (point.getY() < yMin) {
                    yMin = point.getY();
                }
            }
        }

        return yMin;
    }

    public double getZMin() {
        double zMin = Double.MAX_VALUE;

        for (Point3D[] points : surfacePoints) {
            for (Point3D point : points) {
                if (point.getZ() < zMin) {
                    zMin = point.getZ();
                }
            }
        }

        return zMin;
    }

    public double getXMax() {
        double xMax = -Double.MAX_VALUE;

        for (Point3D[] points : surfacePoints) {
            for (Point3D point : points) {
                if (point.getX() > xMax) {
                    xMax = point.getX();
                }
            }
        }

        return xMax;
    }

    public double getYMax() {
        double yMax = -Double.MAX_VALUE;

        for (Point3D[] points : surfacePoints) {
            for (Point3D point : points) {
                if (point.getY() > yMax) {
                    yMax = point.getY();
                }
            }
        }

        return yMax;
    }

    public double getZMax() {
        double zMax = -Double.MAX_VALUE;

        for (Point3D[] points : surfacePoints) {
            for (Point3D point : points) {
                if (point.getZ() > zMax) {
                    zMax = point.getZ();
                }
            }
        }

        return zMax;
    }

    public Color getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(Color bodyColor) {
        this.bodyColor = bodyColor;
    }

    public Point3D getCentre() {
        return centre;
    }

    public Point getCentre(Matrix matrix, int width, int height) {
        Point3D point = new Point3D(-centre.getX(), -centre.getY(), -centre.getZ());
        return calcPoint(point, matrix, width, height);
    }

    public Point getXAxis(Matrix matrix, int width, int height) {
        Point3D point = new Point3D(-centre.getX() - 0.5, -centre.getY(), -centre.getZ());
        return calcPoint(point, matrix, width, height);
    }

    public Point getYAxis(Matrix matrix, int width, int height) {
        Point3D point = new Point3D(-centre.getX(), -centre.getY() - 0.5, -centre.getZ());
        return calcPoint(point, matrix, width, height);
    }

    public Point getZAxis(Matrix matrix, int width, int height) {
        Point3D point = new Point3D(-centre.getX(), -centre.getY(), -centre.getZ() - 0.5);
        return calcPoint(point, matrix, width, height);
    }

    public Matrix getRotationMatrix() {
        return rotationMatrix;
    }

    public void setSpline(Spline spline) {
        this.spline = spline;
    }

    public Spline getSpline() {
        return spline;
    }
}

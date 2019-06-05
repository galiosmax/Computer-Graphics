package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Matrix;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

public class Camera {

    private Point3D cameraPoint;
    private Point3D viewPoint;
    private Point3D VupPoint;

    private Vector xAxis;
    private Vector yAxis;
    private Vector zAxis;

    public Camera(Point3D cameraPoint, Point3D viewPoint, Point3D VupPoint) {
        this.cameraPoint = cameraPoint;
        this.viewPoint = viewPoint;
        this.VupPoint = VupPoint;
        calculateVUp();
    }

    public Matrix calculateMCam() {

        Vector cameraVector = new Vector(cameraPoint).multiply(-1);
        Vector viewVector = new Vector(viewPoint).multiply(-1);
        Vector VupVector = new Vector(VupPoint).multiply(-1);

        Vector k = cameraVector.sub(viewVector).multiply(1d / (viewVector.sub(cameraVector)).norm());
        Vector I = VupVector.multiply(k);
        Vector i = I.multiply(1d / I.norm());
        Vector j = k.multiply(i);

        Point3D iPoint = i.getPoint3D();
        Point3D jPoint = j.getPoint3D();
        Point3D kPoint = k.getPoint3D();

        double[][] matrix1Array = {{iPoint.getX(), iPoint.getY(), iPoint.getZ(), 0}, {jPoint.getX(), jPoint.getY(), jPoint.getZ(), 0}, {kPoint.getX(), kPoint.getY(), kPoint.getZ(), 0}, {0, 0, 0, 1}};
        double[][] matrix2Array = {{1, 0, 0, -cameraPoint.getX()}, {0, 1, 0, -cameraPoint.getY()}, {0, 0, 1, -cameraPoint.getZ()}, {0, 0, 0, 1}};

        return new Matrix(matrix1Array).multiply(new Matrix(matrix2Array));
    }

    public void moveX(double x) {
        cameraPoint = new Vector(cameraPoint).add(xAxis.multiply(x)).getPoint3D();
        calculateVUp();
    }

    public void moveY(double y) {
        cameraPoint = new Vector(cameraPoint).add(yAxis.multiply(y)).getPoint3D();
        calculateVUp();
    }

    public void moveZ(double z) {
        cameraPoint = new Vector(cameraPoint).add(zAxis.multiply(z)).getPoint3D();
        calculateVUp();
    }

    public void calculateVUp() {
        Vector cameraVector = new Vector(cameraPoint);
        Vector viewVector = new Vector(viewPoint);
        Vector VupVector = new Vector(VupPoint);

        zAxis = viewVector.sub(cameraVector).normalize();
        xAxis = zAxis.multiply(VupVector).normalize();
        yAxis = xAxis.multiply(zAxis).normalize();

        VupPoint = yAxis.getPoint3D();
    }

    public Point3D getCameraPoint() {
        return cameraPoint;
    }

    public void setCameraPoint(Point3D cameraPoint) {
        this.cameraPoint = cameraPoint;
        calculateVUp();
    }

    public Point3D getViewPoint() {
        return viewPoint;
    }

    public void setViewPoint(Point3D viewPoint) {
        this.viewPoint = viewPoint;
        calculateVUp();
    }

    public Point3D getVupPoint() {
        return VupPoint;
    }

    public void setVupPoint(Point3D vupPoint) {
        VupPoint = vupPoint;
        calculateVUp();
    }
}

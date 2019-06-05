package ru.nsu.fit.g16203.galios.raytracing.parameters;

import javafx.geometry.Point3D;
import ru.nsu.fit.g16203.galios.raytracing.scene.Camera;

import java.awt.*;

public class RenderParameters {

    private boolean isLoaded = false;
    private Color backgroundColor;
    private double gamma;
    private int depth;
    private Quality quality;
    private Camera camera;
    private double zn;
    private double zf;
    private double sw;
    private double sh;

    public RenderParameters() {
        setDefault();
    }

    public void setDefault() {
        backgroundColor = Color.WHITE;
        gamma = 1;
        depth = 2;
        quality = Quality.NORMAL;
        zn = 3;
        zf = 10;
        sh = 5;
        sw = 5;
    }

    public void calcDefault(Point3D centre, double xMin, double xMax, int width, int height) {

        Point3D viewPoint = new Point3D(centre.getX(), centre.getY(), centre.getZ());
        Point3D VupPoint = new Point3D(0, 0, 1);
        Point3D cameraPoint = new Point3D(-10, centre.getY(), centre.getZ());

        camera = new Camera(cameraPoint, viewPoint, VupPoint);

        camera.calculateVUp();
        zn = (xMin - camera.getCameraPoint().getX()) / 2d;
        zf = xMax - camera.getCameraPoint().getX() + (xMax - xMin) / 2d;
        sh = 5;
        double proportion = (double) width / (double) height;
        sw = proportion * sh;
        isLoaded = true;
    }

    public void init() {
        isLoaded = false;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        if (quality != null) {
            this.quality = quality;
        }
    }
    
    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Point3D getCameraPoint() {
        return camera.getCameraPoint();
    }

    public void setCameraPoint(Point3D cameraPoint) {
        this.camera.setCameraPoint(cameraPoint);
    }

    public Point3D getViewPoint() {
        return camera.getViewPoint();
    }

    public void setViewPoint(Point3D viewPoint) {
        camera.setViewPoint(viewPoint);
    }

    public Point3D getVup() {
        return camera.getVupPoint();
    }

    public void setVup(Point3D vup) {
        camera.setVupPoint(vup);
    }

    public double getZn() {
        return zn;
    }

    public void setZn(double zn) {
        this.zn = zn;
    }

    public double getZf() {
        return zf;
    }

    public void setZf(double zf) {
        this.zf = zf;
    }

    public double getSw() {
        return sw;
    }

    public void setSw(double sw) {
        this.sw = sw;
    }

    public double getSh() {
        return sh;
    }

    public void setSh(double sh) {
        this.sh = sh;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}

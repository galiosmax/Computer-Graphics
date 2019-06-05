package ru.nsu.fit.g16203.galios.raytracing.images;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;
import ru.nsu.fit.g16203.galios.raytracing.panels.OpticalParameters;
import ru.nsu.fit.g16203.galios.raytracing.parameters.RenderParameters;
import ru.nsu.fit.g16203.galios.raytracing.scene.Figure;
import ru.nsu.fit.g16203.galios.raytracing.scene.LightSource;
import ru.nsu.fit.g16203.galios.raytracing.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class Raytracing {

    private Scene scene;
    private RenderParameters renderParameters;
    private int width;
    private int height;
    private String progress;
    private boolean isFinished = false;
    private BufferedImage image;

    public Raytracing(Scene scene, RenderParameters renderParameters, int width, int height) {
        this.scene = scene;
        this.renderParameters = renderParameters;
        this.width = width;
        this.height = height;
        progress = "0%";
    }

    public void run() {

        Thread thread = new Thread(() -> {
            switch (renderParameters.getQuality()) {
                case ROUGH: {
                    image = roughRendering();
                    break;
                }
                case NORMAL: {
                    image = normalRendering();
                    break;
                }
                case FINE: {
                    image = fineRendering();
                    break;
                }
            }
            progress = "100%";
            isFinished = true;
        });
        thread.start();
    }

    private BufferedImage roughRendering() {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Point3D cameraPoint = renderParameters.getCameraPoint();
        Point3D viewPoint = renderParameters.getViewPoint();
        Point3D Vup = renderParameters.getVup();

        double sw = renderParameters.getSw();
        double sh = renderParameters.getSh();

        double dx = 2d * sw / (double) width;
        double dy = -2d * sh / (double) height;

        Vector direction = new Vector(viewPoint.subtract(cameraPoint));
        Vector right = direction.multiply(new Vector(Vup));
        direction = direction.normalize();
        right = right.normalize();

        Vector start = new Vector(cameraPoint.add(direction.multiply(renderParameters.getZn()).getPoint3D()));
        Vector tmp = new Vector(Vup.multiply(sh / 2d).add(right.multiply(-sw / 2d).getPoint3D()));

        start = start.add(tmp);

        for (int j = 0; j < height; j += 2) {

            for (int i = 0; i < width; i += 2) {

                Vector shiftX = right.multiply(dx * i / 2d + dx / 2d);
                Vector shiftY = new Vector(Vup.multiply(dy * j / 2d + dy / 2d));
                direction = start.add(shiftX).add(shiftY).sub(new Vector(cameraPoint));

                Color color = getColor(scene, renderParameters, cameraPoint, direction.normalize());

                image.setRGB(i, j, color.getRGB());
                if (j + 1 < height) {
                    image.setRGB(i, j + 1, color.getRGB());
                }
                if (i + 1 < width) {
                    image.setRGB(i + 1, j, color.getRGB());
                }
                if (i + 1 < width && j + 1 < height) {
                    image.setRGB(i + 1, j + 1, color.getRGB());
                }
            }
            progress = String.format("%.1f", 100d * (double) j / (double) height) + "%";
        }

        image = Gamma.getCorrected(image, renderParameters.getGamma());
        return image;
    }

    private BufferedImage normalRendering() {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Point3D cameraPoint = renderParameters.getCameraPoint();
        Point3D viewPoint = renderParameters.getViewPoint();
        Point3D Vup = renderParameters.getVup();

        double sw = renderParameters.getSw();
        double sh = renderParameters.getSh();

        double dx = sw / (double) width;
        double dy = -sh / (double) height;

        Vector direction = new Vector(viewPoint.subtract(cameraPoint));
        Vector right = direction.multiply(new Vector(Vup));
        direction = direction.normalize();
        right = right.normalize();

        Vector start = new Vector(cameraPoint.add(direction.multiply(renderParameters.getZn()).getPoint3D()));
        Vector tmp = new Vector(Vup.multiply(sh / 2d).add(right.multiply(-sw / 2d).getPoint3D()));

        start = start.add(tmp);

        for (int j = 0; j < height; ++j) {

            for (int i = 0; i < width; ++i) {
                Vector shiftX = right.multiply(dx * i + dx / 2);
                Vector shiftY = new Vector(Vup.multiply(dy * j + dy / 2));
                direction = start.add(shiftX).add(shiftY).sub(new Vector(cameraPoint));

                Color color = getColor(scene, renderParameters, cameraPoint, direction.normalize());

                image.setRGB(i, j, color.getRGB());
            }
            progress = String.format("%.1f", 100d * (double) j / (double) height) + "%";
        }

        image = Gamma.getCorrected(image, renderParameters.getGamma());
        return image;
    }

    private BufferedImage fineRendering() {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Point3D cameraPoint = renderParameters.getCameraPoint();
        Point3D viewPoint = renderParameters.getViewPoint();
        Point3D Vup = renderParameters.getVup();

        double sw = renderParameters.getSw();
        double sh = renderParameters.getSh();

        double dx = sw / (double) (width * 2);
        double dy = -sh / (double) (height * 2);

        Vector direction = new Vector(viewPoint.subtract(cameraPoint));
        Vector right = direction.multiply(new Vector(Vup));
        direction = direction.normalize();
        right = right.normalize();

        Vector start = new Vector(cameraPoint.add(direction.multiply(renderParameters.getZn()).getPoint3D()));
        Vector tmp = new Vector(Vup.multiply(sh / 2d).add(right.multiply(-sw / 2d).getPoint3D()));

        start = start.add(tmp);

        for (int j = 0; j < height; ++j) {

            for (int i = 0; i < width; ++i) {
                Vector shiftX = right.multiply(dx * 2d * i + dx / 2d);
                Vector shiftY = new Vector(Vup.multiply(dy * 2d * j + dy / 2d));
                Vector direction1 = start.add(shiftX).add(shiftY).sub(new Vector(cameraPoint));
                Color color1 = getColor(scene, renderParameters, cameraPoint, direction1.normalize());

                shiftX = right.multiply(dx * 2d * i + 3d * dx / 2d);
                shiftY = new Vector(Vup.multiply(dy * 2d * j + dy / 2d));
                Vector direction2 = start.add(shiftX).add(shiftY).sub(new Vector(cameraPoint));
                Color color2 = getColor(scene, renderParameters, cameraPoint, direction2.normalize());

                shiftX = right.multiply(dx * 2d * i + dx / 2d);
                shiftY = new Vector(Vup.multiply(dy * 2 * j + 3 * dy / 2));
                Vector direction3 = start.add(shiftX).add(shiftY).sub(new Vector(cameraPoint));
                Color color3 = getColor(scene, renderParameters, cameraPoint, direction3.normalize());

                shiftX = right.multiply(dx * 2d * i + 3d * dx / 2d);
                shiftY = new Vector(Vup.multiply(dy * 2d * j + 3d * dy / 2d));
                Vector direction4 = start.add(shiftX).add(shiftY).sub(new Vector(cameraPoint));
                Color color4 = getColor(scene, renderParameters, cameraPoint, direction4.normalize());

                int red = (color1.getRed() + color2.getRed() + color3.getRed() + color4.getRed()) / 4;
                int green = (color1.getGreen() + color2.getGreen() + color3.getGreen() + color4.getGreen()) / 4;
                int blue = (color1.getBlue() + color2.getBlue() + color3.getBlue() + color4.getBlue()) / 4;

                image.setRGB(i, j, new Color(red, green, blue).getRGB());
            }
            progress = String.format("%.1f", 100d * (double) j / (double) height) + "%";
        }

        image = Gamma.getCorrected(image, renderParameters.getGamma());
        return image;
    }

    private Color getColor(Scene scene, RenderParameters renderParameters, Point3D camera, Vector direction) {

        Stack<Pair<Figure, Pair<Point3D, Vector>>> intersections = new Stack<>();
        int depth = renderParameters.getDepth();

        Point3D from = camera;
        Vector vector = direction;

        for (int i = 0; i < depth; ++i) {
            if (from != null && vector != null) {
                Pair<Figure, Pair<Point3D, Vector>> intersection = scene.getIntersection(from, vector);

                intersections.push(intersection);
                if (intersection != null && intersection.getValue() != null && intersection.getValue().getValue() != null) {
                    Vector normal = intersection.getValue().getValue();

                    vector = getReflected(normal, vector);
                    from = intersection.getValue().getKey();
                } else {
                    break;
                }
            }
        }


        MyColor color = new MyColor(0, 0, 0);
        MyColor backgroundColor = new MyColor(renderParameters.getBackgroundColor());

        while (!intersections.empty()) {
            Pair<Figure, Pair<Point3D, Vector>> intersection = intersections.pop();

            if (intersection == null || intersection.getKey() == null) {
                color = backgroundColor;
                continue;
            }

            Figure intersectedFigure = intersection.getKey();
            Point3D point = intersection.getValue().getKey();
            Vector normal = intersection.getValue().getValue();

            if (intersections.size() == depth - 1) {
                color = new MyColor(0, 0, 0);
                continue;
            }
            for (LightSource light : scene.getLightSources()) {
                color = color.add(getIntensity(light, point, camera, normal, intersectedFigure, scene));
            }
        }
        return color.getColor();
    }

    private MyColor getIntensity(LightSource light, Point3D point, Point3D camera, Vector normal, Figure intersectedFigure, Scene scene) {

        Point3D source = light.getPosition();
        Vector direction = new Vector(point.subtract(source)).normalize();

        double dist = source.distance(point);
        Pair<Figure, Pair<Point3D, Vector>> intersection = scene.getIntersection(source, direction);

        if (intersection == null || intersection.getValue() == null || Math.abs(intersection.getValue().getKey().distance(source) - dist) > 0.001) {
            return new MyColor(0, 0, 0);
        }

        Vector L = new Vector(source.subtract(point)).normalize();
        Vector E = new Vector(camera.subtract(point)).normalize();
        Vector H = L.add(E).normalize();

        OpticalParameters optical = intersectedFigure.getOpticalParameters();

        double lCoefficient = normal.scalar(L);
        double hCoefficient = Math.pow(normal.scalar(H), optical.getPower());

        double distFactor = 1d / (1d + dist);

        MyColor ambient = new MyColor(scene.getAmbientLight());
        MyColor lightIntense = new MyColor(light.getColor());
        MyColor diffuse = new MyColor(optical.getDiffuseRed(), optical.getDiffuseGreen(), optical.getDiffuseBlue());
        MyColor reflect = new MyColor(optical.getReflectionRed(), optical.getReflectionGreen(), optical.getReflectionBlue());

        MyColor sum = diffuse.multiply(lCoefficient).add(reflect.multiply(hCoefficient));
        return ambient.multiply(diffuse).add(lightIntense.multiply(distFactor).multiply(sum));
    }

    private Vector getReflected(Vector normal, Vector vector) {
        return normal.multiply(2d * normal.scalar(vector.getInverted())).sub(vector.getInverted());
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getProgress() {
        return progress;
    }

    public BufferedImage getImage() {
        return image;
    }
}

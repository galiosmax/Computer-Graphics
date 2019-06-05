package ru.nsu.fit.g16203.galios.raytracing.scene;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Matrix;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Vector;

import java.awt.*;
import java.util.ArrayList;

public class Scene {

    private Color ambientLight;
    private ArrayList<LightSource> lightSources;
    private ArrayList<Figure> figures;

    public Scene() {
        lightSources = new ArrayList<>();
        figures = new ArrayList<>();
    }

    public ArrayList<ArrayList<Pair<Point, Point>>> applyMatrix(Matrix finalMatrix, int width, int height) {

        ArrayList<ArrayList<Pair<Point, Point>>> result = new ArrayList<>();

        for (Figure figure : figures) {
            result.add(figure.applyMatrix(finalMatrix, width, height));
        }
        return result;
    }

    public Color getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Color ambientLight) {
        this.ambientLight = ambientLight;
    }

    public ArrayList<LightSource> getLightSources() {
        return lightSources;
    }

    public void addLightSource(LightSource lightSource) {
        this.lightSources.add(lightSource);
    }

    public ArrayList<Figure> getFigures() {
        return figures;
    }

    public Figure getFigure(int index) {
        if (index >= 0 && figures.size() > index) {
            return figures.get(index);
        } else {
            return null;
        }
    }

    public void addFigure(Figure figure) {
        this.figures.add(figure);
    }

    public boolean hasFigures() {
        return figures.size() > 0;
    }

    public Pair<Figure, Pair<Point3D, Vector>> getIntersection(Point3D camera, Vector direction) {

        double minDist = Double.MAX_VALUE;
        Figure minFigure = null;
        Pair<Point3D, Vector> minIntersection = null;

        for (Figure figure : figures) {
            Pair<Point3D, Vector> intersection = figure.intersect(camera, direction);
            if (intersection != null && intersection.getKey() != null && intersection.getValue() != null) {
                double currentDist = camera.distance(intersection.getKey());
                if (currentDist < minDist) {
                    minDist = currentDist;
                    minFigure = figure;
                    minIntersection = intersection;
                }
            }
        }
        return new Pair<>(minFigure, minIntersection);
    }

    public double getXMin() {

        double xMin = Double.MAX_VALUE;
        for (Figure figure : figures) {

            double currentXMin = figure.getXMin();
            if (currentXMin < xMin) {
                xMin = currentXMin;
            }
        }
        return xMin;
    }

    public double getXMax() {

        double xMax = -Double.MAX_VALUE;
        for (Figure figure : figures) {

            double currentXMax = figure.getXMax();
            if (currentXMax > xMax) {
                xMax = currentXMax;
            }
        }
        return xMax;
    }

    public double getYMin() {

        double yMin = Double.MAX_VALUE;
        for (Figure figure : figures) {

            double currentYMin = figure.getYMin();
            if (currentYMin < yMin) {
                yMin = currentYMin;
            }
        }
        return yMin;
    }

    public double getYMax() {

        double yMax = -Double.MAX_VALUE;
        for (Figure figure : figures) {

            double currentYMax = figure.getYMax();
            if (currentYMax > yMax) {
                yMax = currentYMax;
            }
        }
        return yMax;
    }

    public double getZMin() {

        double zMin = Double.MAX_VALUE;
        for (Figure figure : figures) {

            double currentZMin = figure.getZMin();
            if (currentZMin < zMin) {
                zMin = currentZMin;
            }
        }
        return zMin;
    }

    public double getZMax() {

        double zMax = -Double.MAX_VALUE;
        for (Figure figure : figures) {

            double currentZMax = figure.getZMax();
            if (currentZMax > zMax) {
                zMax = currentZMax;
            }
        }
        return zMax;
    }

    public void clear() {
        ambientLight = null;
        lightSources = new ArrayList<>();
        figures = new ArrayList<>();
    }
}

package ru.nsu.fit.g16203.galios.filters;

import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class VolumeRendering {

    private static double[] absorption = new double[101];
    private static Color[] emission = new Color[101];
    private static ArrayList<Pair<Point3D, Double>> chargesList = new ArrayList<>();

    private static boolean absorptionActive = true;
    private static boolean emissionActive = true;

    public static void setAbsorption(Pair[] points) {

        int x1 = (int) points[0].getKey();
        double y1 = (double) points[0].getValue();

        for (int i = 1; i < points.length; ++i) {
            int x2 = (int) points[i].getKey();
            double y2 = (double) points[i].getValue();
            if (x2 > x1) {
                double dy = (y2 - y1) / (x2 - x1);
                for (int x = x1; x < x2; ++x) {
                    absorption[x] = Math.max(0d, Math.min(1d, y1));
                    y1 += dy;
                }
                y1 = y2;
            } else {
                absorption[x1] = y2;
                y1 = y2;
            }

            x1 = x2;
        }
        absorption[100] = (double) points[points.length - 1].getValue();
    }

    public static void setEmission(Pair[] points) {
        int x1 = (int) points[0].getKey();
        Color first = (Color) points[0].getValue();

        for (int i = 1; i < points.length; ++i) {
            int x2 = (int) points[i].getKey();
            Color second = (Color) points[i].getValue();

            if (x2 > x1) {
                double red = first.getRed();
                double green = first.getGreen();
                double blue = first.getBlue();

                double dr = (double) (second.getRed() - first.getRed()) / (double) (x2 - x1);
                double dg = (double) (second.getGreen() - first.getGreen()) / (double) (x2 - x1);
                double db = (double) (second.getBlue() - first.getBlue()) / (double) (x2 - x1);

                for (int x = x1; x < x2; ++x) {
                    emission[x] = new Color(Math.max(0, Math.min(255, (int) Math.round(red))), Math.max(0, Math.min(255, (int) Math.round(green))), Math.max(0, Math.min(255, (int) Math.round(blue))));
                    red += dr;
                    green += dg;
                    blue += db;
                }
                first = second;
            } else {
                emission[x1] = second;
                first = second;
            }

            x1 = x2;
        }
        emission[100] = (Color) points[points.length - 1].getValue();
    }

    public static void setCharges(Pair[] charges) {
        for (Pair i : charges) {
            chargesList.add(new Pair<>((Point3D) i.getKey(), (Double) i.getValue()));
        }
    }

    public static void absorptionActive() {
        absorptionActive = !absorptionActive;
    }

    public static void emissionActive() {
        emissionActive = !emissionActive;
    }

    public static BufferedImage render(BufferedImage image, int Nx, int Ny, int Nz) {

        if (!absorptionActive && !emissionActive) {
            return image;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rendered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        double dx = 1d / Nx;
        double dy = 1d / Ny;
        double dz = 1d / Nz;

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int x = 0; x < Nx; ++x) {
            for (int y = 0; y < Ny; ++y) {
                for (int z = 0; z < Nz; ++z) {
                    double res = f(x * dx + dx / 2, y * dy + dy / 2, z * dz + dz / 2);
                    if (res > max) {
                        max = res;
                    }
                    if (res < min) {
                        min = res;
                    }
                }
            }
        }

        double step = (max - min) / 100d;

        double di = (double) Nx / width;
        double dj = (double) Ny / height;

        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {

                double x = (int) (di * i) * dx + dx / 2;
                double y = (int) (dj * j) * dy + dy / 2;

                Color color = new Color(pixels[j * width + i]);
                double red = color.getRed();
                double green = color.getGreen();
                double blue = color.getBlue();

                for (int z = 0; z < Nz; ++z) {
                    double res = f(x, y, z * dz + dz / 2);
                    int level = Math.max(0, Math.min(255, (int) Math.round((res - min) / step)));

                    if (absorptionActive) {
                        double exp = Math.exp(-absorption[level] * dz);
                        red *= exp;
                        green *= exp;
                        blue *= exp;
                    }
                    if (emissionActive) {
                        red += emission[level].getRed() * dz;
                        green += emission[level].getGreen() * dz;
                        blue += emission[level].getBlue() * dz;
                    }
                }
                rendered.setRGB(i, j, new Color((int) Math.round(Math.max(0, Math.min(255, red))), (int) Math.round(Math.max(0, Math.min(255, green))), (int) Math.round(Math.max(0, Math.min(255, blue)))).getRGB());
            }
        }

        return rendered;
    }

    private static double f(double x, double y, double z) {
        double sum = 0;
        Point3D point = new Point3D(x, y, z);

        for (Pair i : chargesList) {
            Point3D centre = (Point3D) i.getKey();
            double dist = point.distance(centre);
            if (dist < 0.1) {
                dist = 0.1;
            }
            sum += (double) i.getValue() / dist;
        }
        return sum;
    }
}

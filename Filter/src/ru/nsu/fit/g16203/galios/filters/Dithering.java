package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Dithering {

    public static BufferedImage getFloyd(BufferedImage image, int redValue, int greenValue, int blueValue) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage floyd = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] red = new int[width * height];
        int[] green = new int[width * height];
        int[] blue = new int[width * height];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);
                red[y * width + x] = color.getRed();
                green[y * width + x] = color.getGreen();
                blue[y * width + x] = color.getBlue();
            }
        }

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(red[y * width + x], green[y * width + x], blue[y * width + x]);
                Color nearest = getNearest(color, redValue, greenValue, blueValue);
                floyd.setRGB(x, y, nearest.getRGB());

                int redDiff = color.getRed() - nearest.getRed();
                int greenDiff = color.getGreen() - nearest.getGreen();
                int blueDiff = color.getBlue() - nearest.getBlue();

                if (y + 1 < height) {
                    red[(y + 1) * width + x] = Math.max(0, Math.min(255, red[(y + 1) * width + x] + (int) ((double) redDiff * (5f / 16f))));
                    green[(y + 1) * width + x] = Math.max(0, Math.min(255, green[(y + 1) * width + x] + (int) ((double) greenDiff * (5f / 16f))));
                    blue[(y + 1) * width + x] = Math.max(0, Math.min(255, blue[(y + 1) * width + x] + (int) ((double) blueDiff * (5f / 16f))));

                    if (x + 1 < width) {
                        red[(y + 1) * width + (x + 1)] = Math.max(0, Math.min(255, red[(y + 1) * width + (x + 1)] + (int) ((double) redDiff * (1f / 16f))));
                        green[(y + 1) * width + (x + 1)] = Math.max(0, Math.min(255, green[(y + 1) * width + (x + 1)] + (int) ((double) greenDiff * (1f / 16f))));
                        blue[(y + 1) * width + (x + 1)] = Math.max(0, Math.min(255, blue[(y + 1) * width + (x + 1)] + (int) ((double) blueDiff * (1f / 16f))));
                    }

                    if (x - 1 >= 0) {
                        red[(y + 1) * width + (x - 1)] = Math.max(0, Math.min(255, red[(y + 1) * width + (x - 1)] + (int) ((double) redDiff * (3f / 16f))));
                        green[(y + 1) * width + (x - 1)] = Math.max(0, Math.min(255, green[(y + 1) * width + (x - 1)] + (int) ((double) greenDiff * (3f / 16f))));
                        blue[(y + 1) * width + (x - 1)] = Math.max(0, Math.min(255, blue[(y + 1) * width + (x - 1)] + (int) ((double) blueDiff * (3f / 16f))));
                    }
                }

                if (x + 1 < width) {
                    red[y * width + (x + 1)] = Math.max(0, Math.min(255, red[y * width + (x + 1)] + (int) ((double) redDiff * (7f / 16f))));
                    green[y * width + (x + 1)] = Math.max(0, Math.min(255, green[y * width + (x + 1)] + (int) ((double) greenDiff * (7f / 16f))));
                    blue[y * width + (x + 1)] = Math.max(0, Math.min(255, blue[y * width + (x + 1)] + (int) ((double) blueDiff * (7f / 16f))));
                }
            }
        }
        return floyd;
    }

    private static Color getNearest(Color color, int redValue, int greenValue, int blueValue) {

        int red = 0, green = 0, blue = 0;

        if (redValue > 1) {
            red = ((int) Math.round(color.getRed() * (redValue - 1) / 255d)) * 255 / (redValue - 1);
        }
        if (greenValue > 1) {
            green = ((int) Math.round(color.getGreen() * (greenValue - 1) / 255d)) * 255 / (greenValue - 1);
        }
        if (blueValue > 1) {
            blue = ((int) Math.round(color.getBlue() * (blueValue - 1) / 255d)) * 255 / (blueValue - 1);
        }

        return new Color(red, green, blue);
    }

    public static BufferedImage getOrdered(BufferedImage image, int redValue, int greenValue, int blueValue, int n) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage ordered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] errorMatrix = getErrors(n);
        double div = 1.0 / Math.pow(n, 2);
        double half = 1.0 / 2.0;
        double dr = 255.0 / (redValue - 1);
        double dg = 255.0 / (greenValue - 1);
        double db = 255.0 / (blueValue - 1);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);

                double err = (errorMatrix[(x % n) * n + y % n] * div - half);

                int red = Math.max(0, Math.min(255, (int) Math.round(color.getRed() + err * dr)));
                int green = Math.max(0, Math.min(255, (int) Math.round(color.getGreen() + err * dg)));
                int blue = Math.max(0, Math.min(255, (int) Math.round(color.getBlue() + err * db)));

                Color newColor = getNearest(new Color(red, green, blue), redValue, greenValue, blueValue);
                ordered.setRGB(x, y, newColor.getRGB());
            }
        }
        return ordered;
    }

    private static int[] getErrors(int n) {

        int[] matrix = new int[n * n];

        if (n == 1) {
            matrix[0] = 0;
            return matrix;
        }

        int len = n / 2;
        int[] smaller = getErrors(len);

        for (int y = 0; y < 2; ++y) {
            for (int k = 0; k < len; ++k) {
                for (int l = 0; l < len; ++l) {
                    matrix[k * len * 2 + (len * y + l)] = 4 * smaller[k * len + l] + 2 * y;
                    matrix[((len + k) * len) * 2 + (len * y + l)] = 4 * smaller[k * len + l] + 3 - 2 * y;
                }
            }
        }
        return matrix;
    }

}

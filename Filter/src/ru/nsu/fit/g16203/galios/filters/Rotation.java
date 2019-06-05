package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rotation {

    public static BufferedImage getRotated(BufferedImage image, int angle) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        double ang = Math.toRadians(angle);
        double sin = Math.sin(ang);
        double cos = Math.cos(ang);

        int x0 = width / 2;
        int y0 = height / 2;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double dx = x - x0;
                double dy = y - y0;

                int rotX = (int) (dx * cos - dy * sin + x0);
                int rotY = (int) (dx * sin + dy * cos + y0);

                if (rotX >= 0 && rotX < width && rotY >= 0 && rotY < height) {
                    rotated.setRGB(x, y, pixels[rotY * width + rotX]);
                } else {
                    rotated.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return rotated;
    }

}

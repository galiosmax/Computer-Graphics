package ru.nsu.fit.g16203.galios.raytracing.images;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Gamma {

    public static BufferedImage getCorrected(BufferedImage image, double gamma) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage corrected = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                Color color = new Color(pixels[y * width + x]);

                double normRed = color.getRed() / 256d;
                double normGreen = color.getGreen() / 256d;
                double normBlue = color.getBlue() / 256d;

                int red = Math.min(255, (int)(Math.round(Math.pow(normRed, gamma) * 256d)));
                int green = Math.min(255, (int)(Math.round(Math.pow(normGreen, gamma) * 256d)));
                int blue = Math.min(255, (int)(Math.round(Math.pow(normBlue, gamma) * 256d)));

                corrected.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return corrected;
    }
}
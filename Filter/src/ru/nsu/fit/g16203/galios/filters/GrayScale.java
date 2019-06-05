package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GrayScale {

    public static BufferedImage getGrayScale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayScale = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);
                int avg = (int)(0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue());
                grayScale.setRGB(x, y, new Color(avg, avg, avg).getRGB());
            }
        }
        return grayScale;
    }

}

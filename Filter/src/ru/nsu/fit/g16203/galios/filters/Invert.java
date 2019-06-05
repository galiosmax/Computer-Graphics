package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Invert {

    public static BufferedImage getInverted(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage inverted = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);
                inverted.setRGB(x, y, new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()).getRGB());
            }
        }
        return inverted;
    }

}

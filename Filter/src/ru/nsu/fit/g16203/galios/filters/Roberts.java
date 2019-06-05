package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Roberts {

    public static BufferedImage getRoberts(BufferedImage image, int level) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage roberts = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels;
        pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height - 1; ++y) {
            for (int x = 0; x < width - 1; ++x) {

                double gx = new Color(pixels[y * width + x]).getRed() - new Color(pixels[(y + 1) * width + x + 1]).getRed();
                double gy = new Color(pixels[y * width + x + 1]).getRed() - new Color(pixels[(y + 1) * width + x]).getRed();

                int edge = (int) Math.sqrt(Math.pow(gy, 2) + Math.pow(gx, 2));
                ;

                if (edge > level) {
                    roberts.setRGB(x, y, new Color(255, 255, 255).getRGB());
                } else {
                    roberts.setRGB(x, y, new Color(0, 0, 0).getRGB());
                }
            }
        }

        for (int i = 0; i < width; ++i) {
            roberts.setRGB(i, height - 1, new Color(0, 0, 0).getRGB());
        }
        for (int i = 0; i < height; ++i) {
            roberts.setRGB(width - 1, i, new Color(0, 0, 0).getRGB());
        }
        return roberts;

    }

}

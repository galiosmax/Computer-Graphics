package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sharpness {

    public static BufferedImage getSharpen(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage sharp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                if (y > 0) {
                    Color newColor = new Color(pixels[(y - 1) * width + x]);
                    red += color.getRed() - newColor.getRed();
                    green += color.getGreen() - newColor.getGreen();
                    blue += color.getBlue() - newColor.getBlue();
                }
                if (y < height - 1) {
                    Color newColor = new Color(pixels[(y + 1) * width + x]);
                    red += color.getRed() - newColor.getRed();
                    green += color.getGreen() - newColor.getGreen();
                    blue += color.getBlue() - newColor.getBlue();
                }
                if (x > 0) {
                    Color newColor = new Color(pixels[y * width + x - 1]);
                    red += color.getRed() - newColor.getRed();
                    green += color.getGreen() - newColor.getGreen();
                    blue += color.getBlue() - newColor.getBlue();
                }
                if (x < width - 1) {
                    Color newColor = new Color(pixels[y * width + x + 1]);
                    red += color.getRed() - newColor.getRed();
                    green += color.getGreen() - newColor.getGreen();
                    blue += color.getBlue() - newColor.getBlue();
                }

                sharp.setRGB(x, y, new Color(Math.max(0, Math.min(255, red)), Math.max(0, Math.min(255, green)), Math.max(0, Math.min(255, blue))).getRGB());
            }
        }
        return sharp;
    }

}

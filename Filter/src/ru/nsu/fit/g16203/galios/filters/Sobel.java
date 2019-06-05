package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sobel {

    public static BufferedImage getSobel(BufferedImage image, int level) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage sobel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels;
        pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[][] pixelMatrix = new int[3][3];
        
        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {

                pixelMatrix[0][0] = new Color(pixels[(y - 1) * width + (x - 1)]).getRed();
                pixelMatrix[0][1] = new Color(pixels[y * width + (x - 1)]).getRed();
                pixelMatrix[0][2] = new Color(pixels[(y + 1) * width + (x - 1)]).getRed();
                pixelMatrix[1][0] = new Color(pixels[(y - 1) * width + x]).getRed();
                pixelMatrix[1][2] = new Color(pixels[(y + 1) * width + x]).getRed();
                pixelMatrix[2][0] = new Color(pixels[(y - 1) * width + (x + 1)]).getRed();
                pixelMatrix[2][1] = new Color(pixels[y * width + (x + 1)]).getRed();
                pixelMatrix[2][2] = new Color(pixels[(y + 1) * width + (x + 1)]).getRed();

                int gy = (pixelMatrix[0][0] * -1) + (pixelMatrix[0][1] * -2) + (pixelMatrix[0][2] * -1) + (pixelMatrix[2][0]) + (pixelMatrix[2][1] * 2) + (pixelMatrix[2][2]);
                int gx = (pixelMatrix[0][0]) + (pixelMatrix[0][2] * -1) + (pixelMatrix[1][0] * 2) + (pixelMatrix[1][2] * -2) + (pixelMatrix[2][0]) + (pixelMatrix[2][2] * -1);

                int edge = (int) Math.sqrt(Math.pow(gy, 2) + Math.pow(gx, 2));

                if (edge > level) {
                    sobel.setRGB(x, y, new Color(255, 255, 255).getRGB());
                } else {
                    sobel.setRGB(x, y, new Color(0, 0, 0).getRGB());
                }
            }
        }

        for (int x = 0; x < width; ++x) {
            sobel.setRGB(x, height - 1, new Color(0, 0, 0).getRGB());
            sobel.setRGB(x, 0, new Color(0, 0, 0).getRGB());
        }
        for (int y = 0; y < height; ++y) {
            sobel.setRGB(width - 1, y, new Color(0, 0, 0).getRGB());
            sobel.setRGB(0, y, new Color(0, 0, 0).getRGB());

        }
        return sobel;
    }

}

package ru.nsu.fit.g16203.galios.filters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Median {

    public static BufferedImage getMedian(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage median = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] pixelArrayRed = new int[25];
        int[] pixelArrayGreen = new int[25];
        int[] pixelArrayBlue = new int[25];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                for (int i = 0; i < 25; ++i) {
                    Color color;
                    try {
                        color = new Color(pixels[(y + i / 5 - 2) * width + x + i % 5 - 2]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        color = new Color(pixels[y * width + x]);
                    }
                    pixelArrayRed[i] = color.getRed();
                    pixelArrayGreen[i] = color.getGreen();
                    pixelArrayBlue[i] = color.getBlue();
                }

                int red = sortArray(pixelArrayRed);
                int green = sortArray(pixelArrayGreen);
                int blue = sortArray(pixelArrayBlue);

                median.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        return median;
    }

    private static int sortArray(int[] array) {
        Arrays.sort(array);
        return array[array.length / 2];
    }

}

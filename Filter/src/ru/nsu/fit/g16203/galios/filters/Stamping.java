package ru.nsu.fit.g16203.galios.filters;

import ru.nsu.fit.g16203.galios.resources.Parameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Stamping {

    public static BufferedImage getStamp(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage stamp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                Color col = new Color(pixels[y * width + x]);
                int color = 0;

                try {
                    color += new Color(pixels[(y - 1) * width + x]).getRed();
                } catch (ArrayIndexOutOfBoundsException e) {
                    color += col.getRed();
                }
                try {
                    color -= new Color(pixels[(y + 1) * width + x]).getRed();
                } catch (ArrayIndexOutOfBoundsException e) {
                    color -= col.getRed();
                }
                try {
                    color -= new Color(pixels[y * width + x - 1]).getRed();
                } catch (ArrayIndexOutOfBoundsException e) {
                    color -= col.getRed();
                }
                try {
                    color += new Color(pixels[y * width + x + 1]).getRed();
                } catch (ArrayIndexOutOfBoundsException e) {
                    color += col.getRed();
                }

                color += Parameters.stampShift;
                color = Math.max(0, Math.min(255, color));
                stamp.setRGB(x, y, new Color(color, color, color).getRGB());
            }
        }
        return stamp;
    }

}

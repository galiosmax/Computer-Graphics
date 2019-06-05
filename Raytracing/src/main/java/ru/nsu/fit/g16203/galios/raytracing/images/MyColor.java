package ru.nsu.fit.g16203.galios.raytracing.images;

import java.awt.*;

public class MyColor {

    private double red, green, blue;

    MyColor(double red, double green, double blue) {

        this.red = red;
        this.green = green;
        this.blue = blue;

    }

    MyColor(Color color) {
        this.red = (double) color.getRed() / 255d;
        this.green = (double) color.getGreen() / 255d;
        this.blue = (double) color.getBlue() / 255d;
    }

    public MyColor add(MyColor color) {
        return new MyColor(red + color.getRed(), green + color.getGreen(), blue + color.getBlue());
    }

    MyColor multiply(double k) {
        return new MyColor(k * red, k * green, k * blue);
    }

    MyColor multiply(MyColor color) {
        return new MyColor(red * color.getRed(), green * color.getGreen(), blue * color.getBlue());
    }

    Color getColor() {
        int red = Math.max(0, Math.min(255, (int) (this.red * 255)));
        int green = Math.max(0, Math.min(255, (int) (this.green * 255)));
        int blue = Math.max(0, Math.min(255, (int) (this.blue * 255)));
        return new Color(red, green, blue);
    }

    double getRed() {
        return red;
    }

    void setRed(double red) {
        this.red = red;
    }

    double getGreen() {
        return green;
    }

    void setGreen(double green) {
        this.green = green;
    }

    double getBlue() {
        return blue;
    }

    void setBlue(double blue) {
        this.blue = blue;
    }
}

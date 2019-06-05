package ru.nsu.fit.g16203.galios.raytracing.panels;

public class OpticalParameters {

    private double diffuseRed;
    private double diffuseGreen;
    private double diffuseBlue;
    private double reflectionRed;
    private double reflectionGreen;
    private double reflectionBlue;
    private double power;


    public double getDiffuseRed() {
        return diffuseRed;
    }

    void setDiffuseRed(double diffuseRed) {
        this.diffuseRed = diffuseRed;
    }

    public double getDiffuseGreen() {
        return diffuseGreen;
    }

    void setDiffuseGreen(double diffuseGreen) {
        this.diffuseGreen = diffuseGreen;
    }

    public double getDiffuseBlue() {
        return diffuseBlue;
    }

    void setDiffuseBlue(double diffuseBlue) {
        this.diffuseBlue = diffuseBlue;
    }

    public double getReflectionRed() {
        return reflectionRed;
    }

    void setReflectionRed(double reflectionRed) {
        this.reflectionRed = reflectionRed;
    }

    public double getReflectionGreen() {
        return reflectionGreen;
    }

    void setReflectionGreen(double reflectionGreen) {
        this.reflectionGreen = reflectionGreen;
    }

    public double getReflectionBlue() {
        return reflectionBlue;
    }

    void setReflectionBlue(double reflectionBlue) {
        this.reflectionBlue = reflectionBlue;
    }

    public double getPower() {
        return power;
    }

    void setPower(double power) {
        this.power = power;
    }
}

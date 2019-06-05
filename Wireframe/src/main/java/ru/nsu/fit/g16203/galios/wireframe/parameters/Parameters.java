package ru.nsu.fit.g16203.galios.wireframe.parameters;

import ru.nsu.fit.g16203.galios.wireframe.matrix.Matrix;
import ru.nsu.fit.g16203.galios.wireframe.surface.Surface;

import java.awt.*;
import java.util.ArrayList;

public class Parameters {

    public int n;
    public int m;
    public int k;
    public double a;
    public double b;
    public double c;
    public double d;

    public double zn;
    public double zf;
    public double sw;
    public double sh;

    public Matrix matrixE;

    public Color backgroundColor;

    public ArrayList<Surface> surfaces;

    public Parameters() {
        setDefault();
    }

    public void setDefault() {
        n = 10;
        m = 10;
        k = 5;
        a = 0;
        b = 1;
        c = 0;
        d = Math.PI * 2;
        zn = 5;
        zf = 15;
        sw = 5;
        sh = 5;
        backgroundColor = Color.BLACK;
        double[][] matrixEArray = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        matrixE = new Matrix(matrixEArray);
        surfaces = new ArrayList<>();
    }


}

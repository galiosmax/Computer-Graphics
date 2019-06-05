package ru.nsu.fit.g16203.galios.isolines.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;

public class FunctionPanel extends JPanel {

    private MyPanel parent;
    private Color defaultIsolineColor = Color.BLACK;
    private Color[] defaultColors = {Color.GREEN, Color.CYAN, Color.ORANGE, Color.BLUE};
    private Color[] colors;
    private Color isolineColor;
    private int winWidth = 750;
    private int winHeight = 750;
    private double defaultA = -3, defaultB = 3, defaultC = -3, defaultD = 3;
    private int defaultK = 30, defaultM = 30;
    private double a, b, c, d;
    private int k, m;
    private double minFunction, maxFunction;
    private double[][] gridFunction;
    private ArrayList<Double> levels;
    private Double lastValue;
    private BufferedImage functionImage;
    private BufferedImage isolineImage;
    private BufferedImage gridImage;
    private BufferedImage dotsImage;
    private boolean interpolation = false;
    private boolean isolines = true;
    private boolean grid = false;
    private boolean interaction = false;
    private boolean dots = false;

    FunctionPanel(MyPanel panel) {
        parent = panel;
        setPreferredSize(new Dimension(winWidth, winHeight));
        isolineColor = defaultIsolineColor;
        a = defaultA;
        b = defaultB;
        c = defaultC;
        d = defaultD;
        k = defaultK;
        m = defaultM;
        colors = defaultColors;
        calcMinMaxFunc();
        levels = calcLevels();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                double funcX = getFunctionX(e.getX());
                double funcY = getFunctionY(e.getY());
                double z = getFunctionValue(funcX, funcY);

                double interpolated = getInterpolatedValue(e.getX(), e.getY());

                parent.setStatusText("X: " + String.format("%.3f", funcX) + "; Y: " + String.format("%.3f", funcY) + "; Z: " + String.format("%.3f", z) + "; Interpolated: " + String.format("%.3f", interpolated) + ";");
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (interaction) {
                    double z = getInterpolatedValue(e.getX(), e.getY());
                    levels.add(z);
                    lastValue = z;

                    drawIsolines();
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (interaction) {
                    double z = getInterpolatedValue(e.getX(), e.getY());
                    levels.add(z);
                    levels.remove(lastValue);
                    lastValue = z;

                    drawIsolines();
                    repaint();
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (functionImage != null) {
            g2.drawImage(functionImage, null, null);
        }

        if (grid && gridImage != null) {
            g2.drawImage(gridImage, null, null);
        }

        if (isolines && isolineImage != null) {
            g2.drawImage(isolineImage, null, null);
        }

        if (dots && dotsImage != null) {
            g2.drawImage(dotsImage, null, null);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (winHeight != height || winWidth != width || this.getX() != x || this.getY() != y) {
            super.setBounds(x, y, width, height);
            winWidth = width;
            winHeight = height;
            redraw();
        }
    }

    private void drawFunction() {
        functionImage = new BufferedImage(winWidth, winHeight, BufferedImage.TYPE_INT_RGB);

        double delta = new BigDecimal((maxFunction - minFunction) / colors.length).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        for (int x = 0; x < winWidth; ++x) {
            for (int y = 0; y < winHeight; ++y) {
                Color color;
                double current = getFunctionValue(getFunctionX(x), getFunctionY(y));

                if (interpolation) {
                    double part = (current - minFunction) / (maxFunction - minFunction);
                    color = parent.getInterpolatedColor(part);
                } else {
                    int index = (int) Math.floor(Math.abs(current - minFunction) / delta);
                    color = colors[Math.max(0, Math.min(colors.length - 1, index))];
                }
                functionImage.setRGB(x, y, color.getRGB());
            }
        }
    }

    private void drawIsolines() {
        isolineImage = new BufferedImage(winWidth, winHeight, BufferedImage.TYPE_INT_ARGB);
        dotsImage = new BufferedImage(winWidth, winHeight, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < k; ++x) {
            for (int y = 0; y < m; ++y) {
                for (double val : levels) {
                    marchingSquares(x, y, val);
                }
            }
        }
    }

    private void drawGrid() {
        gridImage = new BufferedImage(winWidth, winHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = gridImage.createGraphics();
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1f, 1f}, 0f));

        double dk = winWidth / (double) k;
        double dm = winHeight / (double) m;

        for (int i = 0; i < k; ++i) {
            g2.drawLine((int) Math.round(dk * i), 0, (int) Math.round(dk * i), winHeight - 1);
        }
        for (int j = 0; j < m; ++j) {
            g2.drawLine(0, (int) Math.round(dm * j), winWidth - 1, (int) Math.round(dm * j));
        }
    }

    private void marchingSquares(int x, int y, double z) {

        Graphics2D g2 = isolineImage.createGraphics();
        Graphics2D gDots = dotsImage.createGraphics();
        g2.setColor(isolineColor);

        double dk = winWidth / (double) k;
        double dm = winHeight / (double) m;

        int x0 = (int) Math.round(x * dk);
        int y0 = (int) Math.round(y * dm);
        int x1 = (int) Math.round((x + 1) * dk);
        int y1 = (int) Math.round((y + 1) * dm);

        double[] f = {gridFunction[x][y], gridFunction[x + 1][y], gridFunction[x + 1][y + 1], gridFunction[x][y + 1]};
        double epsilon = 0.01;

        boolean finished = false;

        while (!finished) {
            ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < 4; ++i) {
                Point point = getPoint(f[i], f[(i + 1) % 4], z, x0, y0, x1, y1, i);
                if (point != null) {
                    points.add(point);
                }
            }

            int size = points.size();
            if (size == 0) {
                finished = true;
            } else if (size == 1 || size == 3) {
                z += epsilon;
            } else if (size == 2) {
                g2.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
                gDots.drawOval(points.get(0).x - (int) dk / 10 / 2, points.get(0).y - (int) dm / 10 / 2, (int) dk / 10, (int) dm / 10);
                gDots.drawOval(points.get(1).x - (int) dk / 10 / 2, points.get(1).y - (int) dm / 10 / 2, (int) dk / 10, (int) dm / 10);
                finished = true;
            } else {
                int centreX = x0 + (x1 - x0) / 2;
                int centreY = y0 + (y1 - y0) / 2;

                int centre0 = 0;
                int centre1 = (int) Math.sqrt(Math.pow(centreX, 2) + Math.pow(centreY, 2));

                double alpha = (centreX - x0) / (double) centre1;
                double beta = (centreY - y0) / (double) centre1;

                double centreFunc = (f[0] + f[1] + f[2] + f[3]) / 4;

                for (int i = 0; i < 4; ++i) {
                    if (!(z < f[i] && z < centreFunc || z > f[i] && z > centreFunc)) {
                        int delta = (int) Math.round((centre1 - centre0) * (z - f[i]) / (centreFunc - f[i]));
                        Point dot;
                        if (i == 0) {
                            dot = new Point((int) (x0 + delta * alpha), (int) (y0 + delta * beta));
                        } else if (i == 1) {
                            dot = new Point((int) (x1 - delta * alpha), (int) (y0 + delta * beta));
                        } else if (i == 2) {
                            dot = new Point((int) (x1 - delta * alpha), (int) (y1 - delta * beta));
                        } else {
                            dot = new Point((int) (x0 + delta * alpha), (int) (y1 - delta * beta));
                        }

                        Point myPoint1 = points.get(i);
                        Point myPoint2 = points.get((i + 3) % 4);

                        g2.drawLine(dot.x, dot.y, myPoint1.x, myPoint1.y);
                        gDots.drawOval(dot.x - (int) dk / 10 / 2, dot.y - (int) dm / 10 / 2, (int) dk / 10, (int) dm / 10);
                        gDots.drawOval(myPoint1.x - (int) dk / 10 / 2, myPoint1.y - (int) dm / 10 / 2, (int) dk / 10, (int) dm / 10);

                        g2.drawLine(dot.x, dot.y, myPoint2.x, myPoint2.y);
                        gDots.drawOval(myPoint2.x - (int) dk / 10 / 2, myPoint2.y - (int) dm / 10 / 2, (int) dk / 10, (int) dm / 10);
                    }

                }
                finished = true;
            }
        }

    }

    private Point getPoint(double f1, double f2, double z, int x0, int y0, int x1, int y1, int i) {

        if (!(z < f1 && z < f2 || z > f1 && z > f2)) {
            if (f1 <= f2) {

                int deltaX = (int) Math.round((x1 - x0) * (z - f1) / (f2 - f1));
                int deltaY = (int) Math.round((y1 - y0) * (z - f1) / (f2 - f1));
                if (i == 0) {
                    return new Point(x0 + deltaX, y0);
                } else if (i == 1) {
                    return new Point(x1, y0 + deltaY);
                } else if (i == 2) {
                    return new Point(x1 - deltaX, y1);
                } else {
                    return new Point(x0, y1 - deltaY);
                }
            } else {
                int deltaX = (int) Math.round((x1 - x0) * (z - f2) / (f1 - f2));
                int deltaY = (int) Math.round((y1 - y0) * (z - f2) / (f1 - f2));

                if (i == 0) {
                    return new Point(x1 - deltaX, y0);
                } else if (i == 1) {
                    return new Point(x1, y1 - deltaY);
                } else if (i == 2) {
                    return new Point(x0 + deltaX, y1);
                } else {
                    return new Point(x0, y0 + deltaY);
                }
            }

        } else {
            return null;
        }
    }

    private ArrayList<Double> calcLevels() {
        double dF = (maxFunction - minFunction) / colors.length;
        double current = minFunction;
        ArrayList<Double> neededZ = new ArrayList<>();
        for (int i = 0; i < colors.length - 1; ++i) {
            current += dF;
            neededZ.add(current);
        }
        return neededZ;
    }

    private void calcGridFunction() {

        double dk = (b - a) / (double) k;
        double dm = (d - c) / (double) m;

        double x = a;
        double y = c;

        for (int i = 0; i < k + 1; ++i) {
            for (int j = 0; j < m + 1; ++j) {
                gridFunction[i][j] = getFunctionValue(x, y);
                y += dm;
            }
            x += dk;
            y = c;
        }
    }

    void calcMinMaxFunc() {
        double dx = (b - a) / winWidth;
        double dy = (d - c) / winHeight;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (double x = a; x < b; x += dx) {
            for (double y = c; y < d; y += dy) {
                double value = myFunction(x, y);
                if (value > max) {
                    max = value;
                }
                if (value < min) {
                    min = value;
                }
            }
        }

        if (min != Double.MAX_VALUE) {
            minFunction = min;
        }
        if (max != Double.MIN_VALUE) {
            maxFunction = max;
        }
    }

    void clear() {
        gridFunction = new double[k + 1][m + 1];
        levels = calcLevels();
        calcGridFunction();
        calcMinMaxFunc();
        drawFunction();
        drawGrid();
        drawIsolines();
        redraw();
    }

    double getMinFunction() {
        return minFunction;
    }

    double getMaxFunction() {
        return maxFunction;
    }

    private double getFunctionX(int x) {
        return a + (b - a) * x / (double) winWidth;
    }

    private double getFunctionY(int y) {
        return c + (d - c) * y / (double) winHeight;
    }

    private double getFunctionValue(int x, int y) {
        return gridFunction[x][y];
    }

    private double getFunctionValue(double x, double y) {
        return myFunction(x, y);
    }

    private double getInterpolatedValue(int x, int y) {
        double dk = winWidth / (double) k;
        double dm = winHeight / (double) m;

        int x0 = (int) Math.floor(x / dk);
        int y0 = (int) Math.floor(y / dm);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        double z1 = getFunctionValue(x0, y0);
        double z2 = getFunctionValue(x1, y0);
        double z3 = getFunctionValue(x0, y1);
        double z4 = getFunctionValue(x1, y1);

        double partX = (x - x0 * dk) / dk;
        double partY = (y - y0 * dm) / dm;

        double z1Int = z1 + (z2 - z1) * partX;
        double z2Int = z3 + (z4 - z3) * partX;

        return z1Int + (z2Int - z1Int) * partY;
    }

    void setColors(Color[] colors) {
        this.colors = colors;
        levels = calcLevels();
        redraw();
    }

    void setIsolineColor(Color isolineColor) {
        this.isolineColor = isolineColor;
        redraw();
    }

    void setInterpolation() {
        interpolation = !interpolation;
        drawFunction();
    }

    void setIsolines() {
        isolines = !isolines;
        redraw();
    }

    void setGrid() {
        grid = !grid;
        redraw();
    }

    void setInteraction() {
        interaction = !interaction;
        redraw();
    }

    void setDots() {
        dots = !dots;
        redraw();
    }

    double getValueA() {
        return a;
    }

    double getValueB() {
        return b;
    }

    double getValueC() {
        return c;
    }

    double getValueD() {
        return d;
    }

    int getValueK() {
        return k;
    }

    int getValueM() {
        return m;
    }

    void setValueA(double a) {
        this.a = a;
        levels = calcLevels();
        calcMinMaxFunc();
    }

    void setValueB(double b) {
        this.b = b;
        levels = calcLevels();
        calcMinMaxFunc();
    }

    void setValueC(double c) {
        this.c = c;
        levels = calcLevels();
        calcMinMaxFunc();
    }

    void setValueD(double d) {
        this.d = d;
        levels = calcLevels();
        calcMinMaxFunc();
    }

    void setValueK(int k) {
        this.k = k;
    }

    void setValueM(int m) {
        this.m = m;
    }

    void redraw() {
        gridFunction = new double[k + 1][m + 1];
        calcGridFunction();
        calcMinMaxFunc();
        drawFunction();
        drawGrid();
        drawIsolines();
    }

    private double myFunction(double x, double y) {
        return Math.sin(Math.cos(x)) * Math.sin(Math.cos(y));
    }
}

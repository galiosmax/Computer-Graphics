package ru.nsu.fit.g16203.galios.wireframe.panels;

import javafx.geometry.Point3D;
import ru.nsu.fit.g16203.galios.wireframe.matrix.Matrix;
import ru.nsu.fit.g16203.galios.wireframe.parameters.Parameters;
import ru.nsu.fit.g16203.galios.wireframe.surface.Surface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SurfacePanel extends JPanel {

    private MainFrame parent;

    private int width;
    private int height;

    private BufferedImage backgroundImage;
    private BufferedImage surfaceImage;

    private Color backgroundColor;

    private ArrayList<Point[][]> projectionCoordinates;
    private ArrayList<Point> centres;
    private ArrayList<Point> xAxis;
    private ArrayList<Point> yAxis;
    private ArrayList<Point> zAxis;

    private Point sceneCentre;
    private Point sceneXAxis;
    private Point sceneYAxis;
    private Point sceneZAxis;

    private Parameters parameters;

    private Point lastPoint;
    private boolean isSurface = false;
    private boolean isFigures = false;
    private boolean isWires = false;
    private boolean isAxis = false;
    private boolean isCentre = false;
    private Surface toChange;
    private int number = 0;

    SurfacePanel(MainFrame frame, Parameters parameters) {
        parent = frame;
        this.parameters = parameters;
        width = 800;
        height = 800;
        setPreferredSize(new Dimension(width, height));

        backgroundColor = parameters.backgroundColor;
        projectionCoordinates = new ArrayList<>();
        centres = new ArrayList<>();
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        zAxis = new ArrayList<>();
        drawBackground();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                lastPoint = new Point(e.getX(), e.getY());
                if (isFigures) {
                    toChange = getNearestSurface(lastPoint);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if (isSurface) {
                    Point currentPoint = new Point(e.getX(), e.getY());
                    Point difference = new Point(currentPoint.x - lastPoint.x, currentPoint.y - lastPoint.y);
                    lastPoint = currentPoint;

                    Matrix rotZ = Matrix.getRotZ((double) difference.x / 100d);
                    Matrix rotY = Matrix.getRotY((double) difference.y / 100d);

                    if (!isFigures || toChange == null) {
                        parameters.matrixE = parameters.matrixE.multiply(rotY.multiply(rotZ));
                        redraw();
                    } else {
                        toChange.rotateSurface(rotY.multiply(rotZ));
                        redraw();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isSurface) {
                    lastPoint = null;
                    toChange = null;
                }
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double proportion = parameters.zf / parameters.zn;

                if (e.getPreciseWheelRotation() < 0) {
                    if (parameters.zn - 0.1 > 0.5) {
                        parameters.zn -= 0.1;
                        parameters.zf -= 0.1 * proportion;
                    }
                } else {
                    if (parameters.zn + 0.1 < 20) {
                        parameters.zn += 0.1;
                        parameters.zf += 0.1 * proportion;
                    }
                }
                redraw();
            }
        });

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 10, 10, null);
        }
        if (surfaceImage != null) {
            g2.drawImage(surfaceImage, 10, 10, null);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (this.height != height || this.width != width || this.getX() != x || this.getY() != y) {
            if (parameters.surfaces != null && parameters.surfaces.size() > 0 && parameters.surfaces.get(0).getSpline().getPoints().size() > 3) {
                redraw();
            }
        }
    }

    private void drawBackground() {
        if (width > 0 && height > 0) {
            backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = backgroundImage.createGraphics();
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, width - 1, height - 1);
        }
    }

    private void drawSurfaces() {
        if (width > 0 && height > 0) {

            surfaceImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = surfaceImage.createGraphics();

            int n = parameters.n;
            int m = parameters.m;
            int k = parameters.k;
            int mk = m * k;
            int nk = n * k;

            for (int index = 0; index < projectionCoordinates.size(); ++index) {

                Point[][] points = projectionCoordinates.get(index);
                g2.setColor(parameters.surfaces.get(index).getBodyColor());

                if (!isWires) {
                    for (int i = 0; i < nk; ++i) {
                        for (int j = 0; j <= mk; j += k) {
                            if (points[i][j] != null && points[i + 1][j] != null) {
                                g2.drawLine(points[i][j].x, points[i][j].y, points[i + 1][j].x, points[i + 1][j].y);
                            }
                        }
                    }

                    for (int i = 0; i <= nk; i += k) {
                        for (int j = 0; j < mk; ++j) {
                            if (points[i][j] != null && points[i][j + 1] != null) {
                                g2.drawLine(points[i][j].x, points[i][j].y, points[i][j + 1].x, points[i][j + 1].y);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < nk; ++i) {
                        for (int j = 0; j <= mk; ++j) {
                            if (points[i][j] != null && points[i + 1][j] != null) {
                                g2.drawLine(points[i][j].x, points[i][j].y, points[i + 1][j].x, points[i + 1][j].y);
                            }
                        }
                    }

                    for (int i = 0; i <= nk; ++i) {
                        for (int j = 0; j < mk; ++j) {
                            if (points[i][j] != null && points[i][j + 1] != null) {
                                g2.drawLine(points[i][j].x, points[i][j].y, points[i][j + 1].x, points[i][j + 1].y);
                            }
                        }
                    }
                }
            }
            isSurface = true;

            if (isAxis) {
                g2.setColor(Color.RED);

                for (int i = 0; i < centres.size(); ++i) {
                    Point centre = centres.get(i);
                    Point point = xAxis.get(i);

                    g2.drawLine(centre.x, centre.y, point.x, point.y);
                }
                if (isCentre) {
                    g2.drawLine(sceneCentre.x, sceneCentre.y, sceneXAxis.x, sceneXAxis.y);
                }

                g2.setColor(Color.GREEN);

                for (int i = 0; i < centres.size(); ++i) {
                    Point centre = centres.get(i);
                    Point point = yAxis.get(i);

                    g2.drawLine(centre.x, centre.y, point.x, point.y);
                }
                if (isCentre) {
                    g2.drawLine(sceneCentre.x, sceneCentre.y, sceneYAxis.x, sceneYAxis.y);
                }

                g2.setColor(Color.BLUE);

                for (int i = 0; i < centres.size(); ++i) {
                    Point centre = centres.get(i);
                    Point point = zAxis.get(i);

                    g2.drawLine(centre.x, centre.y, point.x, point.y);
                }
                if (isCentre) {
                    g2.drawLine(sceneCentre.x, sceneCentre.y, sceneZAxis.x, sceneZAxis.y);
                }
            }
        }
    }

    private Matrix calcFinalMatrix(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {

        double xDiff = (xMax - xMin) / 2d;
        double yDiff = (yMax - yMin) / 2d;
        double zDiff = (zMax - zMin) / 2d;

        double xMove = (xMax + xMin) / 2d;
        double yMove = (yMax + yMin) / 2d;
        double zMove = (zMax + zMin) / 2d;

        double maxDiff = Math.max(xDiff, Math.max(yDiff, zDiff));
        double factor = 1d / maxDiff;

        double[][] shiftMatrixArray = {
                {1, 0, 0, -xMove},
                {0, 1, 0, -yMove},
                {0, 0, 1, -zMove},
                {0, 0, 0, 1}
        };
        double[][] factorMatrixArray = {
                {factor, 0, 0, 0},
                {0, factor, 0, 0},
                {0, 0, factor, 0},
                {0, 0, 0, 1}
        };

        double zf = parameters.zf;
        double zn = parameters.zn;
        double sw = parameters.sw;
        double sh = parameters.sh;

        double[][] projectionMatrixArray = {
                {2d * zn / sw, 0, 0, 0},
                {0, 2d * zn / sh, 0, 0},
                {0, 0, zf / (zf - zn), -(zf * zn) / (zf - zn)},
                {0, 0, 1, 0}
        };

        Matrix shiftMatrix = new Matrix(shiftMatrixArray);
        Matrix factorMatrix = new Matrix(factorMatrixArray);
        Matrix projectionMatrix = new Matrix(projectionMatrixArray);

        return projectionMatrix.multiply(Matrix.MCam.multiply(parameters.matrixE.multiply(factorMatrix.multiply(shiftMatrix))));
    }

    private void toProjection() {

        ArrayList<Surface> surfaces = parameters.surfaces;

        double xMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        double zMin = Double.MAX_VALUE;
        double zMax = -Double.MAX_VALUE;

        for (Surface surface : surfaces) {
            if (surface.getSpline().getPoints().size() >= 4) {
                surface.calcPoints();

                double currentXMin = surface.getXMin();
                double currentXMax = surface.getXMax();
                double currentYMin = surface.getYMin();
                double currentYMax = surface.getYMax();
                double currentZMin = surface.getZMin();
                double currentZMax = surface.getZMax();

                xMin = currentXMin < xMin ? currentXMin : xMin;
                xMax = currentXMax > xMax ? currentXMax : xMax;
                yMin = currentYMin < yMin ? currentYMin : yMin;
                yMax = currentYMax > yMax ? currentYMax : yMax;
                zMin = currentZMin < zMin ? currentZMin : zMin;
                zMax = currentZMax > zMax ? currentZMax : zMax;
            }
        }

        Matrix finalMatrix = calcFinalMatrix(xMin, xMax, yMin, yMax, zMin, zMax);

        if (!isCentre && surfaces.size() > 1) {
            double xMove = (xMax + xMin) / 2d;
            double yMove = (yMax + yMin) / 2d;
            double zMove = (zMax + zMin) / 2d;

            Point3D centre = new Point3D(xMove, yMove, zMove);
            Point3D x = new Point3D(xMax, centre.getY(), centre.getZ());
            Point3D y = new Point3D(centre.getX(), yMax, centre.getZ());
            Point3D z = new Point3D(centre.getX(), centre.getY(), zMax);

            sceneCentre = surfaces.get(0).calcPoint(centre, finalMatrix, width, height);
            sceneXAxis = surfaces.get(0).calcPoint(x, finalMatrix, width, height);
            sceneYAxis = surfaces.get(0).calcPoint(y, finalMatrix, width, height);
            sceneZAxis = surfaces.get(0).calcPoint(z, finalMatrix, width, height);
            isCentre = true;
        }

        for (int i = 0; i < surfaces.size(); ++i) {
            Surface surface = surfaces.get(i);

            if (surface.getSpline().getPoints().size() >= 4) {
                Point[][] points = surface.applyMatrix(finalMatrix, width, height);

                Point centre = surface.getCentre(finalMatrix, width, height);

                Point x = surface.getXAxis(finalMatrix, width, height);
                Point y = surface.getYAxis(finalMatrix, width, height);
                Point z = surface.getZAxis(finalMatrix, width, height);

                if (projectionCoordinates.size() <= i) {
                    projectionCoordinates.add(points);
                    centres.add(centre);
                    xAxis.add(x);
                    yAxis.add(y);
                    zAxis.add(z);
                } else {
                    projectionCoordinates.set(i, points);
                    centres.set(i, centre);
                    xAxis.set(i, x);
                    yAxis.set(i, y);
                    zAxis.set(i, z);
                }
            }
        }

    }

    private Surface getNearestSurface(Point point) {

        int n = parameters.n;
        int m = parameters.m;
        int k = parameters.k;
        int mk = m * k;
        int nk = n * k;

        Surface surface = null;

        if (projectionCoordinates != null) {

            double nearestDist = Double.MAX_VALUE;
            int surfaceIndex = -1;

            for (int index = 0; index < projectionCoordinates.size(); ++index) {

                Point[][] coords = projectionCoordinates.get(index);

                for (int i = 0; i <= nk; ++i) {
                    for (int j = 0; j < mk; ++j) {
                        if (coords[i][j] != null) {
                            double dist = point.distance(coords[i][j]);
                            if (dist < nearestDist) {
                                nearestDist = dist;
                                surfaceIndex = index;
                            }
                        }
                    }
                }
            }

            if (surfaceIndex != -1) {
                surface = parameters.surfaces.get(surfaceIndex);
            }
        }
        return surface;
    }

    void redraw() {

        isSurface = false;
        projectionCoordinates = new ArrayList<>();
        centres = new ArrayList<>();
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        zAxis = new ArrayList<>();
        sceneCentre = null;
        sceneXAxis = null;
        sceneYAxis = null;
        sceneZAxis = null;
        isCentre = false;
        backgroundColor = parameters.backgroundColor;

        double proportion = parameters.sh / parameters.sw;

        int w = parent.getWidth();
        int h = parent.getHeight();

        if (w - 35 > (double) (h - 155) / proportion) {
            height = h - 155;
            width = (int) ((double) height / proportion);
        } else {
            width = w - 35;
            height = (int) ((double) width * proportion);
        }

        toProjection();
        drawBackground();
        drawSurfaces();
        repaint();
    }

    void setFigures() {
        isFigures = !isFigures;
    }

    void moveSurfaces() {

        if (isSurface) {
            JFrame moveFrame = new JFrame("Moving Surfaces");
            JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
            panel.setBorder(BorderFactory.createTitledBorder("Settings"));

            JPanel numberPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JPanel CxPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JPanel CyPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JPanel CzPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JPanel buttonPanel = new JPanel();

            JLabel numberLabel = new JLabel("Surface Number");
            JLabel CxLabel = new JLabel("X coordinate");
            JLabel CyLabel = new JLabel("Y coordinate");
            JLabel CzLabel = new JLabel("Z coordinate");

            SpinnerModel spinnerModel = new SpinnerNumberModel(number, 0, parameters.surfaces.size() - 1, 1);
            JSpinner numberSpinner = new JSpinner(spinnerModel);

            JSlider CxSlider = new JSlider(-1000, 1000);
            JSlider CySlider = new JSlider(-1000, 1000);
            JSlider CzSlider = new JSlider(-1000, 1000);

            Point3D centre = parameters.surfaces.get(number).getCentre();

            CxSlider.setValue((int) (centre.getX() * 100d));
            CySlider.setValue((int) (centre.getY() * 100d));
            CzSlider.setValue((int) (centre.getZ() * 100d));

            JButton okButton = new JButton("OK");

            numberPanel.add(numberLabel);
            numberPanel.add(numberSpinner);

            CxPanel.add(CxLabel);
            CyPanel.add(CyLabel);
            CzPanel.add(CzLabel);
            CxPanel.add(CxSlider);
            CyPanel.add(CySlider);
            CzPanel.add(CzSlider);

            buttonPanel.add(okButton);

            panel.add(numberPanel);
            panel.add(CxPanel);
            panel.add(CyPanel);
            panel.add(CzPanel);
            panel.add(buttonPanel);

            numberSpinner.addChangeListener(e -> {
                number = (int) numberSpinner.getValue();

                Point3D point = parameters.surfaces.get(number).getCentre();
                CxSlider.setValue((int) (point.getX() * 100d));
                CySlider.setValue((int) (point.getY() * 100d));
                CzSlider.setValue((int) (point.getZ() * 100d));
            });

            CxSlider.addChangeListener(e -> {
                Surface surface = parameters.surfaces.get(number);

                double x = CxSlider.getValue() / 100d;
                double y = CySlider.getValue() / 100d;
                double z = CzSlider.getValue() / 100d;

                surface.moveSurfaceTo(new Point3D(x, y, z));
                redraw();
            });

            CySlider.addChangeListener(e -> {
                Surface surface = parameters.surfaces.get(number);

                double x = CxSlider.getValue() / 100d;
                double y = CySlider.getValue() / 100d;
                double z = CzSlider.getValue() / 100d;

                surface.moveSurfaceTo(new Point3D(x, y, z));
                redraw();
            });

            CzSlider.addChangeListener(e -> {
                Surface surface = parameters.surfaces.get(number);

                double x = CxSlider.getValue() / 100d;
                double y = CySlider.getValue() / 100d;
                double z = CzSlider.getValue() / 100d;

                surface.moveSurfaceTo(new Point3D(x, y, z));
                redraw();
            });

            okButton.addActionListener(e -> {
                moveFrame.setVisible(false);
                moveFrame.dispose();
            });

            moveFrame.add(panel);
            moveFrame.pack();
            moveFrame.setVisible(true);
        }

    }

    void wire() {
        isWires = !isWires;
        redraw();
    }

    void axis() {
        isAxis = !isAxis;
        redraw();
    }
}

package ru.nsu.fit.g16203.galios.raytracing.panels;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.raytracing.images.Raytracing;
import ru.nsu.fit.g16203.galios.raytracing.matrix.Matrix;
import ru.nsu.fit.g16203.galios.raytracing.parameters.RenderParameters;
import ru.nsu.fit.g16203.galios.raytracing.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ScenePanel extends JPanel {

    private MainFrame parent;

    private int width;
    private int height;

    private BufferedImage backgroundImage;
    private BufferedImage surfaceImage;
    private BufferedImage raysImage;

    private Color backgroundColor;

    private Point sceneCentre;
    private Point sceneXAxis;
    private Point sceneYAxis;
    private Point sceneZAxis;

    private RenderParameters renderParameters;
    private Scene scene;
    private ArrayList<ArrayList<Pair<Point, Point>>> projectionCoordinates;

    private Point lastPoint;
    private boolean isRaytracing = false;
    private Timer timer;

    ScenePanel(MainFrame frame, Scene scene, RenderParameters renderParameters) {
        parent = frame;
        this.scene = scene;
        this.renderParameters = renderParameters;
        width = 980;
        height = 875;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);

        backgroundColor = renderParameters.getBackgroundColor();
        projectionCoordinates = new ArrayList<>();
        drawBackground();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = new Point(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isRaytracing) {
                    Point currentPoint = new Point(e.getX(), e.getY());
                    Point difference = new Point(currentPoint.x - lastPoint.x, currentPoint.y - lastPoint.y);
                    lastPoint = currentPoint;

                    Matrix rotZ = Matrix.getRotZ((double) difference.x / 100d);
                    Matrix rotY = Matrix.getRotY(-(double) difference.y / 100d);

                    Point3D cameraPoint = renderParameters.getCameraPoint();
                    Point3D viewPoint = renderParameters.getViewPoint();
                    Matrix PCam = new Matrix(cameraPoint).sub(new Matrix(viewPoint));
                    Matrix matrix = rotY.multiply(rotZ);

                    double[][] newVector = matrix.multiply(PCam).matrix;
                    renderParameters.setCameraPoint(new Point3D(newVector[0][0] + viewPoint.getX(), newVector[1][0] + viewPoint.getY(), newVector[2][0] + viewPoint.getZ()));
                    redraw();
                }
            }

        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isRaytracing) {
                    if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
                        renderParameters.getCamera().moveY(0.3);
                        redraw();
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isRaytracing) {
                    if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        renderParameters.getCamera().moveY(0 - .3);
                        redraw();
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isRaytracing) {
                    if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        renderParameters.getCamera().moveX(0.3);
                        redraw();
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isRaytracing) {
                    if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
                        renderParameters.getCamera().moveX(-0.3);
                        redraw();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isRaytracing) {
                    lastPoint = null;
                }
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!isRaytracing) {

                    if (e.isControlDown()) {
                        double delta = 0.1;
                        if (e.getPreciseWheelRotation() < 0) {
                            renderParameters.getCamera().moveZ(-delta);
                        } else {
                            renderParameters.getCamera().moveZ(delta);
                        }
                    } else {

                        double zn = renderParameters.getZn();
                        double zf = renderParameters.getZf();

                        if (e.getPreciseWheelRotation() < 0) {
                            if (zn < 20) {
                                renderParameters.setZn(zn * 1.03);
                                renderParameters.setZf(zf * 1.03);
                            }
                        } else {
                            if (zn >= 0.5) {
                                renderParameters.setZn(zn / 1.03);
                                renderParameters.setZf(zf / 1.03);
                            }
                        }
                    }
                    redraw();
                }
            }
        });

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isRaytracing && raysImage != null) {
            g2.drawImage(raysImage, 0, 0, null);
        } else {
            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, null);
            }
            if (surfaceImage != null) {
                g2.drawImage(surfaceImage, 0, 0, null);
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (this.height != height || this.width != width || this.getX() != x || this.getY() != y) {
            if (scene.getFigures() != null && scene.getFigures().size() > 0) {
                this.height = height;
                this.width = width;
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
        if (width > 0 && height > 0 && projectionCoordinates != null && projectionCoordinates.size() != 0) {

            surfaceImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = surfaceImage.createGraphics();

            g2.setColor(new Color(255 - backgroundColor.getRed(), 255 - backgroundColor.getGreen(), 255 - backgroundColor.getBlue()));
            for (ArrayList<Pair<Point, Point>> surfaces : projectionCoordinates) {
                for (Pair<Point, Point> pair : surfaces) {
                    if (pair != null) {
                        g2.drawLine(pair.getKey().x, pair.getKey().y, pair.getValue().x, pair.getValue().y);
                    }
                }
            }

            if (scene.hasFigures()) {
                g2.setColor(Color.RED);
                g2.drawLine(sceneCentre.x, sceneCentre.y, sceneXAxis.x, sceneXAxis.y);
                g2.setColor(Color.GREEN);
                g2.drawLine(sceneCentre.x, sceneCentre.y, sceneYAxis.x, sceneYAxis.y);
                g2.setColor(Color.BLUE);
                g2.drawLine(sceneCentre.x, sceneCentre.y, sceneZAxis.x, sceneZAxis.y);
            }
        }
    }

    private Matrix calcFinalMatrix(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {

        double xMove = (xMax + xMin) / 2d;
        double yMove = (yMax + yMin) / 2d;
        double zMove = (zMax + zMin) / 2d;

        double zf = renderParameters.getZf();
        double zn = renderParameters.getZn();
        double sw = renderParameters.getSw();
        double sh = renderParameters.getSh();

        double[][] projectionMatrixArray = {
                {2d * zn / sw, 0, 0, 0},
                {0, 2d * zn / sh, 0, 0},
                {0, 0, zf / (zf - zn), -(zf * zn) / (zf - zn)},
                {0, 0, 1, 0}
        };

        Matrix projectionMatrix = new Matrix(projectionMatrixArray);

        if (!renderParameters.isLoaded()) {
            renderParameters.calcDefault(new Point3D(xMove, yMove, zMove), xMin, xMax, width, height);
        }
        return projectionMatrix.multiply(renderParameters.getCamera().calculateMCam());
    }

    private void toProjection() {

        double xMin = scene.getXMin();
        double xMax = scene.getXMax();
        double yMin = scene.getYMin();
        double yMax = scene.getYMax();
        double zMin = scene.getZMin();
        double zMax = scene.getZMax();

        Matrix finalMatrix = calcFinalMatrix(xMin, xMax, yMin, yMax, zMin, zMax);

        if (scene.hasFigures()) {
            double xMove = (xMax + xMin) / 2d;
            double yMove = (yMax + yMin) / 2d;
            double zMove = (zMax + zMin) / 2d;

            Point3D centre = new Point3D(xMove, yMove, zMove);
            Point3D x = new Point3D(xMax, centre.getY(), centre.getZ());
            Point3D y = new Point3D(centre.getX(), yMax, centre.getZ());
            Point3D z = new Point3D(centre.getX(), centre.getY(), zMax);

            sceneCentre = Matrix.calcPoint(centre, finalMatrix, width, height);
            sceneXAxis = Matrix.calcPoint(x, finalMatrix, width, height);
            sceneYAxis = Matrix.calcPoint(y, finalMatrix, width, height);
            sceneZAxis = Matrix.calcPoint(z, finalMatrix, width, height);
        }

        projectionCoordinates = scene.applyMatrix(finalMatrix, width, height);
    }

    void redraw() {

        projectionCoordinates = new ArrayList<>();
        sceneCentre = null;
        sceneXAxis = null;
        sceneYAxis = null;
        sceneZAxis = null;
        backgroundColor = renderParameters.getBackgroundColor();

        double proportion = (double) width / (double) height;
        renderParameters.setSw(proportion * renderParameters.getSh());

        toProjection();
        drawBackground();
        drawSurfaces();
        requestFocus();
        repaint();
    }

    void doRaytracing() {
        raysImage = null;
        Raytracing rays = new Raytracing(scene, renderParameters, width, height);
        isRaytracing = true;
        rays.run();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!rays.isFinished() && isRaytracing) {
                    parent.setStatus(rays.getProgress());
                } else if (rays.isFinished()) {
                    raysImage = rays.getImage();
                    parent.setImageToSave(raysImage);
                    repaint();
                    timer.cancel();
                    parent.setRaytracing(false);
                } else {
                    timer.cancel();
                    parent.setRaytracing(false);
                }
            }
        };

        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    void stopRaytracing() {
        if (isRaytracing) {
            timer.cancel();
        }
        isRaytracing = false;
        parent.setRaytracing(false);
        repaint();
    }
}

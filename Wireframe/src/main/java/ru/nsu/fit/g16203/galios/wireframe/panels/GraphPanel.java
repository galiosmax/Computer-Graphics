package ru.nsu.fit.g16203.galios.wireframe.panels;

import javafx.geometry.Point2D;
import ru.nsu.fit.g16203.galios.wireframe.spline.Spline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphPanel extends JPanel {

    private Spline spline;

    private Color splineColor;
    private Color backgroundColor;
    private BufferedImage backgroundImage;
    private BufferedImage pointsImage;
    private BufferedImage splineImage;

    private SettingsFrame parent;

    private int width = 1200;
    private int height = 800;

    private final int circleRadius = 5;

    private double xMin = -1;
    private double xMax = 1;
    private double yMin = -1;
    private double yMax = 1;

    private int num = 0;

    private int index = -1;

    private boolean redraw = false;

    GraphPanel(SettingsFrame frame) {
        parent = frame;

        setPreferredSize(new Dimension(width, height));
        if (parent.getParameters().surfaces.get(num).getSpline() == null) {
            parent.getParameters().surfaces.get(num).setSpline(new Spline());
        }
        spline = parent.getParameters().surfaces.get(num).getSpline();
        backgroundColor = parent.getParameters().backgroundColor;
        splineColor = parent.getParameters().surfaces.get(num).getBodyColor();

        if (spline.getPoints().size() > 0) {
            setBounds(spline.getMax());
            drawPoints();
        }
        if (spline.getPoints().size() >= 4) {
            setBounds(spline.getMax());
            spline.calcSpline();
            drawSpline();
        }
        drawBackground();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    Point newPoint = new Point(x, y);

                    int i = getPoint(newPoint);
                    if (i != -1) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            spline.getPoints().remove(i);
                            drawPoints();
                            if (spline.getPoints().size() >= 4) {
                                spline.calcSpline();
                                drawSpline();
                            } else {
                                splineImage = null;
                            }
                            repaint();

                        } else {
                            index = i;
                        }
                    } else {
                        spline.getPoints().add(getCalculatedPoint(newPoint));
                        index = spline.getPoints().size() - 1;
                        drawPoints();
                        if (spline.getPoints().size() >= 4) {
                            spline.calcSpline();
                            drawSpline();
                        } else {
                            splineImage = null;
                        }
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                int x = Math.max(0, Math.min(width - 1, e.getX()));
                int y = Math.max(0, Math.min(height - 1, e.getY()));

                if (index != -1) {
                    Point2D point = getCalculatedPoint(new Point(x, y));
                    spline.getPoints().set(index, point);
                    drawPoints();
                    if (spline.getPoints().size() >= 4) {
                        spline.calcSpline();
                        drawSpline();
                    }
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                index = -1;
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getPreciseWheelRotation() < 0) {
                    if (xMax - 0.1 > 0.3) {
                        if (xMax < 10) {
                            xMax -= 0.1;
                            xMin += 0.1;
                            yMax -= 0.1;
                            yMin += 0.1;
                        } else {
                            xMax -= 1;
                            xMin += 1;
                            yMax -= 1;
                            yMin += 1;
                        }
                    }
                } else {
                    if (xMax < 10) {
                        xMax += 0.1;
                        xMin -= 0.1;
                        yMax += 0.1;
                        yMin -= 0.1;
                    } else {
                        xMax += 1;
                        xMin -= 1;
                        yMax += 1;
                        yMin -= 1;
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
            g2.drawImage(backgroundImage, null, null);
        }
        if (pointsImage != null) {
            g2.drawImage(pointsImage, null, null);
        }
        if (splineImage != null) {
            g2.drawImage(splineImage, null, null);
        }
    }

    private void drawBackground() {
        backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = backgroundImage.createGraphics();

        int x = width / 2;
        int y = height / 2;

        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, width - 1, height - 1);

        g2.setColor(new Color(255 - backgroundColor.getRed(), 255 - backgroundColor.getGreen(), 255 - backgroundColor.getBlue()));

        int currentX = x;
        int currentY = y;
        int dx, dy;
        if (yMax <= 1) {
            dx = (int) ((double) width / (10 * (yMax - yMin)));
            dy = (int) ((double) height / (10 * (xMax - xMin)));
            g2.drawString("Current scale = " + "0.1", 10, 10);
        }
        else if (yMax < 10) {
            dx = (int) ((double) width / (yMax - yMin));
            dy = (int) ((double) height / (xMax - xMin));
            g2.drawString("Current scale = " + "1", 10, 10);
        } else {
            dx = (int) ((double) width / (0.1 * (yMax - yMin)));
            dy = (int) ((double) height / (0.1 * (xMax - xMin)));
            g2.drawString("Current scale = " + "10", 10, 10);
        }
        int max = width / dx;

        while (currentX - dx > 0) {
            currentX -= dx;
            currentY -= dy;
        }

        for (int i = 0; i < max; ++i) {
            g2.fillOval(currentX - 3, y - 3, 6, 6);
            g2.fillOval(x - 3, currentY - 3, 6, 6);
            currentX += dx;
            currentY += dy;
        }

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1f, 1f}, 0f));

        g2.drawLine(x, 0, x, height - 1);
        g2.drawLine(0, y, width - 1, y);
    }

    private void drawPoints() {
        pointsImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = pointsImage.createGraphics();
        Color pointsColor = new Color(255 - backgroundColor.getRed(), 255 - backgroundColor.getGreen(), 255 - backgroundColor.getBlue());
        g2.setColor(pointsColor);
        ArrayList<Point> pixelPoints = toPixels(spline.getPoints());

        for (Point p : pixelPoints) {
            g2.drawOval(p.x - circleRadius, p.y - circleRadius, 2 * circleRadius, 2 * circleRadius);
        }

        Point point1 = pixelPoints.get(0);
        Point point2;
        for (int i = 1; i < pixelPoints.size(); ++i) {
            point2 = pixelPoints.get(i);
            g2.drawLine(point1.x, point1.y, point2.x, point2.y);
            point1 = point2;
        }
    }

    private void drawSpline() {
        splineImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        double length = 0;
        int n = parent.getParameters().n;
        Graphics2D g2 = splineImage.createGraphics();
        g2.setColor(splineColor);

        ArrayList<Point2D> splinePoints = spline.getSplinePoints();

        Point2D point1 = splinePoints.get(0);
        Point2D point2;
        for (int i = 1; i < splinePoints.size(); ++i) {
            point2 = splinePoints.get(i);
            length += point1.distance(point2);
            point1 = point2;
        }

        double start = parent.getParameters().a * length;
        double finish = parent.getParameters().b * length;
        if (length != spline.getLength() || redraw) {
            spline.setLength(length);
        }

        g2.setColor(Color.GRAY);

        double dl = length / n;
        double neededLen = start;
        length = 0;
        point1 = splinePoints.get(0);
        for (int i = 1; i < splinePoints.size(); ++i) {
            point2 = splinePoints.get(i);
            if (length >= start) {
                g2.setColor(splineColor);
            }
            if (length > finish) {
                g2.setColor(Color.GRAY);
            }
            length += point1.distance(point2);

            Point pixelPoint1 = getPixelPoint(point1);
            Point pixelPoint2 = getPixelPoint(point2);

            g2.drawLine(pixelPoint1.x, pixelPoint1.y, pixelPoint2.x, pixelPoint2.y);
            if (length >= neededLen) {
                g2.fillOval(pixelPoint1.x - 3, pixelPoint1.y - 3, 6, 6);
                neededLen += dl;
            }
            point1 = point2;
        }
    }

    private int getPoint(Point point) {

        double minDist = circleRadius;
        int minIndex = -1;

        ArrayList<Point> pixelPoints = toPixels(spline.getPoints());

        for (int i = 0; i < pixelPoints.size(); ++i) {
            double dist = pixelPoints.get(i).distance(point);
            if (dist < minDist) {
                minDist = dist;
                minIndex = i;
            }
        }

        return minIndex;
    }

    private Point2D getCalculatedPoint(Point point) {

        double x = xMin + (xMax - xMin) * point.x / width;
        double y = yMin + (yMax - yMin) * point.y / height;

        return new Point2D(x, y);
    }

    private Point getPixelPoint(Point2D point2D) {
        int x = (int) Math.round(((point2D.getX() - xMin) / (xMax - xMin)) * width);
        int y = (int) Math.round(((point2D.getY() - yMin) / (yMax - yMin)) * height);

        return new Point(x, y);
    }

    private ArrayList<Point> toPixels(ArrayList<Point2D> points) {

        ArrayList<Point> pixelPoints = new ArrayList<>();

        for (Point2D p : points) {
            pixelPoints.add(getPixelPoint(p));
        }
        return pixelPoints;
    }

    void setNum(int num) {
        this.num = num;
        if (parent.getParameters().surfaces.get(num).getSpline() == null) {
            parent.getParameters().surfaces.get(num).setSpline(new Spline());
        }
    }

    void redraw() {
        redraw = true;
        spline = parent.getParameters().surfaces.get(num).getSpline();
        splineColor = parent.getParameters().surfaces.get(num).getBodyColor();
        backgroundColor = parent.getParameters().backgroundColor;
        drawBackground();
        pointsImage = null;
        if (spline.getPoints().size() != 0) {
            drawPoints();
        }
        splineImage = null;
        if (spline.getSplinePoints().size() > 3) {
            drawSpline();
        }
        redraw = false;
        repaint();
    }

    private void setBounds(double max) {
        if (max <= 1) {
            max += 0.1;
        } else if (max <= 10){
            max += 1;
        } else {
            max += 10;
        }
        xMin = -max;
        xMax = max;
        yMin = -max;
        yMax = max;
    }
}

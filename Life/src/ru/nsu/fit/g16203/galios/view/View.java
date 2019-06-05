package ru.nsu.fit.g16203.galios.view;

import ru.nsu.fit.g16203.galios.controller.MainFrame;
import ru.nsu.fit.g16203.galios.model.Condition;
import ru.nsu.fit.g16203.galios.model.Constants;
import ru.nsu.fit.g16203.galios.model.Life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class View extends JPanel {

    private BufferedImage image;
    private Graphics2D g;
    private boolean impacts = false;

    private Point lastPoint;
    private Life life;
    private MainFrame mainFrame;
    private Colors colors;


    private boolean isRunning = false;

    public View(MainFrame frame) {
        this.mainFrame = frame;
        colors = new Colors();
    }

    public void run(Life life) {
        this.life = life;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                lastPoint = life.getCellByCoordinate(x, y);
                try {
                    int color = image.getRGB(x, y);
                    if (!isRunning && color == colors.getColor(Condition.DEAD).getRGB() || color == colors.getColor(Condition.ALIVE).getRGB()) {
                        life.changeState(lastPoint.x, lastPoint.y);
                        repaint();
                        mainFrame.isChanged = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                try {
                    int color = image.getRGB(x, y);
                    if (!isRunning && color == colors.getColor(Condition.DEAD).getRGB() || color == colors.getColor(Condition.ALIVE).getRGB()) {
                        Point point = life.getCellByCoordinate(x, y);
                        if (lastPoint != null && (lastPoint.x != point.x || lastPoint.y != point.y)) {
                            lastPoint = point;
                            life.changeState(point.x, point.y);
                            repaint();
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (impacts && Constants.hexSize >= Constants.impactDrawLowerBound + Constants.lineWidth) {
            BufferedImage impactImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D gImage = (Graphics2D) impactImage.getGraphics();
            gImage.drawImage(image, null, null);
            gImage.setColor(Color.BLACK);
            double[][] impacts = life.getImpacts();
            int fontHeight = Constants.hexSize;
            int fontHeightOffset = 2 * fontHeight / 5;
            gImage.setFont(new Font("ComicSans", Font.BOLD, fontHeight));
            FontMetrics fontMetrics = gImage.getFontMetrics();

            for (int i = 0; i < Constants.fieldWidth; i++) {
                for (int j = 0; j < Constants.fieldHeight; j++) {
                    Point point = life.getCoordinateByCell(i, j);
                    if (point != null) {
                        if (Math.abs(impacts[i][j] - (int) Math.round(impacts[i][j])) < 0.1) {
                            int widthOffset = fontMetrics.stringWidth(String.valueOf((int) Math.round(impacts[i][j]))) / 2;
                            gImage.drawString(String.valueOf((int) Math.round(impacts[i][j])), point.x - widthOffset, point.y + fontHeightOffset);
                        } else {
                            int widthOffset = fontMetrics.stringWidth(String.format("%.1f", impacts[i][j])) / 2;
                            gImage.drawString(String.format("%.1f", impacts[i][j]), point.x - widthOffset, point.y + fontHeightOffset);
                        }
                    }
                }
            }

            g2.drawImage(impactImage, null, null);
        } else {
            g2.drawImage(image, null, null);
        }
    }

    public void span(int x0, int y0, int oldColor, int newColor) {
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x0, y0));
        Point point;

        while (!stack.empty()) {
            point = stack.pop();
            int y = point.y;
            int x = point.x;
            if (image.getRGB(x, y) == oldColor) {

                while (image.getRGB(x, y) == oldColor) {
                    x--;
                }
                x++;

                boolean down = true;
                boolean up = true;

                while (image.getRGB(x, y) == oldColor) {
                    image.setRGB(x, y, newColor);
                    if (down && image.getRGB(x, y - 1) == oldColor) {
                        stack.push(new Point(x, y - 1));
                        down = false;
                    } else if (image.getRGB(x, y - 1) != oldColor) {
                        down = true;
                    }
                    if (up && image.getRGB(x, y + 1) == oldColor) {
                        stack.push(new Point(x, y + 1));
                        up = false;
                    } else if (image.getRGB(x, y - 1) != oldColor) {
                        up = true;
                    }

                    x++;
                }
            }
        }
    }

    public void showImpacts() {
        impacts = !impacts;
        repaint();
    }

    public void drawField() {

        int imageWidth = (int) Math.round(Constants.fieldWidth * Math.pow(3, 0.5) * Constants.hexSize) + Constants.hexSize;
        int imageHeight = (Constants.fieldHeight * 2 * Constants.hexSize * 3 / 4) + 2 * Constants.hexSize;

        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(imageWidth, imageHeight));
        int color = Color.WHITE.getRGB();
        g = (Graphics2D) image.getGraphics();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, color);
            }
        }

        double halfSize = Constants.hexSize / 2.0;
        double delta = Math.sqrt(3) * halfSize;
        double hexWidth = (Math.sqrt(3) * (double) Constants.hexSize);
        double hexHeight = 2.0 * Constants.hexSize - halfSize;
        double x0, y0;

        for (int y = 0; y < Constants.fieldHeight; y += 2) {
            for (int x = 0; x < Constants.fieldWidth; x++) {
                x0 = Constants.hexSize + x * hexWidth;
                y0 = (y + 1) * hexHeight;
                drawHex(x0, y0);
                life.setCellCoordinate(x, y, (int) Math.round(x0), (int) Math.round(y0));
                if (x < Constants.fieldWidth - 1 && y < Constants.fieldHeight - 1) {
                    x0 = Constants.hexSize + delta + x * hexWidth;
                    y0 = (y + 2.0) * hexHeight;
                    drawHex(x0, y0);
                    life.setCellCoordinate(x, y + 1, (int) Math.round(x0), (int) Math.round(y0));
                }
            }
        }

        repaint();
    }

    private void drawHex(double x0, double y0) {

        double halfSize = Constants.hexSize / 2.0;
        double delta = Math.sqrt(3) * halfSize;

        drawLine((int) Math.round(x0), (int) Math.round(y0 - Constants.hexSize), (int) Math.round(x0 + delta), (int) Math.round(y0 - halfSize));
        drawLine((int) Math.round(x0), (int) Math.round(y0 - Constants.hexSize), (int) Math.round(x0 - delta), (int) Math.round(y0 - halfSize));
        drawLine((int) Math.round(x0 + delta), (int) Math.round(y0 - halfSize), (int) Math.round(x0 + delta), (int) Math.round(y0 + halfSize));
        drawLine((int) Math.round(x0 - delta), (int) Math.round(y0 - halfSize), (int) Math.round(x0 - delta), (int) Math.round(y0 + halfSize));
        drawLine((int) Math.round(x0 + delta), (int) Math.round(y0 + halfSize), (int) Math.round(x0), (int) Math.round(y0 + Constants.hexSize));
        drawLine((int) Math.round(x0 - delta), (int) Math.round(y0 + halfSize), (int) Math.round(x0), (int) Math.round(y0 + Constants.hexSize));
        span((int) Math.round(x0), (int) Math.round(y0), Color.WHITE.getRGB(), colors.getColor(Condition.DEAD).getRGB());
    }

    private void drawLine(int x0, int y0, int x1, int y1) {

        if (Constants.lineWidth > 1) {
            g.setStroke(new BasicStroke(Constants.lineWidth));
            g.setColor(Color.BLACK);
            g.drawLine(x0, y0, x1, y1);
        } else {
            bresenham(x0, y0, x1, y1);
        }
    }

    private void bresenham(int x0, int y0, int x1, int y1) {

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int incrementX = Integer.signum(x1 - x0);
        int incrementY = Integer.signum(y1 - y0);
        int di, err, incrementErr, decrementErr;
        int x = x0;
        int y = y0;

        if (dx > dy) {
            di = dx;
            err = -dx;
            incrementErr = 2 * dy;
            decrementErr = -2 * dx;

            for (int i = 0; i <= di; i++) {
                image.setRGB(x, y, Color.BLACK.getRGB());
                x += incrementX;
                err += incrementErr;
                if (err > 0) {
                    y += incrementY;
                    err += decrementErr;
                }
            }
        } else {
            di = dy;
            err = -dy;
            incrementErr = 2 * dx;
            decrementErr = -2 * dy;

            for (int i = 0; i <= di; i++) {
                image.setRGB(x, y, Color.BLACK.getRGB());
                y += incrementY;
                err += incrementErr;
                if (err > 0) {
                    x += incrementX;
                    err += decrementErr;
                }
            }
        }
    }

    public boolean getImpacts() {
        return impacts;
    }

    public void setInactive(boolean x) {
        isRunning = x;
    }
}


package ru.nsu.fit.g16203.galios.isolines.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LegendPanel extends JPanel {

    private MyPanel parent;
    private Color[] defaultColors = {Color.GREEN, Color.CYAN, Color.ORANGE, Color.BLUE};
    private Color[] colors;
    private int legendWidth = 750;
    private int legendHeight = 100;
    private double minFunction, maxFunction;
    private BufferedImage legendImage;
    private boolean interpolation = false;

    LegendPanel(MyPanel panel) {
        parent = panel;
        setPreferredSize(new Dimension(legendWidth, legendHeight));
        colors = defaultColors;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (legendWidth != width || legendHeight != height || this.getX() != x || this.getY() != y) {
            super.setBounds(x, y, width, height);
            legendHeight = height;
            legendWidth = width;
            drawLegend();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (legendImage != null) {
            g2.drawImage(legendImage, null, null);
        }
    }

    void drawLegend() {

        legendImage = new BufferedImage(legendWidth, legendHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) legendImage.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, legendWidth - 1, legendHeight - 20);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, legendWidth - 1, legendHeight - 20);

        int colorAmount = colors.length;
        double prevPos = 0;
        double dc = legendWidth / (double) colorAmount;
        double nextPos = dc;
        double dm = (maxFunction - minFunction) / colorAmount;
        double current = minFunction;

        int fontHeight = 16;
        g2.setFont(new Font("ComicSans", Font.BOLD, fontHeight));
        FontMetrics fontMetrics = g2.getFontMetrics();

        for (int i = 0; i < colorAmount; ++i) {
            int prev = (int) Math.round(prevPos);
            int next = (int) Math.round(nextPos);
            g2.setColor(Color.BLACK);
            g2.drawLine(next, 0, next, legendHeight - 20);

            if (interpolation) {
                Color color = colors[i];
                Color oldColor;
                if (i > 0) {
                    oldColor = colors[i - 1];
                } else {
                    oldColor = color;
                }

                int end = (int) Math.round(prevPos + (nextPos - prevPos) / 2d);
                int diff = (int) Math.round(nextPos - prevPos);

                double diffRed = (color.getRed() - oldColor.getRed()) / 2d;
                double diffGreen = (color.getGreen() - oldColor.getGreen()) / 2d;
                double diffBlue = (color.getBlue() - oldColor.getBlue()) / 2d;

                double dr = (color.getRed() - oldColor.getRed()) / (double) diff;
                double dg = (color.getGreen() - oldColor.getGreen()) / (double) diff;
                double db = (color.getBlue() - oldColor.getBlue()) / (double) diff;

                double red = oldColor.getRed() + diffRed;
                double green = oldColor.getGreen() + diffGreen;
                double blue = oldColor.getBlue() + diffBlue;

                for (int x = prev; x < end; ++x) {
                    g2.setColor(new Color(Math.max(0, Math.min(255, (int) red)), Math.max(0, Math.min(255, (int) green)), Math.max(0, Math.min(255, (int) blue))));
                    red += dr;
                    green += dg;
                    blue += db;
                    g2.drawLine(x, 0, x, legendHeight - 20);
                }

                Color newColor;
                if (i < colorAmount - 1) {
                    newColor = colors[i + 1];
                } else {
                    newColor = color;
                }

                dr = (newColor.getRed() - color.getRed()) / (double) diff;
                dg = (newColor.getGreen() - color.getGreen()) / (double) diff;
                db = (newColor.getBlue() - color.getBlue()) / (double) diff;

                red = color.getRed();
                green = color.getGreen();
                blue = color.getBlue();

                for (int x = end; x < next; ++x) {
                    g2.setColor(new Color(Math.max(0, Math.min(255, (int) red)), Math.max(0, Math.min(255, (int) green)), Math.max(0, Math.min(255, (int) blue))));
                    red += dr;
                    green += dg;
                    blue += db;
                    g2.drawLine(x, 0, x, legendHeight - 20);
                }
            } else {
                parent.span(legendImage, prev + 1, legendHeight / 2, colors[i].getRGB());
            }
            if (i < colorAmount - 1) {
                g2.setColor(Color.BLACK);
                current += dm;
                int widthOffset = fontMetrics.stringWidth(String.format("%.1f", current)) / 2;

                g2.drawString(String.format("%.1f", current), next - widthOffset, legendHeight - 4);
            }
            prevPos = nextPos;
            nextPos += dc;
        }

    }

    void setFunction(double min, double max) {
        minFunction = min;
        maxFunction = max;
    }

    void setInterpolation() {
        interpolation = !interpolation;
        drawLegend();
    }

    void setColors(Color[] colors) {
        this.colors = colors;
        drawLegend();
    }

    Color getInterpolatedColor(double part) {

        int x = (int) (part * legendWidth);
        return new Color(legendImage.getRGB(Math.max(0, Math.min(legendWidth - 1, x)), legendHeight / 2));

    }
}

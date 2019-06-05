package ru.nsu.fit.g16203.galios.panels;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EmissionZone extends JPanel {

    private BufferedImage graph;

    EmissionZone() {
        setPreferredSize(new Dimension(500, 150));
        setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1, 5, 3, true));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (graph != null) {
            g2.drawImage(graph, null, null);
        }
    }

    void drawGraph(Pair[] points) {

        graph = new BufferedImage(500, 150, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < graph.getWidth(); ++i) {
            for (int j = 0; j < graph.getHeight(); ++j) {
                graph.setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
            }
        }
        Graphics2D g2 = graph.createGraphics();

        if (points != null) {
            int x1 = 0, x2, yRed = graph.getHeight() - 1, yGreen = graph.getHeight() - 1, yBlue = graph.getHeight() - 1;
            int factor = 5;

            for (Pair point : points) {
                x2 = (int) point.getKey() * factor;
                Color color = (Color) point.getValue();

                int red = graph.getHeight() - 1 - (int) ((double) color.getRed() / 255d * 100d);
                int green = graph.getHeight() - 3 - (int) ((double) color.getGreen() / 255d * 100d);
                int blue = graph.getHeight() - 5 - (int) ((double) color.getBlue() / 255d * 100d);

                g2.setColor(Color.RED);
                g2.drawLine(x1, yRed, x2, red);
                g2.setColor(Color.GREEN);
                g2.drawLine(x1 + 2, yGreen, x2 + 2, green);
                g2.setColor(Color.BLUE);
                g2.drawLine(x1 + 4, yBlue, x2 + 4, blue);

                x1 = x2;
                yRed = red;
                yGreen = green;
                yBlue = blue;
            }
        }

    }

}

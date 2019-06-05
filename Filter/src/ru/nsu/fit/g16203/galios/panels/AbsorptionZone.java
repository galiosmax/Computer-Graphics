package ru.nsu.fit.g16203.galios.panels;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AbsorptionZone extends JPanel {

    private BufferedImage graph;

    AbsorptionZone() {

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

        g2.setColor(Color.BLACK);

        if (points != null) {
            int x1 = 0, x2, y1 = graph.getHeight() - 1, y2;
            int factor = 5;

            for (Pair point : points) {
                x2 = (int) point.getKey() * factor;
                y2 = graph.getHeight() - 1 - (int) ((double) point.getValue() * 100);

                g2.drawLine(x1, y1, x2, y2);
                x1 = x2;
                y1 = y2;
            }
        }
    }

}

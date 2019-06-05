package ru.nsu.fit.g16203.galios.panels;

import ru.nsu.fit.g16203.galios.resources.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class ZoneA extends JPanel {

    private MyPanel parent;

    private BufferedImage image;

    private Point x, y;
    private boolean drawRect = false;
    private boolean listenerActive = false;

    ZoneA(MyPanel panel) {
        parent = panel;

        setPreferredSize(new Dimension(Parameters.size, Parameters.size));
        setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1, 5, 3, true));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (listenerActive)
                    drawDashedRect(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (listenerActive)
                    drawDashedRect(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (listenerActive) {
                    repaint();
                }
            }
        });
    }

    private void drawDashedRect(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (image != null) {
            Point[] points = parent.getRectCoordinates(x, y);
            this.x = points[0];
            this.y = points[1];
            drawRect = true;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, null, null);
            if (drawRect) {
                g2.setXORMode(Color.WHITE);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5f, 5f}, 0f));
                g2.drawRect(x.x, x.y, y.x - x.x, y.y - x.y);
            }
        } else {
            removeAll();
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        drawRect = false;
    }

    void setActive() {
        listenerActive = !listenerActive;
        if (!listenerActive) {
            drawRect = false;
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}

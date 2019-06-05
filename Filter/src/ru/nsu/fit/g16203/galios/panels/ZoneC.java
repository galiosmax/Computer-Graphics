package ru.nsu.fit.g16203.galios.panels;

import ru.nsu.fit.g16203.galios.resources.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ZoneC extends JPanel {

    private MyPanel parent;

    private final int size = 350;
    private BufferedImage image;

    private boolean isDragged = false;

    ZoneC(MyPanel panel) {
        parent = panel;

        setPreferredSize(new Dimension(size, size));
        setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1, 5, 3, true));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (image != null) {
                    isDragged = true;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragged) {
                    if (e.getX() < -Parameters.gap && e.getX() > -size - Parameters.gap && e.getY() > 0 && e.getY() < size) {
                        parent.copyCToB();
                    }
                    isDragged = false;
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, null, null);
        } else {
            removeAll();
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        parent.setImageToSave(this.image);
    }

    public BufferedImage getImage() {
        return image;
    }
}

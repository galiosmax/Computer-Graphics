package ru.nsu.fit.g16203.galios.isolines.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class MyPanel extends JPanel {

    private MainFrame mainFrame;
    private FunctionPanel functionPanel;
    private LegendPanel legendPanel;
    private int width, height;

    MyPanel(MainFrame frame) {
        mainFrame = frame;
        setLayout(new FlowLayout());

        functionPanel = new FunctionPanel(this);
        legendPanel = new LegendPanel(this);
        functionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(functionPanel);
        add(legendPanel);

        functionPanel.calcMinMaxFunc();
        legendPanel.setFunction(functionPanel.getMinFunction(), functionPanel.getMaxFunction());
        legendPanel.drawLegend();
        functionPanel.redraw();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (this.width != width || this.height != height || this.getX() != x || this.getY() != y) {
            super.setBounds(x, y, width, height);
            setPreferredSize(new Dimension(width, height));
            functionPanel.setBounds(x, y, width - 50, height - 150);
            functionPanel.setPreferredSize(new Dimension(width - 50, height - 150));
            legendPanel.setBounds(x, y, width - 50, 100);
            legendPanel.setPreferredSize(new Dimension(width - 50, 100));
            this.width = width;
            this.height = height;
            repaint();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    void span(BufferedImage image, int x0, int y0, int newColor) {
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x0, y0));
        Point point;
        int oldColor = new Color(image.getRGB(x0, y0)).getRGB();

        while (!stack.empty()) {
            point = stack.pop();
            int y = point.y;
            int x = point.x;
            if (image.getRGB(x, y) == oldColor) {

                while (image.getRGB(x, y) == oldColor && x < image.getWidth() - 1 && y < image.getHeight() - 1) {
                    x--;
                }
                x++;

                boolean down = true;
                boolean up = true;
                if (x > 0 && y > 0 && x < image.getWidth() && y < image.getHeight()) {
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
    }

    void clear() {
        functionPanel.clear();
        repaint();
    }

    Color getInterpolatedColor(double part) {
        return legendPanel.getInterpolatedColor(part);
    }

    void setGrid(int x, int y) {
        if (x >= 10 && x <= 100 && y >= 10 && y <= 100) {
            functionPanel.setValueK(x);
            functionPanel.setValueM(y);

            legendPanel.setFunction(functionPanel.getMinFunction(), functionPanel.getMaxFunction());
            legendPanel.drawLegend();
            functionPanel.redraw();
        }
    }

    void setLevels(Color[] colors) {
        legendPanel.setColors(colors);
        functionPanel.setColors(colors);
        repaint();
    }

    void setIsolineColor(Color isolineColor) {
        functionPanel.setIsolineColor(isolineColor);
    }

    void setStatusText(String text) {
        mainFrame.setStatusText(text);
    }

    void interpolationActive() {
        legendPanel.setInterpolation();
        functionPanel.setInterpolation();
        repaint();
    }

    void isolinesActive() {
        functionPanel.setIsolines();
        repaint();
    }

    void gridActive() {
        functionPanel.setGrid();
        repaint();
    }

    void interactionActive() {
        functionPanel.setInteraction();
        repaint();
    }

    void dotsActive() {
        functionPanel.setDots();
        repaint();
    }

    void settings() {

        JFrame settingsFrame = new JFrame("Settings");
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Isolines Parameters"));

        JPanel aPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel bPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel cPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel dPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel kPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel mPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel buttons = new JPanel();

        JLabel aLabel = new JLabel("X Min");
        JLabel bLabel = new JLabel("X Max");
        JLabel cLabel = new JLabel("Y Min");
        JLabel dLabel = new JLabel("Y Max");
        JLabel kLabel = new JLabel("Grid X");
        JLabel mLabel = new JLabel("Grid Y");

        JTextField aField = new JTextField();
        JTextField bField = new JTextField();
        JTextField cField = new JTextField();
        JTextField dField = new JTextField();
        JTextField kField = new JTextField();
        JTextField mField = new JTextField();

        JSlider aSlider = new JSlider(-10000, -100);
        JSlider bSlider = new JSlider(100, 10000);
        JSlider cSlider = new JSlider(-10000, -100);
        JSlider dSlider = new JSlider(100, 10000);
        JSlider kSlider = new JSlider(10, 100);
        JSlider mSlider = new JSlider(10, 100);

        aField.setText(String.valueOf(functionPanel.getValueA()));
        bField.setText(String.valueOf(functionPanel.getValueB()));
        cField.setText(String.valueOf(functionPanel.getValueC()));
        dField.setText(String.valueOf(functionPanel.getValueD()));
        kField.setText(String.valueOf(functionPanel.getValueK()));
        mField.setText(String.valueOf(functionPanel.getValueM()));

        aSlider.setValue((int) functionPanel.getValueA() * 100);
        bSlider.setValue((int) functionPanel.getValueB() * 100);
        cSlider.setValue((int) functionPanel.getValueC() * 100);
        dSlider.setValue((int) functionPanel.getValueD() * 100);
        kSlider.setValue(functionPanel.getValueK());
        mSlider.setValue(functionPanel.getValueM());

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        aPanel.add(aLabel);
        aPanel.add(aField);
        bPanel.add(bLabel);
        bPanel.add(bField);
        cPanel.add(cLabel);
        cPanel.add(cField);
        dPanel.add(dLabel);
        dPanel.add(dField);
        kPanel.add(kLabel);
        kPanel.add(kField);
        mPanel.add(mLabel);
        mPanel.add(mField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(aPanel);
        panel.add(aSlider);
        panel.add(bPanel);
        panel.add(bSlider);
        panel.add(cPanel);
        panel.add(cSlider);
        panel.add(dPanel);
        panel.add(dSlider);
        panel.add(kPanel);
        panel.add(kSlider);
        panel.add(mPanel);
        panel.add(mSlider);
        panel.add(buttons);

        aField.addActionListener(e -> {
            double val = getValue(aField, -100, -1, functionPanel.getValueA());
            aSlider.setValue((int) val * 100);
        });
        bField.addActionListener(e -> {
            double val = getValue(bField, 1, 100, functionPanel.getValueB());
            bSlider.setValue((int) val * 100);
        });
        cField.addActionListener(e -> {
            double val = getValue(cField, -100, -1, functionPanel.getValueC());
            cSlider.setValue((int) val * 100);
        });
        dField.addActionListener(e -> {
            double val = getValue(dField, 1, 100, functionPanel.getValueD());
            dSlider.setValue((int) val * 100);
        });
        kField.addActionListener(e -> {
            int val = getValue(kField, 10, 100, functionPanel.getValueK());
            kSlider.setValue(val);
        });
        mField.addActionListener(e -> {
            int val = getValue(mField, 10, 100, functionPanel.getValueM());
            mSlider.setValue(val);
        });

        aSlider.addChangeListener(e -> aField.setText(String.valueOf(aSlider.getValue() / 100f)));
        bSlider.addChangeListener(e -> bField.setText(String.valueOf(bSlider.getValue() / 100f)));
        cSlider.addChangeListener(e -> cField.setText(String.valueOf(cSlider.getValue() / 100f)));
        dSlider.addChangeListener(e -> dField.setText(String.valueOf(dSlider.getValue() / 100f)));
        kSlider.addChangeListener(e -> kField.setText(String.valueOf(kSlider.getValue())));
        mSlider.addChangeListener(e -> mField.setText(String.valueOf(mSlider.getValue())));

        okButton.addActionListener(e -> {
            double a = getValue(aField, -100, -1, functionPanel.getValueA());
            double b = getValue(bField, 1, 100, functionPanel.getValueB());
            double c = getValue(cField, -100, -1, functionPanel.getValueC());
            double d = getValue(dField, 1, 100, functionPanel.getValueD());
            int k = getValue(kField, 10, 100, functionPanel.getValueK());
            int m = getValue(mField, 10, 100, functionPanel.getValueM());

            functionPanel.setValueA(a);
            functionPanel.setValueB(b);
            functionPanel.setValueC(c);
            functionPanel.setValueD(d);
            functionPanel.setValueK(k);
            functionPanel.setValueM(m);

            legendPanel.setFunction(functionPanel.getMinFunction(), functionPanel.getMaxFunction());
            legendPanel.drawLegend();
            functionPanel.redraw();

            settingsFrame.setVisible(false);
            settingsFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            settingsFrame.setVisible(false);
            settingsFrame.dispose();
        });

        settingsFrame.add(panel);
        settingsFrame.pack();
        settingsFrame.setVisible(true);

    }

    private int getValue(JTextField textField, int minVal, int maxVal, int defaultValue) {

        int val;
        try {
            val = Integer.parseInt(textField.getText());
            if (val < minVal || val > maxVal) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            val = defaultValue;
        }
        return val;
    }

    private double getValue(JTextField textField, double minVal, double maxVal, double defaultValue) {

        double val;
        try {
            val = Double.parseDouble(textField.getText());
            if (val < minVal || val > maxVal) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            val = defaultValue;
        }
        return val;
    }

}


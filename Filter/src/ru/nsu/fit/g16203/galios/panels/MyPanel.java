package ru.nsu.fit.g16203.galios.panels;

import javafx.util.Pair;
import ru.nsu.fit.g16203.galios.filters.*;
import ru.nsu.fit.g16203.galios.resources.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyPanel extends JPanel {

    private MainFrame parent;

    private int width;
    private int height;

    private ZoneA zoneA;
    private ZoneB zoneB;
    private ZoneC zoneC;
    private AbsorptionZone absorptionZone;
    private EmissionZone emissionZone;

    private BufferedImage imageA;

    private BufferedImage originalImage;

    MyPanel(MainFrame frame) {

        parent = frame;

        setLayout(new FlowLayout());

        JPanel imagePanel = new JPanel(new GridLayout(1, 3, Parameters.gap, Parameters.gap));
        JPanel graphicsPanel = new JPanel(new GridLayout(1, 2, Parameters.gap, Parameters.gap));

        setPreferredSize(new Dimension(Parameters.size * 3 + 100, Parameters.size + 100));

        zoneA = new ZoneA(this);
        zoneB = new ZoneB(this);
        zoneC = new ZoneC(this);
        absorptionZone = new AbsorptionZone();
        emissionZone = new EmissionZone();

        imagePanel.add(zoneA);
        imagePanel.add(zoneB);
        imagePanel.add(zoneC);
        graphicsPanel.add(absorptionZone);
        graphicsPanel.add(emissionZone);

        add(imagePanel);
        add(graphicsPanel);
    }

    public void setImage(BufferedImage image) {
        originalImage = image;

        width = originalImage.getWidth();
        height = originalImage.getHeight();

        if (width > Parameters.size || height > Parameters.size) {
            if (width > height) {
                imageA = new BufferedImage(Parameters.size, (int) ((double) height / (double) width * Parameters.size), BufferedImage.TYPE_INT_RGB);
            } else {
                imageA = new BufferedImage((int) ((double) width / (double) height * Parameters.size), Parameters.size, BufferedImage.TYPE_INT_RGB);
            }
        } else {
            imageA = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g = imageA.createGraphics();
        g.drawImage(originalImage, 0, 0, imageA.getWidth(), imageA.getHeight(), null);
        g.dispose();

        zoneA.setImage(imageA);
        repaint();
    }

    void clear() {
        originalImage = null;
        zoneA.setImage(null);
        zoneB.setImage(null);
        zoneC.setImage(null);
        repaint();
    }

    Point[] getRectCoordinates(int x, int y) {
        int origX = (int) ((double) x * (double) width / (double) imageA.getWidth());
        int origY = (int) ((double) y * (double) height / (double) imageA.getHeight());
        int half = Parameters.size / 2;

        double ratio = (double) imageA.getWidth() / (double) width;

        Point[] points = new Point[2];

        if (width < Parameters.size && height < Parameters.size) {
            points[0] = new Point(0, 0);
            points[1] = new Point(width, height);
        } else if (width > Parameters.size && height < Parameters.size) {
            int minusHalf = origX - half;
            int plusHalf = origX + half;
            if (minusHalf >= 0 && plusHalf < width) {
                points[0] = new Point(minusHalf, 0);
                points[1] = new Point(plusHalf, height - 1);
            } else if (minusHalf < 0) {
                points[0] = new Point(0, 0);
                points[1] = new Point(Parameters.size - 1, height - 1);
            } else {
                points[0] = new Point(width - Parameters.size - 1, 0);
                points[1] = new Point(width - 1, height - 1);
            }
        } else if (width < Parameters.size) {
            int minusHalf = origY - half;
            int plusHalf = origY + half;
            if (minusHalf >= 0 && plusHalf < height) {
                points[0] = new Point(0, minusHalf);
                points[1] = new Point(width - 1, plusHalf);
            } else if (minusHalf < 0) {
                points[0] = new Point(0, 0);
                points[1] = new Point(width - 1, Parameters.size - 1);
            } else {
                points[0] = new Point(0, height - Parameters.size - 1);
                points[1] = new Point(width - 1, height - 1);
            }
        } else {
            int minusHalfX = origX - half;
            int plusHalfX = origX + half;
            int minusHalfY = origY - half;
            int plusHalfY = origY + half;

            if (minusHalfX >= 0 && plusHalfX < width && minusHalfY >= 0 && plusHalfY < height) {
                points[0] = new Point(minusHalfX, minusHalfY);
                points[1] = new Point(plusHalfX, plusHalfY);
            } else if (minusHalfX < 0) {
                if (minusHalfY >= 0 && plusHalfY < height) {
                    points[0] = new Point(0, minusHalfY);
                    points[1] = new Point(Parameters.size - 1, plusHalfY);
                } else if (minusHalfY < 0) {
                    points[0] = new Point(0, 0);
                    points[1] = new Point(Parameters.size - 1, Parameters.size - 1);
                } else {
                    points[0] = new Point(0, height - Parameters.size - 1);
                    points[1] = new Point(Parameters.size - 1, height - 1);
                }
            } else if (plusHalfX >= width) {
                if (minusHalfY >= 0 && plusHalfY < height) {
                    points[0] = new Point(width - Parameters.size - 1, minusHalfY);
                    points[1] = new Point(width - 1, plusHalfY);
                } else if (minusHalfY < 0) {
                    points[0] = new Point(width - Parameters.size - 1, 0);
                    points[1] = new Point(width - 1, Parameters.size);
                } else {
                    points[0] = new Point(width - Parameters.size - 1, height - Parameters.size - 1);
                    points[1] = new Point(width - 1, height - 1);
                }
            } else if (minusHalfY < 0) {
                points[0] = new Point(minusHalfX, 0);
                points[1] = new Point(plusHalfX, Parameters.size);
            } else {
                points[0] = new Point(minusHalfX, height - Parameters.size - 1);
                points[1] = new Point(plusHalfX, height - 1);
            }
        }
        zoneB.setImage(originalImage.getSubimage(points[0].x, points[0].y, points[1].x - points[0].x, points[1].y - points[0].y));

        points[0].x = (int) (points[0].getX() * ratio);
        points[0].y = (int) (points[0].getY() * ratio);
        points[1].x = (int) (points[1].getX() * ratio);
        points[1].y = (int) (points[1].getY() * ratio);
        repaint();
        return points;
    }

    void setActive() {
        zoneA.setActive();
    }

    void copyBToC() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            zoneC.setImage(imageB);
        }
        repaint();
    }

    void copyCToB() {
        BufferedImage imageC = zoneC.getImage();
        if (imageC != null) {
            zoneB.setImage(imageC);
        }
        repaint();
    }

    void setImageToSave(BufferedImage image) {
        parent.setImageToSave(image);
    }

    void grayScale() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = GrayScale.getGrayScale(imageB);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void invert() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = Invert.getInverted(imageB);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void floyd() {

        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame ditheringFrame = new JFrame("Floyd-Steinberg dithering");
        JPanel panel = new JPanel(new FlowLayout());

        JPanel fields = new JPanel(new GridLayout(3, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Color levels"));
        JPanel buttons = new JPanel();

        JLabel redLabel = new JLabel("Red Levels");
        JLabel greenLabel = new JLabel("Green Levels");
        JLabel blueLabel = new JLabel("Blue Levels");

        JTextField redField = new JTextField();
        JTextField greenField = new JTextField();
        JTextField blueField = new JTextField();

        redField.setText(String.valueOf(Parameters.defaultNumberRed));
        greenField.setText(String.valueOf(Parameters.defaultNumberGreen));
        blueField.setText(String.valueOf(Parameters.defaultNumberBlue));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(redLabel);
        fields.add(redField);
        fields.add(greenLabel);
        fields.add(greenField);
        fields.add(blueLabel);
        fields.add(blueField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(buttons);

        okButton.addActionListener(e -> {

            int redVal = getValue(redField, 1, 255, Parameters.defaultNumberRed);
            int greenVal = getValue(greenField, 1, 255, Parameters.defaultNumberGreen);
            int blueVal = getValue(blueField, 1, 255, Parameters.defaultNumberBlue);

            Parameters.defaultNumberRed = redVal;
            Parameters.defaultNumberGreen = greenVal;
            Parameters.defaultNumberBlue = blueVal;

            BufferedImage imageC = Dithering.getFloyd(imageB, redVal, greenVal, blueVal);
            zoneC.setImage(imageC);

            ditheringFrame.setVisible(false);
            ditheringFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            ditheringFrame.setVisible(false);
            ditheringFrame.dispose();
        });

        ditheringFrame.add(panel);
        ditheringFrame.pack();
        ditheringFrame.setVisible(true);
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

    private float getValue(JTextField textField, float minVal, float maxVal, float defaultValue) {

        float val;
        try {
            val = Float.parseFloat(textField.getText());
            if (val < minVal || val > maxVal) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            val = defaultValue;
        }
        return val;
    }

    void ordered() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = Dithering.getOrdered(imageB, 8, 8, 4, 16);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void zoom() {

        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame zoomFrame = new JFrame("Zoom");
        JPanel panel = new JPanel(new FlowLayout());

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Zoom parameters"));
        JPanel buttons = new JPanel();

        JLabel zoomLabel = new JLabel("Zoom Level");

        JTextField zoomField = new JTextField();
        zoomField.setText(String.valueOf(Parameters.defaultZoom));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(zoomLabel);
        fields.add(zoomField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(buttons);

        okButton.addActionListener(e -> {
            int zoomVal = getValue(zoomField, Parameters.zoomMin, Parameters.zoomMax, Parameters.defaultZoom);

            Parameters.defaultZoom = zoomVal;

            BufferedImage imageC = Zoom.getZoomed(imageB, zoomVal);
            zoneC.setImage(imageC);

            zoomFrame.setVisible(false);
            zoomFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            zoomFrame.setVisible(false);
            zoomFrame.dispose();
        });

        zoomFrame.add(panel);
        zoomFrame.pack();
        zoomFrame.setVisible(true);
    }

    private void roberts(int level) {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage grayScale = GrayScale.getGrayScale(imageB);
            BufferedImage imageC = Roberts.getRoberts(grayScale, level);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    private void sobel(int level) {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage grayScale = GrayScale.getGrayScale(imageB);
            BufferedImage imageC = Sobel.getSobel(grayScale, level);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void diffFilter(boolean filter) {

        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame diffFrame = new JFrame("Diff filter");
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Diff parameters"));
        JPanel buttons = new JPanel();

        JLabel diffLabel = new JLabel("Diff Level");

        JTextField diffField = new JTextField();
        JSlider diffSlider = new JSlider(0, 255);

        if (filter) {
            diffField.setText(String.valueOf(Parameters.defaultRoberts));
            diffSlider.setValue(Parameters.defaultRoberts);
        } else {
            diffField.setText(String.valueOf(Parameters.defaultSobel));
            diffSlider.setValue(Parameters.defaultSobel);
        }

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(diffLabel);
        fields.add(diffField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(diffSlider);
        panel.add(buttons);

        diffField.addActionListener(e -> {
            int val;
            if (filter) {
                val = getValue(diffField, 0, 255, Parameters.defaultRoberts);
            } else {
                val = getValue(diffField, 0, 255, Parameters.defaultSobel);
            }
            diffSlider.setValue(val);
        });

        diffSlider.addChangeListener(e -> diffField.setText(String.valueOf(diffSlider.getValue())));

        okButton.addActionListener(e -> {
            int level = getValue(diffField, 0, 255, Parameters.defaultSobel);
            if (filter) {
                roberts(level);
                Parameters.defaultRoberts = level;
            } else {
                sobel(level);
                Parameters.defaultSobel = level;
            }
            diffFrame.setVisible(false);
            diffFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            diffFrame.setVisible(false);
            diffFrame.dispose();
        });

        diffFrame.add(panel);
        diffFrame.pack();
        diffFrame.setVisible(true);
    }

    private void gauss(int radius) {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = Gauss.getGauss(imageB, radius);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void blurFilter() {

        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame gaussFrame = new JFrame("Gauss");
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Gauss parameters"));
        JPanel buttons = new JPanel();

        JLabel gaussLabel = new JLabel("Gauss Level");

        JTextField gaussField = new JTextField();
        JSlider gaussSlider = new JSlider(1, Parameters.gaussMax);

        gaussField.setText(String.valueOf(Parameters.defaultGauss));
        gaussSlider.setValue(Parameters.defaultGauss);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(gaussLabel);
        fields.add(gaussField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(gaussSlider);
        panel.add(buttons);

        gaussField.addActionListener(e -> {
            int val = getValue(gaussField, 0, Parameters.gaussMax, Parameters.defaultGauss);
            gaussSlider.setValue(val);
        });

        gaussSlider.addChangeListener(e -> gaussField.setText(String.valueOf(gaussSlider.getValue())));

        okButton.addActionListener(e -> {
            int radius = getValue(gaussField, 0, Parameters.gaussMax, Parameters.defaultGauss);
            gauss(radius);
            Parameters.defaultGauss = radius;
            gaussFrame.setVisible(false);
            gaussFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            gaussFrame.setVisible(false);
            gaussFrame.dispose();
        });

        gaussFrame.add(panel);
        gaussFrame.pack();
        gaussFrame.setVisible(true);
    }

    void sharpness() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = Sharpness.getSharpen(imageB);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void stamp() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage grayScale = GrayScale.getGrayScale(imageB);
            BufferedImage imageC = Stamping.getStamp(grayScale);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void watercolor() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage median = Median.getMedian(imageB);
            BufferedImage imageC = Sharpness.getSharpen(median);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    private void rotation(int angle) {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = Rotation.getRotated(imageB, angle);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void rotate() {

        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame rotationFrame = new JFrame("Rotation");
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Rotation parameters"));
        JPanel buttons = new JPanel();

        JLabel rotationLabel = new JLabel("Rotation Level");

        JTextField rotationField = new JTextField();
        JSlider rotationSlider = new JSlider(-180, 180);

        rotationField.setText(String.valueOf(Parameters.defaultAngle));
        rotationSlider.setValue(Parameters.defaultAngle);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(rotationLabel);
        fields.add(rotationField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(rotationSlider);
        panel.add(buttons);

        rotationField.addActionListener(e -> {
            int val = getValue(rotationField, -180, 180, Parameters.defaultAngle);
            rotationSlider.setValue(val);
        });

        rotationSlider.addChangeListener(e -> rotationField.setText(String.valueOf(rotationSlider.getValue())));

        okButton.addActionListener(e -> {
            int angle = getValue(rotationField, -180, 180, Parameters.defaultAngle);
            rotation(-angle);
            Parameters.defaultAngle = angle;
            rotationFrame.setVisible(false);
            rotationFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            rotationFrame.setVisible(false);
            rotationFrame.dispose();
        });

        rotationFrame.add(panel);
        rotationFrame.pack();
        rotationFrame.setVisible(true);
    }

    void gamma() {

        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame gammaFrame = new JFrame("Gamma");
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 10));
        fields.setBorder(BorderFactory.createTitledBorder("Gamma parameter"));
        JPanel buttons = new JPanel();

        JLabel gammaLabel = new JLabel("Gamma Level");

        JTextField gammaField = new JTextField();
        JSlider gammaSlider = new JSlider(1, 1000);

        gammaField.setText(String.valueOf(Parameters.defaultGamma));
        gammaSlider.setValue((int) (Parameters.defaultGamma * 100));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        fields.add(gammaLabel);
        fields.add(gammaField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(fields);
        panel.add(gammaSlider);
        panel.add(buttons);

        gammaField.addActionListener(e -> {
            float val = getValue(gammaField, 0.01f, 10f, Parameters.defaultGamma);
            gammaSlider.setValue((int) (val * 100));
        });

        gammaSlider.addChangeListener(e -> gammaField.setText(String.valueOf((gammaSlider.getValue() / 100f))));

        okButton.addActionListener(e -> {
            float gamma = getValue(gammaField, 0.01f, 10f, Parameters.defaultGamma);
            gammaCorrection(gamma);
            Parameters.defaultGamma = gamma;
            gammaFrame.setVisible(false);
            gammaFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            gammaFrame.setVisible(false);
            gammaFrame.dispose();
        });

        gammaFrame.add(panel);
        gammaFrame.pack();
        gammaFrame.setVisible(true);
    }

    private void gammaCorrection(float gamma) {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = Gamma.getCorrected(imageB, gamma);
            zoneC.setImage(imageC);
        }
        repaint();
    }

    void drawAbsorption(Pair[] points) {
        absorptionZone.drawGraph(points);
        VolumeRendering.setAbsorption(points);
        repaint();
    }

    void drawEmission(Pair[] points) {
        emissionZone.drawGraph(points);
        VolumeRendering.setEmission(points);
        repaint();
    }

    void placeCharges(Pair[] points) {
        VolumeRendering.setCharges(points);
    }

    void absorptionActive() {
        VolumeRendering.absorptionActive();
    }

    void emissionActive() {
        VolumeRendering.emissionActive();
    }

    void render() {
        BufferedImage imageB = zoneB.getImage();
        if (imageB == null) {
            return;
        }

        JFrame volumeFrame = new JFrame("Volume Rendering");
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Volume rendering parameters"));

        JPanel xPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel yPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel zPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel buttons = new JPanel();

        JLabel xLabel = new JLabel("Nx");
        JLabel yLabel = new JLabel("Ny");
        JLabel zLabel = new JLabel("Nz");

        JTextField xField = new JTextField();
        JTextField yField = new JTextField();
        JTextField zField = new JTextField();

        JSlider xSlider = new JSlider(1, 350);
        JSlider ySlider = new JSlider(1, 350);
        JSlider zSlider = new JSlider(1, 350);

        xField.setText(String.valueOf(Parameters.defaultVolumeX));
        yField.setText(String.valueOf(Parameters.defaultVolumeY));
        zField.setText(String.valueOf(Parameters.defaultVolumeZ));

        xSlider.setValue((Parameters.defaultVolumeX));
        ySlider.setValue((Parameters.defaultVolumeY));
        zSlider.setValue((Parameters.defaultVolumeZ));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        xPanel.add(xLabel);
        xPanel.add(xField);
        yPanel.add(yLabel);
        yPanel.add(yField);
        zPanel.add(zLabel);
        zPanel.add(zField);

        buttons.add(okButton);
        buttons.add(cancelButton);

        panel.add(xPanel);
        panel.add(xSlider);
        panel.add(yPanel);
        panel.add(ySlider);
        panel.add(zPanel);
        panel.add(zSlider);
        panel.add(buttons);

        xField.addActionListener(e -> {
            int val = getValue(xField, 1, 350, Parameters.defaultVolumeX);
            xSlider.setValue((val));
        });
        yField.addActionListener(e -> {
            int val = getValue(yField, 1, 350, Parameters.defaultVolumeY);
            ySlider.setValue((val));
        });
        zField.addActionListener(e -> {
            int val = getValue(zField, 1, 350, Parameters.defaultVolumeZ);
            zSlider.setValue((val));
        });

        xSlider.addChangeListener(e -> xField.setText(String.valueOf((xSlider.getValue()))));
        ySlider.addChangeListener(e -> yField.setText(String.valueOf((ySlider.getValue()))));
        zSlider.addChangeListener(e -> zField.setText(String.valueOf((zSlider.getValue()))));

        okButton.addActionListener(e -> {
            int Nx = getValue(xField, 1, 350, Parameters.defaultVolumeX);
            int Ny = getValue(yField, 1, 350, Parameters.defaultVolumeY);
            int Nz = getValue(zField, 1, 350, Parameters.defaultVolumeZ);
            renderVolume(Nx, Ny, Nz);

            Parameters.defaultVolumeX = Nx;
            Parameters.defaultVolumeY = Ny;
            Parameters.defaultVolumeZ = Nz;

            volumeFrame.setVisible(false);
            volumeFrame.dispose();

            repaint();
        });

        cancelButton.addActionListener(e -> {
            volumeFrame.setVisible(false);
            volumeFrame.dispose();
        });

        volumeFrame.add(panel);
        volumeFrame.pack();
        volumeFrame.setVisible(true);
    }

    private void renderVolume(int Nx, int Ny, int Nz) {
        BufferedImage imageB = zoneB.getImage();
        if (imageB != null) {
            BufferedImage imageC = VolumeRendering.render(imageB, Nx, Ny, Nz);
            zoneC.setImage(imageC);
        }
        repaint();
    }
}

package ru.nsu.fit.g16203.galios.raytracing.panels;

import ru.nsu.fit.g16203.galios.raytracing.parameters.Quality;
import ru.nsu.fit.g16203.galios.raytracing.parameters.RenderParameters;

import javax.swing.*;
import java.awt.*;

class SettingsPanel extends JPanel {

    private RenderParameters renderParameters;
    private MainFrame parent;

    SettingsPanel(RenderParameters renderParameters, MainFrame mainFrame) {

        this.renderParameters = renderParameters;
        this.parent = mainFrame;
        JFrame frame = new JFrame("Settings");

        JPanel clippingPanel = createClippingPanel();
        JPanel colorPanel = createColorPanel();
        JPanel gammaPanel = createGammaPanel();
        JPanel depthPanel = createDepthPanel();
        JPanel qualityPanel = createQualityPanel();
        JPanel buttonPanel = createButtonPanel(frame);

        add(clippingPanel);
        add(colorPanel);
        add(gammaPanel);
        add(depthPanel);
        add(qualityPanel);
        add(buttonPanel);

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private JPanel createButtonPanel(JFrame frame) {

        JPanel buttons = new JPanel();

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
        });

        buttons.add(okButton);
        return buttons;
    }

    private JPanel createQualityPanel() {

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Quality"));

        JLabel roughLabel = new JLabel("ROUGH");
        JLabel normalLabel = new JLabel("NORMAL");
        JLabel fineLabel = new JLabel("FINE");

        Quality quality = renderParameters.getQuality();

        JRadioButton roughButton = new JRadioButton();
        JRadioButton normalButton = new JRadioButton();
        JRadioButton fineButton = new JRadioButton();

        ButtonGroup group = new ButtonGroup();
        group.add(roughButton);
        group.add(normalButton);
        group.add(fineButton);

        switch (quality) {
            case FINE: {
                fineButton.setSelected(true);
                break;
            }
            case NORMAL: {
                normalButton.setSelected(true);
                break;
            }
            case ROUGH: {
                roughButton.setSelected(true);
                break;
            }
            default: {
                break;
            }
        }

        fineButton.addActionListener(e -> renderParameters.setQuality(Quality.FINE));
        normalButton.addActionListener(e -> renderParameters.setQuality(Quality.NORMAL));
        roughButton.addActionListener(e -> renderParameters.setQuality(Quality.ROUGH));

        panel.add(roughLabel);
        panel.add(roughButton);
        panel.add(normalLabel);
        panel.add(normalButton);
        panel.add(fineLabel);
        panel.add(fineButton);

        return panel;

    }

    private JPanel createGammaPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Gamma"));

        JLabel gammaLabel = new JLabel("Gamma");
        SpinnerModel gammaModel = new SpinnerNumberModel(renderParameters.getGamma(), 0, 10, 0.01);
        JSpinner gammaSpinner = new JSpinner(gammaModel);

        gammaSpinner.addChangeListener(e -> {
            renderParameters.setGamma((double) gammaSpinner.getValue());
            parent.redraw();
        });

        panel.add(gammaLabel);
        panel.add(gammaSpinner);
        return panel;
    }

    private JPanel createDepthPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Depth"));

        JLabel depthLabel = new JLabel("Depth");
        SpinnerModel depthModel = new SpinnerNumberModel(renderParameters.getDepth(), 0, 5, 1);
        JSpinner depthSpinner = new JSpinner(depthModel);

        depthSpinner.addChangeListener(e -> {
            renderParameters.setDepth((int) depthSpinner.getValue());
            parent.redraw();
        });

        panel.add(depthLabel);
        panel.add(depthSpinner);
        return panel;
    }

    private JPanel createColorPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Background color"));

        JLabel redLabel = new JLabel("Red");
        JLabel greenLabel = new JLabel("Green");
        JLabel blueLabel = new JLabel("Blue");

        Color backgroundColor = renderParameters.getBackgroundColor();

        SpinnerModel redModel = new SpinnerNumberModel(backgroundColor.getRed(), 0, 255, 1);
        SpinnerModel greenModel = new SpinnerNumberModel(backgroundColor.getGreen(), 0, 255, 1);
        SpinnerModel blueModel = new SpinnerNumberModel(backgroundColor.getBlue(), 0, 255, 1);

        JSpinner redSpinner = new JSpinner(redModel);
        JSpinner greenSpinner = new JSpinner(greenModel);
        JSpinner blueSpinner = new JSpinner(blueModel);

        redSpinner.addChangeListener(e -> {
            renderParameters.setBackgroundColor(new Color((int) redSpinner.getValue(), renderParameters.getBackgroundColor().getGreen(), renderParameters.getBackgroundColor().getBlue()));
            parent.redraw();
        });

        greenSpinner.addChangeListener(e -> {
            renderParameters.setBackgroundColor(new Color(renderParameters.getBackgroundColor().getRed(), (int) greenSpinner.getValue(), renderParameters.getBackgroundColor().getBlue()));
            parent.redraw();
        });

        blueSpinner.addChangeListener(e -> {
            renderParameters.setBackgroundColor(new Color(renderParameters.getBackgroundColor().getRed(), renderParameters.getBackgroundColor().getGreen(), (int) blueSpinner.getValue()));
            parent.redraw();
        });

        panel.add(redLabel);
        panel.add(redSpinner);
        panel.add(greenLabel);
        panel.add(greenSpinner);
        panel.add(blueLabel);
        panel.add(blueSpinner);
        return panel;
    }

    private JPanel createClippingPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Clipping"));

        JLabel znLabel = new JLabel("Z near");
        JLabel zfLabel = new JLabel("Z far");

        SpinnerModel znModel = new SpinnerNumberModel(renderParameters.getZn(), 0.5, renderParameters.getZf(), 0.1);
        SpinnerModel zfModel = new SpinnerNumberModel(renderParameters.getZf(), renderParameters.getZn(), 100, 0.1);

        JSpinner znSpinner = new JSpinner(znModel);
        JSpinner zfSpinner = new JSpinner(zfModel);

        znSpinner.addChangeListener(e -> {
            renderParameters.setZn((double) znSpinner.getValue());
            if (renderParameters.getZn() == renderParameters.getZf()) {
                znSpinner.setValue(renderParameters.getZf());
            }
            SpinnerModel newZfModel = new SpinnerNumberModel(renderParameters.getZf(), renderParameters.getZn(), 100, 0.1);
            zfSpinner.setModel(newZfModel);
            parent.redraw();
        });

        zfSpinner.addChangeListener(e -> {
            renderParameters.setZf((double) zfSpinner.getValue());
            if (renderParameters.getZn() == renderParameters.getZf()) {
                zfSpinner.setValue(renderParameters.getZn());
            }
            SpinnerModel newZnModel = new SpinnerNumberModel(renderParameters.getZn(), 0.5, renderParameters.getZf(), 0.1);
            znSpinner.setModel(newZnModel);
            parent.redraw();
        });

        panel.add(znLabel);
        panel.add(znSpinner);
        panel.add(zfLabel);
        panel.add(zfSpinner);
        return panel;
    }

}

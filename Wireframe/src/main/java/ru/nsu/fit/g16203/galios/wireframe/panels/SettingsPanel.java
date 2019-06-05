package ru.nsu.fit.g16203.galios.wireframe.panels;

import ru.nsu.fit.g16203.galios.wireframe.parameters.Parameters;
import ru.nsu.fit.g16203.galios.wireframe.spline.Spline;
import ru.nsu.fit.g16203.galios.wireframe.surface.Surface;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

class SettingsPanel extends JPanel {

    private SettingsFrame parent;

    SettingsPanel(SettingsFrame frame, int num) {

        setBorder(BorderFactory.createTitledBorder("Settings"));
        setPreferredSize(new Dimension(1200, 120));
        parent = frame;
        setLayout(new GridLayout(3, 6, 3, 5));

        Parameters parameters = parent.getParameters();

        Color backgroundColor = parameters.backgroundColor;
        if (parameters.surfaces.size() == 0) {
            parameters.surfaces.add(new Surface(parameters));
        }
        if (parameters.surfaces.get(num).getBodyColor() == null) {
            Random rand = new Random();
            parameters.surfaces.get(num).setBodyColor(new Color(rand.nextInt(205) + 50, rand.nextInt(205) + 50, rand.nextInt(205) + 50));
        }
        if (parameters.surfaces.get(num).getSpline() == null) {
            parameters.surfaces.get(num).setSpline(new Spline());
        }
        Color bodyColor = parameters.surfaces.get(num).getBodyColor();

        JSpinner nSpinner = getPanel("n", parameters.n, 5, 50, 1, this);
        JSpinner mSpinner = getPanel("m", parameters.m, 5, 50, 1, this);
        JSpinner kSpinner = getPanel("k", parameters.k, 1, 25, 1, this);
        JSpinner numSpinner = getPanel("№", num, 0, parameters.surfaces.size(), 1, this);
        JSpinner backRedSpinner = getPanel("Background Red", backgroundColor.getRed(), 0, 255, 1, this);
        JSpinner bodyRedSpinner = getPanel("Body Red", bodyColor.getRed(), 0, 255, 1, this);
        JSpinner aSpinner = getPanel("a", parameters.a, 0, 1, 0.01, this);
        JSpinner bSpinner = getPanel("b", parameters.b, 0, 1, 0.01, this);
        JSpinner cSpinner = getPanel("c", parameters.c, 0, Math.PI * 2, 0.01, this);
        JSpinner dSpinner = getPanel("d", parameters.d, 0, Math.PI * 2, 0.01, this);
        JSpinner backGreenSpinner = getPanel("Background Green", backgroundColor.getGreen(), 0, 255, 1, this);
        JSpinner bodyGreenSpinner = getPanel("Body Green", bodyColor.getGreen(), 0, 255, 1, this);
        JSpinner znSpinner = getPanel("Zn", parameters.zn, 0, 100, 0.1, this);
        JSpinner zfSpinner = getPanel("Zf", parameters.zf, 0, 100, 0.1, this);
        JSpinner swSpinner = getPanel("sw", parameters.sw, 0, 100, 0.1, this);
        JSpinner shSpinner = getPanel("sh", parameters.sh, 0, 100, 0.1, this);
        JSpinner backBlueSpinner = getPanel("Background Blue", backgroundColor.getBlue(), 0, 255, 1, this);
        JSpinner bodyBlueSpinner = getPanel("Body Blue", bodyColor.getBlue(), 0, 255, 1, this);

        numSpinner.addChangeListener(e -> {
            int number = (int) numSpinner.getValue();
            if (number >= parameters.surfaces.size()) {
                parameters.surfaces.add(new Surface(parameters));
                Random rand = new Random();
                parameters.surfaces.get(number).setBodyColor(new Color(rand.nextInt(205) + 50, rand.nextInt(205) + 50, rand.nextInt(205) + 50));
                parameters.surfaces.get(number).setSpline(new Spline());
                SpinnerModel spinnerModel = new SpinnerNumberModel(number, 0, parameters.surfaces.size(), 1);
                numSpinner.setModel(spinnerModel);
            }
            Color color = parameters.surfaces.get(number).getBodyColor();
            bodyRedSpinner.setValue(color.getRed());
            bodyGreenSpinner.setValue(color.getGreen());
            bodyBlueSpinner.setValue(color.getBlue());
            parent.setSurface(number);
        });

        nSpinner.addChangeListener(e -> {
            parent.getParameters().n = (int) nSpinner.getValue();
            parent.redraw();
        });

        mSpinner.addChangeListener(e -> {
            parent.getParameters().m = (int) mSpinner.getValue();
            parent.redraw();
        });

        kSpinner.addChangeListener(e -> {
            parent.getParameters().k = (int) kSpinner.getValue();
            parent.redraw();
        });

        aSpinner.addChangeListener(e -> {
            parent.getParameters().a = (double) aSpinner.getValue();
            if (parameters.b == parameters.a) {
                bSpinner.setValue(parameters.b);
            }
            SpinnerModel bModel = new SpinnerNumberModel((double) bSpinner.getValue(), parameters.a, 2d * Math.PI, 0.01);
            bSpinner.setModel(bModel);
            parent.redraw();
        });

        bSpinner.addChangeListener(e -> {
            parent.getParameters().b = (double) bSpinner.getValue();
            if (parameters.b == parameters.a) {
                bSpinner.setValue(parameters.a);
            }
            SpinnerModel aModel = new SpinnerNumberModel((double) aSpinner.getValue(), 0, parameters.b, 0.01);
            aSpinner.setModel(aModel);
            parent.redraw();
        });

        cSpinner.addChangeListener(e -> {
            parent.getParameters().c = (double) cSpinner.getValue();
            if (parameters.c == parameters.d) {
                cSpinner.setValue(parameters.d);
            }
            SpinnerModel dModel = new SpinnerNumberModel((double) dSpinner.getValue(), parameters.c, 2d * Math.PI, 0.01);
            dSpinner.setModel(dModel);
            parent.redraw();
        });

        dSpinner.addChangeListener(e -> {
            parent.getParameters().d = (double) dSpinner.getValue();
            if (parameters.c == parameters.d) {
                dSpinner.setValue(parameters.c);
            }
            SpinnerModel cModel = new SpinnerNumberModel((double) cSpinner.getValue(), 0, parameters.d, 0.01);
            cSpinner.setModel(cModel);
            parent.redraw();
        });

        swSpinner.addChangeListener(e -> {
            parent.getParameters().sw = (double) swSpinner.getValue();
            parent.redraw();
        });

        shSpinner.addChangeListener(e -> {
            parent.getParameters().sh = (double) shSpinner.getValue();
            parent.redraw();
        });

        znSpinner.addChangeListener(e -> {
            parent.getParameters().zn = (double) znSpinner.getValue();
            parent.redraw();
        });

        zfSpinner.addChangeListener(e -> {
            parent.getParameters().zf = (double) zfSpinner.getValue();
            parent.redraw();
        });

        backRedSpinner.addChangeListener(e -> {
            Color color = parameters.backgroundColor;
            parameters.backgroundColor = new Color((int) backRedSpinner.getValue(), color.getGreen(), color.getBlue());
            parent.redraw();
        });

        backGreenSpinner.addChangeListener(e -> {
            Color color = parameters.backgroundColor;
            parameters.backgroundColor = new Color(color.getRed(), (int) backGreenSpinner.getValue(), color.getBlue());
            parent.redraw();
        });

        backBlueSpinner.addChangeListener(e -> {
            Color color = parameters.backgroundColor;
            parameters.backgroundColor = new Color(color.getRed(), color.getGreen(), (int) backBlueSpinner.getValue());
            parent.redraw();
        });

        bodyRedSpinner.addChangeListener(e -> {
            Color color = parameters.surfaces.get((int) numSpinner.getValue()).getBodyColor();
            parameters.surfaces.get((int) numSpinner.getValue()).setBodyColor(new Color((int) bodyRedSpinner.getValue(), color.getGreen(), color.getBlue()));
            parent.redraw();
        });

        bodyGreenSpinner.addChangeListener(e -> {
            Color color = parameters.surfaces.get((int) numSpinner.getValue()).getBodyColor();
            parameters.surfaces.get((int) numSpinner.getValue()).setBodyColor(new Color(color.getRed(), (int) bodyGreenSpinner.getValue(), color.getBlue()));
            parent.redraw();
        });

        bodyBlueSpinner.addChangeListener(e -> {
            Color color = parameters.surfaces.get((int) numSpinner.getValue()).getBodyColor();
            parameters.surfaces.get((int) numSpinner.getValue()).setBodyColor(new Color(color.getRed(), color.getGreen(), (int) bodyBlueSpinner.getValue()));
            parent.redraw();
        });
    }

    private JSpinner getPanel(String text, int value, int min, int max, int step, JPanel mainPanel) {
        SpinnerModel spinnerModel = new SpinnerNumberModel(value, min, max, step);
        return createPanel(text, mainPanel, spinnerModel);
    }

    private JSpinner getPanel(String text, double value, double min, double max, double step, JPanel mainPanel) {
        SpinnerModel spinnerModel = new SpinnerNumberModel(value, min, max, step);
        return createPanel(text, mainPanel, spinnerModel);
    }

    private JSpinner createPanel(String text, JPanel mainPanel, SpinnerModel spinnerModel) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 3));
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        JSpinner spinner = new JSpinner(spinnerModel);
        panel.add(label);
        panel.add(spinner);
        mainPanel.add(panel);
        return spinner;
    }
}

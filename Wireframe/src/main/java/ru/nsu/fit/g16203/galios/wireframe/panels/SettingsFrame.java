package ru.nsu.fit.g16203.galios.wireframe.panels;

import ru.nsu.fit.g16203.galios.wireframe.parameters.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class SettingsFrame extends JFrame {

    private MainFrame parent;
    private Parameters parameters;
    private int num = 0;
    private GraphPanel graphPanel;
    private SettingsPanel settingsPanel;

    SettingsFrame(MainFrame frame, Parameters parameters) {
        super("Settings");
        parent = frame;
        this.parameters = parameters;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    void run() {

        JPanel panel = new JPanel(new FlowLayout());
        panel.setPreferredSize(new Dimension(1200, 960));

        settingsPanel = new SettingsPanel(this, num);
        graphPanel = new GraphPanel(this);
        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        JButton applyButton = new JButton("Apply");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(clearButton);
        
        panel.add(graphPanel);
        panel.add(settingsPanel);
        panel.add(buttonPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitListener();
            }
        });
        okButton.addActionListener(e -> okListener());
        cancelButton.addActionListener(e -> exitListener());
        applyButton.addActionListener(e -> applyListener());
        clearButton.addActionListener(e -> clearListener());
        
        add(panel);
        pack();
        this.setResizable(false);
        setVisible(true);
    }

    private void clearListener() {
        parent.clear(num);
        parameters = parent.getParameters();
        settingsPanel = new SettingsPanel(this, num);
        graphPanel.redraw();
    }

    private void applyListener() {
        redrawSurface();
    }

    private void okListener() {
        redrawSurface();
        exitListener();
    }

    private void exitListener() {
        setVisible(false);
        dispose();
    }

    Parameters getParameters() {
        return parameters;
    }

    void setSurface(int num) {
        if (num >= 0 && num < parameters.surfaces.size()) {
            this.num = num;
            graphPanel.setNum(num);
            graphPanel.redraw();
            repaint();
        }
    }

    void redraw() {
        graphPanel.redraw();
        redrawSurface();
    }

    private void redrawSurface() {
        parent.redraw();
    }

}

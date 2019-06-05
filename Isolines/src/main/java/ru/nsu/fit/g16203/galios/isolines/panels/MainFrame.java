package ru.nsu.fit.g16203.galios.isolines.panels;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainFrame extends JFrame {

    private MyPanel panel;
    private JPanel statusBar;
    private JToolBar toolBar;

    private JLabel status;

    private final String dataPath = System.getProperty("user.dir") + File.separator + "FIT_16203_Galios_Isolines_Data";
    private final String resources = "/";
    private String lastPath = dataPath;

    public MainFrame() {
        super("Isolines");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public void run() {

        panel = new MyPanel(this);
        createMenuAndToolBar();
        createStatusBar();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFileItemListener();
            }
        });
        add(panel);
        pack();
        setVisible(true);
        setPreferredSize(new Dimension(1000, 900));

    }

    private void createMenuAndToolBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Filter Toolbar");
        add(toolBar, BorderLayout.PAGE_START);

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");
        JMenu aboutMenu = new JMenu("About");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(aboutMenu);

        createFileMenuItems(fileMenu);
        createEditMenuItems(editMenu);
        createViewMenuItems(viewMenu);
        createAboutMenuItems(aboutMenu);
    }

    private void createStatusBar() {
        statusBar = new JPanel();
        status = new JLabel("Ready");
        statusBar.add(status);
        add(statusBar, BorderLayout.PAGE_END);
    }

    private void createFileMenuItems(JMenu fileMenu) {

        createMenuAndToolbarItem("Clear", "Clear", "new.png", e -> clearFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Load...", "Load config", "open.png", e -> loadFileItemListener(), fileMenu);
        fileMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Exit", "Close the window", "exit.png", e -> exitFileItemListener(), fileMenu);
        toolBar.addSeparator();
    }

    private void clearFileItemListener() {
        panel.clear();
        lastPath = dataPath;
    }

    private void loadFileItemListener() {

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "txt");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(lastPath));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (!fileChooser.getCurrentDirectory().getAbsolutePath().equals(lastPath)) {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.setFileFilter(filter);
        if (fileChooser.showDialog(null, "Открыть файл") == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(fileName);
            String extension = getExtension(fileName);
            if (extension != null && !extension.equals("txt")) {
                cantReadFile();
                return;
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Point point = readGridValues(reader);
                Color[] colors = readLevels(reader);
                Color isolineColor = readColor(reader);

                if (point == null || colors == null || isolineColor == null) {
                    throw new IOException();
                }
                panel.setGrid(point.x, point.y);
                panel.setLevels(colors);
                panel.setIsolineColor(isolineColor);

                lastPath = file.getParent();
            } catch (IOException e) {
                cantReadFile();
            }
        }
    }

    private Point readGridValues(BufferedReader reader) {
        try {
            String[] noComments = reader.readLine().split("//");
            String[] first = noComments[0].split(" ");
            if (first.length != 2) {
                throw new IOException();
            }
            int gridX = Integer.parseInt(first[0]);
            int gridY = Integer.parseInt(first[1]);
            return new Point(gridX, gridY);
        } catch (IOException | NumberFormatException ex) {
            return null;
        }
    }

    private Color[] readLevels(BufferedReader reader) {
        try {

            String[] noComments = reader.readLine().split("//");
            while (noComments[0].equals("")) {
                noComments = reader.readLine().split("//");
            }
            String[] first = noComments[0].split(" ");
            if (first.length != 1) {
                throw new IOException();
            }
            int levels = Integer.parseInt(first[0]);
            Color[] colors = new Color[levels];
            for (int i = 0; i < levels; ++i) {
                noComments = reader.readLine().split("//");
                first = noComments[0].split(" ");
                if (first.length != 3) {
                    throw new IOException();
                }
                colors[i] = new Color(Integer.parseInt(first[0]), Integer.parseInt(first[1]), Integer.parseInt(first[2]));
            }
            return colors;
        } catch (IOException | NumberFormatException ex) {
            return null;
        }
    }

    private Color readColor(BufferedReader reader) {
        try {
            String[] noComments = reader.readLine().split("//");
            String[] first = noComments[0].split(" ");
            if (first.length != 3) {
                throw new IOException();
            }
            return new Color(Integer.parseInt(first[0]), Integer.parseInt(first[1]), Integer.parseInt(first[2]));
        } catch (IOException | NumberFormatException ex) {
            return null;
        }
    }

    private void cantReadFile() {
        JOptionPane.showMessageDialog(null, "Can't read the file");
    }

    private String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        if (!extension.equals("txt")) {
            return null;
        }
        return extension;
    }

    private void exitFileItemListener() {
        setVisible(false);
        dispose();
        System.exit(0);
    }

    private void createEditMenuItems(JMenu editMenu) {
        createMenuAndToolbarItem("Settings", "Settings", "settings.png", e -> settingsEditItemListener(), editMenu);
        createCheckBoxMenuAndToolbarItem("Interpolate", "Interpolate", "interpolate.png", e -> interpolateEditItemListener(), editMenu, false);
        createCheckBoxMenuAndToolbarItem("isolines", "Isolines", "isolines.png", e -> isolinesEditItemListener(), editMenu, true);
        createCheckBoxMenuAndToolbarItem("Grid", "Grid", "grid.png", e -> gridEditItemListener(), editMenu, false);
        createCheckBoxMenuAndToolbarItem("Interact", "Interact", "interact.png", e -> interactEditItemListener(), editMenu, false);
        createCheckBoxMenuAndToolbarItem("Dots", "Dots", "dots.png", e -> dotsEditItemListener(), editMenu, false);
    }

    private void interpolateEditItemListener() {
        panel.interpolationActive();
    }

    private void isolinesEditItemListener() {
        panel.isolinesActive();
    }

    private void gridEditItemListener() {
        panel.gridActive();
    }

    private void interactEditItemListener() {
        panel.interactionActive();
    }

    private void dotsEditItemListener() {
        panel.dotsActive();
    }

    private void settingsEditItemListener() {
        panel.settings();
    }

    private void createViewMenuItems(JMenu viewMenu) {

        createCheckBoxMenuItem("Toolbar", "Remove toolbar", "toolbar.png", e -> toolbarViewItemListener(), viewMenu);
        createCheckBoxMenuItem("Status bar", "Remove status bar", "status.png", e -> statusViewItemListener(), viewMenu);
    }

    private void toolbarViewItemListener() {
        if (toolBar.isVisible()) {
            toolBar.setVisible(false);
        } else {
            toolBar.setVisible(true);
        }
    }

    private void statusViewItemListener() {
        if (statusBar.isVisible()) {
            statusBar.setVisible(false);
        } else {
            statusBar.setVisible(true);
        }
    }

    private void createAboutMenuItems(JMenu aboutMenu) {
        createMenuAndToolbarItem("About", "Open about window", "about.png", e -> aboutItemListener(), aboutMenu);
    }

    private void aboutItemListener() {

        JDialog aboutDialog = new JDialog();
        JPanel panel = new JPanel();
        JPanel info = new JPanel(new GridLayout(4, 1, 10, 10));
        aboutDialog.setTitle("About \"Isolines\" by Galios Max");

        JLabel photo = new JLabel(new ImageIcon(getClass().getResource(resources + "me.jpg")));
        JLabel progInfo = new JLabel("Program: Isolines v1.0");
        JLabel authorInfo = new JLabel("Author: Galios Maxim");
        JLabel groupInfo = new JLabel("Group: FIT 16203");
        JLabel yearInfo = new JLabel("Year: 2019");
        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.setPreferredSize(new Dimension(80, 30));

        okButton.addActionListener(e -> {
            aboutDialog.setVisible(false);
            aboutDialog.dispose();
        });

        panel.add(photo);
        info.add(progInfo);
        info.add(authorInfo);
        info.add(groupInfo);
        info.add(yearInfo);
        panel.add(info);
        panel.add(okButton);
        aboutDialog.add(panel);
        aboutDialog.pack();
        aboutDialog.setVisible(true);
    }

    private void createMenuAndToolbarItem(String name, String tipText, String imageName, ActionListener e, JMenu menu) {

        JMenuItem item = new JMenuItem(name);
        createItem(item, tipText, imageName, e, menu);
        JButton button = new JButton(item.getIcon());
        createButton(button, item);
    }

    private void createCheckBoxMenuItem(String name, String tipText, String imageName, ActionListener e, JMenu menu) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
        createItem(item, tipText, imageName, e, menu);
        item.setSelected(true);
    }

    private void createCheckBoxMenuAndToolbarItem(String name, String tipText, String imageName, ActionListener e, JMenu menu, boolean active) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
        createItem(item, tipText, imageName, e, menu);
        JToggleButton button = new JToggleButton(item.getIcon());
        createButton(button, item);
        button.addActionListener(event -> checkBoxListener(button, item));
        item.addActionListener(event -> checkBoxListener(item, button));
        button.setSelected(active);
    }

    private void createItem(JMenuItem item, String tipText, String imageName, ActionListener e, JMenu menu) {
        item.setIcon(new ImageIcon(getClass().getResource(resources + imageName)));
        item.setToolTipText(tipText);
        item.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(item.getToolTipText());
            }
        });
        item.addActionListener(e);
        menu.add(item);
    }

    private void createButton(AbstractButton button, JMenuItem item) {
        button.setToolTipText(item.getToolTipText());

        for (MouseMotionListener i : item.getMouseMotionListeners()) {
            button.addMouseMotionListener(i);
        }

        for (ActionListener i : item.getActionListeners()) {
            button.addActionListener(i);
        }
        toolBar.add(button);
    }

    private void checkBoxListener(AbstractButton first, AbstractButton second) {
        if (first.isSelected()) {
            second.setSelected(true);
            first.setSelected(true);
        } else {
            second.setSelected(false);
            first.setSelected(false);
        }
    }

    void setStatusText(String text) {
        status.setText(text);
    }

}

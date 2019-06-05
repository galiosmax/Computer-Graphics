package ru.nsu.fit.g16203.galios.panels;

import javafx.geometry.Point3D;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainFrame extends JFrame {

    private MyPanel panel;
    private JLabel status;

    private JToolBar toolBar;
    private JPanel statusBar;

    private final String dataPath = System.getProperty("user.dir") + File.separator + "FIT_16203_Galios_Filter_Data";
    private String lastPath = dataPath;
    private String lastConfigPath = dataPath;
    private File currentFile = null;
    private BufferedImage imageToSave = null;
    private boolean config = false;

    public MainFrame() {
        super("Filter");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFileItemListener();
            }
        });

        panel = new MyPanel(this);
        createMenuAndToolBar();
        createStatusBar();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1200, 600));

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText("X: " + e.getX() + "; Y: " + e.getY());
            }
        });
        add(scrollPane);
        pack();
        setVisible(true);
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

        createMenuAndToolbarItem("New", "Create new file", "new.png", e -> newFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Load...", "Open image", "open.png", e -> loadFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save", "Save image", "save.png", e -> saveFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save as...", "Save image as...", "save-as.png", e -> saveAsFileItemListener(), fileMenu);
        fileMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Exit", "Close the window", "exit.png", e -> exitFileItemListener(), fileMenu);
        toolBar.addSeparator();
    }

    private void newFileItemListener() {
        askSave();
        panel.clear();
        currentFile = null;
        imageToSave = null;
        lastPath = dataPath;
    }

    private void loadFileItemListener() {
        askSave();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "bmp", "PNG", "BMP");
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
            if (extension == null || extension.equals("txt")) {
                cantReadFile();
                return;
            }

            try {
                panel.setImage(ImageIO.read(file));
                imageToSave = null;
                lastPath = file.getParent();
            } catch (IOException e) {
                cantReadFile();
            }
        }

    }

    private void cantReadFile() {
        JOptionPane.showMessageDialog(null, "Can't read the file");
        currentFile = null;
    }

    private void cantSaveFile() {
        JOptionPane.showMessageDialog(null, "Can't save the file");
        currentFile = null;
    }

    private int askSave() {
        if (imageToSave != null) {
            int response = JOptionPane.showConfirmDialog(null, "Would you like to save the picture?");
            if (response == JOptionPane.OK_OPTION) {
                saveFileItemListener();
            }
            return response;
        }
        return JOptionPane.NO_OPTION;
    }

    private void saveFileItemListener() {
        if (imageToSave != null) {
            if (currentFile == null) {
                saveAsFileItemListener();
            } else {
                save();
            }
        }
    }

    private String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        if (!extension.equals("png") && !extension.equals("bmp") && !extension.equals("PNG") && !extension.equals("BMP") && !extension.equals("txt")) {
            return null;
        }
        return extension;
    }

    private void saveAsFileItemListener() {

        if (imageToSave != null) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "bmp", "PNG", "BMP");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showSaveDialog(null);
            currentFile = fileChooser.getSelectedFile();
            if (currentFile != null) {
                save();
            }
        }
    }

    private void save() {

        String extension = getExtension(currentFile.getName());
        if (extension == null) {
            cantSaveFile();
            saveAsFileItemListener();
            return;
        }

        try {
            ImageIO.write(imageToSave, extension, currentFile);
        } catch (IOException e) {
            cantSaveFile();
        }
    }

    private void exitFileItemListener() {

        int response = askSave();
        if (response == JOptionPane.OK_OPTION || response == JOptionPane.NO_OPTION) {
            setVisible(false);
            dispose();
            System.exit(0);
        }
    }

    private void createEditMenuItems(JMenu editMenu) {

        createCheckBoxMenuAndToolbarItem("Select", "Select", "select.png", e -> selectEditItemListener(), editMenu, false);
        toolBar.addSeparator();
        createMenuAndToolbarItem("B to C", "Copy image from zone B to C", "right.png", e -> bToCEditItemListener(), editMenu);
        createMenuAndToolbarItem("C to B", "Copy image from zone C to B", "left.png", e -> cToBEditItemListener(), editMenu);
        editMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Gray Scale", "Get gray scale image in zone C", "grayscale.png", e -> grayScaleEditItemListener(), editMenu);
        createMenuAndToolbarItem("Negative", "Get inverted image", "invert.png", e -> invertEditItemListener(), editMenu);
        createMenuAndToolbarItem("Floyd-Steinberg dithering", "Get Floyd-Steinberg dithered image", "floyd.png", e -> floydEditItemListener(), editMenu);
        createMenuAndToolbarItem("Ordered dithering", "Get ordered dithered image", "ordered.png", e -> orderedEditItemListener(), editMenu);
        createMenuAndToolbarItem("Zoom", "Get zoomed image", "zoom.png", e -> zoomEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Roberts", "Get Robert's diff image", "roberts.png", e -> robertsEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Sobel", "Get Sobel's diff image", "sobel.png", e -> sobelEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Gauss", "Get Gaussian blur image", "gauss.png", e -> gaussEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Sharpness", "Get sharpened image", "sharpness.png", e -> sharpnessEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Stamping", "Get stamped image", "stamp.png", e -> stampingEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Watercolor", "Get watercolored image", "watercolor.png", e -> watercolorEditMenuItemListener(), editMenu);
        toolBar.addSeparator();
        editMenu.addSeparator();
        createMenuAndToolbarItem("Rotate", "Get rotated image", "rotate.png", e -> rotateEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Gamma", "Change gamma", "gamma.png", e -> gammaEditMenuItemListener(), editMenu);
        toolBar.addSeparator();
        createMenuAndToolbarItem("Config", "Open config", "config.png", e -> configEditItemListener(), editMenu);
        createCheckBoxMenuAndToolbarItem("Absorption", "Absorption", "absorption.png", e -> absorptionEditItemListener(), editMenu, true);
        createCheckBoxMenuAndToolbarItem("Emission", "Emission", "emission.png", e -> emissionEditItemListener(), editMenu, true);
        createMenuAndToolbarItem("Render", "Render", "render.png", e -> renderEditItemListener(), editMenu);
    }

    private void selectEditItemListener() {
        panel.setActive();
    }

    private void bToCEditItemListener() {
        panel.copyBToC();
    }

    private void cToBEditItemListener() {
        panel.copyCToB();
    }

    private void grayScaleEditItemListener() {
        panel.grayScale();
    }

    private void invertEditItemListener() {
        panel.invert();
    }

    private void floydEditItemListener() {
        panel.floyd();
    }

    private void orderedEditItemListener() {
        panel.ordered();
    }

    private void zoomEditMenuItemListener() {
        panel.zoom();
    }

    private void robertsEditMenuItemListener() {
        panel.diffFilter(true);
    }

    private void sobelEditMenuItemListener() {
        panel.diffFilter(false);
    }

    private void gaussEditMenuItemListener() {
        panel.blurFilter();
    }

    private void sharpnessEditMenuItemListener() {
        panel.sharpness();
    }

    private void stampingEditMenuItemListener() {
        panel.stamp();
    }

    private void watercolorEditMenuItemListener() {
        panel.watercolor();
    }

    private void rotateEditMenuItemListener() {
        panel.rotate();
    }

    private void gammaEditMenuItemListener() {
        panel.gamma();
    }

    private void configEditItemListener() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "txt");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(lastConfigPath));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (!fileChooser.getCurrentDirectory().getAbsolutePath().equals(lastConfigPath)) {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.setFileFilter(filter);
        if (fileChooser.showDialog(null, "Открыть файл") == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(fileName);
            String extension = getExtension(fileName);
            if (extension == null || !extension.equals("txt")) {
                cantReadFile();
                return;
            }

            try {

                BufferedReader reader = new BufferedReader(new FileReader(file));

                Pair[] absorptionPoints = readAbsorptionPoints(reader);
                if (absorptionPoints == null) {
                    throw new IOException();
                }

                Pair[] emissionPoints = readEmissionPoints(reader);
                if (emissionPoints == null) {
                    throw new IOException();
                }

                Pair[] chargePoints = readChargePoints(reader);
                if (chargePoints == null) {
                    throw new IOException();
                }

                panel.drawAbsorption(absorptionPoints);
                panel.drawEmission(emissionPoints);
                panel.placeCharges(chargePoints);
                config = true;
                lastConfigPath = file.getParent();
            } catch (IOException e) {
                cantReadFile();
            }
        }

    }

    private Pair[] readAbsorptionPoints(BufferedReader reader) {
        try {
            String[] noComments = reader.readLine().split("//");
            String[] first = noComments[0].split(" ");
            if (first.length != 1) {
                throw new IOException();
            }
            int vertexAmount = Integer.parseInt(first[0]);
            Pair[] points = new Pair[vertexAmount];
            for (int i = 0; i < vertexAmount; ++i) {
                noComments = reader.readLine().split("//");
                first = noComments[0].split(" ");
                if (first.length != 2) {
                    throw new IOException();
                }
                points[i] = new Pair<>(Integer.parseInt(first[0]), Double.parseDouble(first[1]));
            }
            return points;
        } catch (IOException | NumberFormatException ex) {
            return null;
        }
    }

    private Pair[] readEmissionPoints(BufferedReader reader) {
        try {

            String[] noComments = reader.readLine().split("//");
            while (noComments[0].equals("")) {
                noComments = reader.readLine().split("//");
            }
            String[] first = noComments[0].split(" ");
            if (first.length != 1) {
                throw new IOException();
            }
            int vertexAmount = Integer.parseInt(first[0]);
            Pair[] points = new Pair[vertexAmount];
            for (int i = 0; i < vertexAmount; ++i) {
                noComments = reader.readLine().split("//");
                first = noComments[0].split(" ");
                if (first.length != 4) {
                    throw new IOException();
                }
                points[i] = new Pair<>(Integer.parseInt(first[0]), new Color(Integer.parseInt(first[1]), Integer.parseInt(first[2]), Integer.parseInt(first[3])));
            }
            return points;
        } catch (IOException | NumberFormatException ex) {
            return null;
        }
    }

    private Pair[] readChargePoints(BufferedReader reader) {
        try {
            String[] noComments = reader.readLine().split("//");
            while (noComments[0].equals("")) {
                noComments = reader.readLine().split("//");
            }
            String[] first = noComments[0].split(" ");
            if (first.length != 1) {
                throw new IOException();
            }
            int chargeAmount = Integer.parseInt(first[0]);
            Pair[] points = new Pair[chargeAmount];
            for (int i = 0; i < chargeAmount; ++i) {
                noComments = reader.readLine().split("//");
                first = noComments[0].split(" ");
                if (first.length != 4) {
                    throw new IOException();
                }
                points[i] = new Pair<>(new Point3D(Double.parseDouble(first[0]), Double.parseDouble(first[1]), Double.parseDouble(first[2])), Double.parseDouble(first[3]));
            }
            return points;
        } catch (IOException ex) {
            return null;
        }
    }

    private void absorptionEditItemListener() {
        panel.absorptionActive();
    }

    private void emissionEditItemListener() {
        panel.emissionActive();
    }

    private void renderEditItemListener() {
        if (config) {
            panel.render();
        }
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
        aboutDialog.setTitle("About \"Filter\" by Galios Max");

        ImageIcon me = new ImageIcon(System.getProperty("user.dir") + "/src/ru/nsu/fit/g16203/galios/resources/me.jpg");
        JLabel photo = new JLabel(me);
        JLabel progInfo = new JLabel("Program: Filter v1.0");
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

    void setImageToSave(BufferedImage image) {
        this.imageToSave = image;
    }

    private void createMenuAndToolbarItem(String name, String tipText, String imageName, ActionListener e, JMenu menu) {

        JMenuItem item = new JMenuItem(name);
        menu.add(item);
        item.setIcon(new ImageIcon(getClass().getResource("resources/" + imageName)));

        JButton button = new JButton(item.getIcon());
        item.setToolTipText(tipText);

        item.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(item.getToolTipText());
            }
        });

        item.addActionListener(e);
        button.setToolTipText(item.getToolTipText());

        for (MouseMotionListener i : item.getMouseMotionListeners()) {
            button.addMouseMotionListener(i);
        }

        for (ActionListener i : item.getActionListeners()) {
            button.addActionListener(i);
        }
        toolBar.add(button);
    }

    private void createCheckBoxMenuItem(String name, String tipText, String imageName, ActionListener e, JMenu menu) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
        menu.add(item);
        item.setIcon(new ImageIcon(getClass().getResource("resources/" + imageName)));
        item.setToolTipText(tipText);

        item.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(item.getToolTipText());
            }
        });

        item.addActionListener(e);
        item.setSelected(true);
    }

    private void createCheckBoxMenuAndToolbarItem(String name, String tipText, String imageName, ActionListener e, JMenu menu, boolean active) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
        item.setIcon(new ImageIcon(getClass().getResource("resources/" + imageName)));
        JToggleButton button = new JToggleButton(item.getIcon());

        item.setToolTipText(tipText);
        item.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(item.getToolTipText());
            }
        });
        item.addActionListener(e);
        button.addActionListener(event -> checkBoxListener(button, item));
        button.setToolTipText(item.getToolTipText());
        for (MouseMotionListener i : item.getMouseMotionListeners()) {
            button.addMouseMotionListener(i);
        }
        for (ActionListener i : item.getActionListeners()) {
            button.addActionListener(i);
        }
        item.addActionListener(event -> checkBoxListener(item, button));
        button.setSelected(active);
        menu.add(item);
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

}

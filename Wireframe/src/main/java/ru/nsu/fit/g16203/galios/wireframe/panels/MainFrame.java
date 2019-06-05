package ru.nsu.fit.g16203.galios.wireframe.panels;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import ru.nsu.fit.g16203.galios.wireframe.matrix.Matrix;
import ru.nsu.fit.g16203.galios.wireframe.parameters.Parameters;
import ru.nsu.fit.g16203.galios.wireframe.spline.Spline;
import ru.nsu.fit.g16203.galios.wireframe.surface.Surface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {

    private SurfacePanel panel;
    private JPanel statusBar;
    private JToolBar toolBar;

    private JLabel status;
    private Parameters parameters;

    private final String dataPath = System.getProperty("user.dir") + File.separator + "FIT_16203_Galios_Wireframe_Data";
    private final String resources = "/";
    private String lastPath = dataPath;

    public MainFrame() {
        super("Wireframe");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        parameters = new Parameters();
    }

    public void run() {
        try {
            File file = new File(getClass().getResource(resources + "config.txt").getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            readParameters(reader, parameters);
            panel.redraw();

        } catch (Exception ignored) {
        }

        panel = new SurfacePanel(this, parameters);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        createMenuAndToolBar();
        createStatusBar();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFileItemListener();
            }
        });
        add(panel);
        setPreferredSize(new Dimension(835, 955));
        pack();
        setVisible(true);

    }

    private void createMenuAndToolBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Wireframe Toolbar");
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

        createMenuAndToolbarItem("Init", "Init", "init.png", e -> initFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Load...", "Load config", "open.png", e -> loadFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save as...", "Save config as...", "save-as.png", e -> saveAsFileItemListener(), fileMenu);
        fileMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Exit", "Close the window", "exit.png", e -> exitFileItemListener(), fileMenu);
        toolBar.addSeparator();
    }

    private void initFileItemListener() {
        parameters.matrixE = Matrix.getI();
        panel.redraw();
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
            lastPath = file.getParent();
            String extension = getExtension(fileName);
            if (extension != null && !extension.equals("txt")) {
                cantReadFile();
                return;
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                parameters.setDefault();
                readParameters(reader, parameters);
                panel.redraw();

            } catch (IOException | NumberFormatException e) {
                cantReadFile();
            }
        }
    }

    private void readParameters(BufferedReader reader, Parameters parameters) throws IOException {
        readGeneralParameters(reader, parameters);
        readPyramidParameters(reader, parameters);
        Matrix matrix = readMatrix(reader);
        parameters.matrixE = parameters.matrixE.multiply(matrix);
        parameters.backgroundColor = readColor(reader);
        readBodies(reader, parameters);
    }

    private void readGeneralParameters(BufferedReader reader, Parameters parameters) throws IOException {
        String[] first = readLine(reader, 7);

        int n = Integer.parseInt(first[0]);
        int m = Integer.parseInt(first[1]);
        int k = Integer.parseInt(first[2]);
        double a = Double.parseDouble(first[3]);
        double b = Double.parseDouble(first[4]);
        double c = Double.parseDouble(first[5]);
        double d = Double.parseDouble(first[6]);

        if (n > 1 && m > 0 && k > 0 && a >= 0 && a <= b && b <= 1 && c >= 0 && c <= d && d <= 2 * Math.PI) {
            parameters.n = n;
            parameters.m = m;
            parameters.k = k;
            parameters.a = a;
            parameters.b = b;
            parameters.c = c;
            parameters.d = d;
        } else {
            throw new IOException();
        }
    }

    private void readPyramidParameters(BufferedReader reader, Parameters parameters) throws IOException {

        String[] first = readLine(reader, 4);

        double zn = Double.parseDouble(first[0]);
        double zf = Double.parseDouble(first[1]);
        double sw = Double.parseDouble(first[2]);
        double sh = Double.parseDouble(first[3]);

        parameters.zn = zn;
        parameters.zf = zf;
        parameters.sw = sw;
        parameters.sh = sh;
    }

    private Matrix readMatrix(BufferedReader reader) throws IOException {
        Matrix matrix = Matrix.getI();

        for (int i = 0; i < 3; ++i) {
            readRow(reader, matrix, i);
        }
        return matrix;
    }

    private void readRow(BufferedReader reader, Matrix matrix, int row) throws IOException {
        String[] first = readLine(reader, 3);

        matrix.matrix[row][0] = Double.parseDouble(first[0]);
        matrix.matrix[row][1] = Double.parseDouble(first[1]);
        matrix.matrix[row][2] = Double.parseDouble(first[2]);
    }

    private Color readColor(BufferedReader reader) throws IOException {

        String[] first = readLine(reader, 3);
        return new Color(Integer.parseInt(first[0]), Integer.parseInt(first[1]), Integer.parseInt(first[2]));
    }

    private void readBodies(BufferedReader reader, Parameters parameters) throws IOException {
        int k = Integer.parseInt(readLine(reader, 1)[0]);

        for (int i = 0; i < k; ++i) {
            Surface surface = new Surface(parameters);
            surface.setBodyColor(readColor(reader));
            surface.moveSurfaceTo(readCentre(reader));
            surface.rotateSurface(readMatrix(reader));
            readPoints(reader, surface);
            parameters.surfaces.add(surface);
        }

    }

    private Point3D readCentre(BufferedReader reader) throws IOException {

        String[] first = readLine(reader, 3);
        return new Point3D(Double.parseDouble(first[0]), Double.parseDouble(first[1]), Double.parseDouble(first[2]));
    }

    private void readPoints(BufferedReader reader, Surface surface) throws IOException {
        int n = Integer.parseInt(readLine(reader, 1)[0]);

        surface.setSpline(new Spline());
        for (int i = 0; i < n; ++i) {
            surface.getSpline().getPoints().add(readPoint(reader));
        }
        surface.getSpline().calcSpline();
    }

    private Point2D readPoint(BufferedReader reader) throws IOException {
        String[] strings = readLine(reader, 2);
        return new Point2D(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]));
    }

    private String[] readLine(BufferedReader reader, int argsCount) throws IOException {
        String[] noComments = reader.readLine().split("//");
        while (Pattern.matches("\\s*", noComments[0])) {
            noComments = reader.readLine().split("//");
        }
        String[] first = noComments[0].split(" ");
        if (first.length != argsCount) {
            throw new IOException();
        }
        return first;
    }

    private void cantReadFile() {
        JOptionPane.showMessageDialog(null, "Can't read the file");
    }

    private void cantSaveFile() {
        JOptionPane.showMessageDialog(null, "Can't save the file");
    }

    private void saveAsFileItemListener() {

        if (parameters != null) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "txt", "TXT");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showSaveDialog(null);
            File currentFile = fileChooser.getSelectedFile();

            String extension = getExtension(currentFile.getName());
            if (extension == null) {
                cantSaveFile();
                saveAsFileItemListener();
                return;
            }

            try (FileWriter fileWriter = new FileWriter(currentFile)) {

                writeParameters(fileWriter, parameters);

            } catch (IOException e) {
                cantSaveFile();
            }
        }
    }

    private void writeParameters(FileWriter writer, Parameters parameters) throws IOException {

        StringBuilder builder = new StringBuilder();

        writeGeneralParameters(builder, parameters);
        writePyramidParameters(builder, parameters);
        writeMatrix(builder, parameters.matrixE);
        writeColor(builder, parameters.backgroundColor);
        writeBodies(builder, parameters);

        writer.write(builder.toString());
    }

    private void writeGeneralParameters(StringBuilder builder, Parameters parameters) {
        builder.append(parameters.n).append(" ")
                .append(parameters.m).append(" ")
                .append(parameters.k).append(" ")
                .append(parameters.a).append(" ")
                .append(parameters.b).append(" ")
                .append(parameters.c).append(" ")
                .append(parameters.d).append("\n");
    }

    private void writePyramidParameters(StringBuilder builder, Parameters parameters) {

        builder.append(parameters.zn).append(" ")
                .append(parameters.zf).append(" ")
                .append(parameters.sw).append(" ")
                .append(parameters.sh).append("\n");
    }

    private void writeMatrix(StringBuilder builder, Matrix matrix) {

        for (int i = 0; i < 3; ++i) {
            writeRow(builder, matrix, i);
        }
    }

    private void writeRow(StringBuilder builder, Matrix matrix, int row) {

        builder.append(matrix.matrix[row][0]).append(" ")
                .append(matrix.matrix[row][1]).append(" ")
                .append(matrix.matrix[row][2]).append("\n");
    }

    private void writeColor(StringBuilder builder, Color color) {
        builder.append(color.getRed()).append(" ")
                .append(color.getGreen()).append(" ")
                .append(color.getBlue()).append("\n");
    }

    private void writeBodies(StringBuilder builder, Parameters parameters) {
        builder.append(parameters.surfaces.size()).append("\n");

        for (Surface surface : parameters.surfaces) {

            writeColor(builder, surface.getBodyColor());
            writeCentre(builder, surface.getCentre());
            writeMatrix(builder, surface.getRotationMatrix());
            writePoints(builder, surface.getSpline().getPoints());
        }

    }

    private void writeCentre(StringBuilder builder, Point3D centre) {
        builder.append(centre.getX()).append(" ")
                .append(centre.getY()).append(" ")
                .append(centre.getZ()).append("\n");
    }

    private void writePoints(StringBuilder builder, ArrayList<Point2D> points) {
        builder.append(points.size()).append("\n");

        for (Point2D point : points) {
            writePoint(builder, point);
        }
    }

    private void writePoint(StringBuilder builder, Point2D point) {
        builder.append(point.getX()).append(" ")
                .append(point.getY()).append("\n");
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
        createCheckBoxMenuAndToolbarItem("Interact", "Rotate surfaces", "interact.png", e -> interactEditItemListener(), editMenu, false);
        createCheckBoxMenuAndToolbarItem("Wire", "Remove extra lines", "wire.png", e -> wireEditItemListener(), editMenu, true);
        createMenuAndToolbarItem("Move", "Move surfaces", "move.png", e -> moveEditItemListener(), editMenu);
        createCheckBoxMenuAndToolbarItem("Axis", "Show axis", "axis.png", e -> axisEditItemListener(), editMenu, false);
    }

    private void axisEditItemListener() {
        panel.axis();
    }

    private void wireEditItemListener() {
        panel.wire();
    }

    private void moveEditItemListener() {
        panel.moveSurfaces();
    }

    private void interactEditItemListener() {

        panel.setFigures();

    }

    private void settingsEditItemListener() {

        SettingsFrame frame = new SettingsFrame(this, parameters);
        frame.run();
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
        aboutDialog.setTitle("About \"Wireframe\" by Galios Max");

        JLabel photo = new JLabel(new ImageIcon(getClass().getResource(resources + "me.jpg")));
        JLabel progInfo = new JLabel("Program: Wireframe v1.0");
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

    void redraw() {

        double proportion = parameters.sh / parameters.sw;

        int width = getWidth();
        int height = (int) ((double) width * proportion + 120);

        if (height <= 1000 && width <= 1920) {
            setSize(new Dimension(getWidth(), (int) ((double) getWidth() * proportion + 120)));

        }

        panel.redraw();
    }

    void clear(int num) {
        Surface surface = new Surface(parameters);
        surface.setSpline(new Spline());
        parameters.surfaces.set(num, surface);

        panel.redraw();
    }

    Parameters getParameters() {
        return parameters;
    }
}

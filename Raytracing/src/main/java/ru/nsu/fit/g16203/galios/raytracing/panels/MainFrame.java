package ru.nsu.fit.g16203.galios.raytracing.panels;

import javafx.geometry.Point3D;
import ru.nsu.fit.g16203.galios.raytracing.parameters.Quality;
import ru.nsu.fit.g16203.galios.raytracing.parameters.RenderParameters;
import ru.nsu.fit.g16203.galios.raytracing.scene.Box;
import ru.nsu.fit.g16203.galios.raytracing.scene.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {

    private ScenePanel panel;
    private JPanel statusBar;
    private JToolBar toolBar;

    private JLabel status;
    private RenderParameters renderParameters;
    private Scene scene;

    private final String dataPath = System.getProperty("user.dir") + File.separator + "FIT_16203_Galios_Raytracing_Data";
    private final String resources = "/";
    private String lastPath = dataPath;
    private boolean isAfterScene = false;
    private boolean isRaytracing = false;
    private BufferedImage imageToSave;

    public MainFrame() {
        super("Raytracing");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        renderParameters = new RenderParameters();
        scene = new Scene();
    }

    public void run() {
        panel = new ScenePanel(this, scene, renderParameters);
        createMenuAndToolBar();
        createStatusBar();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFileItemListener();
            }
        });
        add(panel);
        setPreferredSize(new Dimension(1000, 1000));
        pack();
        setVisible(true);
    }

    private void createMenuAndToolBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Raytracing Toolbar");
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
        createMenuAndToolbarItem("Load", "Load scene", "load.png", e -> loadSceneFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Load render...", "Load render settings", "open.png", e -> loadRenderFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save render as...", "Save render settings as...", "save-as.png", e -> saveAsFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save image as...", "Save image as...", "image.png", e -> saveImageAsFileItemListener(), fileMenu);
        fileMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Exit", "Close the window", "exit.png", e -> exitFileItemListener(), fileMenu);
        toolBar.addSeparator();
    }

    private void initFileItemListener() {
        if (!isRaytracing) {
            renderParameters.init();
            panel.redraw();
        }
    }

    private File openFile(FileNameExtensionFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(lastPath));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (!fileChooser.getCurrentDirectory().getAbsolutePath().equals(lastPath)) {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.setFileFilter(filter);

        File file = null;
        if (fileChooser.showDialog(null, "Открыть файл") == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            file = new File(fileName);
            lastPath = file.getParent();
        }
        return file;
    }

    private void loadSceneFileItemListener() {
        if (!isRaytracing) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Scene Config Files", "scene");
            File file = openFile(filter);

            if (file != null) {
                loadSceneFile(file);
            }
        }
    }

    private void loadRenderFileItemListener() {

        if (!isRaytracing) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Render Config Files", "render");
            File file = openFile(filter);

            if (file != null) {
                loadRenderFile(file);
            }
        }
    }

    private void loadSceneFile(File file) {
        if (file == null) {
            printMessage("Can't open scene file");
            return;
        }

        File renderFile = new File(getFileName(file.getAbsolutePath()) + ".render");
        String extension = getExtension(file.getName());
        if (extension == null || !extension.equals("scene")) {
            printMessage("Wrong file extension");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            scene.clear();
            readScene(reader);
            panel.redraw();
        } catch (IOException | NumberFormatException e) {
            if (e.getMessage() != null) {
                printMessage(e.getMessage());
            } else {
                printMessage("Can't read scene file");
            }
        }

        isAfterScene = true;
        loadRenderFile(renderFile);
        isAfterScene = false;
    }

    private void loadRenderFile(File file) {
        if (file == null) {
            if (!isAfterScene) {
                printMessage("Can't open render file");
                renderParameters.setDefault();
            }
            return;
        }

        String extension = getExtension(file.getName());
        if (extension == null || !extension.equals("render")) {
            printMessage("Wrong file extension");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            readRenderSettings(reader);
            panel.redraw();
        } catch (IOException | NumberFormatException e) {
            if (e.getMessage() != null) {
                printMessage(e.getMessage());
            } else {
                printMessage("Can't read render file");
            }
        }
    }

    private void readScene(BufferedReader reader) throws IOException {
        readHeader(reader);
    }

    private void readHeader(BufferedReader reader) throws IOException {
        scene.setAmbientLight(readColor(reader));
        readLights(reader);
        readFigures(reader);
    }

    private void readLights(BufferedReader reader) throws IOException {
        int k = Integer.parseInt(readLine(reader, 1)[0]);

        for (int i = 0; i < k; ++i) {
            scene.addLightSource(readLight(reader));
        }
    }

    private LightSource readLight(BufferedReader reader) throws IOException {
        String[] first = readLine(reader, 6);

        Point3D position = new Point3D(Double.parseDouble(first[0]), Double.parseDouble(first[1]), Double.parseDouble(first[2]));
        Color color = new Color(Integer.parseInt(first[3]), Integer.parseInt(first[4]), Integer.parseInt(first[5]));

        return new LightSource(position, color);
    }

    private void readFigures(BufferedReader reader) throws IOException {

        try {
            while (true) {
                String type = readLine(reader, 1)[0];

                switch (type) {
                    case "SPHERE": {
                        scene.addFigure(readSphere(reader));
                        break;
                    }
                    case "BOX": {
                        scene.addFigure(readBox(reader));
                        break;
                    }
                    case "TRIANGLE": {
                        scene.addFigure(readTriangle(reader));
                        break;
                    }
                    case "QUADRANGLE": {
                        scene.addFigure(readQuadrangle(reader));
                        break;
                    }
                    case "STOP": {
                        return;
                    }
                    default: {
                        throw new IOException("WrongFormat");
                    }
                }
            }
        } catch (IOException e) {
            if (scene.getFigures().size() == 0 || e.getMessage() != null && e.getMessage().equals("WrongFormat")) {
                throw e;
            }
        }
    }

    private Sphere readSphere(BufferedReader reader) throws IOException {
        Sphere sphere = new Sphere(readPoint(reader), readDouble(reader));

        sphere.setOpticalParameters(readOpticalParameters(reader));
        return sphere;
    }

    private Box readBox(BufferedReader reader) throws IOException {
        Box box = new Box(readPoint(reader), readPoint(reader));

        box.setOpticalParameters(readOpticalParameters(reader));
        return box;
    }

    private Triangle readTriangle(BufferedReader reader) throws IOException {
        Triangle triangle = new Triangle(readPoint(reader), readPoint(reader), readPoint(reader));

        triangle.setOpticalParameters(readOpticalParameters(reader));
        return triangle;
    }

    private Quadrangle readQuadrangle(BufferedReader reader) throws IOException {
        Quadrangle quadrangle = new Quadrangle(readPoint(reader), readPoint(reader), readPoint(reader), readPoint(reader));

        quadrangle.setOpticalParameters(readOpticalParameters(reader));
        return quadrangle;
    }

    private OpticalParameters readOpticalParameters(BufferedReader reader) throws IOException {
        String[] parameters = readLine(reader, 7);

        OpticalParameters opticalParameters = new OpticalParameters();
        opticalParameters.setDiffuseRed(Double.parseDouble(parameters[0]));
        opticalParameters.setDiffuseGreen(Double.parseDouble(parameters[1]));
        opticalParameters.setDiffuseBlue(Double.parseDouble(parameters[2]));
        opticalParameters.setReflectionRed(Double.parseDouble(parameters[3]));
        opticalParameters.setReflectionGreen(Double.parseDouble(parameters[4]));
        opticalParameters.setReflectionBlue(Double.parseDouble(parameters[5]));
        opticalParameters.setPower(Double.parseDouble(parameters[6]));

        return opticalParameters;
    }

    private void readRenderSettings(BufferedReader reader) throws IOException {
        renderParameters.setBackgroundColor(readColor(reader));
        renderParameters.setGamma(readDouble(reader));
        renderParameters.setDepth((int) readDouble(reader));
        renderParameters.setQuality(readQuality(reader));

        Point3D cameraPoint = readPoint(reader);
        Point3D viewPoint = readPoint(reader);
        Point3D VupPoint = readPoint(reader);

        renderParameters.setCamera(new Camera(cameraPoint, viewPoint, VupPoint));

        readCameraParameters(reader, renderParameters);
        renderParameters.getCamera().calculateVUp();
        renderParameters.setLoaded(true);
    }

    private Quality readQuality(BufferedReader reader) throws IOException {

        String quality = readLine(reader, 1)[0].toUpperCase();

        switch (quality) {
            case "ROUGH":
                return Quality.ROUGH;
            case "NORMAL":
                return Quality.NORMAL;
            case "FINE":
                return Quality.FINE;
            default:
                return null;
        }
    }

    private void readCameraParameters(BufferedReader reader, RenderParameters renderParameters) throws IOException {

        String[] z = readLine(reader, 2);

        double zn = Double.parseDouble(z[0]);
        double zf = Double.parseDouble(z[1]);
        if (zn < zf) {
            renderParameters.setZn(zn);
            renderParameters.setZf(zf);
        } else {
            throw new IOException("Wrong zn/zf parameters");
        }

        String[] s = readLine(reader, 2);

        double sw = Double.parseDouble(s[0]);
        double sh = Double.parseDouble(s[1]);
        if (sw > 0 && sh > 0) {
            renderParameters.setSw(sw);
            renderParameters.setSh(sh);
        } else {
            throw new IOException("Wrong sw/sh parameters");
        }
    }

    private Color readColor(BufferedReader reader) throws IOException {

        String[] first = readLine(reader, 3);
        return new Color(Integer.parseInt(first[0]), Integer.parseInt(first[1]), Integer.parseInt(first[2]));
    }

    private Point3D readPoint(BufferedReader reader) throws IOException {
        String[] strings = readLine(reader, 3);
        return new Point3D(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]));
    }

    private String[] readLine(BufferedReader reader, int argsCount) throws IOException {

        String line = reader.readLine();
        if (line != null) {
            String[] noComments = line.split("//");
            while (Pattern.matches("\\s*", noComments[0])) {
                noComments = reader.readLine().split("//");
            }
            String[] first = noComments[0].split(" ");
            if (first.length != argsCount) {
                throw new IOException();
            }

            return first;
        }
        return new String[]{"STOP"};
    }

    private double readDouble(BufferedReader reader) throws IOException {
        return Double.parseDouble(readLine(reader, 1)[0]);
    }

    private void printMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private void saveImageAsFileItemListener() {
        if (imageToSave != null) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "bmp", "PNG", "BMP");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showSaveDialog(null);
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                saveImage(file);
            }
        }
    }

    private void saveImage(File file) {

        String extension = getExtension(file.getName());
        if (extension == null) {
            printMessage("Can't save the image");
            saveImageAsFileItemListener();
            return;
        }

        try {
            ImageIO.write(imageToSave, extension, file);
        } catch (IOException e) {
            printMessage("Can't save the image");
        }
    }

    private void saveAsFileItemListener() {
        if (!isRaytracing) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Render Config Files", "render");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showSaveDialog(null);
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                saveRenderSettings(file);
            }
        }
    }

    private void saveRenderSettings(File file) {

        String extension = getExtension(file.getName());
        if (extension == null || !extension.equals("render")) {
            printMessage("Can't save render settings");
            saveImageAsFileItemListener();
            return;
        }

        try (FileWriter fileWriter = new FileWriter(file)) {

            writeParameters(fileWriter);

        } catch (IOException e) {
            printMessage("Can't save render settings");
        }
    }

    private void writeParameters(FileWriter fileWriter) throws IOException {

        StringBuilder builder = new StringBuilder();

        writeColor(builder, renderParameters.getBackgroundColor());
        writeNum(builder, renderParameters.getGamma());
        writeNum(builder, renderParameters.getDepth());
        writeQuality(builder, renderParameters.getQuality());
        writeCameraParameters(builder);

        fileWriter.write(builder.toString());
    }

    private void writeCameraParameters(StringBuilder builder) {

        writePoint(builder, renderParameters.getCameraPoint());
        writePoint(builder, renderParameters.getViewPoint());
        writePoint(builder, renderParameters.getVup());

        builder.append(renderParameters.getZn()).append(" ")
                .append(renderParameters.getZf()).append("\n")
                .append(renderParameters.getSw()).append(" ")
                .append(renderParameters.getSh()).append("\n");
    }

    private void writePoint(StringBuilder builder, Point3D point) {
        builder.append(point.getX()).append(" ")
                .append(point.getY()).append(" ")
                .append(point.getZ()).append("\n");
    }

    private void writeQuality(StringBuilder builder, Quality quality) {
        
        switch (quality) {
            case FINE: {
                builder.append("FINE").append("\n");
                break;
            }
            case NORMAL: {
                builder.append("NORMAL").append("\n");
                break;
            }
            case ROUGH: {
                builder.append("ROUGH").append("\n");
                break;
            }
            default: {
                break;
            }
        }
    }

    private void writeNum(StringBuilder builder, int num) {
        builder.append(num).append("\n");
    }

    private void writeNum(StringBuilder builder, double num) {
        builder.append(num).append("\n");
    }

    private void writeColor(StringBuilder builder, Color color) {
        builder.append(color.getRed()).append(" ")
                .append(color.getGreen()).append(" ")
                .append(color.getBlue()).append("\n");
    }

    private String getFileName(String fileName) {
        String name = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            name = fileName.substring(0, i);
        }
        return name;
    }

    private String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    private void exitFileItemListener() {
        if (isRaytracing) {
            panel.stopRaytracing();
        }
        setVisible(false);
        dispose();
        System.exit(0);
    }

    private void createEditMenuItems(JMenu editMenu) {
        createMenuAndToolbarItem("Settings", "Settings", "settings.png", e -> settingsEditItemListener(), editMenu);
        createCheckBoxMenuAndToolbarItem("Select view", "Select view", "view.png", e -> selectViewEditItemListener(), editMenu, true);
        createMenuAndToolbarItem("Render", "Render", "render.png", e -> renderEditItemListener(), editMenu);
    }

    private void renderEditItemListener() {
        panel.doRaytracing();
        isRaytracing = true;
    }

    private void selectViewEditItemListener() {
        panel.stopRaytracing();
    }

    private void settingsEditItemListener() {

        if (!isRaytracing) {
            new SettingsPanel(renderParameters, this);
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
        aboutDialog.setTitle("About \"Raytracing\" by Galios Max");

        JLabel photo = new JLabel(new ImageIcon(getClass().getResource(resources + "me.jpg")));
        JLabel progInfo = new JLabel("Program: Raytracing v1.0");
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
                if (!isRaytracing) {
                    status.setText(item.getToolTipText());
                }
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

    void setStatus(String text) {
        status.setText(text);
        repaint();
    }

    void setImageToSave(BufferedImage image) {
        this.imageToSave = image;
    }

    public void setRaytracing(boolean set) {
        isRaytracing = set;
    }

    void redraw() {
        panel.redraw();
    }
}

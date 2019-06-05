package ru.nsu.fit.g16203.galios.controller;

import ru.nsu.fit.g16203.galios.view.View;
import ru.nsu.fit.g16203.galios.model.Constants;
import ru.nsu.fit.g16203.galios.model.GameMode;
import ru.nsu.fit.g16203.galios.model.Life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class MainFrame extends JFrame {

    private View panel;
    private JLabel status;
    private Life life;

    private boolean isRunning = false;

    private JMenuItem newFileItem;
    private JMenuItem loadFileItem;
    private JMenuItem saveFileItem;
    private JMenuItem saveAsFileItem;
    private JMenuItem exitFileItem;
    private JMenuItem clearEditItem;
    private JMenuItem settingsEditItem;
    private JRadioButtonMenuItem replaceEditItem;
    private JRadioButtonMenuItem xorEditItem;
    private JRadioButtonMenuItem runGameItem;
    private JMenuItem stepGameItem;

    private JButton newButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton settingsButton;
    private JToggleButton replaceButton;
    private JButton clearButton;
    private JButton runButton;
    private JButton stepButton;

    private JToolBar toolBar;
    private JPanel statusBar;
    private File currentFile;

    public boolean isChanged = false;

    private String basePath = System.getProperty("user.dir") + "/src/ru/nsu/fit/g16203/galios/resources/";
    private String lastPath = System.getProperty("user.dir") + "/FIT_16203_Galios_Life_Data";

    public MainFrame() {
        super("Life Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFileItemListener();
            }
        });

        panel = new View(this);
        createMenuAndToolBar();
        createStatusBar();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText("X: " + e.getX() + "; Y: " + e.getY());
            }
        });
        add(scrollPane);
        pack();
        life = new Life(panel);
        setVisible(true);

    }

    private void createMenuAndToolBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Life Game Toolbar");
        add(toolBar, BorderLayout.PAGE_START);

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu gameMenu = new JMenu("Game");
        JMenu viewMenu = new JMenu("View");
        JMenu aboutMenu = new JMenu("About");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(gameMenu);
        menuBar.add(viewMenu);
        menuBar.add(aboutMenu);

        createFileMenuItems(fileMenu, toolBar);
        createEditMenuItems(editMenu, toolBar);
        createGameMenuItems(gameMenu, toolBar);
        createViewMenuItems(viewMenu, toolBar);
        createAboutMenuItems(aboutMenu, toolBar);
    }

    private void createStatusBar() {
        statusBar = new JPanel();
        status = new JLabel("Ready");
        statusBar.add(status);
        add(statusBar, BorderLayout.PAGE_END);
    }

    private void createFileMenuItems(JMenu fileMenu, JToolBar toolBar) {
        newFileItem = new JMenuItem("New");
        loadFileItem = new JMenuItem("Load...");
        saveFileItem = new JMenuItem("Save");
        saveAsFileItem = new JMenuItem("Save as...");
        exitFileItem = new JMenuItem("Exit");
        fileMenu.add(newFileItem);
        fileMenu.add(loadFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(saveAsFileItem);
        fileMenu.addSeparator();
        fileMenu.add(exitFileItem);

        newFileItem.setIcon(new ImageIcon(basePath + "new.png"));
        loadFileItem.setIcon(new ImageIcon(basePath + "open.png"));
        saveFileItem.setIcon(new ImageIcon(basePath + "save.png"));
        saveAsFileItem.setIcon(new ImageIcon(basePath + "save-as.png"));
        exitFileItem.setIcon(new ImageIcon(basePath + "exit.png"));

        newButton = new JButton(newFileItem.getIcon());
        loadButton = new JButton(loadFileItem.getIcon());
        saveButton = new JButton(saveFileItem.getIcon());
        JButton exitButton = new JButton(exitFileItem.getIcon());

        newFileItem.setToolTipText("Create new file");
        loadFileItem.setToolTipText("Load saved game");
        saveFileItem.setToolTipText("Save the game");
        saveAsFileItem.setToolTipText("Save the game as...");
        exitFileItem.setToolTipText("Close the game");

        newFileItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(newFileItem.getToolTipText());
            }
        });
        loadFileItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(loadFileItem.getToolTipText());
            }
        });
        saveFileItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(saveFileItem.getToolTipText());
            }
        });
        saveAsFileItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(saveAsFileItem.getToolTipText());
            }
        });
        exitFileItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(exitFileItem.getToolTipText());
            }
        });

        newFileItem.addActionListener(e -> newFileItemListener());
        loadFileItem.addActionListener(e -> loadFileItemListener());
        saveFileItem.addActionListener(e -> saveFileItemListener());
        saveAsFileItem.addActionListener(e -> saveAsFileItemListener());
        exitFileItem.addActionListener(e -> exitFileItemListener());

        newButton.setToolTipText(newFileItem.getToolTipText());
        loadButton.setToolTipText(loadFileItem.getToolTipText());
        saveButton.setToolTipText(saveFileItem.getToolTipText());
        exitButton.setToolTipText(exitFileItem.getToolTipText());

        for (MouseMotionListener i : newFileItem.getMouseMotionListeners()) {
            newButton.addMouseMotionListener(i);
        }
        for (MouseMotionListener i : loadFileItem.getMouseMotionListeners()) {
            loadButton.addMouseMotionListener(i);
        }
        for (MouseMotionListener i : saveFileItem.getMouseMotionListeners()) {
            saveButton.addMouseMotionListener(i);
        }
        for (MouseMotionListener i : exitFileItem.getMouseMotionListeners()) {
            exitButton.addMouseMotionListener(i);
        }

        for (ActionListener i : newFileItem.getActionListeners()) {
            newButton.addActionListener(i);
        }
        for (ActionListener i : loadFileItem.getActionListeners()) {
            loadButton.addActionListener(i);
        }
        for (ActionListener i : saveFileItem.getActionListeners()) {
            saveButton.addActionListener(i);
        }
        for (ActionListener i : exitFileItem.getActionListeners()) {
            exitButton.addActionListener(i);
        }

        toolBar.add(newButton);
        toolBar.add(loadButton);
        toolBar.add(saveButton);
        toolBar.add(exitButton);
        toolBar.addSeparator();
    }

    private void newFileItemListener() {
        if (isChanged) {
            int response = JOptionPane.showConfirmDialog(null, "Would you like to save the file?");
            if (response == JOptionPane.OK_OPTION) {
                saveFileItemListener();
            }
        }
        clearButton.doClick();
        settingsButton.doClick();
        isChanged = false;
    }

    private void loadFileItemListener() {

        if (isChanged) {
            int response = JOptionPane.showConfirmDialog(null, "Would you like to save the file?");
            if (response == JOptionPane.OK_OPTION) {
                saveFileItemListener();
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(lastPath));
        if (fileChooser.showDialog(null, "Открыть файл") == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            currentFile = new File(fileName);
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(currentFile));
                String[] noComments = fileReader.readLine().split("//");
                String[] first = noComments[0].split(" ");
                if (first.length != 2) {
                    throw new IOException();
                }
                int fieldWidth = Integer.parseInt(first[0]);
                int fieldHeight = Integer.parseInt(first[1]);
                noComments = fileReader.readLine().split("//");
                first = noComments[0].split(" ");
                if (first.length != 1) {
                    throw new IOException();
                }
                int lineWidth = Integer.parseInt(first[0]);
                noComments = fileReader.readLine().split("//");
                first = noComments[0].split(" ");
                if (first.length != 1) {
                    throw new IOException();
                }
                int hexSize = Integer.parseInt(first[0]);
                if (checkParameters(fieldWidth, fieldHeight, lineWidth, hexSize)) {
                    setParameters(fieldWidth, fieldHeight, lineWidth, hexSize);
                    noComments = fileReader.readLine().split("//");
                    first = noComments[0].split(" ");
                    if (first.length != 1) {
                        throw new IOException();
                    }
                    int all = Integer.parseInt(first[0]);
                    String[] line;
                    for (int i = 0; i < all; i++) {
                        noComments = fileReader.readLine().split("//");
                        line = noComments[0].split(" ");
                        if (line.length != 2) {
                            throw new IOException();
                        }
                        int x = Integer.parseInt(line[0]);
                        int y = Integer.parseInt(line[1]);
                        life.changeState(x, y);
                    }
                    isChanged = false;
                    lastPath = currentFile.getParent();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Can't read the file");
                currentFile = null;
            }
        }
    }

    private void saveFileItemListener() {
        if (currentFile == null) {
            saveAsFileItemListener();
        } else {
            save();
        }
    }

    private void saveAsFileItemListener() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.showSaveDialog(null);
        currentFile = fileChooser.getSelectedFile();
        if (currentFile != null) {
            save();
        }
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(currentFile)) {
            StringBuilder toWrite = new StringBuilder();
            toWrite.append(Constants.fieldWidth).append(" ")
                    .append(Constants.fieldHeight).append("\n")
                    .append(Constants.lineWidth).append("\n")
                    .append(Constants.hexSize).append("\n");
            List<Point> points = life.getAliveCells();
            toWrite.append(points.size()).append("\n");
            for (Point i : points) {
                toWrite.append(i.x).append(" ").append(i.y).append("\n");
            }
            fileWriter.write(toWrite.toString());
            isChanged = false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't read the file");
            currentFile = null;
        }
    }

    private void exitFileItemListener() {

        if (isRunning) runButton.doClick();
        if (isChanged) {
            int response = JOptionPane.showConfirmDialog(null, "Would you like to save the file?");
            if (response == JOptionPane.OK_OPTION) {
                saveFileItemListener();
                setVisible(false);
                dispose();
                System.exit(0);
            } else if (response == JOptionPane.NO_OPTION) {
                setVisible(false);
                dispose();
                System.exit(0);
            }
        } else {
            setVisible(false);
            dispose();
            System.exit(0);
        }
    }

    private void createEditMenuItems(JMenu editMenu, JToolBar toolBar) {
        clearEditItem = new JMenuItem("Clear");
        JMenu modeEditItem = new JMenu("Mode");
        settingsEditItem = new JMenuItem("Settings");
        replaceEditItem = new JRadioButtonMenuItem("Replace");
        xorEditItem = new JRadioButtonMenuItem("XOR");
        replaceEditItem.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(replaceEditItem);
        modeEditItem.add(replaceEditItem);
        modeEditItem.add(xorEditItem);
        editMenu.add(clearEditItem);
        editMenu.addSeparator();
        editMenu.add(modeEditItem);
        editMenu.addSeparator();
        editMenu.add(settingsEditItem);

        settingsEditItem.setIcon(new ImageIcon(basePath + "settings.png"));
        replaceEditItem.setIcon(new ImageIcon(basePath + "replace.png"));
        clearEditItem.setIcon(new ImageIcon(basePath + "clear.png"));

        settingsButton = new JButton(settingsEditItem.getIcon());
        replaceButton = new JToggleButton(replaceEditItem.getIcon());
        clearButton = new JButton(clearEditItem.getIcon());

        settingsEditItem.setToolTipText("Open settings");
        modeEditItem.setToolTipText("Replace/XOR mode");
        replaceEditItem.setToolTipText("Turn on the replace mode");
        xorEditItem.setToolTipText("Turn on the XOR mode");
        clearEditItem.setToolTipText("Clear the field");

        settingsEditItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(settingsEditItem.getToolTipText());
            }
        });
        modeEditItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(modeEditItem.getToolTipText());
            }
        });
        replaceEditItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(replaceEditItem.getToolTipText());
            }
        });
        xorEditItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(xorEditItem.getToolTipText());
            }
        });
        clearEditItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(clearEditItem.getToolTipText());
            }
        });

        clearEditItem.addActionListener(e -> clearEditItemListener());
        replaceEditItem.addActionListener(e -> replaceEditItemListener());
        xorEditItem.addActionListener(e -> xorEditItemListener());
        settingsEditItem.addActionListener(e -> settingsEditItemListener());

        settingsButton.setToolTipText(settingsEditItem.getToolTipText());
        replaceButton.setToolTipText(replaceEditItem.getToolTipText());
        clearButton.setToolTipText(clearEditItem.getToolTipText());

        for (MouseMotionListener i : settingsEditItem.getMouseMotionListeners()) {
            settingsButton.addMouseMotionListener(i);
        }
        for (MouseMotionListener i : replaceEditItem.getMouseMotionListeners()) {
            replaceButton.addMouseMotionListener(i);
        }
        for (MouseMotionListener i : clearEditItem.getMouseMotionListeners()) {
            clearButton.addMouseMotionListener(i);
        }
        for (ActionListener i : settingsEditItem.getActionListeners()) {
            settingsButton.addActionListener(i);
        }
        replaceButton.addActionListener(e -> replaceButtonListener());
        for (ActionListener i : clearEditItem.getActionListeners()) {
            clearButton.addActionListener(i);
        }

        toolBar.add(settingsButton);
        toolBar.add(replaceButton);
        toolBar.add(clearButton);
        toolBar.addSeparator();

        replaceButton.setSelected(true);
        replaceEditItem.setSelected(true);
    }

    private void clearEditItemListener() {
        life.clear();
    }

    private void replaceEditItemListener() {
        replaceEditItem.setSelected(true);
        xorEditItem.setSelected(false);
        replaceButton.setSelected(true);
        replaceButton.setToolTipText(xorEditItem.getToolTipText());
        life.setGameMode(GameMode.REPLACE);
    }

    private void xorEditItemListener() {
        replaceEditItem.setSelected(false);
        xorEditItem.setSelected(true);
        replaceButton.setSelected(false);
        replaceButton.setToolTipText(replaceEditItem.getToolTipText());
        life.setGameMode(GameMode.XOR);
    }

    private void replaceButtonListener() {
        if (replaceButton.isSelected()) {
            replaceEditItemListener();
        } else {
            xorEditItemListener();
        }
    }

    private void sliderBoxSettings(JLabel text, JSlider slider, JTextField box, int value, int upperBound) {
        text.setPreferredSize(new Dimension(80, 30));
        box.setPreferredSize(new Dimension(50, 30));
        box.setText(String.valueOf(value));
        slider.setValue(value);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(upperBound - 1);
        slider.setMinorTickSpacing(upperBound / 10);
        slider.setPaintTicks(true);

        box.addActionListener(e -> {
            try {
                int val = Integer.parseInt(box.getText());
                if (val >= 1 && val < upperBound)
                    slider.setValue(val);
                else if (val < 1) {
                    box.setText("1");
                    slider.setValue(1);
                } else {
                    box.setText(String.valueOf(upperBound));
                    slider.setValue(upperBound);
                }
            } catch (Exception ex) {
                slider.setValue(value);
                box.setText(String.valueOf(value));
            }
        });
        slider.addChangeListener(e -> box.setText(String.valueOf(slider.getValue())));
    }

    private void boxSettings(JTextField text, double value) {
        text.setText(String.valueOf(value));
        text.addActionListener(e -> {
            try {
                Double.parseDouble(text.getText());
            } catch (Exception ex) {
                text.setText(String.valueOf(value));
            }
        });
    }

    private void settingsEditItemListener() {

        JDialog settingsDialog = new JDialog();
        settingsDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        settingsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                settingsDialog.setVisible(false);
                settingsDialog.dispose();
            }
        });

        JPanel settingsPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        settingsDialog.setTitle("Settings");

        JPanel cellPanel = new JPanel();
        cellPanel.setBorder(BorderFactory.createTitledBorder("Cell Settings"));
        JLabel lineWidthText = new JLabel("Line Width");
        JSlider lineWidthSlider = new JSlider(1, Constants.lineWidthUpperBound);
        JTextField lineWidthBox = new JTextField();
        sliderBoxSettings(lineWidthText, lineWidthSlider, lineWidthBox, Constants.lineWidth, Constants.lineWidthUpperBound);

        JLabel hexSizeText = new JLabel("Hex Size");
        JSlider hexSizeSlider = new JSlider(1, Constants.hexSizeUpperBound);
        JTextField hexSizeBox = new JTextField();
        sliderBoxSettings(hexSizeText, hexSizeSlider, hexSizeBox, Constants.hexSize, Constants.hexSizeUpperBound);

        cellPanel.add(lineWidthText);
        cellPanel.add(lineWidthBox);
        cellPanel.add(lineWidthSlider);
        cellPanel.add(hexSizeText);
        cellPanel.add(hexSizeBox);
        cellPanel.add(hexSizeSlider);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setBorder(BorderFactory.createTitledBorder("Field Settings"));
        JLabel fieldWidthText = new JLabel("Field Width");
        JSlider fieldWidthSlider = new JSlider(1, Constants.widthUpperBound);
        JTextField fieldWidthBox = new JTextField();
        sliderBoxSettings(fieldWidthText, fieldWidthSlider, fieldWidthBox, Constants.fieldWidth, Constants.widthUpperBound);

        JLabel fieldHeightText = new JLabel("Field Height");
        JSlider fieldHeightSlider = new JSlider(1, Constants.heightUpperBound);
        JTextField fieldHeightBox = new JTextField();
        sliderBoxSettings(fieldHeightText, fieldHeightSlider, fieldHeightBox, Constants.fieldHeight, Constants.heightUpperBound);

        fieldPanel.add(fieldWidthText);
        fieldPanel.add(fieldWidthBox);
        fieldPanel.add(fieldWidthSlider);
        fieldPanel.add(fieldHeightText);
        fieldPanel.add(fieldHeightBox);
        fieldPanel.add(fieldHeightSlider);

        JPanel modePanel = new JPanel();
        modePanel.setBorder(BorderFactory.createTitledBorder("Game Mode"));
        JRadioButton replaceSettingsButton = new JRadioButton();
        JRadioButton xorSettingsButton = new JRadioButton();
        replaceSettingsButton.setText("Replace");
        xorSettingsButton.setText("XOR");
        ButtonGroup group = new ButtonGroup();
        group.add(replaceSettingsButton);
        group.add(xorSettingsButton);
        modePanel.add(replaceSettingsButton);
        modePanel.add(xorSettingsButton);
        if (life.getGameMode() == GameMode.REPLACE) {
            replaceSettingsButton.setSelected(true);
        } else {
            xorSettingsButton.setSelected(true);
        }

        JPanel lifePanel = new JPanel(new GridLayout(2, 6, 15, 10));
        lifePanel.setBorder(BorderFactory.createTitledBorder("Life Settings"));
        JLabel firstImpact = new JLabel("FIRST_IMPACT");
        JLabel secondImpact = new JLabel("SECOND_IMPACT");
        JLabel lifeBegin = new JLabel("LIFE_BEGIN");
        JLabel lifeEnd = new JLabel("LIFE_END");
        JLabel birthBegin = new JLabel("BIRTH_BEGIN");
        JLabel birthEnd = new JLabel("BIRTH_END");
        JTextField firstImpactBox = new JTextField();
        JTextField secondImpactBox = new JTextField();
        JTextField lifeBeginBox = new JTextField();
        JTextField lifeEndBox = new JTextField();
        JTextField birthBeginBox = new JTextField();
        JTextField birthEndBox = new JTextField();
        firstImpactBox.setText(String.valueOf(Constants.FIRST_IMPACT));
        secondImpactBox.setText(String.valueOf(Constants.SECOND_IMPACT));
        lifeBeginBox.setText(String.valueOf(Constants.LIFE_BEGIN));
        lifeEndBox.setText(String.valueOf(Constants.LIFE_END));
        birthBeginBox.setText(String.valueOf(Constants.BIRTH_BEGIN));
        birthEndBox.setText(String.valueOf(Constants.BIRTH_END));

        boxSettings(firstImpactBox, Constants.FIRST_IMPACT);
        boxSettings(secondImpactBox, Constants.SECOND_IMPACT);
        boxSettings(lifeBeginBox, Constants.LIFE_BEGIN);
        boxSettings(lifeEndBox, Constants.LIFE_END);
        boxSettings(birthBeginBox, Constants.BIRTH_BEGIN);
        boxSettings(birthEndBox, Constants.BIRTH_END);

        lifePanel.add(firstImpact);
        lifePanel.add(secondImpact);
        lifePanel.add(lifeBegin);
        lifePanel.add(lifeEnd);
        lifePanel.add(birthBegin);
        lifePanel.add(birthEnd);
        lifePanel.add(firstImpactBox);
        lifePanel.add(secondImpactBox);
        lifePanel.add(lifeBeginBox);
        lifePanel.add(lifeEndBox);
        lifePanel.add(birthBeginBox);
        lifePanel.add(birthEndBox);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            boolean ok = true;

            int lineWidth = lineWidthSlider.getValue();
            int hexSize = hexSizeSlider.getValue();
            int fieldWidth = fieldWidthSlider.getValue();
            int fieldHeight = fieldHeightSlider.getValue();
            double FIRST_IMPACT, SECOND_IMPACT, LIFE_BEGIN, BIRTH_BEGIN, BIRTH_END, LIFE_END;
            try {
                FIRST_IMPACT = Double.parseDouble(firstImpactBox.getText());
            } catch (NumberFormatException ex) {
                FIRST_IMPACT = Constants.FIRST_IMPACT;
            }
            try {
                SECOND_IMPACT = Double.parseDouble(secondImpactBox.getText());
            } catch (NumberFormatException ex) {
                SECOND_IMPACT = Constants.SECOND_IMPACT;
            }
            try {
                LIFE_BEGIN = Double.parseDouble(lifeBeginBox.getText());
            } catch (NumberFormatException ex) {
                LIFE_BEGIN = Constants.LIFE_BEGIN;
            }
            try {
                BIRTH_BEGIN = Double.parseDouble(birthBeginBox.getText());
            } catch (NumberFormatException ex) {
                BIRTH_BEGIN = Constants.BIRTH_BEGIN;
            }
            try {
                BIRTH_END = Double.parseDouble(birthEndBox.getText());
            } catch (NumberFormatException ex) {
                BIRTH_END = Constants.BIRTH_END;
            }
            try {
                LIFE_END = Double.parseDouble(lifeEndBox.getText());
            } catch (NumberFormatException ex) {
                LIFE_END = Constants.LIFE_END;
            }
            if (lineWidth > hexSize) {
                JOptionPane.showMessageDialog(null, "line width can't be bigger than hex size!");
                ok = false;
            }
            if (LIFE_BEGIN < 0 || BIRTH_BEGIN < 0 || BIRTH_END < 0 || LIFE_END < 0) {
                JOptionPane.showMessageDialog(null, "Parameters can't be negative!");
                ok = false;
            }
            if (LIFE_BEGIN > BIRTH_BEGIN) {
                JOptionPane.showMessageDialog(null, "LIVE_BEGIN can't be bigger than BIRTH_BEGIN");
                ok = false;
            }
            if (BIRTH_BEGIN > BIRTH_END) {
                JOptionPane.showMessageDialog(null, "BIRTH_BEGIN can't be more than BIRTH_END!");
                ok = false;
            }
            if (BIRTH_END > LIFE_END) {
                JOptionPane.showMessageDialog(null, "BIRTH_END can't be more than LIFE_END!");
                ok = false;
            }

            if (ok) {
                Constants.lineWidth = lineWidth;
                Constants.hexSize = hexSize;
                Constants.fieldHeight = fieldHeight;
                Constants.fieldWidth = fieldWidth;
                Constants.FIRST_IMPACT = FIRST_IMPACT;
                Constants.SECOND_IMPACT = SECOND_IMPACT;
                Constants.LIFE_BEGIN = LIFE_BEGIN;
                Constants.LIFE_END = LIFE_END;
                Constants.BIRTH_BEGIN = BIRTH_BEGIN;
                Constants.BIRTH_END = BIRTH_END;

                if (replaceSettingsButton.isSelected()) {
                    replaceEditItem.setSelected(true);
                    xorEditItem.setSelected(false);
                    replaceButton.setSelected(true);
                    life.setGameMode(GameMode.REPLACE);
                } else if (xorSettingsButton.isSelected()) {
                    replaceEditItem.setSelected(false);
                    xorEditItem.setSelected(true);
                    replaceButton.setSelected(false);
                    life.setGameMode(GameMode.XOR);
                }

                life.changeSettings();
                settingsDialog.setVisible(false);
                settingsDialog.dispose();
                isChanged = true;
            }
        });

        cancelButton.addActionListener(e -> {
            settingsDialog.setVisible(false);
            settingsDialog.dispose();
        });

        settingsPanel.add(cellPanel);
        settingsPanel.add(fieldPanel);
        settingsPanel.add(modePanel);
        settingsPanel.add(lifePanel);
        settingsPanel.add(buttonPanel);
        settingsDialog.add(settingsPanel);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
    }

    private boolean checkParameters(int fieldWidth, int fieldHeight, int lineWidth, int hexSize) {

        if (fieldWidth > 0 && fieldWidth < Constants.widthUpperBound && fieldHeight > 0 && fieldHeight < Constants.heightUpperBound
                && hexSize > 0 && hexSize < Constants.hexSizeUpperBound) {
            if (hexSize < Constants.lineWidthUpperBound) {
                return lineWidth < hexSize;
            } else return lineWidth < Constants.lineWidthUpperBound;
        }
        return false;
    }

    private void setParameters(int fieldWidth, int fieldHeight, int lineWidth, int hexSize) {
        Constants.fieldWidth = fieldWidth;
        Constants.fieldHeight = fieldHeight;
        Constants.lineWidth = lineWidth;
        Constants.hexSize = hexSize;
        life.clear();
    }

    private void createGameMenuItems(JMenu gameMenu, JToolBar toolBar) {
        runGameItem = new JRadioButtonMenuItem("Run");
        stepGameItem = new JMenuItem("Step");
        gameMenu.add(runGameItem);
        gameMenu.addSeparator();
        gameMenu.add(stepGameItem);

        runGameItem.setIcon(new ImageIcon(basePath + "run.png"));
        stepGameItem.setIcon(new ImageIcon(basePath + "step.png"));

        runButton = new JButton(runGameItem.getIcon());
        stepButton = new JButton(stepGameItem.getIcon());

        runGameItem.setToolTipText("Run the game");
        stepGameItem.setToolTipText("Step the game");

        runGameItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(runGameItem.getToolTipText());
            }
        });
        stepGameItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(stepGameItem.getToolTipText());
            }
        });
        runGameItem.addActionListener(e -> runGameItemListener());
        stepGameItem.addActionListener(e -> stepGameItemListener());

        runButton.setToolTipText(runGameItem.getToolTipText());
        stepButton.setToolTipText(stepGameItem.getToolTipText());

        for (MouseMotionListener i : runGameItem.getMouseMotionListeners()) {
            runButton.addMouseMotionListener(i);
        }
        for (MouseMotionListener i : stepGameItem.getMouseMotionListeners()) {
            stepButton.addMouseMotionListener(i);
        }
        for (ActionListener i : runGameItem.getActionListeners()) {
            runButton.addActionListener(i);
        }
        for (ActionListener i : stepGameItem.getActionListeners()) {
            stepButton.addActionListener(i);
        }

        toolBar.add(runButton);
        toolBar.add(stepButton);
        toolBar.addSeparator();
    }

    private void runGameItemListener() {
        if (isRunning) {
            runButton.setSelected(true);
            runGameItem.setSelected(true);
            replaceEditItem.setEnabled(true);
            replaceButton.setEnabled(true);
            xorEditItem.setEnabled(true);
            stepGameItem.setEnabled(true);
            stepButton.setEnabled(true);
            newFileItem.setEnabled(true);
            newButton.setEnabled(true);
            loadFileItem.setEnabled(true);
            loadButton.setEnabled(true);
            saveAsFileItem.setEnabled(true);
            saveFileItem.setEnabled(true);
            saveButton.setEnabled(true);
            settingsEditItem.setEnabled(true);
            settingsButton.setEnabled(true);
            clearEditItem.setEnabled(true);
            clearButton.setEnabled(true);
            runGameItem.setIcon(new ImageIcon(basePath + "run.png"));
            runButton.setIcon(runGameItem.getIcon());
            runGameItem.setText("Run");
            isRunning = false;
            panel.setInactive(false);
            life.stop();
        } else {
            runButton.setSelected(false);
            runGameItem.setSelected(false);
            replaceEditItem.setEnabled(false);
            replaceButton.setEnabled(false);
            xorEditItem.setEnabled(false);
            stepGameItem.setEnabled(false);
            stepButton.setEnabled(false);
            newFileItem.setEnabled(false);
            newButton.setEnabled(false);
            loadFileItem.setEnabled(false);
            loadButton.setEnabled(false);
            saveAsFileItem.setEnabled(false);
            saveFileItem.setEnabled(false);
            saveButton.setEnabled(false);
            settingsEditItem.setEnabled(false);
            settingsButton.setEnabled(false);
            clearEditItem.setEnabled(false);
            clearButton.setEnabled(false);
            runGameItem.setIcon(new ImageIcon(basePath + "pause.png"));
            runButton.setIcon(runGameItem.getIcon());
            runGameItem.setText("Pause");
            isRunning = true;
            panel.setInactive(true);
            life.run(runButton);
            isChanged = true;
        }
    }

    private void stepGameItemListener() {
        life.step();
        isChanged = true;
    }

    private void createViewMenuItems(JMenu viewMenu, JToolBar toolBar) {
        JCheckBoxMenuItem toolbarViewItem = new JCheckBoxMenuItem("Toolbar");
        JCheckBoxMenuItem statusViewItem = new JCheckBoxMenuItem("Status bar");
        JCheckBoxMenuItem impactViewItem = new JCheckBoxMenuItem("Impact");
        viewMenu.add(toolbarViewItem);
        viewMenu.add(statusViewItem);
        viewMenu.addSeparator();
        viewMenu.add(impactViewItem);

        impactViewItem.setIcon(new ImageIcon(basePath + "impact.png"));

        JToggleButton impactButton = new JToggleButton(impactViewItem.getIcon());

        toolbarViewItem.setToolTipText("Remove tool bar");
        statusViewItem.setToolTipText("Remove status bar");
        impactViewItem.setToolTipText("Turn on impact values");

        toolbarViewItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(toolbarViewItem.getToolTipText());
            }
        });
        statusViewItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(statusViewItem.getToolTipText());
            }
        });
        impactViewItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(impactViewItem.getToolTipText());
            }
        });
        toolbarViewItem.addActionListener(e -> toolbarViewItemListener());
        statusViewItem.addActionListener(e -> statusViewItemListener());
        impactViewItem.addActionListener(e -> impactViewItemListener(impactViewItem, impactButton));

        impactButton.setToolTipText(impactViewItem.getToolTipText());

        for (MouseMotionListener i : impactViewItem.getMouseMotionListeners()) {
            impactButton.addMouseMotionListener(i);
        }
        for (ActionListener i : impactViewItem.getActionListeners()) {
            impactButton.addActionListener(i);
        }

        toolBar.add(impactButton);

        impactButton.setSelected(false);
        impactViewItem.setSelected(false);

        toolBar.addSeparator();
        toolbarViewItem.setSelected(true);
        statusViewItem.setSelected(true);
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

    private void impactViewItemListener(JCheckBoxMenuItem impactItem, JToggleButton impactButton) {
        if (panel.getImpacts()) {
            impactButton.setSelected(false);
            impactItem.setSelected(false);
        } else {
            impactButton.setSelected(true);
            impactItem.setSelected(true);
        }
        panel.showImpacts();
    }

    private void createAboutMenuItems(JMenu aboutMenu, JToolBar toolBar) {
        JMenuItem aboutItem = new JMenuItem("About...");
        aboutMenu.add(aboutItem);

        aboutItem.setIcon(new ImageIcon(basePath + "about.png"));
        aboutItem.setToolTipText("Open About Section");

        aboutItem.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(aboutItem.getToolTipText());
            }
        });
        aboutItem.addActionListener(e -> aboutItemListener());

        JButton aboutButton = new JButton(aboutItem.getIcon());
        aboutButton.setToolTipText(aboutItem.getToolTipText());
        for (MouseMotionListener i : aboutItem.getMouseMotionListeners()) {
            aboutButton.addMouseMotionListener(i);
        }
        for (ActionListener i : aboutItem.getActionListeners()) {
            aboutButton.addActionListener(i);
        }

        toolBar.add(aboutButton);
    }

    private void aboutItemListener() {

        JDialog aboutDialog = new JDialog();
        JPanel panel = new JPanel();
        JPanel info = new JPanel(new GridLayout(4, 1, 10, 10));
        aboutDialog.setTitle("About \"Life\" Game by Galios Max");

        ImageIcon me = new ImageIcon(basePath + "me.jpg");
        JLabel photo = new JLabel(me);
        JLabel gameInfo = new JLabel("Game: Life Game v1.0");
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
        info.add(gameInfo);
        info.add(authorInfo);
        info.add(groupInfo);
        info.add(yearInfo);
        panel.add(info);
        panel.add(okButton);
        aboutDialog.add(panel);
        aboutDialog.pack();
        aboutDialog.setVisible(true);
    }

}

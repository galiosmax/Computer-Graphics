package ru.nsu.fit.g16203.galios.model;

import ru.nsu.fit.g16203.galios.view.View;
import ru.nsu.fit.g16203.galios.view.Colors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Life {

    private Cell[][] field;
    private Point[][] cellCentres;
    private double[][] impacts;
    private View view;
    private Colors colors;

    private GameMode gameMode = GameMode.REPLACE;

    private JButton runButton;
    private Timer timer;

    public Life(View view) {
        this.view = view;
        colors = new Colors();
        field = new Cell[Constants.fieldWidth][Constants.fieldHeight];
        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                if ((j % 2 == 1 && i < Constants.fieldWidth - 1) || (j % 2 == 0))
                    field[i][j] = new Cell();

            }
        }
        cellCentres = new Point[Constants.fieldWidth][Constants.fieldHeight];
        impacts = new double[Constants.fieldWidth][Constants.fieldHeight];
        view.run(this);
        view.drawField();
        calculateImpacts();
    }

    public void setCellCoordinate(int i, int j, int x0, int y0) {
        cellCentres[i][j] = new Point(x0, y0);
    }

    public Point getCellByCoordinate(int x, int y) {

        int xMin = 0, yMin = 0;
        double radMin = Double.MAX_VALUE;
        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                Point point = cellCentres[i][j];
                if (point != null) {
                    double rad = Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2);

                    if (radMin > rad) {
                        radMin = rad;
                        xMin = i;
                        yMin = j;
                    }
                }
            }
        }
        return new Point(xMin, yMin);
    }

    public Point getCoordinateByCell(int i, int j) {
        return cellCentres[i][j];
    }

    public void changeState(int i, int j) {
        try {
            Cell cell = field[i][j];
            if (cell.getCondition() == Condition.DEAD) {
                cell.setCondition(Condition.ALIVE);
                Point point = cellCentres[i][j];
                view.span(point.x, point.y, colors.getColor(Condition.DEAD).getRGB(), colors.getColor(Condition.ALIVE).getRGB());

                calculateImpacts();
            } else {
                if (gameMode == GameMode.XOR) {
                    cell.setCondition(Condition.DEAD);
                    Point point = cellCentres[i][j];
                    view.span(point.x, point.y, colors.getColor(Condition.ALIVE).getRGB(), colors.getColor(Condition.DEAD).getRGB());
                    calculateImpacts();
                }
            }
            view.repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Wrong coordinate");
        }
    }

    public List<Point> getAliveCells() {

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                if ((j % 2 == 1 && i < Constants.fieldWidth - 1) || (j % 2 == 0)) {
                    if (field[i][j].getCondition() == Condition.ALIVE) {
                        points.add(new Point(i, j));
                    }
                }
            }
        }
        return points;
    }

    public void changeSettings() {
        Cell[][] newField = new Cell[Constants.fieldWidth][Constants.fieldHeight];
        cellCentres = new Point[Constants.fieldWidth][Constants.fieldHeight];
        impacts = new double[Constants.fieldWidth][Constants.fieldHeight];

        view.drawField();

        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                if ((j % 2 == 1 && i < Constants.fieldWidth - 1) || (j % 2 == 0)) {
                    try {
                        newField[i][j] = field[i][j];
                        if (newField[i][j].getCondition() == Condition.ALIVE) {
                            Point point = cellCentres[i][j];
                            view.span(point.x, point.y, colors.getColor(Condition.DEAD).getRGB(), colors.getColor(Condition.ALIVE).getRGB());
                        }
                    } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                        newField[i][j] = new Cell();
                    }
                }
            }
        }
        field = newField;
        calculateImpacts();
    }

    public void clear() {
        field = new Cell[Constants.fieldWidth][Constants.fieldHeight];
        cellCentres = new Point[Constants.fieldWidth][Constants.fieldHeight];
        impacts = new double[Constants.fieldWidth][Constants.fieldHeight];

        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                if ((j % 2 == 1 && i < Constants.fieldWidth - 1) || (j % 2 == 0)) {
                    field[i][j] = new Cell();
                }
            }
        }
        view.drawField();
        calculateImpacts();
    }

    public void run(JButton runButton) {
        this.runButton = runButton;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }

    public void step() {

        boolean changed = false;
        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                if ((j % 2 == 1 && i < Constants.fieldWidth - 1) || (j % 2 == 0)) {
                    Cell cell = field[i][j];
                    double impact = cell.getImpact();

                    if (cell.getCondition() == Condition.DEAD) {
                        if (impact >= Constants.BIRTH_BEGIN && impact <= Constants.BIRTH_END) {
                            cell.setCondition(Condition.ALIVE);
                            Point point = cellCentres[i][j];
                            view.span(point.x, point.y, colors.getColor(Condition.DEAD).getRGB(), colors.getColor(Condition.ALIVE).getRGB());
                            changed = true;
                        }
                    } else {
                        if (!(impact >= Constants.LIFE_BEGIN && impact <= Constants.LIFE_END)) {
                            cell.setCondition(Condition.DEAD);
                            Point point = cellCentres[i][j];
                            view.span(point.x, point.y, colors.getColor(Condition.ALIVE).getRGB(), colors.getColor(Condition.DEAD).getRGB());
                            changed = true;
                        }
                    }
                }
            }
        }
        if (changed) {
            calculateImpacts();
            view.repaint();
        } else {
            if (runButton != null) {
                runButton.doClick();
            }
        }
    }

    private void calculateImpacts() {
        for (int i = 0; i < Constants.fieldWidth; i++) {
            for (int j = 0; j < Constants.fieldHeight; j++) {
                if ((j % 2 == 1 && i < Constants.fieldWidth - 1) || (j % 2 == 0)) {
                    calculateImpact(i, j);
                }
            }
        }
    }

    private void calculateImpact(int i, int j) {

        double impact = 0;
        if (j % 2 == 0) {
            if (i > 0 && j > 0 && field[i - 1][j - 1].getCondition() == Condition.ALIVE) {
                impact += Constants.FIRST_IMPACT;
            }
            if (i > 0 && j < Constants.fieldHeight - 1 && field[i - 1][j + 1].getCondition() == Condition.ALIVE) {
                impact += Constants.FIRST_IMPACT;
            }
            if (i < Constants.fieldWidth - 2 && j > 0 && field[i + 1][j - 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
            if (i < Constants.fieldWidth - 2 && j < Constants.fieldHeight - 1 && field[i + 1][j + 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
            if (i > 1 && j < Constants.fieldHeight - 1 && field[i - 2][j + 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
            if (i > 1 && j > 0 && field[i - 2][j - 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
        } else {
            if (i < Constants.fieldWidth - 1 && j > 0 && field[i + 1][j - 1].getCondition() == Condition.ALIVE) {
                impact += Constants.FIRST_IMPACT;
            }
            if (i < Constants.fieldWidth - 1 && j < Constants.fieldHeight - 1 && field[i + 1][j + 1].getCondition() == Condition.ALIVE) {
                impact += Constants.FIRST_IMPACT;
            }
            if (i < Constants.fieldWidth - 3 && j > 0 && field[i + 2][j - 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
            if (i < Constants.fieldWidth - 3 && j < Constants.fieldHeight - 1 && field[i + 2][j + 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
            if (i > 0 && j < Constants.fieldHeight - 1 && field[i - 1][j + 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
            if (i > 0 && j > 0 && field[i - 1][j - 1].getCondition() == Condition.ALIVE) {
                impact += Constants.SECOND_IMPACT;
            }
        }

        if (i > 0 && field[i - 1][j].getCondition() == Condition.ALIVE) {
            impact += Constants.FIRST_IMPACT;
        }
        if (i < Constants.fieldWidth - ((j + 1) % 2) && j > 0 && field[i][j - 1].getCondition() == Condition.ALIVE) {
            impact += Constants.FIRST_IMPACT;
        }
        if (i < Constants.fieldWidth - 1 - (j % 2) && field[i + 1][j].getCondition() == Condition.ALIVE) {
            impact += Constants.FIRST_IMPACT;
        }
        if (i < Constants.fieldWidth - ((j + 1) % 2) && j < Constants.fieldHeight - 1 && field[i][j + 1].getCondition() == Condition.ALIVE) {
            impact += Constants.FIRST_IMPACT;
        }
        if (j > 1 && field[i][j - 2].getCondition() == Condition.ALIVE) {
            impact += Constants.SECOND_IMPACT;
        }
        if (j < Constants.fieldHeight - 2 && field[i][j + 2].getCondition() == Condition.ALIVE) {
            impact += Constants.SECOND_IMPACT;
        }
        field[i][j].setImpact((double) Math.round(impact * 100) / 100d);
        impacts[i][j] = (double) Math.round(impact * 100) / 100d;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public double[][] getImpacts() {
        return impacts;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}

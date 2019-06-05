package ru.nsu.fit.g16203.galios;

import ru.nsu.fit.g16203.galios.controller.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        MainFrame mainFrame = new MainFrame();

    }

}

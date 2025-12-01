package com.example.logisim;

import javax.swing.SwingUtilities;
import com.example.logisim.gui.MainWindow;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow w = new MainWindow();
            w.setVisible(true);
        });
    }
}
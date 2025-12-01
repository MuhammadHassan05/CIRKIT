package com.example.logisim.gui;

import javax.swing.*;
import java.awt.*;

public class CircuitDesigner extends JFrame {
    public CircuitDesigner() {
        setTitle("Logisim Circuit Designer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a drawing panel
        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.WHITE);
        add(drawingPanel, BorderLayout.CENTER);

        // Add buttons for actions
        JButton addButton = new JButton("Add Component");
        add(addButton, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CircuitDesigner().setVisible(true));
    }
}

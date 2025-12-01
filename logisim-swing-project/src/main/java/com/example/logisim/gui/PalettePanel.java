package com.example.logisim.gui;

import com.example.logisim.model.Gate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class PalettePanel extends JPanel {
    public PalettePanel(Consumer<Gate.Type> selectionCallback) {
        setPreferredSize(new Dimension(160, 0));
        setLayout(new GridLayout(0,1));
        add(new JLabel("Palette"));

        JButton and = new JButton("AND");
        and.addActionListener((ActionEvent e) -> selectionCallback.accept(Gate.Type.AND));
        add(and);

        JButton or = new JButton("OR");
        or.addActionListener((ActionEvent e) -> selectionCallback.accept(Gate.Type.OR));
        add(or);

        JButton not = new JButton("NOT");
        not.addActionListener((ActionEvent e) -> selectionCallback.accept(Gate.Type.NOT));
        add(not);

        JButton input = new JButton("INPUT");
        input.addActionListener((ActionEvent e) -> selectionCallback.accept(Gate.Type.INPUT));
        add(input);

        JButton output = new JButton("OUTPUT");
        output.addActionListener((ActionEvent e) -> selectionCallback.accept(Gate.Type.OUTPUT));
        add(output);
    }
}
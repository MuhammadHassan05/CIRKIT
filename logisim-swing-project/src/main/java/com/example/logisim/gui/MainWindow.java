package com.example.logisim.gui;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;
import com.example.logisim.db.*;
import com.example.logisim.export.LogisimExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class MainWindow extends JFrame {
    private final Circuit circuit = new Circuit();
    private final AtomicReference<Gate.Type> selectedType = new AtomicReference<>();
    private final com.example.logisim.db.FileDatabase db;

    public MainWindow() {
        super("CirKit — Logisim Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        db = new com.example.logisim.db.FileDatabase();
        db.init();

        PalettePanel palette = new PalettePanel(t -> selectedType.set(t));
        add(palette, BorderLayout.WEST);

        TruthTablePanel truthPanel = new TruthTablePanel(circuit);
        CircuitCanvas canvas = new CircuitCanvas(circuit, selectedType, g -> truthPanel.showFor(g));
        add(canvas, BorderLayout.CENTER);
        add(truthPanel, BorderLayout.EAST);

        JToolBar toolBar = new JToolBar();
        JButton btnNew = new JButton("New");
        btnNew.addActionListener(e -> {
            circuit.clear();
            canvas.repaint();
        });
        toolBar.add(btnNew);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> db.saveCircuit("default", circuit));
        toolBar.add(btnSave);

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(e -> {
            Circuit loaded = db.loadCircuit("default");
            if (loaded != null) {
                circuit.clear();
                circuit.addAll(loaded.getGates());
                canvas.repaint();
            }
        });
        toolBar.add(btnLoad);

        JToggleButton wireToggle = new JToggleButton("Wire");
        toolBar.add(wireToggle);
        wireToggle.addActionListener(e -> canvas.setWireMode(wireToggle.isSelected()));

        JButton btnExport = new JButton("Export .circ");
        btnExport.addActionListener(e -> {
            try {
                File out = new File(System.getProperty("user.home"), "circuit.circ");
                LogisimExporter.export(circuit, out);
                JOptionPane.showMessageDialog(this, "Exported to: " + out.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        });
        toolBar.add(btnExport);

        add(toolBar, BorderLayout.NORTH);
    }
}
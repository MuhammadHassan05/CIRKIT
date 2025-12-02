package com.example.logisim.gui;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;
import com.example.logisim.model.Connection;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class TruthTablePanel extends JPanel {
    private final JTextArea area = new JTextArea();
    private final Circuit circuit;

    public TruthTablePanel(Circuit circuit) {
        this.circuit = circuit;
        setPreferredSize(new Dimension(300, 0));
        setLayout(new BorderLayout());
        add(new JLabel("Truth Table"), BorderLayout.NORTH);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void showFor(Gate g) {
        if (g == null) {
            area.setText("No gate selected.");
            return;
        }
        // find immediate input source gates (ordered by insertion order)
        List<Connection> ins = new ArrayList<>();
        for (Connection c : circuit.getConnections()) if (c.toId.equals(g.id)) ins.add(c);
        int n = ins.size();
        if (n == 0) {
            area.setText("Gate has no inputs. Current state: " + (circuit.evaluateGate(g.id, null) ? "ON" : "OFF"));
            return;
        }
        if (n > 4) {
            area.setText("Gate has " + n + " inputs — truth table too large to display.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Gate: %s (id=%s)\n", g.type.name(), g.id));
        sb.append("Inputs: ");
        List<String> varIds = new ArrayList<>();
        for (int i=0;i<ins.size();i++) {
            String sid = ins.get(i).fromId;
            varIds.add(sid);
            sb.append(sid).append(i<ins.size()-1?", ":"\n");
        }
        sb.append('\n');
        // header
        for (int i=0;i<n;i++) sb.append(String.format(" I%d ", i));
        sb.append(" | OUT\n");
        sb.append("".repeat(Math.max(0, n*4+5))).append("\n");
        int rows = 1<<n;
        for (int r=0;r<rows;r++) {
            Map<String,Boolean> overrides = new HashMap<>();
            for (int i=0;i<n;i++) {
                boolean bit = ((r >> (n-1-i)) & 1) == 1;
                overrides.put(varIds.get(i), bit);
                sb.append(bit?" 1  ":" 0  ");
            }
            boolean out = circuit.evaluateGate(g.id, overrides);
            sb.append(" |  ").append(out?"1":"0").append('\n');
        }
        // current evaluated state
        sb.append('\n');
        boolean cur = circuit.evaluateGate(g.id, null);
        sb.append("Current output: ").append(cur?"ON":"OFF");
        area.setText(sb.toString());
    }
}

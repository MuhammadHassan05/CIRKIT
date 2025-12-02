package com.example.logisim.gui;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;
import com.example.logisim.model.Connection;

import javax.swing.*;
import javax.swing.AbstractAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

public class CircuitCanvas extends JPanel {
    private final Circuit circuit;
    private final AtomicReference<Gate.Type> selectedType;
    private final int grid = 20;

    private java.util.List<Gate> selectedGates = new java.util.ArrayList<>();
    private Gate hoveredGate = null;
    private Point mousePos = new Point(0,0);
    // current wire start id (null when not creating a wire)
    private String wireFromId = null;
    // when true, left-click starts/completes wires (toolbar toggle)
    private boolean wireMode = false;
    private Rectangle selectionRect = null;
    // for group dragging
    private java.util.Map<String, Point> groupDragOffsets = new java.util.HashMap<>();

    private final java.util.function.Consumer<Gate> selectionCallback;

    public CircuitCanvas(Circuit circuit, AtomicReference<Gate.Type> selectedType, java.util.function.Consumer<Gate> selectionCallback) {
        this.circuit = circuit;
        this.selectedType = selectedType;
        this.selectionCallback = selectionCallback;
        setBackground(Color.WHITE);
        setFocusable(true);

        // Bind Delete key to remove selected gate
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DELETE"), "delete");
        getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!selectedGates.isEmpty()) {
                    circuit.takeSnapshot();
                    for (Gate g : new java.util.ArrayList<>(selectedGates)) {
                        circuit.removeGateById(g.id);
                    }
                    selectedGates.clear();
                    repaint();
                }
            }
        });

        // Escape cancels wire creation
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelWire");
        getActionMap().put("cancelWire", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wireFromId = null;
                repaint();
            }
        });
        // Undo/Redo
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control Z"), "undo");
        getActionMap().put("undo", new AbstractAction() { public void actionPerformed(ActionEvent e) { circuit.undo(); repaint(); } });
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control Y"), "redo");
        getActionMap().put("redo", new AbstractAction() { public void actionPerformed(ActionEvent e) { circuit.redo(); repaint(); } });

        MouseHandler mh = new MouseHandler();
        addMouseListener(mh);
        addMouseMotionListener(mh);
    }

    public void setWireMode(boolean on) {
        this.wireMode = on;
        // cancel any in-progress wire when toggling off
        if (!on) {
            this.wireFromId = null;
        }
        repaint();
    }

    private class MouseHandler extends MouseAdapter {
        private Gate dragging = null;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                // find gate under mouse
                for (Gate g : circuit.getGates()) {
                    Rectangle r = new Rectangle(g.x, g.y, 48, 24);
                    if (r.contains(e.getPoint())) {
                        // Shift+click or toolbar wire mode starts/completes a wire between gates
                        if (wireMode || e.isShiftDown()) {
                            circuit.takeSnapshot();
                            if (wireFromId == null) {
                                wireFromId = g.id;
                            } else {
                                if (!wireFromId.equals(g.id)) {
                                    circuit.addConnection(new Connection(wireFromId, g.id));
                                }
                                wireFromId = null;
                            }
                            repaint();
                            return;
                        }
                        // Ctrl-click toggles multi-select
                        if (e.isControlDown()) {
                            if (selectedGates.removeIf(x->x.id.equals(g.id))) {
                                // removed
                            } else {
                                selectedGates.add(g);
                            }
                            // notify selection change
                            repaint();
                            if (selectionCallback != null) selectionCallback.accept(selectedGates.size()==1?selectedGates.get(0):null);
                            return;
                        }
                        // normal selection + drag
                        if (!selectedGates.stream().anyMatch(x->x.id.equals(g.id))) {
                            selectedGates.clear();
                            selectedGates.add(g);
                        }
                        if (selectionCallback != null) selectionCallback.accept(selectedGates.size()==1?selectedGates.get(0):null);
                        dragging = g;
                        // prepare offsets for group drag
                        groupDragOffsets.clear();
                        for (Gate sg : selectedGates) {
                            groupDragOffsets.put(sg.id, new Point(e.getX() - sg.x, e.getY() - sg.y));
                        }
                        requestFocusInWindow();
                        repaint();
                        return;
                    }
                }
                // not on a gate -> if wireMode is active, start/clear wire; otherwise place new if type selected
                if (wireMode) {
                    // clicking empty space cancels any in-progress wire
                    wireFromId = null;
                    selectedGates.clear();
                    repaint();
                } else {
                    Gate.Type t = selectedType.get();
                    if (t != null) {
                        circuit.takeSnapshot();
                        int gx = (e.getX() / grid) * grid;
                        int gy = (e.getY() / grid) * grid;
                        String id = "g" + System.currentTimeMillis();
                        Gate g = new Gate(id, t, gx, gy);
                        circuit.addGate(g);
                        selectedGates.clear(); selectedGates.add(g);
                        repaint();
                    } else {
                        // clear selection when clicking empty space without a selected type
                        selectedGates.clear();
                        // start selection rectangle
                        selectionRect = new Rectangle(e.getX(), e.getY(), 0, 0);
                        repaint();
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                // right-click: if over a gate, toggle INPUT gate state; otherwise clear selection and cancel wire
                boolean handled = false;
                for (Gate g : circuit.getGates()) {
                    Rectangle r = new Rectangle(g.x, g.y, 48, 24);
                    if (r.contains(e.getPoint())) {
                        if (g.type == Gate.Type.INPUT) {
                            circuit.takeSnapshot();
                            g.value = !g.value;
                            handled = true;
                            repaint();
                            break;
                        }
                    }
                }
                if (!handled) {
                    selectedGates.clear();
                    wireFromId = null;
                    if (selectionCallback != null) selectionCallback.accept(null);
                    repaint();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectionRect != null) {
                selectionRect.width = e.getX() - selectionRect.x;
                selectionRect.height = e.getY() - selectionRect.y;
                // update temporary hover selection
                repaint();
                return;
            }
            if (dragging != null) {
                // move all selected gates
                for (Gate sg : selectedGates) {
                    Point off = groupDragOffsets.get(sg.id);
                    if (off == null) continue;
                    int nx = e.getX() - off.x;
                    int ny = e.getY() - off.y;
                    sg.x = (nx / grid) * grid;
                    sg.y = (ny / grid) * grid;
                }
                mousePos = e.getPoint();
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mousePos = e.getPoint();
            // update hovered gate
            hoveredGate = null;
            for (Gate g : circuit.getGates()) {
                Rectangle r = new Rectangle(g.x, g.y, 48, 24);
                if (r.contains(e.getPoint())) { hoveredGate = g; break; }
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectionRect != null) {
                // select gates inside rect
                Rectangle r = selectionRect.getWidth()<0 || selectionRect.getHeight()<0 ? selectionRect.getBounds() : selectionRect;
                selectedGates.clear();
                for (Gate g : circuit.getGates()) {
                    Rectangle gr = new Rectangle(g.x, g.y, 48, 24);
                    if (r.intersects(gr)) selectedGates.add(g);
                }
                selectionRect = null;
                repaint();
                return;
            }
            // finish drag: record snapshot (only if moved)
            if (!groupDragOffsets.isEmpty()) {
                circuit.takeSnapshot();
            }
            dragging = null;
            groupDragOffsets.clear();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(230,230,230));
        for (int x=0; x<getWidth(); x+=grid) g.drawLine(x,0,x,getHeight());
        for (int y=0; y<getHeight(); y+=grid) g.drawLine(0,y,getWidth(),y);

        // draw connections
        g.setColor(Color.BLUE);
        for (Connection c : circuit.getConnections()) {
            Gate a = circuit.getGates().stream().filter(gt -> gt.id.equals(c.fromId)).findFirst().orElse(null);
            Gate b = circuit.getGates().stream().filter(gt -> gt.id.equals(c.toId)).findFirst().orElse(null);
            if (a != null && b != null) {
                int ax = a.x + 24, ay = a.y + 12;
                int bx = b.x + 24, by = b.y + 12;
                g.drawLine(ax, ay, bx, by);
            }
        }

        // if a wire is being created (Shift+click started), draw current rubber-band
        if (wireFromId != null && mousePos != null) {
            Gate from = circuit.getGates().stream().filter(gt -> gt.id.equals(wireFromId)).findFirst().orElse(null);
            if (from != null) {
                int fx = from.x + 24, fy = from.y + 12;
                g.setColor(Color.GRAY);
                g.drawLine(fx, fy, mousePos.x, mousePos.y);
                g.fillOval(mousePos.x-3, mousePos.y-3, 6, 6);
            }
        }

        // draw selection rectangle
        if (selectionRect != null) {
            g.setColor(new Color(0,120,215,80));
            Rectangle r = selectionRect.getWidth()<0 || selectionRect.getHeight()<0 ? selectionRect.getBounds() : selectionRect;
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(new Color(0,120,215,180));
            g.drawRect(r.x, r.y, r.width, r.height);
        }

        // draw gates
        for (Gate gate : circuit.getGates()) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(gate.x, gate.y, 48, 24);
            g.setColor(Color.BLACK);
            g.drawRect(gate.x, gate.y, 48, 24);
            g.drawString(gate.type.name(), gate.x + 6, gate.y + 16);
            // selection highlights for multi-select
            boolean isSelected = selectedGates.stream().anyMatch(sg->sg.id.equals(gate.id));
            if (isSelected) {
                g.setColor(new Color(255, 220, 0, 180));
                g.drawRect(gate.x-2, gate.y-2, 52, 28);
            }
            // hover highlight (only if not selected)
            if (!isSelected && hoveredGate != null && hoveredGate.id.equals(gate.id)) {
                g.setColor(new Color(255, 140, 0, 140));
                g.drawRect(gate.x-1, gate.y-1, 50, 26);
            }
        }
    }
}

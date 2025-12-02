package com.example.logisim.model;

import java.util.*;

public class Circuit {
    private final List<Gate> gates = new ArrayList<>();
    private final List<Connection> connections = new ArrayList<>();
    public void addGate(Gate g){ gates.add(g); }
    public void addConnection(Connection c){ connections.add(c); }
    public void clear() { gates.clear(); }
    public void addAll(List<Gate> list) { gates.addAll(list); }
    public void removeGate(Gate g) { gates.remove(g); }
    // Remove gate by id and all connections referencing it
    public void removeGateById(String id) {
        gates.removeIf(g -> g.id.equals(id));
        connections.removeIf(c -> c.fromId.equals(id) || c.toId.equals(id));
    }
    public List<Gate> getGates(){ return Collections.unmodifiableList(gates); }
    public List<Connection> getConnections(){ return Collections.unmodifiableList(connections); }

    // --- Simple snapshot-based undo/redo ---
    private final Deque<Snapshot> undoStack = new ArrayDeque<>();
    private final Deque<Snapshot> redoStack = new ArrayDeque<>();

    public void takeSnapshot() {
        undoStack.push(createSnapshot());
        redoStack.clear();
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public void undo() {
        if (!canUndo()) return;
        Snapshot s = undoStack.pop();
        redoStack.push(createSnapshot());
        restoreSnapshot(s);
    }

    public void redo() {
        if (!canRedo()) return;
        Snapshot s = redoStack.pop();
        undoStack.push(createSnapshot());
        restoreSnapshot(s);
    }

    private Snapshot createSnapshot() {
        List<Gate> gcopy = new ArrayList<>();
        for (Gate g : gates) gcopy.add(new Gate(g.id, g.type, g.x, g.y, g.value));
        List<Connection> ccopy = new ArrayList<>();
        for (Connection c : connections) ccopy.add(new Connection(c.fromId, c.toId));
        return new Snapshot(gcopy, ccopy);
    }

    private void restoreSnapshot(Snapshot s) {
        gates.clear(); gates.addAll(s.gates);
        connections.clear(); connections.addAll(s.connections);
    }

    private static class Snapshot {
        final List<Gate> gates;
        final List<Connection> connections;
        Snapshot(List<Gate> gates, List<Connection> connections) { this.gates = gates; this.connections = connections; }
    }

    // Evaluate gate output recursively. `overrides` can set specific INPUT gate values for temporary evaluation.
    public boolean evaluateGate(String gateId, java.util.Map<String,Boolean> overrides) {
        return evaluateGateInternal(gateId, overrides, new HashSet<>());
    }

    private boolean evaluateGateInternal(String gateId, java.util.Map<String,Boolean> overrides, Set<String> visited) {
        if (visited.contains(gateId)) return false; // avoid cycles
        visited.add(gateId);
        Gate g = gates.stream().filter(gt -> gt.id.equals(gateId)).findFirst().orElse(null);
        if (g == null) return false;
        // allow overriding any gate's logical value for temporary evaluation (useful for truth table variables)
        if (overrides != null && overrides.containsKey(g.id)) return overrides.get(g.id);
        if (g.type == Gate.Type.INPUT) {
            return g.value;
        }
        // collect input values from incoming connections
        List<Boolean> inputs = new ArrayList<>();
        for (Connection c : connections) {
            if (c.toId.equals(g.id)) {
                boolean val = evaluateGateInternal(c.fromId, overrides, visited);
                inputs.add(val);
            }
        }
        // default for no inputs
        if (g.type == Gate.Type.NOT) {
            boolean in = !inputs.isEmpty() && Boolean.TRUE.equals(inputs.get(0));
            return !in;
        }
        switch (g.type) {
            case AND: {
                boolean a = true; for (Boolean b : inputs) a = a && Boolean.TRUE.equals(b); return a;
            }
            case OR: {
                boolean a = false; for (Boolean b : inputs) a = a || Boolean.TRUE.equals(b); return a;
            }
            case XOR: {
                boolean a = false; for (Boolean b : inputs) a = a ^ Boolean.TRUE.equals(b); return a;
            }
            case OUTPUT: {
                return !inputs.isEmpty() && Boolean.TRUE.equals(inputs.get(0));
            }
            default: return false;
        }
    }
}
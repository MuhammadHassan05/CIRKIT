package com.example.logisim.export;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;

import java.io.*;
import java.util.List;

public class LogisimExporter {
    // Map our Gate.Type to a Logisim component name (best-effort)
    private static String mapToLogisimName(Gate.Type t) {
        switch (t) {
            case AND: return "AND Gate";
            case OR: return "OR Gate";
            case NOT: return "NOT Gate";
            case XOR: return "XOR Gate";
            case INPUT: return "Input Pin";
            case OUTPUT: return "Output Pin";
            default: return t.name();
        }
    }

    // Format a point as Logisim expects (x,y)
    private static String fmtPoint(int x, int y) {
        return String.format("(%d,%d)", x, y);
    }

    public static void export(Circuit c, File out) throws IOException {
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"))) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            w.write("<project source=\"CirKit\" version=\"1.0\">\n");
            w.write("  <circuit name=\"main\">\n");

            List<Gate> gates = c.getGates();
            for (Gate g : gates) {
                String name = mapToLogisimName(g.type);
                // Logisim uses a "loc" attribute for component placement in many schemas
                String loc = fmtPoint(g.x, g.y);
                w.write(String.format("    <comp name=\"%s\" loc=\"%s\">\n", escapeXml(name), escapeXml(loc)));
                // add an id attribute so we can cross-reference when reading (custom attribute)
                w.write(String.format("      <a name=\"id\" val=\"%s\"/>\n", escapeXml(g.id)));
                w.write("    </comp>\n");
            }

            // compute per-gate incoming/outgoing connection lists so we can place wires at distinct pin locations
            java.util.Map<String, java.util.List<com.example.logisim.model.Connection>> incoming = new java.util.HashMap<>();
            java.util.Map<String, java.util.List<com.example.logisim.model.Connection>> outgoing = new java.util.HashMap<>();
            for (com.example.logisim.model.Connection conn : c.getConnections()) {
                incoming.computeIfAbsent(conn.toId, k -> new java.util.ArrayList<>()).add(conn);
                outgoing.computeIfAbsent(conn.fromId, k -> new java.util.ArrayList<>()).add(conn);
            }

            for (com.example.logisim.model.Connection conn : c.getConnections()) {
                Gate a = findGateById(gates, conn.fromId);
                Gate b = findGateById(gates, conn.toId);
                if (a == null || b == null) continue;

                java.util.List<com.example.logisim.model.Connection> outs = outgoing.getOrDefault(a.id, java.util.Collections.emptyList());
                java.util.List<com.example.logisim.model.Connection> ins = incoming.getOrDefault(b.id, java.util.Collections.emptyList());

                int outIdx = outs.indexOf(conn);
                int outTotal = outs.size();
                int inIdx = ins.indexOf(conn);
                int inTotal = ins.size();

                Point ap = attachPointForOutput(a, outIdx, outTotal);
                Point bp = attachPointForInput(b, inIdx, inTotal);

                w.write(String.format("    <wire from=\"%s\" to=\"%s\"/>\n", fmtPoint(ap.x, ap.y), fmtPoint(bp.x, bp.y)));
            }

            w.write("  </circuit>\n");
            w.write("</project>\n");
        }
    }

    private static Gate findGateById(List<Gate> gates, String id) {
        for (Gate g : gates) if (g.id.equals(id)) return g;
        return null;
    }

    // Helper to compute an output attach point for a gate (right-middle by default)
    private static Point attachPointForOutput(Gate g, int index, int total) {
        int w = 48, h = 24;
        int x = g.x, y = g.y;

        // Per-component output templates
        if (total <= 1 || index < 0) {
            switch (g.type) {
                case INPUT:
                    // Input component's output on the right middle
                    return new Point(x + w, y + h/2);
                case NOT:
                    // NOT gate: single output on right middle
                    return new Point(x + w, y + h/2);
                case AND:
                case OR:
                case XOR:
                    // common case: single output on right-middle
                    return new Point(x + w, y + h/2);
                    default:
                        return new Point(x + w, y + h/2);
            }
        }

        // distribute outputs along the right edge (for multi-output components)
        double step = (double) h / (total + 1);
        int py = (int) Math.round(y + step * (index + 1));
        return new Point(x + w, py);
    }

    // Helper to compute an input attach point for a gate (left-middle by default)
    private static Point attachPointForInput(Gate g, int index, int total) {
        int h = 24;
        int x = g.x, y = g.y;
        if (total <= 1 || index < 0) {
            return new Point(x, y + h/2);
        }

        // Per-component pin templates for inputs (prefer specific templates where appropriate)
        switch (g.type) {
            case AND:
            case OR:
            case XOR:
                // common visual arrangement: spread inputs vertically along left edge
                switch (total) {
                    case 2: {
                        int p1 = y + (int) Math.round(h * 0.33);
                        int p2 = y + (int) Math.round(h * 0.66);
                        return new Point(x, index == 0 ? p1 : p2);
                    }
                    case 3: {
                        int p1 = y + (int) Math.round(h * 0.2);
                        int p2 = y + (int) Math.round(h * 0.5);
                        int p3 = y + (int) Math.round(h * 0.8);
                        return new Point(x, index == 0 ? p1 : (index == 1 ? p2 : p3));
                    }
                    default: {
                        // fallback: evenly distribute
                        double step = (double) h / (total + 1);
                        int py = (int) Math.round(y + step * (index + 1));
                        return new Point(x, py);
                    }
                }
            case NOT:
            case OUTPUT:
            case INPUT:
            default:
                // single-input style: left-middle or distributed if multiple
                double step = (double) h / (total + 1);
                int py = (int) Math.round(y + step * (index + 1));
                return new Point(x, py);
        }
    }

    // small Point helper to avoid extra imports at top
    private static class Point { public final int x, y; public Point(int x, int y) { this.x=x; this.y=y; } }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
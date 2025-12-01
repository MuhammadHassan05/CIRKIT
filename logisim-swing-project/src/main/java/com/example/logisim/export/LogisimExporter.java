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

            // write wires using center points of components
            for (com.example.logisim.model.Connection conn : c.getConnections()) {
                Gate a = findGateById(gates, conn.fromId);
                Gate b = findGateById(gates, conn.toId);
                if (a == null || b == null) continue;
                int ax = a.x + 24; int ay = a.y + 12;
                int bx = b.x + 24; int by = b.y + 12;
                w.write(String.format("    <wire from=\"%s\" to=\"%s\"/>\n", fmtPoint(ax, ay), fmtPoint(bx, by)));
            }

            w.write("  </circuit>\n");
            w.write("</project>\n");
        }
    }

    private static Gate findGateById(List<Gate> gates, String id) {
        for (Gate g : gates) if (g.id.equals(id)) return g;
        return null;
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
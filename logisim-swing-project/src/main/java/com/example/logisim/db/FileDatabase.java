package com.example.logisim.db;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;
import com.example.logisim.model.Connection;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Simple JSON-backed file persistence (no external JSON library). Format:
// { "gates":[{...}], "connections":[{...}] }
public class FileDatabase {
    private final File folder;

    public FileDatabase() {
        this.folder = new File("data");
        if (!folder.exists()) folder.mkdirs();
    }

    public void init() {
        // nothing to do for file-based DB
    }

    public void saveCircuit(String name, Circuit circuit) {
        File out = new File(folder, name + ".json");
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"gates\":[");
        boolean first = true;
        for (Gate g : circuit.getGates()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('{')
              .append("\"id\":\"").append(escape(g.id)).append("\",")
              .append("\"type\":\"").append(g.type.name()).append("\",")
              .append("\"x\":").append(g.x).append(',')
              .append("\"y\":").append(g.y)
              .append('}');
        }
        sb.append("],\"connections\":[");
        first = true;
        for (Connection c : circuit.getConnections()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('{')
              .append("\"from\":\"").append(escape(c.fromId)).append("\",")
              .append("\"to\":\"").append(escape(c.toId)).append("\"")
              .append('}');
        }
        sb.append(" ]}\n");

        try (Writer w = new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8)) {
            w.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Circuit loadCircuit(String name) {
        Circuit c = new Circuit();
        File in = new File(folder, name + ".json");
        if (!in.exists()) return c;
        String content;
        try (FileInputStream fis = new FileInputStream(in)) {
            byte[] data = new byte[(int) in.length()];
            int read = fis.read(data);
            content = new String(data, 0, Math.max(0, read), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return c;
        }

        // crude parsing (sufficient for our simple format)
        int gi = content.indexOf("\"gates\"");
        if (gi >= 0) {
            int start = content.indexOf('[', gi);
            int end = content.indexOf(']', start);
            if (start >= 0 && end >= 0) {
                String gatesBlock = content.substring(start+1, end).trim();
                if (!gatesBlock.isEmpty()) {
                    String[] items = splitTopLevel(gatesBlock);
                    for (String it : items) {
                        String id = extractJsonString(it, "id");
                        String type = extractJsonString(it, "type");
                        int x = extractJsonInt(it, "x");
                        int y = extractJsonInt(it, "y");
                        try {
                            Gate.Type t = Gate.Type.valueOf(type);
                            c.addGate(new Gate(id, t, x, y));
                        } catch (IllegalArgumentException ex) {
                            // unknown type, skip
                        }
                    }
                }
            }
        }

        int ci = content.indexOf("\"connections\"");
        if (ci >= 0) {
            int start = content.indexOf('[', ci);
            int end = content.indexOf(']', start);
            if (start >= 0 && end >= 0) {
                String connBlock = content.substring(start+1, end).trim();
                if (!connBlock.isEmpty()) {
                    String[] items = splitTopLevel(connBlock);
                    for (String it : items) {
                        String from = extractJsonString(it, "from");
                        String to = extractJsonString(it, "to");
                        if (from != null && to != null) c.addConnection(new Connection(from, to));
                    }
                }
            }
        }

        return c;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String[] splitTopLevel(String block) {
        List<String> items = new ArrayList<>();
        int brace = 0, start = 0;
        for (int i=0;i<block.length();i++) {
            char ch = block.charAt(i);
            if (ch == '{') { if (brace==0) start = i; brace++; }
            else if (ch == '}') { brace--; if (brace==0) items.add(block.substring(start, i+1)); }
        }
        return items.toArray(new String[0]);
    }

    private static String extractJsonString(String obj, String key) {
        String q = '"' + key + '"';
        int k = obj.indexOf(q);
        if (k < 0) return null;
        int colon = obj.indexOf(':', k);
        if (colon < 0) return null;
        int firstQuote = obj.indexOf('"', colon+1);
        if (firstQuote < 0) return null;
        int secondQuote = obj.indexOf('"', firstQuote+1);
        if (secondQuote < 0) return null;
        return obj.substring(firstQuote+1, secondQuote);
    }

    private static int extractJsonInt(String obj, String key) {
        String q = '"' + key + '"';
        int k = obj.indexOf(q);
        if (k < 0) return 0;
        int colon = obj.indexOf(':', k);
        if (colon < 0) return 0;
        int i = colon+1;
        while (i < obj.length() && (obj.charAt(i)==' '||obj.charAt(i)=='\"')) i++;
        int j = i;
        while (j < obj.length() && (Character.isDigit(obj.charAt(j)) || obj.charAt(j)=='-' )) j++;
        try { return Integer.parseInt(obj.substring(i, j)); } catch (Exception ex) { return 0; }
    }
}

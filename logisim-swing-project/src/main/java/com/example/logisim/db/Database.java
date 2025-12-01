package com.example.logisim.db;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clean Database helper (single-definition).
 */
public class Database {
    private static final String URL = "jdbc:h2:./data/circuits";

    public void init() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            try (Statement s = conn.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS components (id IDENTITY PRIMARY KEY, circuit_name VARCHAR(255), gid VARCHAR(255), type VARCHAR(50), x INT, y INT);");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCircuit(String name, Circuit circuit) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM components WHERE circuit_name = ?")) {
                del.setString(1, name);
                del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement("INSERT INTO components(circuit_name,gid,type,x,y) VALUES(?,?,?,?,?)")) {
                for (Gate g : circuit.getGates()) {
                    ins.setString(1, name);
                    ins.setString(2, g.id);
                    ins.setString(3, g.type.name());
                    ins.setInt(4, g.x);
                    ins.setInt(5, g.y);
                    ins.addBatch();
                }
                ins.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Circuit loadCircuit(String name) {
        Circuit c = new Circuit();
        try (Connection conn = DriverManager.getConnection(URL)) {
            try (PreparedStatement q = conn.prepareStatement("SELECT gid, type, x, y FROM components WHERE circuit_name = ? ORDER BY id")) {
                q.setString(1, name);
                try (ResultSet rs = q.executeQuery()) {
                    while (rs.next()) {
                        String gid = rs.getString(1);
                        String type = rs.getString(2);
                        int x = rs.getInt(3);
                        int y = rs.getInt(4);
                        try {
                            Gate g = new Gate(gid, Gate.Type.valueOf(type), x, y);
                            c.addGate(g);
                        } catch (IllegalArgumentException ex) {
                            // skip unknown types
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }
}

package com.example.logisim.db;

/**
 * Simple holder for SQL migration schema used by the optional Database helper.
 */
public class Migrations {
    public static final String SCHEMA =
        "CREATE TABLE IF NOT EXISTS components (" +
        "id IDENTITY PRIMARY KEY, " +
        "circuit_name VARCHAR(255), " +
        "gid VARCHAR(255), " +
        "type VARCHAR(50), " +
        "x INT, " +
        "y INT" +
        ");\n" +
        "CREATE TABLE IF NOT EXISTS connections (" +
        "id IDENTITY PRIMARY KEY, " +
        "from_id BIGINT, " +
        "to_id BIGINT, " +
        "FOREIGN KEY (from_id) REFERENCES components(id), " +
        "FOREIGN KEY (to_id) REFERENCES components(id)" +
        ");\n";
}
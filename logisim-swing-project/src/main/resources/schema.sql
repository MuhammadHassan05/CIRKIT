-- Schema for circuits (components table)
CREATE TABLE IF NOT EXISTS components (
    id IDENTITY PRIMARY KEY,
    circuit_name VARCHAR(255),
    gid VARCHAR(255),
    type VARCHAR(50),
    x INT,
    y INT
);

CREATE TABLE IF NOT EXISTS connections (
    id IDENTITY PRIMARY KEY,
    from_component_gid VARCHAR(255),
    to_component_gid VARCHAR(255)
);

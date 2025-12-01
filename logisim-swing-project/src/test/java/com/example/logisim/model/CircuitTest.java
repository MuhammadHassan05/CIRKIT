package com.example.logisim.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CircuitTest {
    @Test
    public void addRemoveGateAndConnections() {
        Circuit c = new Circuit();
        Gate g1 = new Gate("g1", Gate.Type.AND, 0,0);
        Gate g2 = new Gate("g2", Gate.Type.OR, 20,0);
        c.addGate(g1);
        c.addGate(g2);
        assertEquals(2, c.getGates().size());
        c.addConnection(new Connection("g1","g2"));
        assertEquals(1, c.getConnections().size());
        c.removeGateById("g1");
        assertEquals(1, c.getGates().size());
        // connection referencing removed gate should be gone
        assertEquals(0, c.getConnections().size());
    }
}

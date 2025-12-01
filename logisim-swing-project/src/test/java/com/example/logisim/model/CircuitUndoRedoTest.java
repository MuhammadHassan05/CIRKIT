package com.example.logisim.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CircuitUndoRedoTest {
    @Test
    public void snapshotUndoRedo() {
        Circuit c = new Circuit();
        c.takeSnapshot();
        Gate g1 = new Gate("g1", Gate.Type.AND, 0,0);
        c.addGate(g1);
        assertEquals(1, c.getGates().size());
        c.takeSnapshot();
        Gate g2 = new Gate("g2", Gate.Type.OR, 20,0);
        c.addGate(g2);
        assertEquals(2, c.getGates().size());
        c.undo();
        assertEquals(1, c.getGates().size());
        c.redo();
        assertEquals(2, c.getGates().size());
    }
}

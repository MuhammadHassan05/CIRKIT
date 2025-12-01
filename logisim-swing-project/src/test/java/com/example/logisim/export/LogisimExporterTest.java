package com.example.logisim.export;

import com.example.logisim.model.Circuit;
import com.example.logisim.model.Gate;
import com.example.logisim.model.Connection;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class LogisimExporterTest {
    @Test
    public void exportCreatesXmlFile() throws Exception {
        Circuit c = new Circuit();
        Gate g1 = new Gate("g1", Gate.Type.INPUT, 0, 0);
        Gate g2 = new Gate("g2", Gate.Type.OUTPUT, 40, 0);
        c.addGate(g1); c.addGate(g2);
        c.addConnection(new Connection("g1","g2"));

        File tmp = File.createTempFile("circuit-test", ".xml");
        tmp.deleteOnExit();
        LogisimExporter.export(c, tmp);

        String content = new String(Files.readAllBytes(tmp.toPath()));
        assertTrue(content.contains("<project"));
        assertTrue(content.contains("<circuit"));
        assertTrue(content.contains("<comp"));
        assertTrue(content.contains("<wire"));
    }
}

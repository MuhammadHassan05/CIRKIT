### Step-by-Step Guide to Create a Logisim Circuit Generator

#### Step 1: Set Up Your Development Environment
1. **Install Java Development Kit (JDK)**: Make sure you have the JDK installed on your machine.
2. **Set Up an IDE**: Use an IDE like IntelliJ IDEA or Eclipse for Java development.
3. **Add Required Libraries**: If you're using a graphics library, ensure it's included in your project. For example, you might use Java's built-in `java.awt` and `javax.swing` for GUI.

#### Step 2: Create the Basic Structure
1. **Create a New Java Project**: Start a new project in your IDE.
2. **Create Main Class**: Create a main class (e.g., `LogisimCircuitGenerator`) with a `main` method.

```java
public class LogisimCircuitGenerator {
    public static void main(String[] args) {
        // Initialize your GUI here
    }
}
```

#### Step 3: Design the GUI
1. **Create a JFrame**: Use `JFrame` to create the main window.
2. **Add Components**: Add buttons, text fields, and panels to allow users to input circuit parameters.

```java
import javax.swing.*;

public class LogisimCircuitGenerator {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Logisim Circuit Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Add components here (buttons, text fields, etc.)
        
        frame.setVisible(true);
    }
}
```

#### Step 4: Implement Circuit Logic
1. **Define Circuit Elements**: Create classes for different circuit elements (e.g., AND, OR, NOT gates).
2. **Create a Circuit Class**: This class will manage the connections and logic of the circuit.

```java
class CircuitElement {
    // Define properties and methods for circuit elements
}

class Circuit {
    // Manage elements and connections
}
```

#### Step 5: Database Integration
1. **Set Up Database**: Use SQLite or another SQL database to store circuit configurations.
2. **Connect to Database**: Use JDBC to connect to your database and perform CRUD operations.

```java
import java.sql.*;

public class DatabaseManager {
    private Connection connect() {
        // Connect to your database
    }
    
    public void saveCircuit(Circuit circuit) {
        // Save circuit to database
    }
    
    public Circuit loadCircuit(int id) {
        // Load circuit from database
    }
}
```

#### Step 6: Graphics Rendering
1. **Override `paintComponent`**: In a custom JPanel, override the `paintComponent` method to draw the circuit elements.

```java
class CircuitPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw circuit elements here
    }
}
```

#### Step 7: Event Handling
1. **Add Action Listeners**: Add listeners to buttons to handle user actions (e.g., adding elements, saving circuits).

```java
button.addActionListener(e -> {
    // Handle button click
});
```

#### Step 8: Testing and Debugging
1. **Test Your Application**: Run your application and test all functionalities.
2. **Debug Issues**: Use debugging tools in your IDE to fix any issues.

#### Step 9: Documentation
1. **Comment Your Code**: Add comments to explain your code.
2. **Create a User Guide**: Write a brief guide on how to use your application.

### Additional Tips
- **Version Control**: Use Git for version control to manage your project files.
- **Modular Design**: Keep your code modular to make it easier to add new features later.
- **User Input Validation**: Ensure you validate user inputs to avoid errors.

By following these steps, you should be able to create a Logisim circuit generator using Java Swing and integrate it with a database. If you have specific questions or need help with a particular part of the assignment, feel free to ask!

Run locally (one-step)
----------------------
Open PowerShell in the project folder and run:

```powershell
cd 'd:\SCD\PROJECT\CIRKIT\logisim-swing-project'
.\setup-and-run.ps1
```

What this does:
- If a Gradle wrapper (`gradlew.bat`) is present the script will run `gradlew run`.
- Otherwise it will call `build-and-run.ps1` which compiles sources with the JDK, creates `dist/logisim-swing-project.jar`, and runs it.

Requirements:
- JDK 17+ on PATH (javac, java, jar)

Quick usage tips inside the app:
- Select a gate type from the palette and left-click the canvas to place a gate.
- Left-click a gate to select and drag to move it (snap to grid).
- Shift + left-click on one gate then Shift + left-click another to create a wire (rubber-band preview while creating).
- Press `Delete` (when the canvas is focused) to remove the selected gate and its wires.
- Press `Esc` to cancel an in-progress wire.

Persistence and export:
- Save/load circuits to `data/<name>.json` using the app's Save/Load commands.
- Export to a Logisim-like XML with the Export button; open the produced file in Logisim (may require manual adjustments for exact Logisim versions).

Running tests
-------------
Unit tests use JUnit 5. To run tests:

Windows (PowerShell):
```powershell
cd 'd:\SCD\PROJECT\CIRKIT\logisim-swing-project'
.\gradlew.bat test
```

Unix/macOS:
```sh
./gradlew test
```

Notes:
- The shim `gradlew`/`gradlew.bat` prefers a system Gradle if installed; otherwise it falls back to the included `build-and-run.ps1` JDK-based packager for running the app. Running tests requires Gradle or running tests from an IDE with JDK support.
- Ensure JDK 17+ is installed and `javac`, `java`, and `jar` are on PATH when using the JDK fallback.

Release packaging
-----------------
After building `dist/logisim-swing-project.jar` you can create a release ZIP that bundles the JAR and helper scripts:

```powershell
cd 'd:\SCD\PROJECT\CIRKIT\logisim-swing-project'
.\create-release.ps1
```

This creates `release-<timestamp>.zip` containing the runnable JAR and scripts.
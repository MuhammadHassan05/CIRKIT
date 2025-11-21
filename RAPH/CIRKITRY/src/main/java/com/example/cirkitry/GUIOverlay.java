package com.example.cirkitry;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GUIOverlay {

    private VBox root;
    private Label cameraPosLabel;
    private VBox toolPalette;
    private MenuBar menuBar;

    public GUIOverlay() {
        createOverlay();
    }

    private void createOverlay() {

        // -----------------------
        // 1. Create MenuBar
        // -----------------------
        menuBar = new MenuBar();

        // ----- File menu -----
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);

        // ----- Components menu -----
        Menu compMenu = new Menu("Components");
        MenuItem gateItem = new MenuItem("Gate");
        MenuItem wireItem = new MenuItem("Wire");
        MenuItem lightItem = new MenuItem("Light");
        MenuItem jointItem = new MenuItem("Joint");
        compMenu.getItems().addAll(gateItem, wireItem, lightItem, jointItem);

        // Add menus to bar
        menuBar.getMenus().addAll(fileMenu, compMenu);

        // -----------------------
        // 2. Camera Position Label
        // -----------------------
        cameraPosLabel = new Label("Camera: (0,0,0)");
        cameraPosLabel.setTextFill(Color.WHITE);

        // -----------------------
        // 3. Example tool palette
        // -----------------------
        Button toolWire = new Button("Wire");
        Button toolGate = new Button("Gate");
        toolPalette = new VBox(10, toolWire, toolGate);

        // -----------------------
        // 4. Assemble overlay root
        // -----------------------
        root = new VBox(10, menuBar, cameraPosLabel, toolPalette);
        root.setPickOnBounds(false);   // allow 3D dragging behind UI
    }

    public VBox getRoot() {
        return root;
    }

    public Label getCameraPosLabel() {
        return cameraPosLabel;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}

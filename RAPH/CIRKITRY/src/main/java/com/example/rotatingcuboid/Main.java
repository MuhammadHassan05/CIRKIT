package com.example.rotatingcuboid;

import java.util.HashSet;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Main extends Application {

    // Camera controls
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final HashSet<KeyCode> pressedKeys = new HashSet<>();

    // Movement speed
    private static final double MOVE_SPEED = 10.0;
    private static final double ROTATE_SPEED = 0.5;

    // Rotation angles
    private double angleX = 0;
    private double angleY = 0;

    @Override
    public void start(Stage stage) {




        // Create a cuboid (width, height, depth)
        Box box = new Box(200, 150, 100);

        // Add material (color + lighting)
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.CORNFLOWERBLUE);
        material.setSpecularColor(Color.LIGHTBLUE);
        box.setMaterial(material);

        // Rotate transforms
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        box.getTransforms().addAll(rotateX, rotateY);

        // Group and scene
        Group root = new Group(box);
        Scene scene = new Scene(root, 1400, 800, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

        // Camera setup
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-600); // move back to view object
        scene.setCamera(camera);

        // Keyboard controls
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        // Animation loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Rotate cube continuously
                angleX += 0.5;
                angleY += 0.5;
                rotateX.setAngle(angleX);
                rotateY.setAngle(angleY);

                // Camera movement
                handleMovement();
            }
        }.start();

        stage.setTitle("JavaFX 3D - Rotating Cuboid with Camera Movement");
        stage.setScene(scene);
        stage.show();
    }

    private void handleMovement() {
        double change = MOVE_SPEED;

        if (pressedKeys.contains(KeyCode.W))
            camera.setTranslateZ(camera.getTranslateZ() + change);
        if (pressedKeys.contains(KeyCode.S))
            camera.setTranslateZ(camera.getTranslateZ() - change);
        if (pressedKeys.contains(KeyCode.A))
            camera.setTranslateX(camera.getTranslateX() - change);
        if (pressedKeys.contains(KeyCode.D))
            camera.setTranslateX(camera.getTranslateX() + change);
        if (pressedKeys.contains(KeyCode.SPACE))
            camera.setTranslateY(camera.getTranslateY() - change);
        if (pressedKeys.contains(KeyCode.SHIFT))
            camera.setTranslateY(camera.getTranslateY() + change);
    }

    public static void main(String[] args) {
        launch();
    }
}

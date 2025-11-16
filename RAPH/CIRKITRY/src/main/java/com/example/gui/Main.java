package com.example.gui;

import java.util.HashSet;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Main extends Application {

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final HashSet<KeyCode> pressedKeys = new HashSet<>();

    private static final double MOVE_SPEED = 10.0;
    private static final double MOUSE_SENSITIVITY = 0.2;

    private double angleX = 0;
    private double angleY = 0;

    // Mouse drag tracking
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private boolean rightMouseDown = false;

    @Override
    public void start(Stage stage) {
        // --- 3D Object ---
        Box box = new Box(200, 150, 100);
        PhongMaterial material = new PhongMaterial(Color.CORNFLOWERBLUE);
        box.setMaterial(material);

        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        box.getTransforms().addAll(rotateX, rotateY);

        Group root3D = new Group(box);
        SubScene subScene3D = new SubScene(root3D, 1400, 800, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.BLACK);
        subScene3D.setCamera(camera);

        // --- GUI Overlay ---
        Label cameraPosLabel = new Label();
        cameraPosLabel.setTextFill(Color.WHITE);
        VBox guiOverlay = new VBox(cameraPosLabel);
        guiOverlay.setPickOnBounds(false);

        StackPane root = new StackPane();
        root.getChildren().addAll(subScene3D, guiOverlay);

        Scene scene = new Scene(root, 1400, 800, true);

        // --- Camera setup ---
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-600);

        // --- Keyboard controls ---
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        // --- Mouse look controls ---
        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                rightMouseDown = true;
                lastMouseX = e.getSceneX();
                lastMouseY = e.getSceneY();
            }
        });

        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                rightMouseDown = false;
            }
        });

        scene.setOnMouseDragged(e -> {
            if (rightMouseDown) {
                double deltaX = e.getSceneX() - lastMouseX;
                double deltaY = e.getSceneY() - lastMouseY;

                angleY += deltaX * MOUSE_SENSITIVITY;
                angleX -= deltaY * MOUSE_SENSITIVITY;

                // Clamp pitch to avoid flipping
                angleX = Math.max(-90, Math.min(90, angleX));

                lastMouseX = e.getSceneX();
                lastMouseY = e.getSceneY();
            }
        });

        // --- Animation loop ---
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Rotate cube
                rotateX.setAngle(rotateX.getAngle() + 0.5);
                rotateY.setAngle(rotateY.getAngle() + 0.5);

                // Move camera
                handleMovement();

                // Apply camera rotation
                camera.getTransforms().clear();
                camera.getTransforms().addAll(
                        new Rotate(angleY, Rotate.Y_AXIS),
                        new Rotate(angleX, Rotate.X_AXIS),
                        new javafx.scene.transform.Translate(camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ())
                );

                // Calculate forward vector
                double radY = Math.toRadians(angleY);
                double radX = Math.toRadians(angleX);
                double forwardX = Math.sin(radY) * Math.cos(radX);
                double forwardY = Math.sin(radX);
                double forwardZ = Math.cos(radY) * Math.cos(radX);

                // Update GUI
                cameraPosLabel.setText(String.format(
                        "Camera Position: [X: %.1f, Y: %.1f, Z: %.1f]\nForward: [%.2f, %.2f, %.2f]",
                        camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(),
                        forwardX, forwardY, forwardZ
                ));
            }
        }.start();

        stage.setTitle("3D Scene with Camera Look & GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void handleMovement() {
        double change = MOVE_SPEED;

        // Calculate forward vector from angles
        double radY = Math.toRadians(angleY);
        double radX = Math.toRadians(angleX);
        double forwardX = Math.sin(radY) * Math.cos(radX);
        double forwardY = Math.sin(radX);
        double forwardZ = Math.cos(radY) * Math.cos(radX);

        double rightX = Math.sin(radY - Math.PI / 2);
        double rightZ = Math.cos(radY - Math.PI / 2);

        if (pressedKeys.contains(KeyCode.W)) {
            camera.setTranslateX(camera.getTranslateX() + forwardX * change);
            camera.setTranslateY(camera.getTranslateY() + forwardY * change);
            camera.setTranslateZ(camera.getTranslateZ() + forwardZ * change);
        }
        if (pressedKeys.contains(KeyCode.S)) {
            camera.setTranslateX(camera.getTranslateX() - forwardX * change);
            camera.setTranslateY(camera.getTranslateY() - forwardY * change);
            camera.setTranslateZ(camera.getTranslateZ() - forwardZ * change);
        }
        if (pressedKeys.contains(KeyCode.A)) {
            camera.setTranslateX(camera.getTranslateX() - rightX * change);
            camera.setTranslateZ(camera.getTranslateZ() - rightZ * change);
        }
        if (pressedKeys.contains(KeyCode.D)) {
            camera.setTranslateX(camera.getTranslateX() + rightX * change);
            camera.setTranslateZ(camera.getTranslateZ() + rightZ * change);
        }
        if (pressedKeys.contains(KeyCode.SPACE))
            camera.setTranslateY(camera.getTranslateY() - change);
        if (pressedKeys.contains(KeyCode.SHIFT))
            camera.setTranslateY(camera.getTranslateY() + change);
    }

    public static void main(String[] args) {
        launch();
    }
}

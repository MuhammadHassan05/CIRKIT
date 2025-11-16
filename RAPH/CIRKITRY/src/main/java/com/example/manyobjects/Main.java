package com.example.manyobjects;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * JavaFX 3D Strategy-Game Style Camera Controller
 *
 * Controls:
 *  - Right Mouse Drag: Pan
 *  - Middle Mouse Drag: Rotate around map
 *  - Scroll: Zoom
 *  - WASD: Move (speed scales with zoom)
 */
public class Main extends Application {

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Translate camPos = new Translate(0, -100, -200);
    private final Rotate camPitch = new Rotate(-45, Rotate.X_AXIS); // tilt downward
    private final Rotate camYaw = new Rotate(0, Rotate.Y_AXIS);

    double lastMouseX, lastMouseY;

    @Override
    public void start(Stage stage) {

        Group world = new Group();

        // Simple ground grid
        Box ground = new Box(500, 1, 500);
        ground.setMaterial(new PhongMaterial(Color.DARKGRAY));
        world.getChildren().add(ground);

        // Some objects
        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
                Box b = new Box(10, 10, 10);
                b.setTranslateX(i * 30);
                b.setTranslateZ(j * 30);
                b.setMaterial(new PhongMaterial(Color.color(Math.random(), Math.random(), Math.random())));
                world.getChildren().add(b);
            }
        }

        Group root = new Group(world);

        SubScene scene3D = new SubScene(root, 900, 600, true, SceneAntialiasing.BALANCED);
        scene3D.setFill(Color.GRAY);

        camera.getTransforms().addAll(
                camYaw,
                camPitch,
                camPos
        );
        camera.setFarClip(5000);
        scene3D.setCamera(camera);

        Scene uiScene = new Scene(new Group(scene3D));

        // MOUSE CONTROLS
        scene3D.setOnMousePressed(e -> {
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        scene3D.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - lastMouseX;
            double dy = e.getSceneY() - lastMouseY;

            if (e.isSecondaryButtonDown()) {
                // RIGHT MOUSE = PAN
                camPos.setX(camPos.getX() - dx * 0.5);
                camPos.setZ(camPos.getZ() + dy * 0.5);
            } else if (e.isMiddleButtonDown()) {
                // MIDDLE MOUSE = ROTATE
                camYaw.setAngle(camYaw.getAngle() - dx * 0.2);

                double newPitch = camPitch.getAngle() + dy * 0.2;
                newPitch = Math.max(-80, Math.min(-20, newPitch)); // prevent flipping
                camPitch.setAngle(newPitch);
            }

            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        // SCROLL = ZOOM
        scene3D.setOnScroll(e -> {
            double zoom = e.getDeltaY() * 0.5;
            camPos.setZ(camPos.getZ() + zoom);
            camPos.setY(camPos.getY() + zoom * 0.5);
        });

        // KEYBOARD MOVEMENT
        uiScene.setOnKeyPressed(e -> {
            double speed = Math.max(5, Math.abs(camPos.getZ()) * 0.05); // speed scales with zoom

            double yawRad = Math.toRadians(camYaw.getAngle());
            double forwardX = Math.sin(yawRad);
            double forwardZ = Math.cos(yawRad);

            double rightX = Math.cos(yawRad);
            double rightZ = -Math.sin(yawRad);

            if (e.getCode() == KeyCode.W) {
                camPos.setX(camPos.getX() + forwardX * speed);
                camPos.setZ(camPos.getZ() + forwardZ * speed);
            }
            if (e.getCode() == KeyCode.S) {
                camPos.setX(camPos.getX() - forwardX * speed);
                camPos.setZ(camPos.getZ() - forwardZ * speed);
            }
            if (e.getCode() == KeyCode.A) {
                camPos.setX(camPos.getX() - rightX * speed);
                camPos.setZ(camPos.getZ() - rightZ * speed);
            }
            if (e.getCode() == KeyCode.D) {
                camPos.setX(camPos.getX() + rightX * speed);
                camPos.setZ(camPos.getZ() + rightZ * speed);
            }
        });

        stage.setScene(uiScene);
        stage.setTitle("JavaFX Strategy Camera Demo");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

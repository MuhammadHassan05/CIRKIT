package com.example.freefly;

import java.util.HashSet;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Main extends Application {

    private final HashSet<KeyCode> keysPressed = new HashSet<>();

    private double mouseOldX, mouseOldY;
    private final double mouseSensitivity = 0.2;
    private final double movementSpeed = 0.3;

    // Camera vectors
    private Vector3f position = new Vector3f(0, 0, -10);
    private Vector3f forward = new Vector3f(0, 0, 1);
    private Vector3f up = new Vector3f(0, 1, 0);

    private PerspectiveCamera camera = new PerspectiveCamera(true);

    @Override
    public void start(Stage stage) {
        // Cube at origin
        Box cube = new Box(2, 2, 2);
        cube.setMaterial(new PhongMaterial(Color.RED));

        Group root = new Group(cube);
        root.getChildren().add(camera);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTBLUE);
        scene.setCamera(camera);

        camera.setNearClip(0.1);
        camera.setFarClip(1000);

        // Mouse look
        scene.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });
        scene.setOnMouseDragged(this::handleMouse);

        // Keyboard input
        scene.setOnKeyPressed(e -> keysPressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        // Animation loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCamera();
            }
        };
        timer.start();

        stage.setScene(scene);
        stage.setTitle("Free-Fly 6DOF Camera");
        stage.show();
    }

    private void handleMouse(MouseEvent event) {
        double dx = event.getSceneX() - mouseOldX;
        double dy = event.getSceneY() - mouseOldY;

        rotateYawPitch(dx * mouseSensitivity, -dy * mouseSensitivity);

        mouseOldX = event.getSceneX();
        mouseOldY = event.getSceneY();
    }

    private void updateCamera() {
       Vector3f right = forward.cross(up).normalize();

        if (keysPressed.contains(KeyCode.W)) position = position.add(forward.scale(movementSpeed));
        if (keysPressed.contains(KeyCode.S)) position = position.subtract(forward.scale(movementSpeed));
        if (keysPressed.contains(KeyCode.A)) position = position.subtract(right.scale(movementSpeed));
        if (keysPressed.contains(KeyCode.D)) position = position.add(right.scale(movementSpeed));
        if (keysPressed.contains(KeyCode.SPACE)) position = position.add(up.scale(movementSpeed));
        if (keysPressed.contains(KeyCode.SHIFT)) position = position.subtract(up.scale(movementSpeed));

        // Apply position
        camera.setTranslateX(position.x);
        camera.setTranslateY(position.y);
        camera.setTranslateZ(position.z);

        // Compute rotations from forward and up
        // Compute rotations from forward and up


// Pitch rotation (X-axis)
double pitch = Math.toDegrees(Math.asin(forward.y));

// Yaw rotation (Y-axis)
double yaw = Math.toDegrees(Math.atan2(forward.x, forward.z));

// Apply rotations
camera.getTransforms().setAll(
        new Rotate(-pitch, Rotate.X_AXIS),
        new Rotate(yaw, Rotate.Y_AXIS)
);

    }

    private void rotateYawPitch(double deltaYawDeg, double deltaPitchDeg) {
        // Convert to radians
        double yawRad = Math.toRadians(deltaYawDeg);
        double pitchRad = Math.toRadians(deltaPitchDeg);

        // Right vector
        Vector3f right = forward.cross(up).normalize();

        // Yaw around world up
        forward = forward.rotateAroundAxis(new Vector3f(0, 1, 0), yawRad).normalize();

        // Pitch around right
        forward = forward.rotateAroundAxis(right, pitchRad).normalize();

        // Recompute up
        up = right.cross(forward).normalize();
    }

    public static void main(String[] args) {
        launch();
    }
}

// Simple 3D vector class
class Vector3f {
    public double x, y, z;

    public Vector3f(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }

    public Vector3f add(Vector3f o) { return new Vector3f(x + o.x, y + o.y, z + o.z); }
    public Vector3f subtract(Vector3f o) { return new Vector3f(x - o.x, y - o.y, z - o.z); }
    public Vector3f scale(double s) { return new Vector3f(x * s, y * s, z * s); }

    public Vector3f cross(Vector3f o) {
        return new Vector3f(
                y * o.z - z * o.y,
                z * o.x - x * o.z,
                x * o.y - y * o.x
        );
    }

    public Vector3f normalize() {
        double len = Math.sqrt(x*x + y*y + z*z);
        return new Vector3f(x / len, y / len, z / len);
    }

    public Vector3f rotateAroundAxis(Vector3f axis, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double dot = x*axis.x + y*axis.y + z*axis.z;

        double rx = x * cos + (axis.y*z - axis.z*y) * sin + axis.x * dot * (1 - cos);
        double ry = y * cos + (axis.z*x - axis.x*z) * sin + axis.y * dot * (1 - cos);
        double rz = z * cos + (axis.x*y - axis.y*x) * sin + axis.z * dot * (1 - cos);

        return new Vector3f(rx, ry, rz);
    }
}

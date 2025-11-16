package com.example.cirkitry;

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
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Main extends Application {

    private final HashSet<KeyCode> keysPressed = new HashSet<>();

    // Mouse
    private double mouseOldX, mouseOldY;
    private final double mouseSensitivity = 0.15;

    // Movement
    private final double moveSpeed = 0.5;

    // Camera orientation (yaw rotates around Y, pitch rotates camera X)
    private double yaw = 0;
    private double pitch = 0;

    private Vector3f position = new Vector3f(0, 20, -80);

    private PerspectiveCamera camera = new PerspectiveCamera(true);

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        Group grid = makeGrid(400, 50);
        root.getChildren().add(grid);

        root.getChildren().add(camera);

        Scene scene = new Scene(root, 1000, 800, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);

        camera.setNearClip(0.1);
        camera.setFarClip(3000);

        // Initial camera transform
        updateCameraTransform();

        // Mouse look
        scene.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });
        scene.setOnMouseDragged(this::handleMouseLook);

        // Keyboard input
        scene.setOnKeyPressed(e -> keysPressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveCamera();
                updateCameraTransform();
            }
        };
        timer.start();

        stage.setScene(scene);
        stage.setTitle("Smooth Free-Fly Camera");
        stage.show();
    }

    // -------------------------------------------------------------------------
    // ---------------------------- CAMERA CONTROL -----------------------------
    // -------------------------------------------------------------------------

    private void handleMouseLook(MouseEvent e) {
        double dx = e.getSceneX() - mouseOldX;
        double dy = e.getSceneY() - mouseOldY;

        yaw += dx * mouseSensitivity;
        pitch -= dy * mouseSensitivity;

        // Clamp pitch
        pitch = Math.max(-89, Math.min(89, pitch));

        mouseOldX = e.getSceneX();
        mouseOldY = e.getSceneY();
    }

    private void moveCamera() {
        Vector3f forward = getForwardVector();
        Vector3f right = new Vector3f(
                Math.sin(Math.toRadians(yaw - 90)),
                0,
                Math.cos(Math.toRadians(yaw - 90))
        );

        if (keysPressed.contains(KeyCode.W)) position = position.add(forward.scale(moveSpeed));
        if (keysPressed.contains(KeyCode.S)) position = position.subtract(forward.scale(moveSpeed));
        if (keysPressed.contains(KeyCode.A)) position = position.subtract(right.scale(moveSpeed));
        if (keysPressed.contains(KeyCode.D)) position = position.add(right.scale(moveSpeed));

        if (keysPressed.contains(KeyCode.SHIFT)) position = position.add(new Vector3f(0, 1, 0).scale(moveSpeed));
        if (keysPressed.contains(KeyCode.SPACE)) position = position.subtract(new Vector3f(0, 1, 0).scale(moveSpeed));
    }

    private void updateCameraTransform() {
        camera.setTranslateX(position.x);
        camera.setTranslateY(position.y);
        camera.setTranslateZ(position.z);

        camera.getTransforms().setAll(
                new Rotate(-pitch, Rotate.X_AXIS),
                new Rotate(yaw, Rotate.Y_AXIS)
        );
    }

    private Vector3f getForwardVector() {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double x = Math.sin(yawRad) * Math.cos(pitchRad);
        double y = Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        return new Vector3f(x, y, z).normalize();
    }

    // -------------------------------------------------------------------------
    // ------------------------------ GRID -------------------------------------
    // -------------------------------------------------------------------------

    private Group makeGrid(int size, int gap) {
        Group g = new Group();
        int half = size / 2;

        for (int i = -half; i <= half; i += gap) {
            g.getChildren().add(createGridLine(-half, 0, i, half, 0, i, 2, Color.GRAY));
            g.getChildren().add(createGridLine(i, 0, -half, i, 0, half, 2, Color.GRAY));
        }

        return g;
    }

    private Cylinder createGridLine(double x1, double y1, double z1,
                                    double x2, double y2, double z2,
                                    double thickness, Color color)
    {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx*dx + dz*dz);

        Cylinder line = new Cylinder(thickness, length);
        line.setMaterial(new PhongMaterial(color));

        line.setTranslateX((x1 + x2) / 2);
        line.setTranslateY(y1);
        line.setTranslateZ((z1 + z2) / 2);

        if (dx == 0) {
            line.setRotationAxis(Rotate.X_AXIS);
            line.setRotate(90);
        } else {
            line.setRotationAxis(Rotate.Z_AXIS);
            line.setRotate(90);
        }

        return line;
    }

    public static void main(String[] args) { launch(); }
}


// -----------------------------------------------------------------------------
// ----------------------------- Vector3f Helper --------------------------------
// -----------------------------------------------------------------------------

class Vector3f {
    public double x, y, z;

    public Vector3f(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }

    public Vector3f add(Vector3f o) { return new Vector3f(x + o.x, y + o.y, z + o.z); }
    public Vector3f subtract(Vector3f o) { return new Vector3f(x - o.x, y - o.y, z - o.z); }
    public Vector3f scale(double s) { return new Vector3f(x * s, y * s, z * s); }

    public Vector3f normalize() {
        double len = Math.sqrt(x*x + y*y + z*z);
        return new Vector3f(x/len, y/len, z/len);
    }
}

package com.example.cirkitry;

import java.util.HashSet;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Affine;


public class MotionF 
 {


    private Group posNode = new Group();
    private Group yawNode = new Group();
    private Group pitchNode = new Group();
    private Group rollNode = new Group();
    private Group node;


    private final HashSet<KeyCode> activeKeys = new HashSet<>();



    
 private double lastMouseX;
private double lastMouseY;
private boolean mouseDragging = false;

private double mouseSensitivity = 0.2; // you can adjust this


    MotionF(Group Node)
    {

        node = Node;

        posNode.getChildren().add(pitchNode);
        pitchNode.getChildren().add(yawNode);
        yawNode.getChildren().add(rollNode);
        rollNode.getChildren().add(node);





    }

    void attachMouseEvent(Scene scene)
    {
        scene.setOnMousePressed(e -> {
    if (e.isPrimaryButtonDown()) {
        mouseDragging = true;
        lastMouseX = e.getSceneX();
        lastMouseY = e.getSceneY();
            }  
         });

scene.setOnMouseReleased(e -> {
    mouseDragging = false;
});

scene.setOnMouseDragged(e -> {
    if (!mouseDragging) return;

    double dx = e.getSceneX() - lastMouseX;
    double dy = e.getSceneY() - lastMouseY;

    lastMouseX = e.getSceneX();
    lastMouseY = e.getSceneY();

      yawNode.setRotate(yawNode.getRotate()-dy*mouseSensitivity);
      pitchNode.setRotate(pitchNode.getRotate() + dx*mouseSensitivity);
});

    }



    
public void update() {

    handleKeyTranslate();

    handleKeyRotate();
}


private void move(Point3D dir, double amount) {
    posNode.setTranslateX(posNode.getTranslateX() + dir.getX() * amount);
    posNode.setTranslateY(posNode.getTranslateY() + dir.getY() * amount);
    posNode.setTranslateZ(posNode.getTranslateZ() + dir.getZ() * amount);
}

    public void attachKeyControls(Scene scene) {

    scene.setOnKeyPressed(e -> {
        activeKeys.add(e.getCode());
    });

    scene.setOnKeyReleased(e -> {
        activeKeys.remove(e.getCode());
    });
}

    private void handleKeyTranslate() {

        double speed = 1;
        Point3D forward = getForward();
        Point3D right   = getRight();
        Point3D up      = getUp();

             if (activeKeys.contains(KeyCode.W)) {
        move(forward, speed);
    }
    if (activeKeys.contains(KeyCode.S)) {
        move(forward, -speed);
    }
    if (activeKeys.contains(KeyCode.A)) {
        move(right, -speed);
    }
    if (activeKeys.contains(KeyCode.D)) {
        move(right, speed);
    }
    if (activeKeys.contains(KeyCode.SPACE)) {
        move(up, speed);
    }
    if (activeKeys.contains(KeyCode.CONTROL)) {
        move(up, -speed);
    }


        
    }

    private void handleKeyRotate()
    {

    double Rspeed = 1;

    if (activeKeys.contains(KeyCode.Q))
        rotateNode(yawNode, getRightFrom(yawNode), Rspeed);

    if (activeKeys.contains(KeyCode.E))
        rotateNode(yawNode, getRightFrom(yawNode), -Rspeed);

    if (activeKeys.contains(KeyCode.R))
        rotateNode(pitchNode, getUpFrom(pitchNode), -Rspeed);

    if (activeKeys.contains(KeyCode.T))
        rotateNode(pitchNode, getUpFrom(pitchNode), Rspeed);

    if (activeKeys.contains(KeyCode.Y))
        rotateNode(rollNode, getForwardFrom(rollNode), -Rspeed);

    if (activeKeys.contains(KeyCode.U))
        rotateNode(rollNode, getForwardFrom(rollNode), Rspeed);
    }

   private Point3D getForward() {
    return node.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(0, 0, 1))
            .normalize();
}

private Point3D getUp() {
    return node.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(0, -1, 0))
            .normalize();
}

private Point3D getRight() {
    return node.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(1, 0, 0))
            .normalize();
}

/// MOTIONF EXCLUSIVES

private void rotateNode(Group node, Point3D axis, double angle) {
    Affine affine = new Affine(node.getLocalToParentTransform());
    affine.appendRotation(angle, 0, 0, 0, axis);
    node.getTransforms().setAll(affine);
}


private Point3D getRightFrom(Group g) {
    return g.getLocalToSceneTransform().deltaTransform(new Point3D(1,0,0)).normalize();
}
private Point3D getUpFrom(Group g) {
    return g.getLocalToSceneTransform().deltaTransform(new Point3D(0,-1,0)).normalize();
}
private Point3D getForwardFrom(Group g) {
    return g.getLocalToSceneTransform().deltaTransform(new Point3D(0,0,1)).normalize();
}


// ====================== POSITION HELPERS ======================

/** Returns the world-space position of the Motion root node */
public Point3D getPosition() {
    return new Point3D(
        posNode.getTranslateX(),
        posNode.getTranslateY(),
        posNode.getTranslateZ()
    );
}

/** Sets the exact world position (teleport without resetting rotation) */
public void setPosition(double x, double y, double z) {
    posNode.setTranslateX(x);
    posNode.setTranslateY(y);
    posNode.setTranslateZ(z);
}

/** Teleport that ALSO resets pitch/yaw/roll nodes to origin */
public void teleport(double x, double y, double z) {
    setPosition(x, y, z);

    pitchNode.setRotate(0);
    yawNode.setRotate(0);
    rollNode.setRotate(0);
}

// ====================== ROTATION HELPERS ======================

/** Returns yaw/pitch/roll as Point3D(pitch,yaw,roll) */
public Point3D getRotation() {
    return new Point3D(
        pitchNode.getRotate(),
        yawNode.getRotate(),
        rollNode.getRotate()
    );
}

/** Sets rotation on all 3 local axes */
public void setRotation(double pitch, double yaw, double roll) {
    pitchNode.setRotate(pitch);
    yawNode.setRotate(yaw);
    rollNode.setRotate(roll);
}

/** Reset all rotations to zero */
public void resetRotation() {
    pitchNode.setRotate(0);
    yawNode.setRotate(0);
    rollNode.setRotate(0);
}

// ====================== DIRECTION HELPERS ======================

/** Returns a full 3×3 basis (forward, right, up) */
public Point3D[] getBasis() {
    return new Point3D[] { getForward(), getRight(), getUp() };
}

// ====================== ORIENTATION HELPERS ======================

/** Point the camera at a world-space target */
public void lookAt(Point3D target) {

    Point3D pos = getPosition();
    Point3D dir = target.subtract(pos);

    double yaw = Math.toDegrees(Math.atan2(dir.getX(), dir.getZ()));
    double pitch = Math.toDegrees(Math.atan2(-dir.getY(), 
                      Math.sqrt(dir.getX()*dir.getX() + dir.getZ()*dir.getZ())));

    pitchNode.setRotate(pitch);
    yawNode.setRotate(yaw);

    // roll stays unchanged
}


public Group getRootNode() {
    return posNode;
}
    
}

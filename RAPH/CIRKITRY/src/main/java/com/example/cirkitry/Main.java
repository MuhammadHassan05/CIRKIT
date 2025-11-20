package com.example.cirkitry;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
 import javafx.scene.shape.Box;
 import javafx.scene.shape.CullFace;
 import javafx.scene.shape.MeshView;
 import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;



// import java.util.HashSet;


// import javafx.application.Application;
// import javafx.geometry.Point3D;
// import javafx.scene.Group;
// import javafx.scene.PerspectiveCamera;
// import javafx.scene.Scene;
// import javafx.scene.SceneAntialiasing;
// import javafx.scene.SubScene;

// import javafx.scene.input.KeyCode;

// import javafx.scene.paint.Color;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.Box;
// import javafx.scene.shape.Cylinder;
// import javafx.scene.transform.Rotate;
// import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // ------------------------
        // 1. Create world
        // ------------------------
        Group world = new Group();
        MObj.addAxisBoxes(world);
        world.getChildren().add(MObj.createRepeatingFloor(new Image(getClass().getResource("/tile.jpeg").toExternalForm()),0.5,100));

        // ------------------------
        // 2. Create SubScene
        // ------------------------
        SubScene subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);

        // ------------------------
        // 3. Create camera
        // ------------------------
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(50000);

        Group cameraHolder = new Group(camera);
        cameraHolder.setTranslateZ(-800);

        // ------------------------
        // 4. Create DirectionSphere
        // ------------------------
        DirectionSphere ds = new DirectionSphere();
        ds.setTranslateX(0);
        ds.setTranslateY(0);
        ds.setTranslateZ(0);

        // Motion controls the DirectionSphere
        Motion motion = new Motion(cameraHolder);
        // Motion motion = new Motion(cameraHolder); // --> MOVEABLE CAMERA 

        // world.getChildren().add(motion.getRootNode());

        subScene.setCamera(camera);

        // ------------------------
        // 5. GUI overlay
        // ------------------------
        Label cameraPosLabel = new Label();
        cameraPosLabel.setTextFill(Color.WHITE);
        VBox guiOverlay = new VBox(cameraPosLabel);
        guiOverlay.setPickOnBounds(false);

        StackPane root = new StackPane();
        root.getChildren().addAll(subScene, guiOverlay);

        Scene scene = new Scene(root, 800, 600);

        // Attach controls
        motion.attachMouseEvent(scene);
        motion.attachKeyControls(scene);

        // ------------------------
        // 6. Animation loop
        // ------------------------
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                String str = "";
                str = MObj.PrintCoord(str, "Position", motion.getPosition());
                str = MObj.PrintCoord(str, "DS Local", ds);
                cameraPosLabel.setText(str);

                motion.update();
            }
        }.start();

        // ------------------------
        // 7. Setup Stage
        // ------------------------
        stage.setTitle("Motion Class Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


// =======================
// Helper Class: WObj
// =======================
class MObj {

    public static String PrintCoord(String str, String label, Group g) {
        double x = g.getTranslateX();
        double y = g.getTranslateY();
        double z = g.getTranslateZ();

        if (!str.isEmpty()) str += "\n";
        str += String.format("%s: [X: %.1f, Y: %.1f, Z: %.1f]\n", label, x, y, z);
        return str;
    }

    public static String PrintCoord(String str, String label, Point3D g) {
        double x = g.getX();
        double y = g.getY();
        double z = g.getZ();

        if (!str.isEmpty()) str += "\n";
        str += String.format("%s: [X: %.1f, Y: %.1f, Z: %.1f]\n", label, x, y, z);
        return str;
    }

    // ------------------------
    // Axis boxes helper
    // ------------------------
    public static void addAxisBoxes(Group root) {
        double offset = 50;
        double boxSize = 5;

        // Positive axis colors
        PhongMaterial redPos = new PhongMaterial(Color.RED);
        PhongMaterial greenPos = new PhongMaterial(Color.LIME);
        PhongMaterial bluePos = new PhongMaterial(Color.BLUE);

        // Negative axis colors (darker)
        PhongMaterial redNeg = new PhongMaterial(Color.RED.darker());
        PhongMaterial greenNeg = new PhongMaterial(Color.LIME.darker());
        PhongMaterial blueNeg = new PhongMaterial(Color.BLUE.darker());

        // +X, -X
        Box boxPosX = new Box(boxSize, boxSize, boxSize);
        boxPosX.setTranslateX(offset);
        boxPosX.setMaterial(redPos);

        Box boxNegX = new Box(boxSize, boxSize, boxSize);
        boxNegX.setTranslateX(-offset);
        boxNegX.setMaterial(redNeg);

        // +Y, -Y
        Box boxPosY = new Box(boxSize, boxSize, boxSize);
        boxPosY.setTranslateY(offset);
        boxPosY.setMaterial(greenPos);

        Box boxNegY = new Box(boxSize, boxSize, boxSize);
        boxNegY.setTranslateY(-offset);
        boxNegY.setMaterial(greenNeg);

        // +Z, -Z
        Box boxPosZ = new Box(boxSize, boxSize, boxSize);
        boxPosZ.setTranslateZ(offset);
        boxPosZ.setMaterial(bluePos);

        Box boxNegZ = new Box(boxSize, boxSize, boxSize);
        boxNegZ.setTranslateZ(-offset);
        boxNegZ.setMaterial(blueNeg);

        root.getChildren().addAll(boxPosX, boxNegX, boxPosY, boxNegY, boxPosZ, boxNegZ);
    }

public static MeshView createRepeatingFloor(
        Image img,
        double texelScale,
        double repeatScale        // how many times texture repeats in world space
) {
    float imgW = (float) img.getWidth();   // pixel width
    float imgH = (float) img.getHeight();  // pixel height

    // Natural world size of ONE texture tile
    float tileW = (float) (imgW * texelScale);
    float tileH = (float) (imgH * texelScale);

    // Final world size
    float worldW = tileW * (float) repeatScale;
    float worldH = tileH * (float) repeatScale;

    float halfW = worldW / 2f;
    float halfH = worldH / 2f;

    TriangleMesh mesh = new TriangleMesh();

    // WORLD GEOMETRY
    mesh.getPoints().addAll(
            -halfW, 0, -halfH,
             halfW, 0, -halfH,
             halfW, 0,  halfH,
            -halfW, 0,  halfH
    );

    // UV COORDS (TILING)
    // if repeatScale = 2 → UV goes 0..2
    mesh.getTexCoords().addAll(
            0, 0,
            (float) repeatScale, 0,
            (float) repeatScale, (float) repeatScale,
            0, (float) repeatScale
    );

    mesh.getFaces().addAll(
            0,0, 1,1, 2,2,
            0,0, 2,2, 3,3
    );

    PhongMaterial mat = new PhongMaterial();
    mat.setDiffuseMap(img);

    MeshView mv = new MeshView(mesh);
    mv.setMaterial(mat);
    mv.setCullFace(CullFace.NONE);
    mv.getTransforms().add(new Rotate(90,Rotate.X_AXIS));
    

    return mv;
}


// getTransforms().add(new Rotate(90,Rotate.X_AXIS));

}




// package com.example.cirkitry;

// // import javafx.animation.AnimationTimer;

// import javafx.application.Application;
//  import javafx.scene.Group;
// import javafx.scene.PerspectiveCamera;
// import javafx.scene.Scene;
// import javafx.scene.SceneAntialiasing;
// import javafx.scene.SubScene;
// import javafx.scene.image.Image;
// import javafx.scene.paint.Color;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.Box;
// import javafx.scene.transform.Rotate;
// import javafx.stage.Stage;

// public class Main extends Application {

//     @Override
//     public void start(Stage stage) {
//         Group root = new Group();

//         // ---- Create a huge flat box (sheet) ----
//         double width = 1000;
//         double height = 1000;
//         double depth = 1; // very thin
//         Box sheet = new Box(width, height, depth);

//         // Rotate so it lies flat on XY plane
//         sheet.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

//         // ---- Apply grid texture ----
//         PhongMaterial material = new PhongMaterial();
//         Image gridImage = new Image("file:resources/grid.jpg"); // replace with your JPEG path
//         material.setDiffuseMap(gridImage);
//         sheet.setMaterial(material);

//         root.getChildren().add(sheet);

//         // ---- Camera ----
//         PerspectiveCamera camera = new PerspectiveCamera(true);
//         camera.setTranslateZ(-500); // pull back to see the sheet
//         camera.setNearClip(0.1);
//         camera.setFarClip(2000);

//         SubScene subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
//         subScene.setFill(Color.BLACK);
//         subScene.setCamera(camera);

//         Group sceneRoot = new Group(subScene);
//         Scene scene = new Scene(sceneRoot, 800, 600);

//         stage.setScene(scene);
//         stage.setTitle("Flat Grid Sheet");
//         stage.show();
//     }

//     public static void main(String[] args) {
//         launch();
//     }
// }

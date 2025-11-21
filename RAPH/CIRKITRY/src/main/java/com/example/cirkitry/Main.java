package com.example.cirkitry;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
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

        MeshView tile = MObj.createRepeatingFloor(new Image(getClass().getResource("/tile.jpeg").toExternalForm()),0.5,100);

        Group gate = MObj.createGate(-5,-5,2,3,1);
        Group gate1 = MObj.createGate(-5,5,2,3,0);
        Group gate2 = MObj.createGate(5,5,2,3,0);
        Group gate3 = MObj.createGate(5,-5,2,3,0);
        Group joint = MObj.createConnect(2,2,true,true,false,true,0);
        Group wire = MObj.createWire(1, 1,6,3);
        Group elbow = MObj.createElbowPipe(-1,-1,0);
        Group light = MObj.createLight(3,-3,0);
        world.getChildren().addAll(tile,gate,joint,wire,gate1,gate2,gate3,elbow,light);








        
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
        GUIOverlay gui = new GUIOverlay();

        StackPane root = new StackPane();
        root.getChildren().addAll(subScene, gui.getRoot());

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
                gui.getCameraPosLabel().setText(str);

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
public static Group createWire(int x,int y,int length,int direction)
    {

        if(x==0)
        {
            x=1;
        }
        if(y==0)
        {
            y=1;
        }



       Group wire = new Group();

       Cylinder pipe = new Cylinder(15,50*length);


       wire.getChildren().addAll(pipe);
       int u =1;
       int v=1;


       if(direction==0||direction==2)
       {
         wire.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));
         u=1;
         v=length;
       }
       else if(direction==1||direction==3)
       {
        wire.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));
        u=length;
         v=1;
       }
       


       if(x>0)
       {
           wire.setTranslateX(x*50-(50*u)/2);
       }
       else{
           wire.setTranslateX(x*50+(50*u)/2);
       }

       if(y>0)
       {
            wire.setTranslateY(y*50-(50*v)/2);
           
       }
       else{
 wire.setTranslateY(y*50+(50*v)/2);
       }
       

       // Y-axis - fixed to match createConnect logic

       if(x>0)
    {
        wire.setTranslateX(wire.getTranslateX()+50*(u-1));
    }
     
    if (y > 0) {
        wire.setTranslateY(wire.getTranslateY()+50*(v-1));
    } 
    

           if(direction==0)
       {
         wire.setTranslateY(wire.getTranslateY()-50*(v-1));
        

       }
       else if(direction==3)
       {
        wire.setTranslateX(wire.getTranslateX()-50*(u-1));
       }


        return wire;
    }


public static Group createGate(int x, int y,int cHeight, int cWidth,int direction) {
    if(x == 0) {
        x = 1;
    }
    if(y == 0) {
        y = 1;
    }
    Group gate = new Group();

    Box body = new Box(50 * cWidth, 50 * cHeight, 50);
    gate.getChildren().addAll(body);
    gate.setTranslateZ(-50/2);


    int u=1;
    int v=1;
    
        if(direction==0||direction==2){
         gate.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));
        u=cWidth;
        v=cHeight;
    }
    else if(direction==1||direction==3){
        gate.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));
         u=cHeight;
        v=cWidth; 
    }
   

    // X-axis - same logic as createConnect
    if (x > 0) {
        gate.setTranslateX(x * 50 - (50 * u) / 2);
    } else {
        gate.setTranslateX(x * 50 + (50 * u) / 2);
    }

    // Y-axis - fixed to match createConnect logic
    if (y > 0) {
        gate.setTranslateY(y * 50 - (50 * v) / 2);
    } else {
        gate.setTranslateY(y * 50 + (50 * v) / 2);
    }


    // X-axis - same logic as createConnect
    if (x > 0) {
        gate.setTranslateX(gate.getTranslateX()+50*(u-1));
        
    } 

    // Y-axis - fixed to match createConnect logic
    if (y > 0) {
        gate.setTranslateY(gate.getTranslateY()+50*(v-1));
    } 


        if(direction==0)
       {
         gate.setTranslateY(gate.getTranslateY()-50*(v-1));
        

       }
       else if(direction==3)
       {
        gate.setTranslateX(gate.getTranslateX()-50*(u-1));
       }

    

    return gate;
}

    public static Group createConnect(int x,int y,Boolean one,Boolean two,Boolean three,Boolean four,int orientation)
    {

        if(x==0)
        {
            x=1;
        }
        if(y==0)
        {
            y=1;
        }
        Group conn = new Group();

         Sphere body = new Sphere(16);

         body.getTransforms().add(new Rotate(90,Rotate.X_AXIS));
         



        


         conn.getChildren().addAll(body);
         if(one)
         {
 Cylinder pipe = new Cylinder(15,20);
         pipe.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));
         pipe.setTranslateY(-15);
         conn.getChildren().addAll(pipe);
         }
         if(two)
         {
 Cylinder pipe = new Cylinder(15,20);
         pipe.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));
         pipe.setTranslateX(15);
         conn.getChildren().addAll(pipe);
         }
         if(three)
         {
             Cylinder pipe = new Cylinder(15,20);
         pipe.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));
         pipe.setTranslateY(15);
         conn.getChildren().addAll(pipe);

         }
         if(four)
         {
            Cylinder pipe = new Cylinder(15,20);
         pipe.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));
         pipe.setTranslateX(-15);
         conn.getChildren().addAll(pipe);

         }
         
         
         if (orientation==0) {
         conn.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));

    }
    else if (orientation==1) {
        conn.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));

    }
    else if (orientation==2) {
        conn.getTransforms().add(new Rotate(180,Rotate.Z_AXIS));

    }
    else
    {
        conn.getTransforms().add(new Rotate(270,Rotate.Z_AXIS));
  
    }

         
       if(x>0)
       {
 conn.setTranslateX(x*50-(50)/2);
       }
       else
        {
conn.setTranslateX(x*50+(50)/2);
       }

       if(y>0)
       {
conn.setTranslateY(y*50-(50)/2);
       }
       else
        {
         conn.setTranslateY(y*50+(50)/2);
       }


      
      



         


        return conn;
    }

    

public static Group createElbowPipe(int x,int y,int orientation) {
    
     if(x==0)
        {
            x=1;
        }
        if(y==0)
        {
            y=1;
        }
    
    Group elbow = new Group();

    
    // Horizontal cylinder
    Cylinder horizontal = new Cylinder(15,25);
    horizontal.setTranslateY(25/2);
    
    
    // Vertical cylinder  
    Cylinder vertical = new Cylinder(15,25);
    
    
    vertical.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
    vertical.setTranslateX(25/2);


    
    // Curved joint (sphere section)
    Sphere joint = new Sphere(15);
    
    
    elbow.getChildren().addAll(horizontal, vertical, joint);

      if (orientation==0) {
         elbow.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));

    }
    else if (orientation==1) {
        elbow.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));

    }
    else if (orientation==2) {
        elbow.getTransforms().add(new Rotate(180,Rotate.Z_AXIS));

    }
    else
    {
        elbow.getTransforms().add(new Rotate(270,Rotate.Z_AXIS));
  
    }

         
       if(x>0)
       {
 elbow.setTranslateX(x*50-(50)/2);
       }
       else
        {
elbow.setTranslateX(x*50+(50)/2);
       }

       if(y>0)
       {
elbow.setTranslateY(y*50-(50)/2);
       }
       else
        {
         elbow.setTranslateY(y*50+(50)/2);
       }

    
    return elbow;
}

public static Group createLight(int x,int y,int orientation)
{

         if(x==0)
        {
            x=1;
        }
        if(y==0)
        {
            y=1;
        }


    Group bulb = new Group();

    Cylinder base = new Cylinder(70,40);
    base.getTransforms().add(new Rotate(90,Rotate.X_AXIS));


    Cylinder bell = new Cylinder(54,160);
    
    bell.getTransforms().add(new Rotate(90,Rotate.X_AXIS));


    Sphere dome = new Sphere(54);
dome.setTranslateZ(-80);

    Cylinder conn = new Cylinder(15,50);
    conn.setTranslateY(-50);
    

    bulb.getChildren().addAll(base,bell,dome,conn);

    
      if (orientation==0) {
         bulb.getTransforms().add(new Rotate(0,Rotate.Z_AXIS));

    }
    else if (orientation==1) {
        bulb.getTransforms().add(new Rotate(90,Rotate.Z_AXIS));

    }
    else if (orientation==2) {
        bulb.getTransforms().add(new Rotate(180,Rotate.Z_AXIS));

    }
    else
    {
        bulb.getTransforms().add(new Rotate(270,Rotate.Z_AXIS));
  
    }

         
       if(x>0)
       {
 bulb.setTranslateX(x*50-(50*3)/2);
       }
       else
        {
bulb.setTranslateX(x*50+(50*3)/2);
       }

       if(y>0)
       {
bulb.setTranslateY(y*50-(50*3)/2);
       }
       else
        {
         bulb.setTranslateY(y*50+(50*3)/2);
       }

           // X-axis - same logic as createConnect
    if (x > 0) {
        bulb.setTranslateX(bulb.getTranslateX()+50*(2));
        
    } 

    // Y-axis - fixed to match createConnect logic
    if (y > 0) {
        bulb.setTranslateY(bulb.getTranslateY()+50*(2));
    } 

    SelectionManager.makeSelectable(bulb);

    return bulb;

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

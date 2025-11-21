
package com.example.cirkitry;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


public class SelectionManager {
    private static Group selectedObject = null;
    private static Box highlightBox = null;
    
    public static void makeSelectable(Group object) {
        object.setOnMouseClicked(event -> {
            // Deselect previous selection
            if (selectedObject != null) {
                clearSelection(selectedObject);
            }
            
            // Select new object
            selectObject(object);
            event.consume(); // Prevent event propagation
        });
    }
    
private static void selectObject(Group object) {
    selectedObject = object;
    
    // Create outline highlight box
    Bounds bounds = object.getBoundsInParent();
    
    // Make the highlight box slightly larger than the object
    highlightBox = new Box(
        bounds.getWidth() * 1.1, 
        bounds.getHeight() * 1.1, 
        bounds.getDepth() * 1.1
    );
    
    // Use bright color with transparency
    PhongMaterial highlightMaterial = new PhongMaterial();
    highlightMaterial.setDiffuseColor(Color.CYAN.deriveColor(0, 1, 1.5, 0.3)); // Bright + transparent
    highlightMaterial.setSpecularColor(Color.WHITE);
    
    highlightBox.setMaterial(highlightMaterial);
    
    // Position the highlight box to match the object
    highlightBox.setTranslateX(object.getTranslateX());
    highlightBox.setTranslateY(object.getTranslateY());
    highlightBox.setTranslateZ(object.getTranslateZ());
    
    // Copy any rotations from the original object
    if (!object.getTransforms().isEmpty()) {
        highlightBox.getTransforms().addAll(object.getTransforms());
    }
    
    // Add highlight to the same parent
    Parent parent = object.getParent();
    if (parent instanceof Group) {
        ((Group) parent).getChildren().add(highlightBox);
    }
}
    private static void clearSelection(Group object) {
        // Remove highlight box
        if (highlightBox != null) {
            Parent parent = highlightBox.getParent();
            if (parent instanceof Group) {
                ((Group) parent).getChildren().remove(highlightBox);
            }
            highlightBox = null;
        }
    }

//     private static void selectObject(Group object) {
//     selectedObject = object;
    
//     // Add glow effect to all 3D shapes in the group
//     for (Node node : object.getChildren()) {
//         if (node instanceof Shape3D) {
//             PhongMaterial glowMaterial = new PhongMaterial(Color.CYAN);
//             glowMaterial.setSelfIlluminationMap(new Image("glow_pattern.png")); // Optional
//             ((Shape3D) node).setMaterial(glowMaterial);
//         }
        
//     }
// }


// private static void clearSelection(Group object) {
//     // Restore original materials
//     for (Node node : object.getChildren()) {
//         if (node instanceof Shape3D) {
//             // Set back to default material
//             PhongMaterial defaultMaterial = new PhongMaterial(Color.GRAY);
//             ((Shape3D) node).setMaterial(defaultMaterial);
//         }
//     }
// }
    
    public static Group getSelectedObject() {
        return selectedObject;
    }
    
    public static void deselectAll() {
        if (selectedObject != null) {
            clearSelection(selectedObject);
            selectedObject = null;
        }
    }
}



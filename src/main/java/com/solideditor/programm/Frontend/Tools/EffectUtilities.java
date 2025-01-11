package com.solideditor.programm.Frontend.Tools;
import javafx.scene.Node;
import javafx.stage.Stage;

//TODO: Not working properly
//src: https://stackoverflow.com/questions/18792822/dragging-an-undecorated-stage-in-javafx
public class EffectUtilities {
    public static void makeDraggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        final boolean[] resizing = {false}; // Flag to detect resizing

        byNode.setOnMousePressed(mouseEvent -> {
            if (stage.getScene().getCursor() != javafx.scene.Cursor.DEFAULT) {
                resizing[0] = true; // Disable dragging when resizing
            } else {
                resizing[0] = false;
                dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                dragDelta.y = stage.getY() - mouseEvent.getScreenY();
            }
        });

        byNode.setOnMouseDragged(mouseEvent -> {
            if (!resizing[0]) { // Only drag when not resizing
                stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
    }

    /** Records relative x and y coordinates. */
    private static class Delta {
        double x, y;
    }
}

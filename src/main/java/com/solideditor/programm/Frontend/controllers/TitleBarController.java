package com.solideditor.programm.Frontend.controllers;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.fxml.FXML;


//controller for interacting with title bar functionalities
public class TitleBarController {
    @FXML
    private TabPane tabPane; // is bind with by fx:id
    @FXML
    public Tab defaultTab; // is bind with fx:id

    /* Getters ********************************************************************************************************/
    public TabPane getTabPane() {
        return tabPane; // expose TabPane to other controllers
    };
    public Tab getDefaultTab () { return defaultTab; };
    /******************************************************************************************************************/
    @FXML
    private void minimizeWindow() {}

    // Handles window maximization
    @FXML
    private void maximizeWindow() {
//        if (stage != null) {
//            if (stage.isMaximized()) {
//                // Restore window to previous size
//                stage.setMaximized(false);
//            } else {
//                // Set the stage to full screen
//                stage.setMaximized(true);
//
//                // Reset bounds to ensure it fills the screen
//                stage.setHeight(stage.getScene().getWindow().getHeight());
//                stage.setWidth(stage.getScene().getWindow().getWidth());
//            }
//        }
    }

    // Handles window closing
    @FXML
    private void closeWindow() {
        Platform.exit();
    }
}

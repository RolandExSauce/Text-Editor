package com.solideditor.programm.Frontend.controllers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TitleBarController {
    @FXML
    private TabPane tabPane; // is bind with by fx:id
    public Tab defaultTab;

    public TabPane getTabPane() {
        return tabPane; // expose TabPane to other controllers
    };

    @FXML
    private void minimizeWindow() {
//        if (stage != null) {
//            stage.setIconified(true);
//        }
    }

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

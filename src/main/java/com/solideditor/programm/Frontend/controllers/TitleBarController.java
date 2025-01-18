package com.solideditor.programm.Frontend.controllers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class TitleBarController {
    public Button btnMaximize;
    @FXML
    private TabPane tabPane; // is bind with by fx:id
    public Tab defaultTab;
    private Stage primaryStage;

    /**
     * Verknüpft die Bühne (Stage) mit diesem Controller.
     *
     * @param primaryStage Die Hauptbühne der Anwendung.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        System.out.println("Primary stage has been set: " + primaryStage);
    }

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
    private void onMaximizeClicked() {
        if (primaryStage != null) {
            if (primaryStage.isMaximized()) {
                // Restore to previous window size
                primaryStage.setMaximized(false);
                btnMaximize.setText("🗖"); // Update button text to "maximize"
            } else {
                // Maximize the window
                primaryStage.setMaximized(true);
                btnMaximize.setText("🗗"); // Update button text to "restore"
            }
        } else {
            System.err.println("Primary stage is not set.");
        }
    }


    // Handles window closing
    @FXML
    private void closeWindow() {
        Platform.exit();
    }
}
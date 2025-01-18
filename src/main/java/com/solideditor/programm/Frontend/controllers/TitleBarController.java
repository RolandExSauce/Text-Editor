package com.solideditor.programm.Frontend.controllers;
<<<<<<< Updated upstream
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

=======
import com.solideditor.programm.API.IConnectAppUtils;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import lombok.Getter;


//controller class for interacting with title bar functionalities
>>>>>>> Stashed changes
public class TitleBarController {
    public Button btnMaximize;
    @FXML
    @Getter
    private TabPane tabPane; // is bind with by fx:id
<<<<<<< Updated upstream
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

=======

    @FXML
    @Getter
    public Tab defaultTab; // is bind with fx:id
    /******************************************************************************************************************/
>>>>>>> Stashed changes
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
        IConnectAppUtils.graceFullShutDown();
    };
}

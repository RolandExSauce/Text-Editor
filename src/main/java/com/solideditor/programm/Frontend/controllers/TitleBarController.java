package com.solideditor.programm.Frontend.controllers;
import com.solideditor.programm.API.IConnectAppUtils;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import lombok.Getter;


//controller class for interacting with title bar functionalities
public class TitleBarController {
    @FXML
    @Getter
    private TabPane tabPane; // is bind with by fx:id

    @FXML
    @Getter
    public Tab defaultTab; // is bind with fx:id
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
        IConnectAppUtils.graceFullShutDown();
    };
}

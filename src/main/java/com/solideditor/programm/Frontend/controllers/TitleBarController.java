package com.solideditor.programm.Frontend.controllers;
import com.solideditor.programm.API.IConnectAppUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Getter;


//titlebar controller
public class TitleBarController {
    public Button btnMaximize;

    @FXML
    @Getter
    private TabPane tabPane;

    @FXML
    @Getter
    public Tab defaultTab;
    /******************************************************************************************************************/
    @FXML
    private void minimizeWindow() {}
    /******************************************************************************************************************/
    @FXML
    private void maximizeWindow() {}
    /******************************************************************************************************************/
    public void onMaximizeClicked(ActionEvent actionEvent) {}
    /******************************************************************************************************************/
    // close window
    @FXML
    private void closeWindow() {
        IConnectAppUtils.graceFullShutDown();
    };
}

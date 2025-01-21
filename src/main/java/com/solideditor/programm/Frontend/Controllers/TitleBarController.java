package com.solideditor.programm.Frontend.Controllers;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.API.IConnectAppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;


//Controller for interacting with titlebar
public class TitleBarController {
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); // get singleton instance
    @FXML
    public Button btnMaximize;
    @FXML
    public ImageView maximizeIcon;
    private final Image maximizeImage =
            new Image(Objects.requireNonNull(getClass()
                    .getResourceAsStream("/com/solideditor/programm/static/icons/maximize1.png")));
    private final Image restoreImage =
            new Image(Objects.requireNonNull(getClass()
                    .getResourceAsStream("/com/solideditor/programm/static/icons/maximize2.png")));

    @FXML
    @Getter
    private TabPane tabPane;

    @FXML
    @Getter
    private Tab defaultTab;

    @Setter
    private Stage primaryStage;

    /******************************************************************************************************************/
    @FXML
    public void initialize() {
        initMainUtilities();
        maximizeIcon.setImage(maximizeImage); //set initial icon
    };
    /******************************************************************************************************************/
    // init stuff coming from IConnectApp
    private void initMainUtilities() {
        //or add listener ? 🤔, as for now its working, cuz this line "this.delayedStageForControllers.set(primaryStage)"
        //in IConnectApp sets it before initialize here in TitleBarController gets called ? 🤔
        this.primaryStage = iConnectApp.getDelayedStageForControllers().getValue();
    };
    /******************************************************************************************************************/
    @FXML
    private void minimizeWindow() {
        if (primaryStage != null) {
            primaryStage.setIconified(true); // Minimize the window
        } else {
            System.err.println("Primary stage is not set.");
        }
    }
    /******************************************************************************************************************/
    public void onMaximizeClicked() {
        if (primaryStage != null) {
            if (primaryStage.isMaximized()) {
                primaryStage.setMaximized(false);
                maximizeIcon.setImage(maximizeImage); // change to maximize icon
            } else {
                primaryStage.setMaximized(true);
                maximizeIcon.setImage(restoreImage); // change to restore icon
            }
        } else {
            System.err.println("Primary/Delayed stage is not set.");
        }
    };
    /******************************************************************************************************************/
    // close window
    @FXML
    private void closeWindow() {
        IConnectAppUtils.graceFullShutDown();
    };
}

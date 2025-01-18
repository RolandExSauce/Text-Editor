package com.solideditor.programm.Frontend.controllers;
import com.solideditor.programm.API.IConnectApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import java.io.IOException;


// Main controller
public class MainController {
    IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance();// get IConnectApp singleton instance
    private final CodeArea codeArea = new CodeArea(); // init CodeArea

    @FXML
    private VBox codeAreaContainer;

    @FXML
    private VBox titleBarMenuBarContainer;

    @FXML
    private VBox bottomContainer;
    /******************************************************************************************************************/
    @FXML
    private void initialize() {
        // set CodeArea in IConnectApp for global access
        iConnectApp.setCodeArea(codeArea);

        // add scrollbar
        ScrollPane scrollPane = new ScrollPane(codeArea);
        scrollPane.setFitToWidth(true); // ensure CodeArea fits horizontally
        scrollPane.setFitToHeight(true); // ensure CodeArea fits vertically

        VBox.setVgrow(codeArea, Priority.ALWAYS); // allow the CodeArea to grow dynamically
        codeAreaContainer.getChildren().add(codeArea);

        loadIncludedFXML("/com/solideditor/programm/fxml/titlebar.fxml", titleBarMenuBarContainer);
        loadIncludedFXML("/com/solideditor/programm/fxml/menubar.fxml", titleBarMenuBarContainer);
        loadIncludedFXML("/com/solideditor/programm/fxml/footerbar.fxml", bottomContainer);
        // After loading FXML components, ensure TabPane is set in IConnectApp
        if (iConnectApp.getTabPane() == null) {
            System.err.println("TabPane is null after loading TitleBarController. Check FXML and controller.");
        };
    };
    /******************************************************************************************************************/
    public void setPrimaryStage(Stage primaryStage) {
        iConnectApp.setPrimaryStage(primaryStage); // pass stage to IConnectApp for global access
    };
    /******************************************************************************************************************/
    // load fxml and init some stuffs through controllers
    private void loadIncludedFXML(String fxmlPath, VBox targetContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            targetContainer.getChildren().add(loader.load());
            Object controller = loader.getController();

            if (controller instanceof MenuBarController menuBarController) {
                if (menuBarController.getSearchTxtField() != null) {
                    iConnectApp.setSearchTxtField(menuBarController.getSearchTxtField());
                };
            };

            if (controller instanceof TitleBarController titleBarController) {
                //set default tab and tabPane from title bar controller
                iConnectApp.setTabPane(titleBarController.getTabPane());
                iConnectApp.initDefaultTab(titleBarController.getDefaultTab());
            };

        } catch (IOException e) {
            //e.printStackTrace();
        }
    };
}

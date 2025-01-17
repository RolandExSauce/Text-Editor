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
    private final IConnectApp IConnectApp = com.solideditor.programm.API.IConnectApp.getInstance(); // init the global IConnectApp
    private final CodeArea codeArea = new CodeArea(); // init CodeArea
    private Stage primaryStage;

    @FXML
    private VBox codeAreaContainer;

    @FXML
    private VBox titleBarMenuBarContainer;

    @FXML
    private VBox bottomContainer;
    /******************************************************************************************************************/
    @FXML
    private void initialize() {
//        IConnectApp.setPrimaryStage(primaryStage);
        // add scrollbar
        ScrollPane scrollPane = new ScrollPane(codeArea);
        scrollPane.setFitToWidth(true); // ensure CodeArea fits horizontally
        scrollPane.setFitToHeight(true); // ensure CodeArea fits vertically



        VBox.setVgrow(codeArea, Priority.ALWAYS); // allow the CodeArea to grow dynamically
        codeAreaContainer.getChildren().add(codeArea);

        // Set the CodeArea in IConnectApp for global access
        IConnectApp.setCodeArea(codeArea);

        loadIncludedFXML("/com/solideditor/programm/fxml/titlebar.fxml", titleBarMenuBarContainer);
        loadIncludedFXML("/com/solideditor/programm/fxml/menubar.fxml", titleBarMenuBarContainer);
        loadIncludedFXML("/com/solideditor/programm/fxml/footerbar.fxml", bottomContainer);
    };
    /******************************************************************************************************************/
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        IConnectApp.setPrimaryStage(primaryStage); // Pass stage to IConnectApp
        System.out.println("IConnectApp: Primary stage set: " + primaryStage);
        System.out.println("TitleBarController: Primary stage set: " + primaryStage);
    };
    /******************************************************************************************************************/
    // load fxml and init some stuffs through controllers
    private void loadIncludedFXML(String fxmlPath, VBox targetContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            targetContainer.getChildren().add(loader.load());
            Object controller = loader.getController();


            if (controller instanceof TitleBarController titleBarController) {
                titleBarController.setPrimaryStage(primaryStage);
                IConnectApp.setTabPane(titleBarController.getTabPane());
            };

            if (controller instanceof MenuBarController menuBarController) {
                menuBarController.setMainUtilities(IConnectApp);
                if (menuBarController.getSearchTxtField() != null) {
                    IConnectApp.setSearchTxtField(menuBarController.getSearchTxtField());
                };
            };


            if (controller instanceof FooterBarController footerBarController) {
                if (IConnectApp.getCodeArea() != null) {
                    footerBarController.setMainUtilities(IConnectApp);
                } else {
                    System.err.println("CodeArea ist noch nicht initialisiert.");
                }

            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    };
}

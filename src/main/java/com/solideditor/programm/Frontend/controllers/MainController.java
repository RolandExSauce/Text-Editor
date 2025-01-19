package com.solideditor.programm.Frontend.controllers;
import com.solideditor.programm.API.IConnectApp;
import javafx.event.ActionEvent;
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
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); //get IConnectApp singleton instance
    private final CodeArea codeArea = new CodeArea(); //init CodeArea

    @FXML
    private VBox codeAreaContainer;

    @FXML
    private VBox titleBarMenuBarContainer;

    @FXML
    private VBox bottomContainer;
    /******************************************************************************************************************/
    @FXML
    private void initialize() {
        iConnectApp.setUpCodeArea(codeArea); // set CodeArea in IConnectApp for global access
        // add scrollbar
        ScrollPane scrollPane = new ScrollPane(codeArea);
        scrollPane.setFitToWidth(true); // ensure CodeArea fits horizontally
        scrollPane.setFitToHeight(true); // ensure CodeArea fits vertically

        VBox.setVgrow(codeArea, Priority.ALWAYS); // allow the CodeArea to grow dynamically
        codeArea.getStyleClass().add("code-area");
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
                    iConnectApp.setUpSearchTxtField(menuBarController.getSearchTxtField());
                };
            };

            if (controller instanceof TitleBarController titleBarController) {
                //set default tab and tabPane from title bar controller
                iConnectApp.setUpTabPane(titleBarController.getTabPane());
                iConnectApp.initDefaultTab(titleBarController.getDefaultTab());
            };

            if (controller instanceof FooterBarController footerBarController) {
                iConnectApp.setWordCountAndSignCount(
                        footerBarController.getWordCountLabel(),
                        footerBarController.getSignCountLabel()
                );
            };

        } catch (IOException e) {
            //e.printStackTrace();
        }
    };
    
    //TODO: implement here

    public void onSave(ActionEvent actionEvent) {
    }

    public void onDiscard(ActionEvent actionEvent) {
    }

    public void onCancelDialog(ActionEvent actionEvent) {
    }

    public void onCloseDialog(ActionEvent actionEvent) {
    }
}

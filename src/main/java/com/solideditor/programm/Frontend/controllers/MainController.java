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
    private final IConnectApp IConnectApp = new IConnectApp(); // init the global IConnectApp
    private final CodeArea codeArea = new CodeArea(); // init CodeArea

    @FXML
    private VBox codeAreaContainer;

    @FXML
    private VBox titleBarMenuBarContainer;

    @FXML
    private VBox bottomContainer;

    public void setPrimaryStage(Stage primaryStage) {
        IConnectApp.setPrimaryStage(primaryStage); // Pass stage to IConnectApp
    }

    @FXML
    private void initialize() {
        loadIncludedFXML("/com/solideditor/programm/fxml/titlebar.fxml", titleBarMenuBarContainer);
        loadIncludedFXML("/com/solideditor/programm/fxml/menubar.fxml", titleBarMenuBarContainer);
        loadIncludedFXML("/com/solideditor/programm/fxml/footerbar.fxml", bottomContainer);

        // add scrollbar
        ScrollPane scrollPane = new ScrollPane(codeArea);
        scrollPane.setFitToWidth(true); // Ensure CodeArea fits horizontally
        scrollPane.setFitToHeight(true); // Ensure CodeArea fits vertically

        VBox.setVgrow(codeArea, Priority.ALWAYS); // allow the CodeArea to grow dynamically
        codeAreaContainer.getChildren().add(codeArea);

        // Set the CodeArea in IConnectApp
        IConnectApp.setCodeArea(this.codeArea);
    }

    private void loadIncludedFXML(String fxmlPath, VBox targetContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            targetContainer.getChildren().add(loader.load());
            Object controller = loader.getController();

            if (controller instanceof MenuBarController menuBarController) {
                menuBarController.setMainUtilities(IConnectApp);
                if (menuBarController.getSearchTxtField() != null) {
                    IConnectApp.setSearchTxtField(menuBarController.getSearchTxtField());
                }

                if (menuBarController.getComboBox() != null) {
                    IConnectApp.setFontsComboBox(menuBarController.getComboBox());
                }
            }

            if (controller instanceof TitleBarController titleBarController) {
                IConnectApp.setTabPane(titleBarController.getTabPane());
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}

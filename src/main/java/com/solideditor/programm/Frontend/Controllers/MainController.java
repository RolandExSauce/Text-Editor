package com.solideditor.programm.Frontend.Controllers;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import java.io.IOException;


// Main controller
public class MainController {
    private static final String className = MainController.class.getSimpleName();

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
    public void initialize() {
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
        if (iConnectApp.getTabPane() != null && iConnectApp.getCodeArea() != null) {
            LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.SUCCESS_CODE_2002);
        };
    };
    /******************************************************************************************************************/
    public void setPrimaryStage(Stage stagePassedFromMain) {
        iConnectApp.setUpStage(stagePassedFromMain); // Pass stage to IConnectApp for global access

        // pass stage to TitleBarController
        for (Node node : titleBarMenuBarContainer.getChildren()) {
            FXMLLoader loader = (FXMLLoader) node.getProperties().get("loader"); // retrieve loader
            if (loader != null) {
                Object controller = loader.getController();

                //pass to other controllers if needed and try to access if null, access the delayed stage
                // view logic from TitleBarController(initMainUtilities)
                if (controller instanceof TitleBarController titleBarController) {
                    titleBarController.setPrimaryStage(stagePassedFromMain);
                };
            };
        };
    };
    /******************************************************************************************************************/
    // load fxml and init some stuffs through controllers
    private void loadIncludedFXML(String fxmlPath, VBox targetContainer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node loadedNode = loader.load();
            targetContainer.getChildren().add(loadedNode);

            // Store the loader reference for later use
            loadedNode.getProperties().put("loader", loader);

            Object controller = loader.getController();

            if (controller instanceof MenuBarController menuBarController) {
                if (menuBarController.getSearchTxtField() != null) {
                    iConnectApp.setUpSearchTxtField(menuBarController.getSearchTxtField());
                };
            };
            if (controller instanceof TitleBarController titleBarController) {
                if (titleBarController.getTabPane() != null
                        & titleBarController.getDefaultTab() != null) {
                    iConnectApp.setUpTabPane(titleBarController.getTabPane());
                    iConnectApp.initDefaultTab(titleBarController.getDefaultTab());
                };
            };
            if (controller instanceof FooterBarController footerBarController) {
                if (footerBarController.getWordCountLabel() != null &
                        footerBarController.getSignCountLabel() != null){
                    iConnectApp.setWordCountAndSignCount(
                            footerBarController.getWordCountLabel(),
                            footerBarController.getSignCountLabel()
                    );
                };
            };
        } catch (IOException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4000);
            } catch (RuntimeException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4001);
            };
    };

    public void onSave(ActionEvent actionEvent) {
    }

    public void onDiscard(ActionEvent actionEvent) {
    }

    public void onCancelDialog(ActionEvent actionEvent) {
    }

    public void onCloseDialog(ActionEvent actionEvent) {
    }
}

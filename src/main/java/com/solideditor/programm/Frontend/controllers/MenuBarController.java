package com.solideditor.programm.Frontend.Controllers;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.App;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.LayoutSettings;
import com.solideditor.programm.Backend.Services.FileIOService;
import com.solideditor.programm.Frontend.Tools.ControllerUtils;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.util.List;
import java.util.Map;


//Controller for interacting with buttons in menu bar
public class MenuBarController {
    private static final String className = MenuBarController.class.getSimpleName();
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); // get singleton instance
    @FXML
    private ComboBox<String> fontsComboBox;
    public Button btnPrint;
    public Button btnSave;
    public Button btnNewFile;
    public Button btnOpenFile;

    @FXML
    @Getter
    private TextField searchTxtField; //currently only exposed to Main controller

    @FXML
    @Getter
    private Label lableFoundAt;
    private TabPane tabPane;
    private Map<String, TabEntry> openTabs;
    private CodeArea codeArea;
    /******************************************************************************************************************/
    @FXML
    public void initialize() {
        initMainUtilities();
        ControllerUtils.addComboBoxSelectionListener(fontsComboBox, () -> {
            String selectedFont = fontsComboBox.getValue();
            if (selectedFont != null) {
                LayoutSettings.changeFont(codeArea, selectedFont);
            };
        });
        // Set an action for the print button to open the dialog
        btnPrint.setOnAction(event -> openDialog());
    };
    /******************************************************************************************************************/
    // init stuff coming from IConnectApp and combo box
    private void initMainUtilities() {
        this.tabPane = iConnectApp.getTabPane();
        this.openTabs = iConnectApp.getOpenTabs();
        List<String> fonts = iConnectApp.getFonts();
        this.codeArea = iConnectApp.getCodeArea();
        ControllerUtils.initializeComboBox(fontsComboBox, fonts);
        //System.out.println("MenuClass:" +className);

    };
    /******************************************************************************************************************/
    //open an existing file
    @FXML
    private void onOpenFileClicked() {
        if (iConnectApp != null) {
            if (tabPane != null && openTabs != null) {
                FileIOService.openFile();
            };
        };
    };
    /******************************************************************************************************************/
    //create new file, if user selects existing file, open that one and overriding its content
    @FXML
    private void onCreateFileClicked() {
        if (iConnectApp != null) {
            if (tabPane != null && openTabs != null) {
                FileIOService.createNewFile();
            };
        };
    };
    /******************************************************************************************************************/
    //save a file with an existing path
    @FXML
    private void onSaveFileClicked() {
        if (iConnectApp != null) {
            if (tabPane != null && openTabs != null) {
                FileIOService.saveFileOrSaveNewFileUnderDir();
            };
        };
    };
    /******************************************************************************************************************/
  //  @FXML
//    private void onPrintClicked() {};

    // Method to open the dialog
    private void openDialog() {
        try {
            // Load the dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/solideditor/programm/static/dialog.fxml"));
            Scene dialogScene = new Scene(loader.load());

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            dialogStage.setScene(dialogScene);
            dialogStage.setTitle("Unsaved Changes");
            dialogStage.show();

            // Pass the stage to the dialog controller
            DialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

        } catch (IOException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4000);
        } catch (RuntimeException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4001);
        };
    }
};


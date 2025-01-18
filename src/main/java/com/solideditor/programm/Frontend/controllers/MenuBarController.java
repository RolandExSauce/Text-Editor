package com.solideditor.programm.Frontend.controllers;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.services.FileIOService;
import com.solideditor.programm.Frontend.Tools.ControllerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import java.util.List;
import java.util.Map;


//Controller for interacting with buttons in menu bar
public class MenuBarController {
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); // get singleton instance
    @FXML
    public ComboBox<String> fontsComboBox;
    public Button btnBold;
    public Button btnItalian;
    public Button btnUnderline;
    public Button btnPrint;
    public Button btnSave;
    public Button btnNewFile;
    public Button btnOpenFile;

    @FXML
    @Getter
    public TextField searchTxtField; //currently only exposed to Main controller
    private TabPane tabPane;
    private Map<String, TabEntry> openTabs;
    /******************************************************************************************************************/
    @FXML
    public void initialize() {
        initMainUtilities();
        //TODO: update code Area here
        ControllerUtils.addComboBoxSelectionListener(fontsComboBox, () -> {
            //String selectedFont = fontsComboBox.getValue();
            /* for debug
            if (selectedFont != null) {
                System.out.println("Selected font: " + selectedFont);
            }
            */
        });
    };
    /******************************************************************************************************************/
    // init stuff coming from IConnectApp
    public void initMainUtilities() {
        this.tabPane = iConnectApp.getTabPane();
        this.openTabs = iConnectApp.getOpenTabs();
        List<String> fonts = iConnectApp.getFonts();
        ControllerUtils.initializeComboBox(fontsComboBox, fonts);
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
    @FXML
    private void onPrintClicked() { };
    /******************************************************************************************************************/
    @FXML
    private void onSelectFontClicked() {
        System.out.println("Font change triggered!");
    };
    /******************************************************************************************************************/
    @FXML
    private void onChangeToBoldClicked() {
        System.out.println("Bold style applied!");
    };
    /******************************************************************************************************************/
    @FXML
    private void onChangeToItalicClicked() {
        System.out.println("Italic style applied!");
    };
    /******************************************************************************************************************/
    @FXML
    private void onUnderlineTxtClicked() {
        System.out.println("Underline style applied!");
    };

};

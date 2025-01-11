package com.solideditor.programm.Frontend.controllers;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Backend.services.FileIOService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Map;

//Controller for interacting with buttons in menu bar
public class MenuBarController {
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
    public TextField searchTxtField; //for search input in file

    //from central IConnectApp Class
    private IConnectApp IConnectApp;

    // References to shared resources
    private TabPane tabPane;
    private Map<Tab, String> tabFileMap;

    public TextField getSearchTxtField() {
        return searchTxtField; // expose text field to other controllers (I think for now just MainController ?,
        // will be needed in order to reflect changes on codeArea
    };

    //same for comboBox
    public ComboBox<String> getComboBox(){ return fontsComboBox; };


    //TODO: load fonts from IConnectApp, but init comboBox here ?
    @FXML
    public void initialize() {
        // Add sample items
        fontsComboBox.getItems().addAll("Short", "Medium-length item", "A very long selected item that adjusts width");

        // Bind width dynamically to the selected item's size
        fontsComboBox.prefWidthProperty().bind(
                fontsComboBox.getEditor().textProperty().length().multiply(7) // Adjust multiplier to fit your font size
                        .add(30) // Padding for dropdown arrow and padding
        );

        // Set a default selected item (optional)
        fontsComboBox.getSelectionModel().select(0);
    };


    //used in MainController, primarily for getting the tabPane set through TitleBarController
    //and other stuffs like tabFileMap used for global access
    //TODO: put this in other layer ?
    public void setMainUtilities(IConnectApp IConnectApp) {
        this.IConnectApp = IConnectApp;
        this.tabPane = IConnectApp.getTabPane();
        this.tabFileMap = IConnectApp.getTabFileMap();
    };
    /******************************************************************************************************************/
    //open file
    @FXML
    private void onOpenFileClicked() {
        if (IConnectApp != null) {
            if (tabPane != null && tabFileMap != null) {
                FileIOService.openFile(IConnectApp);
            }
        }
    };
    /******************************************************************************************************************/
    //create new file, if user selects existing file, open that one
    @FXML
    private void onCreateFileClicked() {
        if (IConnectApp != null) {
            if (tabPane != null && tabFileMap != null) {
                FileIOService.createNewFile(IConnectApp);
            }
        }
    };
    /******************************************************************************************************************/
    @FXML
    private void onSaveFileClicked() {
        if (IConnectApp != null) {
            if (tabPane != null && tabFileMap != null) {
                FileIOService.saveFile(IConnectApp);
            }
        }
    };
    /******************************************************************************************************************/
    @FXML
    private void onPrintClicked() {
        System.out.println("Print button clicked!");
        // Add logic to print the content of the current CodeArea
    };
    /******************************************************************************************************************/
    @FXML
    private void onSearchTxtInFileClicked() {
        System.out.println("Search initiated!");
        // Implement search logic in the CodeArea
    };
    /******************************************************************************************************************/
    @FXML
    private void onSelectFontClicked() {
        System.out.println("Font change triggered!");
        // Add logic to change the font of the CodeArea
    };
    /******************************************************************************************************************/
    @FXML
    private void onChangeToBoldClicked() {
        System.out.println("Bold style applied!");
        //codeArea.setStyle("-fx-font-weight: bold;");
    };
    /******************************************************************************************************************/
    @FXML
    private void onChangeToItalicClicked() {
        System.out.println("Italic style applied!");
        //codeArea.setStyle("-fx-font-style: italic;");
    };
    /******************************************************************************************************************/
    @FXML
    private void onUnderlineTxtClicked() {
        System.out.println("Underline style applied!");
        //codeArea.setStyle("-fx-underline: true;");
    };

}

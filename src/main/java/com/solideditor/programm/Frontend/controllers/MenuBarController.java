package com.solideditor.programm.Frontend.controllers;

import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Backend.services.FileIOService;
import com.solideditor.programm.Frontend.Tools.ChangeFontUtils;
import com.solideditor.programm.Frontend.Tools.ControllerUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;

import java.util.List;
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
    public TextField searchTxtField;
    public Label lableFoundAt;

    //from central IConnectApp Class
    private IConnectApp IConnectApp;

    // References to shared resources
    private TabPane tabPane;
    private Map<Tab, String> tabFileMap;
    /******************************************************************************************************************/
    @FXML
    public void initialize() {

        initializeFont();
        // Check if fontsComboBox is available
        if (fontsComboBox == null) {
            System.err.println("fontsComboBox is null. Cannot initialize.");
            return;
        };

        // Add a placeholder until fonts are provided
        fontsComboBox.setItems(FXCollections.observableArrayList("Loading..."));

        // Add a listener to handle ComboBox selection changes (if applicable later)
        ControllerUtils.addComboBoxSelectionListener(fontsComboBox, () -> {
            String selectedFont = fontsComboBox.getValue();
            if (selectedFont != null) {
                System.out.println("Selected font: " + selectedFont);
                // Add logic to apply the selected font to the CodeArea or other UI components
            }
        });
    };
    /******************************************************************************************************************/
    public TextField getSearchTxtField() {
        return searchTxtField; // expose text field to other controllers (I think for now just MainController ?,
        // will be needed in order to reflect changes on codeArea
    };
    /******************************************************************************************************************/
    //used in MainController, primarily for getting the tabPane set through TitleBarController
    //and other stuffs like tabFileMap used for global access
    public void setMainUtilities(IConnectApp IConnectApp) {
        this.IConnectApp = IConnectApp;
        this.tabPane = IConnectApp.getTabPane();
        this.tabFileMap = IConnectApp.getTabFileMap();
        List<String> fonts = IConnectApp.getFonts();

        // Now initialize fontsComboBox since fonts are available
        if (fontsComboBox != null && fonts != null && !fonts.isEmpty()) {
            fontsComboBox.setItems(FXCollections.observableArrayList(fonts));
            fontsComboBox.getSelectionModel().select(0); // Default to first font
        } else {
            System.err.println("Fonts or fontsComboBox is null or empty. Cannot populate ComboBox.");
        }
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
    private void onSelectFontClicked() {
        System.out.println("Font change triggered!");
        // Add logic to change the font of the CodeArea
    };
    /******************************************************************************************************************/
    @FXML
    private void onChangeToBoldClicked() {
        System.out.println("Bold-Button gedrückt!");

        // Prüfen, ob die CodeArea verfügbar ist
        if (IConnectApp != null && IConnectApp.getCodeArea() != null) {
            ControllerUtils.makeTextBold(IConnectApp.getCodeArea());
        } else {
            System.err.println("CodeArea ist nicht initialisiert.");
        }
    }
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
    /******************************************************************************************************************/
    @FXML
    private void initializeFont() {
        if (fontsComboBox != null) {
            fontsComboBox.valueProperty().addListener((obs, oldFont, newFont) -> {
                if (newFont != null && !newFont.isEmpty()) {
                    System.out.println("Schriftart ausgewählt: " + newFont);

                    // Anwenden des Fonts auf den markierten Text in der CodeArea
                    if (IConnectApp != null && IConnectApp.getCodeArea() != null) {
                        ChangeFontUtils.changeFontForSelectedText(IConnectApp.getCodeArea(), newFont);
                    }
                }
            });
        }

    }




}

package com.solideditor.programm.API;
import com.solideditor.programm.App;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.services.TabManagerService;
import com.solideditor.programm.Frontend.Tools.GetFontsFromJSON;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IConnectApp {
    private final ChangeListener<String> codeAreaChangeListener;
    private static final String pathToJSONFonts = "/com/solideditor/programm/static/fonts.json";
    private final List<String> fonts; // Store loaded fonts
    private ComboBox<String> fontComboBox;
    private TextField searchTxtField;
    private TabPane tabPane;
    private final Map<Tab, String> tabFileMap = new HashMap<>();
    private CodeArea codeArea;

    private Stage primaryStage;
    private String unsavedContent = "";

    public IConnectApp() {
        // Load fonts from JSON or use defaults
        this.fonts = loadFonts();

        // Initialize the codeAreaChangeListener
        this.codeAreaChangeListener = IConnectAppListeners
                .createCodeAreaChangeListener(tabPane, tabFileMap, codeArea, new String[]{unsavedContent});
    };

    // Helper method to load fonts
    private List<String> loadFonts() {
        try (InputStream resourceStream = Objects.requireNonNull(App.class.getResourceAsStream(pathToJSONFonts))) {
            return GetFontsFromJSON.appFontsFromStream(resourceStream);
        } catch (Exception e) {
            System.err.println("Error loading fonts from JSON: " + e.getMessage());
            return List.of("Arial", "Times New Roman", "Verdana", "Georgia");
        }
    };


    //Set CodeArea
    public void setCodeArea(CodeArea codeArea) {
        if (codeArea == null) {
            throw new IllegalArgumentException("CodeArea cannot be null");
        }
        this.codeArea = codeArea;

        // Add change listener for the CodeArea
        addCodeAreaChangeListener();
        //System.out.println("CodeArea set successfully in IConnectApp.");
    };


    // TabPane setup
    public void setTabPane(TabPane tabPane) {
        if (tabPane == null) {
            throw new IllegalArgumentException("TabPane cannot be null");
        }
        this.tabPane = tabPane;
        detectTabPaneChanges();

        if (codeArea != null) {
            addCodeAreaChangeListener();
        }

        if (searchTxtField != null) {
            addSearchTxtFieldListener();
        }
    };


    public void setSearchTxtField(TextField txtField) {
        //System.out.println("Setting searchTxtField: " + txtField);
        this.searchTxtField = txtField;

        // Add listener if TabPane is already set
        if (searchTxtField != null) {
            addSearchTxtFieldListener();
        }
    };


    // ComboBox setup
    public void setFontsComboBox(ComboBox<String> comboBox) {
        this.fontComboBox = comboBox;
        if (fontComboBox != null) {
            fontComboBox.setItems(FXCollections.observableArrayList(fonts));
            if (!fonts.isEmpty()) {
                fontComboBox.setValue(fonts.getFirst()); // Default font
            }

            fontComboBox.setOnAction(event -> {
                String selectedFont = fontComboBox.getValue();
                System.out.println("Selected font: " + selectedFont);
            });
        } else {
            System.err.println("fontComboBox is null!");
        }
    };

    // Utility methods
    public TabPane getTabPane() {
        return tabPane;
    };

    public CodeArea getCodeArea() {
        return codeArea;
    };

    public Stage getPrimaryStage() {
        return primaryStage;
    };

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    };

    public Map<Tab, String> getTabFileMap() {
        return tabFileMap;
    };

    public void addCodeAreaChangeListener() {
        codeArea.textProperty().addListener(
                IConnectAppListeners.createCodeAreaChangeListener(tabPane, tabFileMap, codeArea, new String[]{unsavedContent})
        );
    };

    public void addSearchTxtFieldListener() {
        IConnectAppListeners.addSearchTxtFieldListener(searchTxtField, tabPane, tabFileMap, codeArea);
    };

    public void detectTabPaneChanges() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {

            if (newTab != null) {
                String newFilePath = tabFileMap.get(newTab);
//                System.out.println("Tab switched. New file path: " + newFilePath);
                if (newFilePath != null) {

                    System.out.println("File path: "+ newFilePath);
                    FileInfo fileInfo = TabManagerService.getFileInfo(newFilePath);

                    //TODO: info is null ????
                    System.out.println("info from switched tab: "+ fileInfo);

                    if (fileInfo != null) {
                        String newContent = fileInfo.fileContent();

                        if (!newContent.equals(codeArea.getText())) { // Update only if different

                            unsavedContent = newContent;

                            // Temporarily remove listener during updates
                            codeArea.textProperty().removeListener(codeAreaChangeListener);

                            // Update CodeArea content
                            codeArea.clear();
                            codeArea.replaceText(newContent);

                            // Reattach listener
                            codeArea.textProperty().addListener(codeAreaChangeListener);
                        }
                    } else {
                        codeArea.clear();
                        unsavedContent = "";
                    }
                }
            } else {
                // Clear CodeArea if no tab is selected
                codeArea.clear();
                unsavedContent = "";
            }
        });
    };

}

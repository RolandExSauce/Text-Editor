package com.solideditor.programm.API;
import com.solideditor.programm.App;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.LayoutHelpers;
import com.solideditor.programm.Backend.services.TabManagerService;
import com.solideditor.programm.Frontend.Tools.GetFontsFromJSON;
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


// Interface Class to bridge functionalities from Backend Services and Frontend Controllers
public class IConnectApp {
    private static final String pathToJSONFonts = "/com/solideditor/programm/static/fonts.json";
    private final List<String> fonts; // Store loaded fonts
    private TextField searchTxtField;
    private TabPane tabPane;
    private final Map<Tab, String> tabFileMap = new HashMap<>();
    private CodeArea codeArea;
    private Stage primaryStage;
    private static IConnectApp instance;
    //no arg constructor to init some values
    public IConnectApp() {
        this.fonts = loadFonts();
    };

    public static IConnectApp getInstance() {
        if (instance == null) {
            instance = new IConnectApp();
        }
        return instance;
    }
    /* Setters to set references for global usage *********************************************************************/
    public void setCodeArea(CodeArea codeArea) {
        if (codeArea == null) throw new IllegalArgumentException("CodeArea cannot be null");
        this.codeArea = codeArea;
        addCodeAreaChangeListener();

    };

    public void setTabPane(TabPane tabPane) {
        if (tabPane == null) throw new IllegalArgumentException("TabPane cannot be null");
        this.tabPane = tabPane;
        detectTabPaneChanges();
    };

    public void setSearchTxtField(TextField txtField) {
        if (txtField == null) throw new IllegalArgumentException("Search TextField cannot be null");
        this.searchTxtField = txtField;
        addSearchTxtFieldListener();
    };

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    };

    /* Getters to hold references to use globally *********************************************************************/
    public TabPane getTabPane() {
        return tabPane;
    };

    public CodeArea getCodeArea() {
        return codeArea;
    };

    public Stage getPrimaryStage() {
        return primaryStage;
    };

    public Map<Tab, String> getTabFileMap() {
        return tabFileMap;
    };

    public List<String> getFonts() { return fonts; };
    /******************************************************************************************************************/
    //Listener for CodeArea changes
    public void addCodeAreaChangeListener() {
        if (codeArea != null && tabPane != null) {
            codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
                // Get the currently selected tab
                Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
//                System.out.println("Current selected Tab: "+ selectedTab);

                if (selectedTab != null) {
                    String filePath = tabFileMap.get(selectedTab);
                    if (filePath != null) {
                        FileInfo fileInfo = TabManagerService.getFileInfo(filePath);
                        if (fileInfo != null) {
                            // Update the tab's indicator based on changes
                            boolean hasChanges = !newValue.equals(fileInfo.fileContent());
                            FileHelpers.updateTabIfFileSavedIndicator(selectedTab, hasChanges);

                            // Save unsaved content in TabManagerService for persistence
                            TabManagerService.saveUnsavedContent(filePath, newValue);
                        }
                    }

                    else {
                        // Handle unsaved tabs (no file path)
                        String unsavedTabName = "unsaved::" + selectedTab.getText(); // Construct key for unsaved tabs

                        // Retrieve unsaved content from TabManagerService
                        String unsavedContent = TabManagerService.getUnsavedContentForUnsavedFiles(unsavedTabName);

                        // Check if the content has changed
                        boolean hasChanges = !newValue.equals(unsavedContent);

                        // Update the tab's indicator based on changes
                        FileHelpers.updateTabIfFileSavedIndicator(selectedTab, hasChanges);

                        // Save the updated content in TabManagerService
                        TabManagerService.saveUnsavedContent(unsavedTabName, newValue);
                    };

                }
            });
        } else {
            System.err.println("CodeArea or TabPane is null! Cannot add listener.");
        }
    };
    /******************************************************************************************************************/
    // Listener for Search TextField changes
    public void addSearchTxtFieldListener() {
        if (searchTxtField != null) {
            searchTxtField.textProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("being typed: " + newValue);
                if (newValue == null || newValue.isEmpty()) {
                    LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, "", codeArea);
                } else {
                    LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, newValue, codeArea);
                }
            });
        } else {
            System.err.println("Search TextField is null! Cannot add listener.");
        }
    };
    /******************************************************************************************************************/
    // Detect changes on TabPane
    public void detectTabPaneChanges() {
        if (tabPane != null) {
            tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (oldTab != null) {
                    // Save unsaved content of the current tab before switching
                    String oldFilePath = tabFileMap.get(oldTab);
                    if (oldFilePath != null) {
                        TabManagerService.saveUnsavedContent(oldFilePath, codeArea.getText());
                    }
                };

                if (newTab != null) {
                    String newFilePath = tabFileMap.get(newTab);


                    //If a tab is saved (file was opened)
                    if (newFilePath != null) {

                        String newKey = tabFileMap.get(newTab);
                        //System.out.println("File path: " + newFilePath);
                        FileInfo fileInfo = TabManagerService.getFileInfo(newFilePath);

                        // Check for unsaved content first
                        String unsavedContent = TabManagerService.getUnsavedContentForSavedFiles(newKey);
                        String contentToLoad = (unsavedContent != null) ? unsavedContent :
                                Objects.requireNonNull(fileInfo).fileContent();

                        if (!contentToLoad.equals(codeArea.getText())) {
                            codeArea.clear();
                            codeArea.replaceText(contentToLoad);
                        }
                    }

                    //If a tab is not saved (file was not opened)
                    else {
                        // Handle unsaved tabs (no file path)
                        String unsavedTabName = "unsaved::"+newTab.getText(); //for unsaved "unsaved::" + tabName is key

                        // Load unsaved content for the tab
                        String unsavedContent = TabManagerService.getUnsavedContentForUnsavedFiles(unsavedTabName);

                        System.out.println("got unsaved content: "+unsavedContent);

                        // Default to empty content if no unsaved content is found
                        String contentToLoad = (unsavedContent != null) ? unsavedContent : "";

                        // Update the CodeArea only if the content has changed
                        if (!contentToLoad.equals(codeArea.getText())) {
                            codeArea.clear();
                            codeArea.replaceText(contentToLoad);
                        }
                    }

                } else {
                    // Clear CodeArea if no tab is selected
                    codeArea.clear();
                }
            });
        } else {
            System.err.println("TabPane is null! Cannot detect changes.");
        }
    };
    /******************************************************************************************************************/
    // Helper method to load fonts from GetFontsFromJSON class
    private List<String> loadFonts() {
        try (InputStream resourceStream = Objects.requireNonNull(App.class.getResourceAsStream(pathToJSONFonts))) {
            return GetFontsFromJSON.appFontsFromStream(resourceStream);
        } catch (Exception e) {
            return List.of("Arial", "Times New Roman", "Verdana", "Georgia"); // default fonts on failure
        }
    };
}
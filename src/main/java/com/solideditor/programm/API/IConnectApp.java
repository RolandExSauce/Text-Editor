package com.solideditor.programm.API;
import com.solideditor.programm.App;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.LayoutHelpers;
import com.solideditor.programm.Backend.services.FileIOService;
import com.solideditor.programm.Backend.services.TabManagerService;
import com.solideditor.programm.Frontend.Tools.GetFontsFromJSON;
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
    private static final int MAX_OPEN_TABS = 5; //max number of opened tab files

    //Also nice solution to use "::" cuz new files can't be saved using colon afaik, but only on WINDOWS ?
    private static final String prefKeyForUnsavedFile = "unsaved::"; //just to stay consistent globally
    private static final IConnectApp instance = new IConnectApp(); // IConnectApp Singleton instance
    private static final String pathToJSONFonts = "/com/solideditor/programm/static/fonts.json";
    private final List<String> fonts; // Store loaded fonts
    private TextField searchTxtField;
    private TabPane tabPane;

    // stores string key filename + TabEntry (contains Tab, FileInfo and maybe unsaved content)
    private final Map<String, TabEntry> openTabs = new HashMap<>();
    private CodeArea codeArea;
    private Stage primaryStage;

    //return singleton instance, so that other classes can use the same instance
    public static IConnectApp getIconnectAppInstance() {
        return instance;
    };

    //no arg constructor to init some values, maybe add more
    private IConnectApp() {
        this.fonts = loadFonts(); //load fonts
    };

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

    //set default tab and add it to open tabs
    public void setDefaultTab(Tab defaultTab) {
        if (defaultTab == null) throw new IllegalArgumentException("Default tab cannot be null");
        // Generate the key for the default tab
        String defaultTabKey = TabManagerService.genFileKey(defaultTab.getText(), null);
        // Check if the tab is already in openTabs, if not add it
        if (!openTabs.containsKey(defaultTabKey)) {
            FileInfo unsavedFileInfo = new FileInfo(defaultTabKey, null, defaultTab.getText(), null);
            openTabs.put(defaultTabKey, new TabEntry(defaultTab, unsavedFileInfo));
        } else System.out.println("Default tab already exists in openTabs.");
        // Add the default tab to the TabPane if it's not already there
        if (!tabPane.getTabs().contains(defaultTab)) tabPane.getTabs().add(defaultTab);
        defaultTab.setOnClosed(e -> FileIOService.closeFile(defaultTab));
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
    public boolean isTabLimitReached() {
        return openTabs.size() >= MAX_OPEN_TABS;
    };
    public String getPrefKeyForUnsavedFile(){ return prefKeyForUnsavedFile; }
    public TabPane getTabPane() {
        return tabPane;
    };
    public CodeArea getCodeArea() {
        return codeArea;
    };
    public Stage getPrimaryStage() {
        return primaryStage;
    };
    public Map<String, TabEntry> getOpenTabs() {
        return openTabs;
    };
    public List<String> getFonts() { return fonts; };
    /******************************************************************************************************************/
    //Listener for CodeArea changes
    //TODO: for default tab graphic is null ???
    public void addCodeAreaChangeListener() {
        if (codeArea != null && tabPane != null) {
            codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
                // Get the currently selected tab
                Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                //System.out.println("Current selected Tab: "+ selectedTab);

                if (selectedTab != null) {
                    TabEntry tabEntry = TabManagerService.getTabEntryByTab(selectedTab);
                    if (tabEntry != null) {
                    String filePath = tabEntry.fileInfo().filePathOrTempTabName();
                    if (filePath != null) {
                        FileInfo fileInfo = tabEntry.fileInfo();
                        // Update the tab's indicator based on changes
                        boolean hasChanges = !newValue.equals(fileInfo.fileContent());
                        FileHelpers.updateTabIfFileSavedIndicator(selectedTab, hasChanges);

                        // Save unsaved content in TabManagerService for persistence
                        TabManagerService.saveUnsavedContent(filePath, newValue);
                    };
                }
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
                    LayoutHelpers.highlightWordSequenceFound(tabPane, openTabs, "", codeArea);
                } else {
                    LayoutHelpers.highlightWordSequenceFound(tabPane, openTabs, newValue, codeArea);
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
                    TabEntry tabEntry = TabManagerService.getTabEntryByTab(oldTab);
                    if (tabEntry != null) {
                        String oldFilePath = tabEntry.fileInfo().filePathOrTempTabName();
                        if (oldFilePath != null) {
                            TabManagerService.saveUnsavedContent(oldFilePath, codeArea.getText());
                        }
                    }
                };

                //if new tab is not null
                if (newTab != null) {
                    TabEntry tabEntry = TabManagerService.getTabEntryByTab(newTab);
                    if (tabEntry != null) {
                        String newFilePath = tabEntry.fileInfo().filePathOrTempTabName();

                        //If a tab is saved (file was opened)
                        if (newFilePath != null) {
                            String openTabKey = tabEntry.fileInfo().filePathOrTempTabName();
                            //System.out.println("File path: " + openTabKey);
                            FileInfo fileInfo = tabEntry.fileInfo();

                            // Check for unsaved content first
                            String unsavedContent = TabManagerService.getUnsavedContentForSavedFiles(openTabKey);
                            String contentToLoad = (unsavedContent != null) ? unsavedContent :
                                    Objects.requireNonNull(fileInfo).fileContent();

                            // check if content to load is null
                            if (contentToLoad == null) {
                                contentToLoad = ""; //just to avoid null pointer exc upon calling equals below
                            };

                            // Update CodeArea only if content differs
                            if (!contentToLoad.equals(codeArea.getText())) {
                                codeArea.clear();
                                codeArea.replaceText(contentToLoad);
                            };
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

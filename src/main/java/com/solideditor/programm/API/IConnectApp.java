package com.solideditor.programm.API;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.LayoutSettings;
import com.solideditor.programm.Backend.Services.FileIOService;
import com.solideditor.programm.Backend.Services.TabManagerService;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;
import org.fxmisc.richtext.CodeArea;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


// Interface Class to bridge functionalities from Backend Services and Frontend Controllers
@Data
public class IConnectApp {

    private static final String className = IConnectApp.class.getSimpleName();

    private final ObjectProperty<Stage> delayedStageForControllers = new SimpleObjectProperty<>();

    private static final IConnectApp instance = new IConnectApp(); // IConnectApp Singleton instance

    private static final int MAX_OPEN_TABS = 5; //max number of opened tab files, scalable

    //Also nice solution to use "::" cuz new files can't be saved using colon afaik, but maybe only on WINDOWS ?
    private static final String prefKeyForUnsavedFile = "unsaved::"; //just to stay consistent globally

    private final List<String> fonts;

    private TextField searchTxtField;

    private TabPane tabPane;

    private Label lableFoundAt;

    private Label wordCountLabel;

    private Label signCountLabel;

    //IMPORTANT: FOR DEFAULT TABS, the key will include a prefix plus the tab name,
    // NEED TO UPDATE TO "<ACTUAL FILEPATH>::<FILENAME>" ON SAVE NEW FILE
    private final Map<String, TabEntry> openTabs = new HashMap<>();   // stores string key filename + TabEntry

    private CodeArea codeArea;

    private Stage primaryStage;

    //return singleton instance, so that other classes can use the same instance
    public static IConnectApp getIconnectAppInstance() {
        return instance;
    };

    //no arg constructor to init some values, maybe add more
    private IConnectApp() {
        this.fonts = LayoutSettings.loadFonts();
        LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.SUCCESS_CODE_2002);
    };

    /* Setters to set references for global usage *********************************************************************/
    public void setUpStage(Stage stage){
        setPrimaryStage(stage);
        this.delayedStageForControllers.set(primaryStage);
    };

    //as soon as codeArea is set up, listen to changes
    public void setUpCodeArea(CodeArea codeArea){
        setCodeArea(codeArea);
        listenForCodeAreaChangesForOpenFile();
    };

    //word count labels and sign count labels will listen for code area changes
    public void setWordCountAndSignCount (Label wordCount, Label signCount){
        setWordCountLabel(wordCount);
        setSignCountLabel(signCount);
        listenForCodeAreaChangesForOpenFile();
    };

    //label found at will listen to search text field changes
    public void setUpLabelFoundAt (Label lableFoundAt){
        setLableFoundAt(lableFoundAt);
        listenForSearchTextFieldChangesForOpenFile();
    };

    public void setUpTabPane(TabPane tabPane) {
        setTabPane(tabPane);
        detectTabPaneChanges();
        listenForCodeAreaChangesForOpenFile();
    };
    /******************************************************************************************************************/
    //set default tab and add it to open tabs map
    public void initDefaultTab(Tab defaultTab) {
        String defaultTabName = defaultTab.getText();
        Tab defaultTabWithIndicator =
                TabManagerService.createTabWithIfFileSavedIndicator(defaultTab);

        // Generate the key for the default tab
        String defaultTabKey = TabManagerService.genFileKey(defaultTabName, null);

        // Check if the tab is already in openTabs, if not add it
        if (!openTabs.containsKey(defaultTabKey)) {
            FileInfo unsavedFileInfo = new FileInfo(defaultTabKey,
                    null, defaultTabName, null);
            openTabs.put(defaultTabKey, new TabEntry(defaultTabWithIndicator, unsavedFileInfo));
        };
        // Add the default tab to the TabPane if it's not already there
        if (!tabPane.getTabs().contains(defaultTabWithIndicator)) {
            tabPane.getTabs().add(defaultTabWithIndicator);
        };
        tabPane.getSelectionModel().select(defaultTabWithIndicator);
        tabPane.requestFocus();
        Objects.requireNonNull(defaultTabWithIndicator)
        .setOnCloseRequest( e -> {
            e.consume();
            FileIOService.closeFile(defaultTabWithIndicator);
        });
    };
    /******************************************************************************************************************/
    public void setUpSearchTxtField(TextField searchTxtField) {
        setSearchTxtField(searchTxtField);
        listenForSearchTextFieldChangesForOpenFile();
        listenForCodeAreaChangesForOpenFile();
    };
    /******************************************************************************************************************/
    public boolean getIsTabLimitReached() {
        return openTabs.size() >= MAX_OPEN_TABS; // check tab limit
    };

    public String getPrefKeyForUnsavedFile(){ return prefKeyForUnsavedFile; } // return pref key unsaved files
    /******************************************************************************************************************/
    // Detect changes on TabPane
    private void detectTabPaneChanges() {
        if(codeArea != null & tabPane != null){
            IConnectAppUtils.detectTabPaneChanges(tabPane, codeArea);
            listenForCodeAreaChangesForOpenFile();
            LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.SUCCESS_CODE_2003);
        };
    };
    /******************************************************************************************************************/
    //Listener for CodeArea changes
    private void listenForCodeAreaChangesForOpenFile() {
        if(codeArea != null & tabPane != null & wordCountLabel != null & signCountLabel != null){
            IConnectAppUtils.addCodeAreaChangeListener(
                    codeArea, tabPane, wordCountLabel,
                    signCountLabel, lableFoundAt, searchTxtField
            );
            LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.SUCCESS_CODE_2003);
        };
    };
    /******************************************************************************************************************/
    //Listener for search field changes
    private void listenForSearchTextFieldChangesForOpenFile() {
        if(codeArea != null & tabPane != null & searchTxtField != null & lableFoundAt != null){
            IConnectAppUtils.addSearchTxtFieldListener(searchTxtField, codeArea, tabPane, lableFoundAt);
            LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.SUCCESS_CODE_2003);

        };
    };
    //NOTE: In application.log, log infos appear in different order due to threading conditions
    //in javafx thread, e.g. codeArea could still be null while tabPane is not, to visualize make
    //sure err codes have distinguishable names
};

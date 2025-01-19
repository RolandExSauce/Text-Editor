package com.solideditor.programm.API;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.LayoutSettings;
import com.solideditor.programm.Backend.services.FileIOService;
import com.solideditor.programm.Backend.services.TabManagerService;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.Data;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Interface Class to bridge functionalities from Backend Services and Frontend Controllers
@Data
public class IConnectApp {

    private static final IConnectApp instance = new IConnectApp(); // IConnectApp Singleton instance

    private static final int MAX_OPEN_TABS = 5; //max number of opened tab files, scalable

    //Also nice solution to use "::" cuz new files can't be saved using colon afaik, but maybe only on WINDOWS ?
    private static final String prefKeyForUnsavedFile = "unsaved::"; //just to stay consistent globally
    private final List<String> fonts;
    private TextField searchTxtField;

    private TabPane tabPane;

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
        this.fonts = LayoutSettings.loadFonts(); //load fonts
    };

    /* Setters to set references for global usage *********************************************************************/
    public void setUpCodeArea(CodeArea codeArea){
        if (codeArea == null) throw new IllegalArgumentException("CodeArea cannot be null");
        setCodeArea(codeArea);
        listenForCodeAreaChangesForOpenFile();
    };

    public void setWordCountAndSignCount (Label wordCount, Label signCount){
        if (wordCount == null && signCount == null) throw new IllegalArgumentException("TabPane cannot be null");
        setWordCountLabel(wordCount);
        setSignCountLabel(signCount);
        listenForCodeAreaChangesForOpenFile();
    };

    public void setUpTabPane(TabPane tabPane) {
        if (tabPane == null) throw new IllegalArgumentException("TabPane cannot be null");

        setTabPane(tabPane);
        detectTabPaneChanges();
        listenForCodeAreaChangesForOpenFile();
    };
    /******************************************************************************************************************/
    //set default tab and add it to open tabs
    public void initDefaultTab(Tab defaultTab) {
        if (defaultTab == null) throw new IllegalArgumentException("Default tab cannot be null");
        String dfTabName = defaultTab.getText();

        // Create a circle to act as the save state indicator
        Circle indicator = new Circle(5); // Radius of 5
        indicator.setFill(Color.TRANSPARENT); // Initially transparent

        // Set the circle as the tab's graphic
        defaultTab.setGraphic(indicator);

        // Generate the key for the default tab
        String defaultTabKey = TabManagerService.genFileKey(dfTabName, null);

        // Check if the tab is already in openTabs, if not add it
        if (!openTabs.containsKey(defaultTabKey)) {
            FileInfo unsavedFileInfo = new FileInfo(defaultTabKey,
                    null, dfTabName, null);
            openTabs.put(defaultTabKey, new TabEntry(defaultTab, unsavedFileInfo));
        } else System.out.println("Default tab already exists in openTabs.");
        // Add the default tab to the TabPane if it's not already there
        if (!tabPane.getTabs().contains(defaultTab)) tabPane.getTabs().add(defaultTab);
        defaultTab.setOnClosed(e -> FileIOService.closeFile(defaultTab));
    };
    /******************************************************************************************************************/
    public void setUpSearchTxtField(TextField txtField) {
        if (txtField == null) throw new IllegalArgumentException("Search TextField cannot be null");
        setSearchTxtField(txtField);
        addSearchTxtFieldListener();
        listenForCodeAreaChangesForOpenFile();
    };
    /******************************************************************************************************************/
    public boolean isTabLimitReached() {
        return openTabs.size() >= MAX_OPEN_TABS; // check tab limit
    };
    /******************************************************************************************************************/
    public String getPrefKeyForUnsavedFile(){ return prefKeyForUnsavedFile; } // return pref key unsaved files
    /******************************************************************************************************************/
    // Listener for Search TextField changes
    private void addSearchTxtFieldListener() {
        if (searchTxtField != null) {
            searchTxtField.textProperty().addListener((observable, oldValue, newValue) -> {
                if(codeArea != null & tabPane != null) {
                    if (newValue == null || newValue.isEmpty()) {
                        LayoutSettings.highlightWordSequenceFound(tabPane, "", codeArea);
                    } else {
                        LayoutSettings.highlightWordSequenceFound(tabPane, newValue, codeArea);
                    };
//                    FileHelpers.findWordInFileAndReturnPos(newValue);
                }
            });
        } else {
            System.err.println("Search TextField is null! Cannot add listener.");
        }
    };
    /******************************************************************************************************************/
    // Detect changes on TabPane
    private void detectTabPaneChanges() {
        if(codeArea != null & tabPane != null){
            IConnectAppUtils.detectTabPaneChanges(tabPane, codeArea);
            listenForCodeAreaChangesForOpenFile();
        };
    };
    /******************************************************************************************************************/
    //Listener for CodeArea changes
    private void listenForCodeAreaChangesForOpenFile() {
        if(codeArea != null & tabPane != null & wordCountLabel != null & signCountLabel != null){
            IConnectAppUtils.addCodeAreaChangeListener(codeArea, tabPane, wordCountLabel, signCountLabel);
        };
    };
}

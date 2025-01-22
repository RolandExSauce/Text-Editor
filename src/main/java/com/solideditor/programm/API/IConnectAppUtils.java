package com.solideditor.programm.API;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Services.TabManagerService;
import com.solideditor.programm.Backend.Utils.LayoutSettings;
import com.solideditor.programm.Frontend.Tools.ControllerUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.CodeArea;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



//extracted listeners here to keep code clean
public class IConnectAppUtils {

    // Listener for Search TextField changes
    public static void addSearchTxtFieldListener(
            TextField searchTxtField,
            CodeArea codeArea,
            TabPane tabPane,
            Label lableFoundAt
    ) {
        if (searchTxtField != null) {
            searchTxtField.textProperty().addListener((observable, oldValue, newValue) -> {

                //Highlight text found and return positions
                //TODO: WHEN TAB IS SWITCHED THE STYLES ARE ALSO RESETTED, PROBABLY BECAUSE TEXT AREA IS CLEARED

                if(codeArea != null & tabPane != null) {
                    if (newValue == null || newValue.isEmpty()) {
                        LayoutSettings.highlightWordSequenceFound(tabPane, "", codeArea, codeArea.getText());
                    } else {
                        LayoutSettings.highlightWordSequenceFound(tabPane, newValue, codeArea, codeArea.getText());
                    };

                    // Extract and format row positions
                    Map<Integer, List<Integer>> rowPositions =
                            FileHelpers.findWordInFileAndReturnPos(codeArea.getText(), newValue);

                    if (rowPositions.isEmpty()) {
                        //System.out.println("No matches found.");
                        lableFoundAt.setText("No matches.");
                    } else {
                        // Extract the keys (row numbers) from the map
                        String rowNumbers = rowPositions.keySet().stream()
                                .map(String::valueOf) // Convert each key to a string
                                .sorted() // Optional: Sort the row numbers
                                .reduce((a, b) -> a + ", " + b) // Join with commas
                                .orElse(""); // Handle empty case

                        System.out.println("Row numbers: " + rowNumbers);
                        lableFoundAt.setText("Found at rows: " + rowNumbers);
                    }
                }
            });

        };
    };
    /******************************************************************************************************************/
    //Listener for codeArea changes
    public static void addCodeAreaChangeListener(
            CodeArea codeArea, TabPane tabPane,
            Label wordCount, Label signCount
    ) {
            codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
                // Get the currently selected tab
                Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                if (selectedTab != null) {
                    TabEntry tabEntry = TabManagerService.getTabEntryByTab(selectedTab);
                    if (tabEntry != null) {
                        String filePath = tabEntry.fileInfo().filePathOrTempTabName();
                        if (filePath != null) {
                            boolean hasChanges = FileHelpers.fileTabHasChanges(newValue, tabEntry);
                            FileHelpers.updateTabIfFileSavedIndicator(selectedTab, hasChanges);

                            // Save unsaved content in TabManagerService for persistence
                            TabManagerService.saveUnsavedContent(filePath, newValue);

                            int wordCountValue = ControllerUtils.countWordsInCodeArea(newValue);
                            wordCount.setText(String.valueOf(wordCountValue));
                            signCount.setText(String.valueOf(newValue.length()));
                        };
                    };
                };
            });
    };
    /******************************************************************************************************************/
    //detect tab pane changes
    public static void detectTabPaneChanges(TabPane tabPane, CodeArea codeArea) {
            tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {

                if (oldTab != null) {
                    // Save unsaved content of the current tab before switching
                    TabEntry tabEntry = TabManagerService.getTabEntryByTab(oldTab);
                    if (tabEntry != null) {
                        String oldFilePath = tabEntry.fileInfo().filePathOrTempTabName();
                        if (oldFilePath != null) {
                            TabManagerService.saveUnsavedContent(oldFilePath, codeArea.getText());
                        };
                    };
                };

                //if new tab is not null
                if (newTab != null) {
                    TabEntry tabEntry = TabManagerService.getTabEntryByTab(newTab);
                    if (tabEntry != null) {
                        String filePath = tabEntry.fileInfo().filePathOrTempTabName();

                        if (filePath != null) {
                            String openTabKey = tabEntry.fileInfo().filePathOrTempTabName();
                            FileInfo fileInfo = tabEntry.fileInfo();

                            // Check for unsaved content first
                            String unsavedContent = TabManagerService.getUnsavedContentForSavedFiles(openTabKey);
                            String contentToLoad = (unsavedContent != null) ? unsavedContent :
                                    Objects.requireNonNull(fileInfo).fileContent();

                            // Update CodeArea only if content differs
                            if (!contentToLoad.equals(codeArea.getText())) {
                                codeArea.clear();
                                codeArea.replaceText(contentToLoad);
                            };
                        }
                    };
                }
                else {
                    codeArea.clear(); //clear if no tab selected
                };
            });
    };
    /******************************************************************************************************************/
    //exit app, actually don't belong here in a class called listener
    public static void graceFullShutDown(){
        // Create a ScheduledExecutorService for delayed execution
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            scheduler.schedule(() -> {
                Platform.runLater(() -> {
                    System.out.println("All tabs closed. Counter reset. Exiting app gracefully.");
                    Platform.exit();
                });
            }, 400, TimeUnit.MILLISECONDS);
            scheduler.shutdown(); //shut down scheduler
        };
    };
};

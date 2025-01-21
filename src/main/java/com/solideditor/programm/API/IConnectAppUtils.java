package com.solideditor.programm.API;
import java.util.concurrent.ScheduledExecutorService;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Services.TabManagerService;
import com.solideditor.programm.Frontend.Tools.ControllerUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


//extracted here to keep code clean
public class IConnectAppUtils {

    //add listener for code area
    public static void addCodeAreaChangeListener(CodeArea codeArea, TabPane tabPane, Label wordCount, Label signCount) {
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
    //exit app
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

package com.solideditor.programm.Backend.Services;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.FileUtils;
import com.solideditor.programm.Frontend.Controllers.DialogController;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import java.util.concurrent.CompletableFuture;

//TODO: BAD PRACTICE =>  DialogController.openDialog, here a method from a controller is being
//called from Service != don't align with MVC pattern, for now we good, to lazy 😪 to fix
// FIXME: call services methods from the controller



//file service class to interact with controllers
public class FileIOService {
    private static final String className = FileIOService.class.getSimpleName();
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); //get singleton instance
    /******************************************************************************************************************/
    //save new file under "xy dir" or replace file existing file with new content
    public static void saveUnsavedNewFile() {
        // Get the currently selected tab
        Tab selectedTab = iConnectApp.getTabPane().getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1000);
            return;
        };

        // Get the current text area text to save on file creation
        String contentToSave  = iConnectApp.getCodeArea().getText();

        // Open a "Save As" dialog to get the file path
        FileInfo fileInfo = FileUtils.saveNewFileUnderDirAndGetFileContent(
                iConnectApp.getPrimaryStage(), contentToSave );

        if (fileInfo == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1001);
            return;
        };

        String newKey = TabManagerService.genFileKey(fileInfo.fileName(), fileInfo.filePathOrTempTabName());
        String oldKey = iConnectApp.getPrefKeyForUnsavedFile() + selectedTab.getText();

        // update the openTabs map
        iConnectApp.getOpenTabs().put(newKey, new TabEntry(selectedTab, fileInfo));
        iConnectApp.getOpenTabs().remove(oldKey); // remove old key
        selectedTab.setText(fileInfo.fileName()); // update tab title
        FileHelpers.updateTabIfFileSavedIndicator(selectedTab, false); // Mark as saved

    };
    /******************************************************************************************************************/
    //save file
    public static void saveFile() {

        //get the current tab
        Tab selectedTab = iConnectApp.getTabPane().getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1000);
            return;
        };

        //get the file path for the selected tab
        TabEntry tabEntry = TabManagerService.getTabEntryByTab(selectedTab);
        String filePath = tabEntry.fileInfo().filePathOrTempTabName();

        if (filePath == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1001);
            return;
        };

        //get the current content of the CodeArea
        String updatedContent = iConnectApp.getCodeArea().getText();

        //save the changes to the file
        FileUtils.saveChangesToFile(new FileInfo(filePath, updatedContent));

        //update the fileInfo in TabManagerService, so when switching tabs content is also updated
        TabManagerService.updateFileInfo(filePath, updatedContent);

        // Mark the file as saved by updating the tab indicator
        FileHelpers.updateTabIfFileSavedIndicator(selectedTab, false);
    };
    /******************************************************************************************************************/
    public static void saveFileOrSaveNewFileUnderDir(){
        //get the current tab
        Tab selectedTab = iConnectApp.getTabPane().getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1000);
            return;
        };

        //get the file path for the selected tab
        TabEntry tabEntry = TabManagerService.getTabEntryByTab(selectedTab);
        String filePath = tabEntry.fileInfo().filePathOrTempTabName();

        if (filePath == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1005);
        }
        else {
            if(filePath.startsWith(iConnectApp.getPrefKeyForUnsavedFile())) saveUnsavedNewFile();
            else saveFile();
        };
    };
    /******************************************************************************************************************/
    // open file
    public static void openFile() {

        //first check tab limit
        if (iConnectApp.getIsTabLimitReached()) {
             DialogController.openDialog(DialogController.DialogKIND.MAX_TAB_LIMIT);
            return;
        };

        FileInfo fileInfo = FileUtils.openFileAndGetContent(iConnectApp.getPrimaryStage());
        if (fileInfo == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1001);
            return;
        };

            // Check if the file is already open
            Tab existingTab = TabManagerService.getOpenTab(fileInfo.fileName(), fileInfo.filePathOrTempTabName());
            if (existingTab != null) {
                // Ensure the existing tab is visible in the TabPane
                if (!iConnectApp.getTabPane().getTabs().contains(existingTab)) {
                    iConnectApp.getTabPane().getTabs().add(existingTab);
                }
                iConnectApp.getTabPane().getSelectionModel().select(existingTab);
                iConnectApp.getTabPane().requestFocus();
            }
            else {

                // If not open, create a new tab and add it using TabManagerService
                Tab newTab = TabManagerService.createTabWithIfFileSavedIndicator(fileInfo.fileName());
                Tab addedTab = TabManagerService.openFileAndAddToOpenTabs(fileInfo, newTab);

                if (addedTab != null) {
                    addedTab.setOnCloseRequest( e -> {
                        e.consume();
                        closeFile(addedTab);
                    });

                    iConnectApp.getTabPane().getTabs().add(addedTab);
                    //ONLY AFTER SETTING the tabFileMap we focus on that added tab
                    iConnectApp.getTabPane().getSelectionModel().select(addedTab);
                    iConnectApp.getTabPane().requestFocus();

                    // Set the content of the CodeArea
                    iConnectApp.getCodeArea().clear();
                    iConnectApp.getCodeArea().replaceText(fileInfo.fileContent());
                } else {
                    LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1002);
                };
            }
    };
    /******************************************************************************************************************/
    //create a new file
    public static void createNewFile() {
        //putting this here on the higher level works instead of doing it inside createUnsavedTabAndAddToOpenTabs
        if (iConnectApp.getIsTabLimitReached()) {
                DialogController.openDialog(DialogController.DialogKIND.MAX_TAB_LIMIT);
                return;
        };
        // Create a new unsaved tab with default name
        Tab addedUnsavedTab = TabManagerService.createUnsavedTabAndAddToOpenTabs();

        if(addedUnsavedTab == null){
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1003);
            return;
        };

        // Add the tab to the TabPane
        iConnectApp.getTabPane().getTabs().add(addedUnsavedTab);

        addedUnsavedTab.setOnCloseRequest( e -> {
            e.consume();
          closeFile(addedUnsavedTab);
        });

        //ONLY AFTER SETTING the openTab map we focus on that added tab
        iConnectApp.getTabPane().getSelectionModel().select(addedUnsavedTab);
        iConnectApp.getTabPane().requestFocus();
        iConnectApp.getCodeArea().clear();
    };
    /******************************************************************************************************************/
    // Close a file and its tab
    public static void closeFile(Tab tab) {
        if (tab == null || iConnectApp.getTabPane() == null || iConnectApp.getOpenTabs() == null) {
            System.out.println("Error: Invalid state. Cannot close file.");
            return;
        };

        // Get the associated TabEntry
        TabEntry tabEntry = TabManagerService.getTabEntryByTab(tab);
        if (tabEntry == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1006);
            return;
        };

        String filePathOrDefaultTabName = tabEntry.fileInfo().filePathOrTempTabName();
        boolean areChangesSaved = TabManagerService.areChangesSaved(tab); //check if tab has changes

        // Handle unsaved tab Files, the ones which still has "unsaved::" in their name
        if (filePathOrDefaultTabName != null && filePathOrDefaultTabName
                .contains(iConnectApp.getPrefKeyForUnsavedFile())) {

            //User creates a new tab ("unsaved") and don't write anything then simply closes it
            //check if unsaved tabs has changes
            if (areChangesSaved) {
                //TODO: ALSO CHECK IF ITS THE LAST TAB THEN EXIT THE APP

                //changes are saved
                boolean removedFromManager = TabManagerService
                        .removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);
                if(!removedFromManager) {
                    //here log an error cuz the tab MUST have been created "unsaved" file tab
                    LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
                    return;
                };
            } else {
                //changes are not saved, that means user created a new tab, made some changes, did not save and
                //is now trying to close it
                CompletableFuture<DialogController.DialogResult> dialogResultCompletableFuture =
                        DialogController.openDialog(DialogController.DialogKIND.UNSAVED_CHANGES);

                dialogResultCompletableFuture
                        .thenAccept(result -> {
                            switch (result) {
                                case SAVE -> {
                                    saveUnsavedNewFile(); //because it's a created tab (unsaved)
                                    Platform.runLater(() -> {
                                        iConnectApp.getOpenTabs().remove(filePathOrDefaultTabName);
                                        iConnectApp.getTabPane().getTabs().remove(tab);
                                    });
                                }
                                case DISCARD -> {
                                    Platform.runLater(() -> {
                                        //TODO: test before removal
                                        iConnectApp.getOpenTabs().remove(filePathOrDefaultTabName);
                                        iConnectApp.getTabPane().getTabs().remove(tab);
                                    });
                                }
                                case CANCEL -> {
                                } //do nothing
                                default -> System.out.println("Cancel, doing nothing");
                            }
                        });
            }
        }


        //files with valid paths, opened through "open button"
        else if (filePathOrDefaultTabName != null) {

            //check if saved tab files has changes
            if (areChangesSaved) {
                //TODO: ALSO CHECK IF ITS THE LAST TAB THEN EXIT THE APP

                //changes are saved
                boolean removedFromManager = TabManagerService
                        .removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);

                if(!removedFromManager) {
                    //here prompt a dialog cuz the file could have been deleted from filesystem
                    DialogController.openDialog(DialogController.DialogKIND.FILE_NOT_EXIST);
                    //LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
                    return;
                };

                boolean resourceClosed = FileUtils.closeFileRessource(filePathOrDefaultTabName); //close the file ressource
                if (!resourceClosed) {
                    DialogController.openDialog(DialogController.DialogKIND.FILE_NOT_EXIST);
                    System.out.println("Could not close file for" + filePathOrDefaultTabName); //If file is deleted in filesystem
                }

            } else {
                //changes are not saved and user opened a file through "open button",
                // made some changes, did not save and is now trying to close it
                CompletableFuture<DialogController.DialogResult> dialogResultCompletableFuture =
                        DialogController.openDialog(DialogController.DialogKIND.UNSAVED_CHANGES);

                dialogResultCompletableFuture
                        .thenAccept(result -> {
                            switch (result) {
                                case SAVE -> {
                                    saveFile(); //because it's an already saved file tab
                                    Platform.runLater(() -> {
                                        iConnectApp.getOpenTabs().remove(filePathOrDefaultTabName);
                                        iConnectApp.getTabPane().getTabs().remove(tab);
                                        FileUtils.closeFileRessource(filePathOrDefaultTabName); //close the file ressource
                                    });
                                }
                                case DISCARD -> {
                                    Platform.runLater(() -> {

                                        // Remove from TabManagerService and mappings
                                        boolean removedFromManager = TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);

                                        if(!removedFromManager) {
                                            //here prompt a dialog cuz the file could have been deleted from filesystem
                                            DialogController.openDialog(DialogController.DialogKIND.FILE_NOT_EXIST);
                                            //LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
                                            return;
                                        };

                                        boolean resourceClosed = FileUtils.closeFileRessource(filePathOrDefaultTabName); //close the file ressource
                                        if (!resourceClosed) {
                                            DialogController.openDialog(DialogController.DialogKIND.FILE_NOT_EXIST);
                                            System.out.println("Could not close file for" + filePathOrDefaultTabName); //If file is deleted in filesystem
                                        }

                                    });
                                }
                                case CANCEL -> {
                                } //do nothing
                                default -> System.out.println("SERVICE: No valid result.");
                            }
                        });
            };
        }
        else {
            // Handle edge case where no file path is associated
            System.out.println("No file path associated with the tab. Removing from TabPane.");
            iConnectApp.getTabPane().getTabs().remove(tab);
        }

    };

}
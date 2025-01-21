package com.solideditor.programm.Backend.Services;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.API.IConnectAppUtils;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.FileUtils;
import com.solideditor.programm.Frontend.Controllers.DialogController;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.scene.control.Tab;


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
        if (iConnectApp.isTabLimitReached()) {
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

                    iConnectApp.getTabPane().getTabs().add(addedTab);
                    //ONLY AFTER SETTING the tabFileMap we focus on that added tab
                    iConnectApp.getTabPane().getSelectionModel().select(addedTab);
                    iConnectApp.getTabPane().requestFocus();

                    // Set the content of the CodeArea
                    iConnectApp.getCodeArea().clear();
                    iConnectApp.getCodeArea().replaceText(fileInfo.fileContent());

                    addedTab.setOnClosed(e -> closeFile(addedTab));
                } else {
                    LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1002);
                };
            }
    };
    /******************************************************************************************************************/
    //create a new file
    public static void createNewFile() {

        System.out.println("size tabpane start: "+iConnectApp.getTabPane().getTabs().size());
        System.out.println("size opentabs start: "+ iConnectApp.getOpenTabs().size());

        //putting this here on the higher level works instead of doing it inside createUnsavedTabAndAddToOpenTabs
        if (iConnectApp.isTabLimitReached()) {
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

        addedUnsavedTab.setOnClosed(e -> closeFile(addedUnsavedTab));

        //ONLY AFTER SETTING the openTab map we focus on that added tab
        iConnectApp.getTabPane().getSelectionModel().select(addedUnsavedTab);
        iConnectApp.getTabPane().requestFocus();
        iConnectApp.getCodeArea().clear();
    };
    /******************************************************************************************************************/
    // Close a file and its tab
//    public static void closeFile(Tab tab) {
//        if (tab == null || iConnectApp.getTabPane() == null || iConnectApp.getOpenTabs() == null) {
//            System.out.println("Error: Invalid state. Cannot close file.");
//            return;
//        };
//
//        // Get the associated TabEntry
//        TabEntry tabEntry = TabManagerService.getTabEntryByTab(tab);
//        if (tabEntry == null) {
//            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1006);
//            return;
//        };
//
//        String filePathOrDefaultTabName = tabEntry.fileInfo().filePathOrTempTabName();
//        // Handle unsaved tabs, check if file tab name still has "unsaved::"
//        if (filePathOrDefaultTabName != null && filePathOrDefaultTabName.contains(iConnectApp.getPrefKeyForUnsavedFile())) {
//            // Remove from TabManagerService and mappings
//            boolean removedFromManager = TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);
//            if (removedFromManager) {
//                iConnectApp.getTabPane().getTabs().remove(tab);
//                iConnectApp.getOpenTabs().remove(filePathOrDefaultTabName);
//            } else {
//                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
//            };
//
//        } else if (filePathOrDefaultTabName != null) {   // Handle saved tabs with a valid file path
//
//            // Attempt to close the file resource
//            boolean resourceClosed = FileUtils.closeFileRessource(filePathOrDefaultTabName);
//            if (!resourceClosed) {
//                DialogController.openDialog(DialogController.DialogKIND.FILE_NOT_EXIST);
//            };
//
//            // Remove from TabManagerService and mappings
//            if (!TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName)) {
//                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
//            };
//
//        } else {
//            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1007);
//            iConnectApp.getTabPane().getTabs().remove(tab);
//        }
//
//        // check if file is saved
//        boolean checkIfSaved = TabManagerService.areChangesSaved(tab);
//
//        //it's the last tab so also close the app
//        if (iConnectApp.getTabPane().getTabs().isEmpty() && iConnectApp.getOpenTabs().isEmpty()) {
//            if(checkIfSaved) IConnectAppUtils.graceFullShutDown();
//            //open dialog to give user options
//            else  {
//                DialogController.openDialog(DialogController.DialogKIND.UNSAVED_CHANGES);
//            };
//
//            TabManagerService.resetUntitledTabCounter();
//        }
//
//        //it's not the last tab
//        else {
//
//            if(!checkIfSaved){
//                DialogController.openDialog(DialogController.DialogKIND.UNSAVED_CHANGES);
//            };
//
//        }
//    };
    public static void closeFile(Tab tab) {

        System.out.println("size tabpane start: "+iConnectApp.getTabPane().getTabs().size());
        System.out.println("size opentabs start: "+ iConnectApp.getOpenTabs().size());


        if (tab == null || iConnectApp.getTabPane() == null || iConnectApp.getOpenTabs() == null) {
            System.out.println("Error: Invalid state. Cannot close file.");
            return;
        }

        // Get the associated TabEntry
        TabEntry tabEntry = TabManagerService.getTabEntryByTab(tab);
        if (tabEntry == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1006);
            return;
        }

        // Check if there are unsaved changes
        boolean hasUnsavedChanges = !TabManagerService.areChangesSaved(tab);

        // If there are unsaved changes, show the dialog
        if (hasUnsavedChanges) {
            System.out.println("has changes in FIO");
            DialogController.openDialog(DialogController.DialogKIND.UNSAVED_CHANGES)
                    .thenAccept(result -> {
                        switch (result) {
                            case SAVE -> {

                                System.out.println("save the dialog please");
                                // Save the file, then close the tab
//                                saveFile();
//                                closeTab(tab, tabEntry);
                            }
                            case DISCARD -> {
                                // Discard changes and close the tab
//                                closeTab(tab, tabEntry);
                            }
                            case CANCEL -> {
                                // Do nothing, user cancelled the close action
                            }
                            default -> {
                                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1007);
                            }
                        }

                        // If it's the last tab, handle application shutdown
                        if (iConnectApp.getTabPane().getTabs().isEmpty() && iConnectApp.getOpenTabs().isEmpty()) {
                            IConnectAppUtils.graceFullShutDown();
                            TabManagerService.resetUntitledTabCounter();
                        }
                    });
        } else {

            System.out.println("size tabpane: "+iConnectApp.getTabPane().getTabs().size());
            System.out.println("size opentabs: "+ iConnectApp.getOpenTabs().size());



            // If it's the last tab, handle application shutdown
            if (iConnectApp.getTabPane().getTabs().size() == 1 && iConnectApp.getOpenTabs().size() == 1) {

                System.out.println("hereeeee");

//                System.out.println("size: "+iConnectApp.getOpenTabs().size());

                System.out.println("has no changes in FIO and is last tab");
                IConnectAppUtils.graceFullShutDown();
                TabManagerService.resetUntitledTabCounter();
            }
//            else {
//                //System.out.println("has no changes in FIO and is not last tab");
//                closeTab(tab, tabEntry);
//            }
        }
    };

    /**
     * Closes the tab and performs necessary cleanup.
     */
    private static void closeTab(Tab tab, TabEntry tabEntry) {
        String filePathOrDefaultTabName = tabEntry.fileInfo().filePathOrTempTabName();

        if (filePathOrDefaultTabName != null && filePathOrDefaultTabName.contains(iConnectApp.getPrefKeyForUnsavedFile())) {
            // Remove unsaved file mapping
            boolean removed = TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);
            if (removed) {
                iConnectApp.getTabPane().getTabs().remove(tab);
                iConnectApp.getOpenTabs().remove(filePathOrDefaultTabName);
            } else {
                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
            }
        } else if (filePathOrDefaultTabName != null) {
            // Close the file resource
            boolean resourceClosed = FileUtils.closeFileRessource(filePathOrDefaultTabName);
            if (!resourceClosed) {
                DialogController.openDialog(DialogController.DialogKIND.FILE_NOT_EXIST);
            }

            // Remove from TabManagerService and mappings
            boolean removed = TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);
            if (!removed) {
                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
            }
        }

        // Remove the tab from the TabPane
        iConnectApp.getTabPane().getTabs().remove(tab);
    }

}
package com.solideditor.programm.Backend.services;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.API.IConnectAppUtils;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.FileUtils;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.scene.control.Tab;


//file service class to interact with controllers
public class FileIOService {
    private static final String className = FileIOService.class.getName();
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
        String fakePath = tabEntry.fileInfo().filePathOrTempTabName();

        if (fakePath == null) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1005);
        }
        else {
            if(fakePath.startsWith(iConnectApp.getPrefKeyForUnsavedFile())) saveUnsavedNewFile();
            else saveFile();
        };
    };
    /******************************************************************************************************************/
    // open file
    public static void openFile() {
        //first check tab limit
        if (iConnectApp.isTabLimitReached()) {
            //TODO: waiting to implement open dialog warning
            System.out.println("OpenFile: Maximum tabs reached.");
            return; // prevent opening a new tab
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
                    // Set up tab behavior and add to TabPane
                    addedTab.setOnClosed(e -> closeFile(addedTab));
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
        if (iConnectApp.isTabLimitReached()) {
            //TODO: waiting to implement open dialog warning
            System.out.println("createNewFile: Maximum tabs reached.");
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

        // on tab is closed
        addedUnsavedTab.setOnClosed(e -> closeFile(addedUnsavedTab));

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
        // Handle unsaved tabs, check if file tab name still has "unsaved::"
        if (filePathOrDefaultTabName != null && filePathOrDefaultTabName.contains(iConnectApp.getPrefKeyForUnsavedFile())) {
            // Remove from TabManagerService and mappings
            boolean removedFromManager = TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName);
            if (removedFromManager) {
                iConnectApp.getTabPane().getTabs().remove(tab);
                iConnectApp.getOpenTabs().remove(filePathOrDefaultTabName);
            } else {
                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
            };

        } else if (filePathOrDefaultTabName != null) {   // Handle saved tabs with a valid file path
            // Attempt to close the file resource
            boolean resourceClosed = FileUtils.closeFileRessource(filePathOrDefaultTabName);
            if (!resourceClosed) {
                //TODO: fxml dialog FIle could not be found, must be deleted in filesystem
                System.out.println("Could not close file for" + filePathOrDefaultTabName);
            };

            // Remove from TabManagerService and mappings
            if (!TabManagerService.removeFileFromOpenTabs(tab.getText(), filePathOrDefaultTabName)) {
                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1004);
            };

        } else {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_1007);
            iConnectApp.getTabPane().getTabs().remove(tab);
        }

        // If all tabs are closed, reset the counter
        if (iConnectApp.getTabPane().getTabs().isEmpty() && iConnectApp.getOpenTabs().isEmpty()) {
            TabManagerService.resetUntitledTabCounter();

            //TODO: waiting to implement open dialog warning
            //check if changes and then save and close based on yes, cancel, blabla
            //FileHelpers.fileTabHasChanges(iConnectApp.getCodeArea().getText(),tabEntry);
            // saveFile();
            IConnectAppUtils.graceFullShutDown();
        };
    };
}
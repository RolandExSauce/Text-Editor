package com.solideditor.programm.Backend.services;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.FileUtils;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import java.util.Map;

//TODO: remove duplicate code: tabPane, tabFileMap, codeArea, etc...

//utility class to interact with controllers, will interact with <currently unknown, waiting for frontend>
public class FileIOService {

    //save file
    public static void saveFile(IConnectApp IConnectApp) {
        TabPane tabPane = IConnectApp.getTabPane();
        Map<Tab, String> tabFileMap = IConnectApp.getTabFileMap();
        CodeArea codeArea = IConnectApp.getCodeArea();

        //get the current tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            System.out.println("No tab selected to save.");
            return;
        }

        //get the file path for the selected tab
        String filePath = tabFileMap.get(selectedTab);
        if (filePath == null) {
            System.out.println("No file path associated with the selected tab.");
            return;
        }

        //retrieve fileInfo from tab manager
        FileInfo fileInfo = TabManagerService.getFileInfo(filePath);
        if (fileInfo == null) {
            System.out.println("No FileInfo found for the file path: " + filePath);
            return;
        }

        //get the current content of the CodeArea
        String updatedContent = codeArea.getText();

        //save the changes to the file
        FileUtils.saveChangesToFile(new FileInfo(filePath, updatedContent));

        //update the fileInfo in TabManagerService, so when switching tabs content is also updated
        TabManagerService.updateFileInfo(filePath, updatedContent);

        // Mark the file as saved by updating the tab indicator
        FileHelpers.updateTabIfFileSavedIndicator(selectedTab, false);

        //System.out.println("File saved: " + filePath);
    };
    /******************************************************************************************************************/
    // Open a new file and create a tab
    public static void openFile(IConnectApp IConnectApp) {
        TabPane tabPane = IConnectApp.getTabPane();
        Map<Tab, String> tabFileMap = IConnectApp.getTabFileMap();
        CodeArea codeArea = IConnectApp.getCodeArea();
        Stage primaryStage = IConnectApp.getPrimaryStage();

        FileInfo fileInfo = FileUtils.openFileAndGetContent(primaryStage);
        if (fileInfo != null) {
            // Check if the file is already open using TabManagerService
            Tab existingTab = TabManagerService.getOpenTab(fileInfo.fileName(), fileInfo.filePath());
            if (existingTab != null) {
                //System.out.println("Tab already open for file: " + fileInfo.filePath());
                // Ensure the existing tab is visible in the TabPane
                if (!tabPane.getTabs().contains(existingTab)) {
                    tabPane.getTabs().add(existingTab);
                }
                tabPane.getSelectionModel().select(existingTab);
                tabPane.requestFocus();
                return;
            }

            // If not open, create a new tab and add it using TabManagerService
            Tab newTab = FileHelpers.createTabWithIfFileSavedIndicator(fileInfo.fileName());
            Tab addedTab = TabManagerService.addFileToOpenTabs(fileInfo, newTab);

            if (addedTab != null) {
                // Set up tab behavior and add to TabPane
                addedTab.setOnClosed(e -> closeFile(IConnectApp, addedTab));
                tabPane.getTabs().add(addedTab);
                tabPane.getSelectionModel().select(addedTab);

                // Map the tab to its file path for quick lookups
                tabFileMap.put(addedTab, fileInfo.filePath());

                // Set the content of the CodeArea
                codeArea.clear();
                codeArea.replaceText(fileInfo.fileContent());
            } else {
                System.out.println("Error: Could not add new tab for file: " + fileInfo.filePath());
            }
        }
    };
    /******************************************************************************************************************/
    //Create a new file or open an existing one and create a tab
    public static void createOrOpenFile(IConnectApp IConnectApp) {
        TabPane tabPane = IConnectApp.getTabPane();
        Map<Tab, String> tabFileMap = IConnectApp.getTabFileMap();
        CodeArea codeArea = IConnectApp.getCodeArea();
        Stage primaryStage = IConnectApp.getPrimaryStage();
        FileInfo fileInfo = FileUtils.createOrOpenFileAndGetContent(primaryStage);
        if (fileInfo != null) {
            Tab existingTab = TabManagerService.getOpenTab(fileInfo.fileName(), fileInfo.filePath());
            if (existingTab != null) {
                // Re-add and select existing tab if not already in TabPane
                if (!tabPane.getTabs().contains(existingTab)) {
                    tabPane.getTabs().add(existingTab);
                }
                tabPane.getSelectionModel().select(existingTab);
                tabPane.requestFocus();
            } else {
                // Create a new tab
                Tab newTab = new Tab(fileInfo.fileName());
                newTab.setOnClosed(e -> closeFile(IConnectApp, newTab));
                tabPane.getTabs().add(newTab);
                tabPane.getSelectionModel().select(newTab);
                tabFileMap.put(newTab, fileInfo.filePath());

                // Update CodeArea content
                codeArea.clear();
                codeArea.replaceText(fileInfo.fileContent());
            }
        }

    };
    /******************************************************************************************************************/
    // Close a file and its tab
    private static void closeFile(IConnectApp IConnectApp, Tab tab) {
        TabPane tabPane = IConnectApp.getTabPane();
        Map<Tab, String> tabFileMap = IConnectApp.getTabFileMap();

        if (tab == null || tabPane == null || tabFileMap == null) {
            //System.out.println("Error: Invalid state. Cannot close file.");
            return;
        };

        // Get the associated file path
        String filePath = tabFileMap.get(tab);

        if (filePath != null) {
            // Attempt to close the file resource
            boolean resourceClosed = FileUtils.closeFileRessource(filePath);
            if (!resourceClosed) {
                System.out.println("Warning: Could not close file resource for " + filePath);
            }

            // Remove the tab from TabManagerService and mappings
            String fileName = tab.getText();
            boolean removedFromManager = TabManagerService.removeFileFromOpenTabs(fileName, filePath);

            //also remove from tab pane
            if (removedFromManager) {
                tabPane.getTabs().remove(tab);
                tabFileMap.remove(tab);
                //System.out.println("Tab closed: " + fileName);
            } else {
                System.out.println("Error: Failed to remove tab from TabManagerService.");
            }
        } else {
            //System.out.println("Error: No file path associated with the selected tab. Performing cleanup.");
            tabPane.getTabs().remove(tab);
        }
    };
}
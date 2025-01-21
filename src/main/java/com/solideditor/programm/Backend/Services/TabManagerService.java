package com.solideditor.programm.Backend.Services;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Map;


//utility class for handling tab opening/closure, will interact with FileService class
public class TabManagerService {
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); // get singleton instance
    private static int untitledTabCounter = 1; // counter to track default tabs creation
    /******************************************************************************************************************/
        public static void saveUnsavedContent(String filePathOrTabName, String content) {
            for (Map.Entry<String, TabEntry> entry : iConnectApp.getOpenTabs().entrySet()) {
                FileInfo fileInfo = entry.getValue().fileInfo();
                // Match either the file path (for saved files) or the tab name (for unsaved tabs)
                if ((fileInfo.filePathOrTempTabName() != null &&
                        fileInfo.filePathOrTempTabName().equals(filePathOrTabName))
                        || (fileInfo.filePathOrTempTabName() == null && entry.getKey().equals(filePathOrTabName))) {
                    Tab tab = entry.getValue().tab();
                    iConnectApp.getOpenTabs().put(entry.getKey(), new TabEntry(tab, fileInfo, content));
                    return;
                };
            };
        };
    /******************************************************************************************************************/
    //Get unsaved content for a file path
    public static String getUnsavedContentForSavedFiles(String filePath) {
        for (TabEntry entry : iConnectApp.getOpenTabs().values()) {
            if (entry.fileInfo().filePathOrTempTabName().equals(filePath)) {
                return entry.unsavedContent();
            }
        }
        return null; // No unsaved content
    };
    /******************************************************************************************************************/
    //open new tab for a file getting "opened"
    public static Tab openFileAndAddToOpenTabs(FileInfo fileInfo, Tab tab) {
        String fileKey = genFileKey(fileInfo.fileName(), fileInfo.filePathOrTempTabName());
        // Add the file to openTabs if the key is not already present
        if (!iConnectApp.getOpenTabs().containsKey(fileKey)) {
            iConnectApp.getOpenTabs().put(fileKey, new TabEntry(tab, fileInfo));
            return tab; // Return the added tab
        } else {
            return null; // return null if the tab was not added
        }
    };
    /******************************************************************************************************************/
    // Create a new tab with a graphic indicator or adjust a default one, e.g. default untitled tab
    public static Tab createTabWithIfFileSavedIndicator(Object tabNameOrTab) {
        Circle indicator = new Circle(4); // radius of 4
        indicator.setFill(Color.TRANSPARENT); // default is transparent

        if(tabNameOrTab instanceof Tab existingTabPassed){
            existingTabPassed.setGraphic(indicator); //add circle to tab's graphic
            return existingTabPassed;
        };
        //assume it's the tabName so create a new tab
        if(tabNameOrTab instanceof String tabNamePassed) {
            Tab newTab = new Tab(tabNamePassed);
            newTab.setGraphic(indicator);
            return newTab;
        };
        return null;
    };
    /******************************************************************************************************************/
    //add a new tab without path to create new file, then if save clicked => "save under"
    public static Tab createUnsavedTabAndAddToOpenTabs() {
        Tab newUnsavedTab;
        String tabName;
        String filePathOrTempTabName;

        // Generate a unique tab name and key
        do {
            tabName = "Untitled Tab " + untitledTabCounter++;
            filePathOrTempTabName = genFileKey(tabName, null);
        } while (iConnectApp.getOpenTabs().containsKey(filePathOrTempTabName)); // ensure the key is unique

        // Create FileInfo and Tab for the new unsaved file
        FileInfo unsavedFileInfo = new FileInfo(filePathOrTempTabName, null, tabName, null);
        newUnsavedTab = createTabWithIfFileSavedIndicator(tabName);

        //IMPORTANT: THIS ONLY WORKS BECAUSE SAVING IS UPDATING ACCORDINGLY
        // Add the new tab to the openTabs map
        iConnectApp.getOpenTabs().put(filePathOrTempTabName, new TabEntry(newUnsavedTab, unsavedFileInfo));

        return newUnsavedTab;
    };
    /******************************************************************************************************************/
    // remove a tab
    public static boolean removeFileFromOpenTabs(String fileName, String filePath) {
        String uniqueKey = genFileKey(fileName, filePath);

        // Check if the tab exists in openTabs
        if (iConnectApp.getOpenTabs().containsKey(uniqueKey)) {
            // Remove the tab from openTabs
            iConnectApp.getOpenTabs().remove(uniqueKey);

            // Remove the corresponding tab from the TabPane
            iConnectApp.getTabPane().getTabs().removeIf(tab -> tab.getText().equals(fileName));

            // Optional: Adjust the counter to the lowest available number
            adjustUntitledTabCounter();
            return true;
        } else return false;
    };

    /******************************************************************************************************************/
    private static void adjustUntitledTabCounter() {
        int highestUsed = 1; // Start at 1 for "Untitled Tab 1"
        for (TabEntry entry : iConnectApp.getOpenTabs().values()) {
            String tabName = entry.tab().getText();
            if (tabName.startsWith("Untitled Tab ")) {
                try {
                    int number = Integer.parseInt(tabName.substring(13));
                    if (number > highestUsed) {
                        highestUsed = number;
                    }
                } catch (NumberFormatException ignored) {
                    //Ignore non-numeric titles
                }
            }
        };
        untitledTabCounter = highestUsed + 1;
    };
    /******************************************************************************************************************/
    public static void resetUntitledTabCounter() {
        untitledTabCounter = 1; // Reset to the initial value
    };
    /******************************************************************************************************************/
    // Get the Tab associated with an open file
    public static Tab getOpenTab(String fileName, String filePath) {
        String uniqueKey = genFileKey(fileName, filePath);
        TabEntry entry = iConnectApp.getOpenTabs().get(uniqueKey); // retrieve the TabEntry
        //System.out.println("Tab found: " + (entry != null ? entry.tab().getText() : "null"));
        return entry != null ? entry.tab() : null; // return the Tab if it exists
    };
    /******************************************************************************************************************/
    // generate a unique key by combining filePath and fileName
    public static String genFileKey(String fileName, String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            // Check if filePath is already in the format "unsaved::<fileName>" or "<path>::<fileName>"
            if (filePath.contains("::")) {
                return filePath; // return as-is if it's already formatted
            };
            return filePath + "::" + fileName; // Append fileName if not already included
        } else if (fileName.startsWith(iConnectApp.getPrefKeyForUnsavedFile())) {
            // Avoid duplicating the unsaved prefix
            return fileName;
        } else {
            return iConnectApp.getPrefKeyForUnsavedFile() + fileName;
        }
    };
    /******************************************************************************************************************/
    // Update the FileInfo for a specific file path
    public static void updateFileInfo(String filePath, String newContent) {
        for ( Map.Entry<String, TabEntry> entry : iConnectApp.getOpenTabs().entrySet()) {
            if (entry.getValue().fileInfo().filePathOrTempTabName().equals(filePath)) {

                Tab tab = entry.getValue().tab();

                // Update the FileInfo with the new content
                FileInfo updatedFileInfo = new FileInfo(filePath, newContent);

                // Clear unsaved content when updating FileInfo (if necessary)
                iConnectApp.getOpenTabs().put(entry.getKey(), new TabEntry(tab, updatedFileInfo, null));

                System.out.println("FileInfo updated and unsaved content cleared for file path: " + filePath);
                return;
            }
        };
    };
    /******************************************************************************************************************/
    //Finds the TabEntry associated with the given Tab
    public static TabEntry getTabEntryByTab(Tab tab) {
        if (tab == null) return null;
        for (Map.Entry<String, TabEntry> entry : iConnectApp.getOpenTabs().entrySet()) {
            if (entry.getValue().tab().equals(tab)) return entry.getValue();
        };
        return null; // no tab entry found
    };

    // Helper method to check if FileInfo exists in openTabs
    public static FileInfo findFileInfoInOpenTabs(String filePath) {
        for (TabEntry tabEntry : iConnectApp.getOpenTabs().values()) {
            FileInfo fileInfo = tabEntry.fileInfo();
            if (fileInfo.filePathOrTempTabName().equals(filePath)) {
                return fileInfo;
            }
        }
        return null;
    };
}


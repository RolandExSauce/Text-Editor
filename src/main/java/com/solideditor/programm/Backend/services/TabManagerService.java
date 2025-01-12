package com.solideditor.programm.Backend.services;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.HashMap;
import java.util.Map;


//utility class for handling tab opening/closure, will interact with FileService class
public class TabManagerService {

    private static int untitledTabCounter = 2; // Start from 2 since "Untitled Tab 1" is pre-created
    private static final int MAX_TABS = 5; //max number of opened tab files

    // stores file key and filename, file key is created with filePath+fileName
    private static final Map<String, TabEntry> openTabs = new HashMap<>();

    /******************************************************************************************************************/
    // Add or update unsaved content
//    public static void saveUnsavedContent(String filePathOrTabName, String content) {
//        System.out.println("got file path/name: "+filePathOrTabName);
//
//        for (Map.Entry<String, TabEntry> entry : openTabs.entrySet()) {
//            System.out.println("yes, save please"+entry.getValue().fileInfo());
//
//            if (entry.getValue().fileInfo().filePath().equals(filePathOrTabName)) {
//
//                System.out.println("yes coming until here");
//
//                Tab tab = entry.getValue().tab();
//                FileInfo fileInfo = entry.getValue().fileInfo();
//                openTabs.put(entry.getKey(), new TabEntry(tab, fileInfo, content));
//                return;
//            }
//        }
//    };

    /******************************************************************************************************************/
        public static void saveUnsavedContent(String filePathOrTabName, String content) {
            for (Map.Entry<String, TabEntry> entry : openTabs.entrySet()) {
                FileInfo fileInfo = entry.getValue().fileInfo();
                // Match either the file path (for saved files) or the tab name (for unsaved tabs)
                if ((fileInfo.filePath() != null && fileInfo.filePath().equals(filePathOrTabName))
                        || (fileInfo.filePath() == null && entry.getKey().equals(filePathOrTabName))) {
                    Tab tab = entry.getValue().tab();
                    openTabs.put(entry.getKey(), new TabEntry(tab, fileInfo, content));
                    return;
                }
            }
        };
    /******************************************************************************************************************/
//     Get unsaved content for a file path
    public static String getUnsavedContentForSavedFiles(String filePath) {
        for (TabEntry entry : openTabs.values()) {
            if (entry.fileInfo().filePath().equals(filePath)) {
                return entry.unsavedContent();
            }
        }
        return null; // No unsaved content
    };
    //handle both scenarios: get unsaved content for a tab which is saved/or not saved yet (in case of new file)
    public static String getUnsavedContentForUnsavedFiles(String keyIsTabNameOrFilePath) {
        for (Map.Entry<String, TabEntry> entry : openTabs.entrySet()) {
            String fileKey = entry.getKey();
            // Check if the key matches either the file path or the tab name
            if (fileKey.equals(keyIsTabNameOrFilePath)) {
                return entry.getValue().unsavedContent();
            } else if (entry.getValue().fileInfo().filePath() != null
                    && entry.getValue().fileInfo().filePath().equals(keyIsTabNameOrFilePath)) {
                return entry.getValue().unsavedContent();
            }
        }
        return null; // No unsaved content
    };
    /******************************************************************************************************************/
    // add a new tab with path
    public static Tab fileMaybeHasPathAddToOpenTabs(FileInfo fileInfo, Tab tab) {

        String fileKey = genFileKey(fileInfo.fileName(), fileInfo.filePath());
        if (openTabs.size() >= MAX_TABS) {
            System.out.println("Error: maximum number of tabs reached."); // TODO: Prompt an error dialog message
            return null; // Cannot add more tabs
        }

        // Add the file to openTabs if the key is not already present
        if (!openTabs.containsKey(fileKey)) {
            openTabs.put(fileKey, new TabEntry(tab, fileInfo));
            return tab; // Return the added tab
        } else {
            return null; // Return null if the tab was not added
        }
    };
    /******************************************************************************************************************/
    // Create a new tab with a graphic indicator
    public static Tab createTabWithIfFileSavedIndicator(String tabTitle) {
        Tab tab = new Tab(tabTitle);

        // Create a circle to act as the save state indicator
        Circle indicator = new Circle(5); // Radius of 5
        indicator.setFill(Color.TRANSPARENT); // Initially transparent

        // Set the circle as the tab's graphic
        tab.setGraphic(indicator);

        return tab;
    };
    /******************************************************************************************************************/
    //add a new tab without path, then if on clicked, will check and "save under"
    public static Tab createUnsavedTabAndAddToOpenTabs() {
        // Generate the name for the new unsaved tab
        String tabName = "Untitled Tab " + untitledTabCounter++;

        // Null values for unsaved file, tabName will be used as potential fileName
        FileInfo unsavedFileInfo = new FileInfo(null, null, tabName, null);
        Tab newUnsavedTab = new Tab(tabName); // Create a new Tab with the generated name


        // Set a graphic (e.g., a circle) to indicate save state
        Circle indicator = new Circle(5); // Radius of 5
        indicator.setFill(Color.TRANSPARENT); // Initially transparent
        newUnsavedTab.setGraphic(indicator); // Set the graphic for the tab

        if (fileMaybeHasPathAddToOpenTabs(unsavedFileInfo, newUnsavedTab) == null) {
            // If unsaved tab cannot be added, debug here
            System.out.println("Error: Could not add the new unsaved tab.");
            return null; // Return null if the tab couldn't be added
        }
        return newUnsavedTab;
    };
    /******************************************************************************************************************/
    // remove a tab
    public static boolean removeFileFromOpenTabs(String fileName, String filePath) {
        String uniqueKey = genFileKey(fileName, filePath);
        if (openTabs.containsKey(uniqueKey)) {
            openTabs.remove(uniqueKey);
            // Optional: Adjust the counter to the lowest available number
            adjustUntitledTabCounter();

            return true;
        } else {
            return false; // tab not found
        }
    };
    /******************************************************************************************************************/
    private static void adjustUntitledTabCounter() {
        int highestUsed = 1; // Start at 1 for "Untitled Tab 1"
        for (TabEntry entry : openTabs.values()) {
            String tabName = entry.tab().getText();
            if (tabName.startsWith("Untitled Tab ")) {
                try {
                    int number = Integer.parseInt(tabName.substring(13));
                    if (number > highestUsed) {
                        highestUsed = number;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric titles
                }
            }
        }
        untitledTabCounter = highestUsed + 1;
    };
    /******************************************************************************************************************/
    // Get the Tab associated with an open file
    public static Tab getOpenTab(String fileName, String filePath) {
        String uniqueKey = genFileKey(fileName, filePath);

        System.out.println("unique key: "+ uniqueKey);

        TabEntry entry = openTabs.get(uniqueKey); // Retrieve the TabEntry
        System.out.println("Tab found: " + (entry != null ? entry.tab().getText() : "null"));
        return entry != null ? entry.tab() : null; // Return the Tab if it exists
    };
    /******************************************************************************************************************/
    // generate a unique key by combining filePath and fileName
   // filePath could be null in case of new file creation with the file not saved yet
    private static String genFileKey(String fileName, String filePath) {
        return (filePath != null ? filePath : "unsaved") + "::" + fileName;
    }
    /******************************************************************************************************************/
    // Get the FileInfo associated with a file path
    public static FileInfo getFileInfo(String filePath) {
        for (TabEntry entry : openTabs.values()) {
            if (entry.fileInfo().filePath().equals(filePath)) {
                return entry.fileInfo();
            }
        }
        return null;
    };
    /******************************************************************************************************************/
    // Update the FileInfo for a specific file path
    public static void updateFileInfo(String filePath, String newContent) {
        for (Map.Entry<String, TabEntry> entry : openTabs.entrySet()) {
            if (entry.getValue().fileInfo().filePath().equals(filePath)) {
                Tab tab = entry.getValue().tab();

                // Update the FileInfo with the new content
                FileInfo updatedFileInfo = new FileInfo(filePath, newContent);

                // Clear unsaved content when updating FileInfo (if necessary)
                openTabs.put(entry.getKey(), new TabEntry(tab, updatedFileInfo, null));

                System.out.println("FileInfo updated and unsaved content cleared for file path: " + filePath);
                return;
            }
        }
        //System.out.println("Error: No FileInfo found for file path: " + filePath);
    };

}


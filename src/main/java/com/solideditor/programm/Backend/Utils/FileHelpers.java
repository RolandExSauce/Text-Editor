package com.solideditor.programm.Backend.Utils;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.Services.TabManagerService;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javafx.stage.FileChooser;
import java.nio.file.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


//helper class for FileUtils class
public class FileHelpers {

    // Preferences to save and load the last opened directory
    private static final Preferences prefs = Preferences.userNodeForPackage(FileHelpers.class);
    public static final String LAST_OPENED_DIR_KEY = "lastOpenedDir";
    /******************************************************************************************************************/
    //save last opened directory
    public static void saveLastOpenedDir(String path) {
        prefs.put(LAST_OPENED_DIR_KEY, path);
    }
    /******************************************************************************************************************/
    // after modifying get the file chooser
    @NotNull
    public static FileChooser getFileChooser(String fileChooserTitle) {
        String lastDirPath = prefs.get(LAST_OPENED_DIR_KEY, null); // Load last opened directory
        FileChooser fileChooser = modChooserFirst(fileChooserTitle, lastDirPath);

        //add extension filters
        fileChooser.getExtensionFilters().clear(); //clear previous filters to avoid duplicates
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
        );
        return fileChooser;
    };
    /******************************************************************************************************************/
    //update chooser with last opened dir first
    @NotNull
    private static FileChooser modChooserFirst(String fileChooserTitle, String lastDirPath) {
        File lastOpenedDirectory = null;

        if (lastDirPath != null) {
            File directory = new File(lastDirPath);
            if (directory.exists()) {
                lastOpenedDirectory = directory;
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserTitle);
        // Set the initial directory to the last opened directory
        if (lastOpenedDirectory != null && lastOpenedDirectory.exists()) {
            fileChooser.setInitialDirectory(lastOpenedDirectory);
        }
        return fileChooser;
    };
    /******************************************************************************************************************/
    // overload helper methods for getting file extension name
    public static String getFileTypeByExtension(Object input) {
        String fileName;
        if (input instanceof File) {
            fileName = ((File) input).getName();
        } else if (input instanceof String) {
            fileName = (String) input;
        } else {
            throw new IllegalArgumentException("Input must be either File or String");
        }
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex == -1) ? "Unknown" : fileName.substring(lastIndex + 1);
    };
    /******************************************************************************************************************/
    //get file name from a given path
    public static String getFileNameFromPath(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    };
    /******************************************************************************************************************/
    // helper methods to provide FileInfo from existing file or return existing one from a new File record
    //used for opening files
    public static <T> FileInfo provideExistingFileInfoOrCreate(T input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        };
        if (input instanceof String filePath) {
            if (filePath.isBlank()) {
                throw new IllegalArgumentException("File path can't be blank");
            };
            // Check if FileInfo already exists in openTabs
            FileInfo existingFileInfo = TabManagerService.findFileInfoInOpenTabs(filePath);
            return existingFileInfo != null ? existingFileInfo : new FileInfo(filePath);

        } else if (input instanceof File file) {
            if (!file.exists()) {
                throw new IllegalArgumentException("File must exist");
            };
            String absolutePath = file.getAbsolutePath();

            // Check if FileInfo already exists in openTabs
            FileInfo existingFileInfo = TabManagerService.findFileInfoInOpenTabs(absolutePath);
            return existingFileInfo != null ? existingFileInfo : new FileInfo(absolutePath);
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getName());
        }
    };

    //used to create a new file with codeArea content => option 1. user selects existing file, then that file's content
    // is overridden or Option 2: user creates new file and the codeArea content  is added as file content to that new file
    public static <T> FileInfo provideExistingFileInfoOrCreate(T input, String content) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (input instanceof String filePath) {
            if (filePath.isBlank()) {
                throw new IllegalArgumentException("File path can't be blank");
            };

            // Check if FileInfo already exists in openTabs
            FileInfo existingFileInfo = TabManagerService.findFileInfoInOpenTabs(filePath);
            return existingFileInfo != null ? existingFileInfo : new FileInfo(filePath, content);

        } else if (input instanceof File file) {
            if (!file.exists()) {
                throw new IllegalArgumentException("File must exist");
            };

            String absolutePath = file.getAbsolutePath();
            // Check if FileInfo already exists in openTabs
            FileInfo existingFileInfo = TabManagerService.findFileInfoInOpenTabs(absolutePath);
            return existingFileInfo != null ? existingFileInfo : new FileInfo(absolutePath, content);

        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getName());
        }
    };
    /******************************************************************************************************************/
    //read content from a file and return it
    public static String readFileContent(String filePath) {
        StringBuilder fileData = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Replace invalid characters with a space or remove them entirely
                line = line.replaceAll("\\p{C}", " "); // Replace control characters with space
                fileData.append(line).append("\n");
            }
        } catch (MalformedInputException e) {
            System.err.println("Malformed input: The file might not be UTF-8 encoded. File path: " + filePath);
            return ""; // Return empty content for malformed input
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return fileData.toString();
    };
    /******************************************************************************************************************/
    public static boolean fileTabHasChanges(String newValue, TabEntry tabEntry) {
        FileInfo fileInfo = tabEntry.fileInfo();

        //do this cuz unsavedFileInfo is created with content "null" but if codeArea is
        //empty it equals a string "" that way graphic indicator is also updated for unsaved files correctly
        String fileContent =
                fileInfo.fileContent() == null ? "": fileInfo.fileContent();

        // update the tab's indicator based on changes
        return !newValue.equals(fileContent);
    };
    /******************************************************************************************************************/
    // method to update the tab indicator based on the save state
    public static void updateTabIfFileSavedIndicator(Tab tab, boolean isModified) {
        if (tab == null || tab.getGraphic() == null) {
            System.err.println("Tab or Tab graphic is null! Cannot update indicator.");
            return;
        }

        if (tab.getGraphic() instanceof Circle indicator) {
            indicator.setFill(isModified ? Color.DARKGRAY : Color.TRANSPARENT);
        } else {
            System.err.println("Tab graphic is not a Circle. Cannot update indicator.");
        }
    };
    /******************************************************************************************************************/
    //find index positions of a search to text in a file
    public static List<Integer> findSubstringPositions(String content, String searchText) {
        List<Integer> positions = new ArrayList<>();
        if (content == null || searchText == null || searchText.isEmpty()) {
            return positions; // Return empty if content or searchText is invalid
        }
        String lowerContent = content.toLowerCase();
        String lowerSearch = searchText.toLowerCase();
        int index = lowerContent.indexOf(lowerSearch);

        while (index != -1) {
            positions.add(index);
            index = lowerContent.indexOf(lowerSearch, index + 1); // Find next occurrence
        }
        return positions;
    };
    /******************************************************************************************************************/
    //search for a word in file and return the position, will return the row and all indexes from that word within the row
    public static Map<Integer, List<Integer>> findWordInFileAndReturnPos(String content, String searchText) {
        Map<Integer, List<Integer>> rowPositions = new HashMap<>();
        if (content == null || searchText == null || searchText.isEmpty()) {
            return rowPositions; // Return empty if content or searchText is invalid
        };
        String[] lines = content.split("\n"); // Split content into lines
        for (int row = 0; row < lines.length; row++) {
            List<Integer> positions = findSubstringPositions(lines[row], searchText); // Use the existing method
            if (!positions.isEmpty()) {
                rowPositions.put(row + 1, positions); // Store row (1-based) and column indexes
            }
        };
        return rowPositions;
    };
}

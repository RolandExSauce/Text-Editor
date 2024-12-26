package com.example.programm.backend.Utils;
import com.example.programm.backend.DTO.FileInfo;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.prefs.Preferences;
import javafx.stage.FileChooser;
import java.nio.file.*;
import java.io.*;


//helper class for FileUtils class
public class FileHelpers {
    // Preferences to save and load the last opened directory
    private static final Preferences prefs = Preferences.userNodeForPackage(FileHelpers.class);
    public static final String LAST_OPENED_DIR_KEY = "lastOpenedDir";
    /********************************************************************************************************************/
    //save last opened directory
    public static void saveLastOpenedDir(String path) {
        prefs.put(LAST_OPENED_DIR_KEY, path);
    }
    /********************************************************************************************************************/
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
    /********************************************************************************************************************/
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
    /********************************************************************************************************************/
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
    /********************************************************************************************************************/
    //get file name from a given path
    public static String getFileNameFromPath(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    };
    /********************************************************************************************************************/
    // helper method to create FileInfo from a File record
    public static FileInfo createFileInfo(Object input) {
        if (input instanceof String) {
            return new FileInfo((String) input);
        } else if (input instanceof File) {
            return new FileInfo(((File) input).getAbsolutePath());
        } else {
            throw new IllegalArgumentException("Input must be either String or File");
        }
    };
    /********************************************************************************************************************/
    //read content from a file and return it
    public static String readFileContent(String filePath) {
        StringBuilder fileData = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileData.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return fileData.toString();
    };
    /********************************************************************************************************************/
    // check if file content has changed
    public static boolean trackIfChangesMade(Path filePath, String newContent) {
        try {
            // get current content or empty if file does not exist
            String currentContent = Files.exists(filePath) ? Files.readString(filePath) : "";
            System.out.println("Changes made: " + !currentContent.equals(newContent));
            return !currentContent.equals(newContent); // return true if content different
        } catch (IOException e) {
            // debug err
            // e.printStackTrace();
            return false;
        }
    };
}

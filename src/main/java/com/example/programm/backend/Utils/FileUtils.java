package com.example.programm.backend.Utils;
import com.example.programm.backend.DTO.FileInfo;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;


//utility class to handle all file related operations, will interact with FileService class
public class FileUtils {
    // create file method
    public static FileInfo createOrOpenFileAndGetContent(Stage mainStage) {
        FileChooser fileChooser = FileHelpers.getFileChooser("Create/Replace File");
        File file = fileChooser.showSaveDialog(mainStage);

        // check if file selected
        if (file != null) {
            FileHelpers.saveLastOpenedDir(file.getParentFile().getAbsolutePath());
            try {
                // check if the file already exists
                if (file.exists()) return FileHelpers.createFileInfo(file);
                else {
                    if (file.createNewFile()) return FileHelpers.createFileInfo(file);
                    else return null; // file creation failed
                }
            } catch (IOException e) {
                System.err.println("error: " + e.getMessage());
                return null; // error during file creation
            }
        } else return null; // return null on cancel
    };
    //I've modified "createOrOpenFileAndGetContent" so that if we select an existing file
    //instead of creating a new one it simply opens that file instead of a "creation failing error" =>
    //in this sense it does actually get the job done for "openFileAndGetContent" as well, but
    //in explorer we get a message "file blabla already exists, do you want to replace it" ? so we don't want that on
    //a select/open file operation actually
/********************************************************************************************************************/
    // open a file and return its content
    public static FileInfo openFileAndGetContent(Stage mainStage) {
        FileChooser fileChooser = FileHelpers.getFileChooser("Open File");
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            FileHelpers.saveLastOpenedDir(file.getParentFile().getAbsolutePath());
            //String content = FileHelpers.readFileContent(file.getPath());
            return FileHelpers.createFileInfo(file);
        } else return null; //on cancel
    };
    /********************************************************************************************************************/
    // save changes to file
    public static void saveChangesToFile(FileInfo fileInfo) {
        // Validate input
        if (fileInfo == null) {
            throw new IllegalArgumentException("FileInfo cannot be null");
        }
        if (fileInfo.filePath() == null || fileInfo.filePath().isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }
        Path filePath = Path.of(fileInfo.filePath());
        // Check if the file exists
        if (!Files.exists(filePath)) {
            System.out.println("File does not exist"); //TODO: Prompt dialog
        };
        // Check if there's content to save
        if (fileInfo.fileContent() == null) {
            //System.out.println("No changes detected. File content is null.");
            return; // Nothing to save
        };
        //overwrite/write new content to file
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(fileInfo.fileContent());
            writer.newLine(); // Add a newline if needed
        } catch (IOException e) {
            System.out.println("error: "+e.getMessage());
        }
    };
    /********************************************************************************************************************/
    // close file resource, much better approach then keeping track of
    //opened files by putting lang lasting FileReader as the value => did this, got warnings
    //to use with try with ressource but, it's too sophisticated
    public static void closeFileRessource(String filePath) {
        // Use try-with-resources to open and immediately close the file
        try (FileReader fileReader = new FileReader(filePath)) {
            System.out.println("File closed successfully: " + filePath);
        } catch (IOException e) {
            System.out.println("Error while closing file: " + e.getMessage()); //for debug
        }
    };
}

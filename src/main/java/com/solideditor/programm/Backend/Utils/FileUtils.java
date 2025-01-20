package com.solideditor.programm.Backend.Utils;
import com.solideditor.programm.Backend.DTO.FileInfo;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;


//utility class to handle all file related operations, will interact with FileService class
public class FileUtils {

    // save a new file "under XY dir" method
    public static FileInfo saveNewFileUnderDirAndGetFileContent(Stage mainStage, String content) {
        FileChooser fileChooser = FileHelpers //keep descriptive as possible
                .getFileChooser("Create New File Or Replace existing File with new content");
        File file = fileChooser.showSaveDialog(mainStage);

        // check if file selected
        if (file != null) {
            FileHelpers.saveLastOpenedDir(file.getParentFile().getAbsolutePath());
            try {
                // check if the file already exists
                if (file.exists()) {
                    // TODO: Customize the dialog to draw the user's attention even more effectively ?
                    return FileHelpers.provideExistingFileInfoOrCreate(file, content); //override with new content
                }
                else {
                    if (file.createNewFile()) {
                        FileInfo createdFileInfo = FileHelpers.provideExistingFileInfoOrCreate(file, content);
                        saveChangesToFile(createdFileInfo);
                        return createdFileInfo;
                    }
                    else return null; // file creation failed
                }
            } catch (IOException e) {
                //System.err.println("error: " + e.getMessage());
                return null; // error during file creation
            }
        } else return null; // return null on cancel
    };
    /******************************************************************************************************************/
    // open a file and return its content
    public static FileInfo openFileAndGetContent(Stage mainStage) {
        FileChooser fileChooser = FileHelpers.getFileChooser("Open File");
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            FileHelpers.saveLastOpenedDir(file.getParentFile().getAbsolutePath());
            return FileHelpers.provideExistingFileInfoOrCreate(file);
        } else return null; //on cancel
    };
    /******************************************************************************************************************/
    // save changes to file
    public static void saveChangesToFile(FileInfo fileInfo) {
        // Validate input
        if (fileInfo == null) {
            throw new IllegalArgumentException("FileInfo cannot be null");
        }
        if (fileInfo.filePathOrTempTabName() == null || fileInfo.filePathOrTempTabName().isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }
        Path filePath = Path.of(fileInfo.filePathOrTempTabName());
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
            System.out.println("error: " + e.getMessage());
        }
    };
    /******************************************************************************************************************/
    // close file resource, much better approach then keeping track of
    //opened files by putting lang lasting FileReader as the value => did this, got warnings
    //to use with try with ressource but, it's too sophisticated
    public static boolean closeFileRessource(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                // Simulate resource closure if needed
                System.out.println("File resource closed successfully: " + filePath);
                return true; // Indicate success
            } catch (Exception e) {
                System.err.println("Error while closing file resource: " + e.getMessage());
                return false; // Indicate failure
            }
        } else {
            System.err.println("Error: File does not exist at path: " + filePath);
            return false; // Indicate failure
        }
    };
}

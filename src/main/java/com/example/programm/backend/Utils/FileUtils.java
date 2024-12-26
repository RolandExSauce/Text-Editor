package com.example.programm.backend.Utils;
import com.example.programm.backend.DTO.FileInfo;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
                if (file.exists()) {
                    //System.out.println("file already exists. Opening the existing file.");
                    String content = FileHelpers.readFileContent(file.getPath());
                    // TODO: return FileInfo object with existing file content or rather delete content ?
                    return FileHelpers.createFileInfo(file);
                } else {
                    // try creating a new file
                    if (file.createNewFile()) {
                        return FileHelpers.createFileInfo(file);
                    } else {
                        System.err.println("error: file creation failed.");
                        return null; // file creation failed
                    }
                }
            } catch (IOException e) {
                System.err.println("error: " + e.getMessage());
                return null; // error during file creation
            }
        } else {
            System.out.println("operation cancelled by user.");
            return null; // return null on cancel
        }
    };
/********************************************************************************************************************/
    //we've modified "createOrOpenFileAndGetContent" so that if we select an existing file
    //instead of creating a new one it simply opens that file instead of a "creation failing error" =>
    //in this sense it does actually get the job done for "openFileAndGetContent" as well, but
    //in explorer we get a message "file blabla already exists, do you want to replace it" ? so we don't want that on
    //a select/open file operation actually

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
    public static FileInfo saveChangesToFile(FileInfo fileInfo) {
        try {
            // get file path
            Path filePath = Path.of(fileInfo.filePath());

            // check if file exists
            if (!Files.exists(filePath)) {
                System.err.println("error: file does not exist: " + filePath);
                return null; // file not found, return null
            }

            // if fileContent is null, treat as no changes made
            if (fileInfo.fileContent() == null) {
                //System.out.println("no changes detected. file content is null.");
                return fileInfo; // no changes, return the same FileInfo
            }

            // check if changes made
            if (FileHelpers.trackIfChangesMade(filePath, fileInfo.fileContent())) {
                // write or append content to file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileInfo.filePath(), true))) {
                    bw.write(fileInfo.fileContent());
                    bw.newLine(); // add a newline after appending content
                }
                //System.out.println("changes saved to file: " + fileInfo.filePath());
            } else {
                System.out.println("no changes detected. file not updated.");
            }
            return fileInfo;
        } catch (IOException e) {
            // debug err
            System.err.println("Error saving file: " + e.getMessage());
            return null; // an error occurred, return null
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

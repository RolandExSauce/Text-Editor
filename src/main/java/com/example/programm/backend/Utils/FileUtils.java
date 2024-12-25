package com.example.programm.backend.Utils;
import java.nio.charset.StandardCharsets;

import com.example.programm.backend.DTO.FileInfo;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.nio.file.*;
import java.io.*;

//all methods related to handle file operations
public class FileUtils {

    // create file method
    public static boolean createNewFile(Stage mainStage) {
        FileChooser fileChooser = FileHelpers.getFileChooser("Create new File");
        // show save file dialog
        File file = fileChooser.showSaveDialog(mainStage);

        // check if file selected
        if (file != null) {
            // update and save the last opened directory to preferences
            FileHelpers.saveLastOpenedDir(file.getParentFile().getAbsolutePath());

            try {
                // try creating file
                return file.createNewFile();
            } catch (IOException e) {
                // debug
                //e.printStackTrace();
                return false;
            }
        } else {
            // return false on cancel
            return false;
        }
    }

    // open a file and return its content
    public static FileInfo openFileAndGetContent(Stage mainStage) {
        FileChooser fileChooser = FileHelpers.getFileChooser("Open File");
        // show open file dialog
        File file = fileChooser.showOpenDialog(mainStage);

        if (file != null) {
            // update and save the last opened directory to preferences
            FileHelpers.saveLastOpenedDir(file.getParentFile().getAbsolutePath());
            StringBuilder fileData = new StringBuilder();

            // read file line by line
            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fileData.append(line).append("\n");
                }
            } catch (IOException e) {
                // debug
                //e.printStackTrace();
                return null;
            }

            // extract file information
            String fileName = file.getName();
            String fileType = FileHelpers.getFileExtension(file);

            String filePath = file.getAbsolutePath();

            // return all information in FileInfo object
            return new FileInfo(fileName, fileType, filePath, fileData.toString());
        } else {
            // return null on cancel
            return null;
        }
    }

    //save changes to file
    public static boolean saveChangesToFile(FileInfo fileInfo) {
        try {
            // get file path
            Path filePath = Path.of(fileInfo.filePath());

            // check if file exists
            if (!Files.exists(filePath)) {
                System.err.println("error: file does not exist: " + filePath);
                return false; // file not found, return false
            }

            // if fileContent is null, treat as no changes made
            if (fileInfo.fileContent() == null) {
                System.out.println("no changes detected. file content is null.");
                return false; // no changes, return false
            }

            // check if changes made
            if (FileHelpers.trackIfChangesMade(filePath, fileInfo.fileContent())) {
                // write or append content to file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileInfo.filePath(), true))) {
                    bw.write(fileInfo.fileContent());
                    bw.newLine(); // add a newline after appending content
                }
                System.out.println("changes saved to file: " + fileInfo.filePath());
                return true; // saving was successful
            } else {
                // content not changed
                System.out.println("no changes detected. file not updated.");
                return false; // nothing changed, saving not needed
            }
        } catch (IOException e) {
            // debug err
            //e.printStackTrace();
            return false; // an error occurred during the process
        }
    }


    //close a single file ressource, TODO: later implement with multi tabs
    public void closeFile(FileReader fileReader) {
        try {
            if (fileReader != null) {
                fileReader.close();
                System.out.println("File closed.");
            } else {
                System.out.println("File was not open.");
            }
        } catch (IOException e) {
            System.err.println("Error closing file: " + e.getMessage());
        }
    }



}

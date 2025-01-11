package com.solideditor.programm.Backend.DTO;
import com.solideditor.programm.Backend.Utils.FileHelpers;


// encapsulate file data as dto
public record FileInfo(String filePath, String fileContent, String fileName, String fileType) {

    // Overloaded constructor with default values
    public FileInfo(String filePath) {
        this(filePath,
                FileHelpers.readFileContent(filePath),
                FileHelpers.getFileNameFromPath(filePath),
                FileHelpers.getFileTypeByExtension(filePath));
    };

    public FileInfo(String filePath, String fileContent) {
        this(filePath,
                fileContent,
                FileHelpers.getFileNameFromPath(filePath),
                FileHelpers.getFileTypeByExtension(filePath));
    }
};



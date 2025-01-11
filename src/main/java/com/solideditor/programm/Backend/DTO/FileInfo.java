package com.solideditor.programm.Backend.DTO;
import com.solideditor.programm.Backend.Utils.FileHelpers;


// encapsulate file data as dto
public record FileInfo(String filePath, String fileContent, String fileName, String fileType) {

    // Overloaded constructor with default values
    public FileInfo(String filePath) {
        this(filePath,
                filePath != null ? FileHelpers.readFileContent(filePath) : null,
                filePath != null ? FileHelpers.getFileNameFromPath(filePath) : null,
                filePath != null ? FileHelpers.getFileTypeByExtension(filePath) : null
        );
    };

    public FileInfo(String filePath, String fileContent) {
        this(filePath,
                fileContent,
                filePath != null ? FileHelpers.getFileNameFromPath(filePath) : null,
                filePath != null ? FileHelpers.getFileTypeByExtension(filePath) : null
        );
    }
};

//Adjusted to include null cuz FileInfo used in TabEntry cuz we want to be able to create file (creates a tab)
//but no guarantee user will also save that file


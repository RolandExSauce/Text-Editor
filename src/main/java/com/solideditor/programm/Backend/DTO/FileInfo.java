package com.solideditor.programm.Backend.DTO;
import com.solideditor.programm.Backend.Utils.FileHelpers;


// encapsulate file data as dto
public record FileInfo(String filePathOrTempTabName, String fileContent, String fileName, String fileType) {

    // Overloaded constructor with default values
    public FileInfo(String filePathOrTempTabName) {
        this(filePathOrTempTabName,
                FileHelpers.readFileContent(filePathOrTempTabName),
                FileHelpers.getFileNameFromPath(filePathOrTempTabName),
                FileHelpers.getFileTypeByExtension(filePathOrTempTabName)
        );
    };

    public FileInfo(String filePathOrTempTabName, String fileContent) {
        this(filePathOrTempTabName,
                fileContent,
                FileHelpers.getFileNameFromPath(filePathOrTempTabName),
                FileHelpers.getFileTypeByExtension(filePathOrTempTabName)
        );
    }
};




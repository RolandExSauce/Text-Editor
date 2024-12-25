package com.example.programm.backend.DTO;
import com.example.programm.backend.Utils.FileHelpers;


// encapsulate file data as dto
public record FileInfo(String fileName, String fileType, String filePath, String fileContent) {

        // overloaded constructor with default values for fileName and fileType
        public FileInfo(String filePath, String fileContent) {
            this(FileHelpers.getFileNameFromPath(filePath), FileHelpers.getFileExtension(filePath), filePath, fileContent);
        }
}

package com.example.programm.backend.DTO;
import com.example.programm.backend.Utils.FileHelpers;


// encapsulate file data as dto
public record FileInfo(String filePath, String fileName, String fileType, String fileContent) {
        // overloaded constructor with default values for fileName and fileType
        public FileInfo(String filePath) {
            this(filePath,FileHelpers.getFileNameFromPath(filePath), FileHelpers.getFileTypeByExtension(filePath),
                    FileHelpers.readFileContent(filePath));
        };
}

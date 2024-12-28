package com.example.programm.backend.services;
import com.example.programm.backend.DTO.FileInfo;
import java.util.HashMap;
import java.util.Map;

//utility class for handling tab opening/closure, will interact with FileService class
public class TabManager {
    private static final int MAX_TABS = 5; //max number of opened tab files

    // stores file key and filename, file key is created with filePath+fileName
    private static final Map<String, FileInfo> openTabs = new HashMap<>();

    //retrieve file info based on it's tab
    public static FileInfo geFileInfo(String filePath){
        for(FileInfo fileInfo : openTabs.values()){
            if(fileInfo.filePath().equals(filePath)) return fileInfo;
        }
        return null;
    };
    /********************************************************************************************************************/
    // add a new tab
    public static boolean addFileToOpenTabs(FileInfo fileInfo) {
        String fileKey = genFileKey(fileInfo.fileName(), fileInfo.filePath());
        if (openTabs.size() >= MAX_TABS) {
            System.out.println("error: maximum number of tabs reached."); //TODO: will prompt a error dialog message
            return false; // cannot add more tabs
        }

        //put the file in openTabs if key not inside
        if (!openTabs.containsKey(fileKey)) {
            openTabs.put(fileKey, fileInfo);
            FileInfo info = openTabs.get(fileKey);
            System.out.println("tab added: " + info.fileContent());
            return true;
        } else return false;
    };
    /********************************************************************************************************************/
    // remove a tab
    public static boolean removeFileFromOpenTabs(String fileName, String filePath) {
        String uniqueKey = genFileKey(fileName, filePath);
        if (openTabs.containsKey(uniqueKey)) {
            openTabs.remove(uniqueKey);
            System.out.println("tab removed: " + fileName);
            return true;
        } else {
            System.out.println("error: tab not found: " + fileName);
            return false; // tab not found
        }
    };
    /********************************************************************************************************************/
    // TODO: use this method somewhere, if file is already open, we will jump back to that tab
    public static boolean isFileTabOpen(String fileName, String filePath) {
        String uniqueKey = genFileKey(fileName, filePath);
        return openTabs.containsKey(uniqueKey);
    };
    /********************************************************************************************************************/
    // TODO: remove this later if everything works ?
    //  print all open tabs, will be removed, just for debugging currently
    public static void printOpenTabs() {
        if (openTabs.isEmpty()) {
            System.out.println("no open tabs.");
        } else {
            System.out.println("open tabs:");
            openTabs.forEach((uniqueKey, fileName) -> {
                System.out.println("uniqueKey: " + uniqueKey + ", fileName: " + fileName);
            });
        }
    };
    /********************************************************************************************************************/
    // generate a unique key by combining filePath and fileName
    private static String genFileKey(String fileName, String filePath) {
        return filePath + "::" + fileName; // use "::" as the delimiter
    };
}


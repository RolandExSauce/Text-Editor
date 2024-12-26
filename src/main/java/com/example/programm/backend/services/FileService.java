package com.example.programm.backend.services;
import com.example.programm.backend.DTO.FileInfo;
import com.example.programm.backend.Utils.FileUtils;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.util.Map;

//utility class to interact with controllers, will interact with <currently unknown, waiting for frontend>
public class FileService {

    //open new file and a tab
    public static void openFile(TabPane tabPane, Map<Tab, String> tabFileMap, Stage primaryStage) {
        FileInfo fileInfo = FileUtils.openFileAndGetContent(primaryStage);
        if (fileInfo != null) {
            //boolean added = TabManager.addFileToOpenTabs(fileInfo.fileName(), fileInfo.filePath());
            boolean added = TabManager.addFileToOpenTabs(fileInfo);
            if (added) {
                Tab newTab = new Tab(fileInfo.fileName());
                newTab.setOnClosed(e -> closeFile(tabPane, tabFileMap, newTab));
                tabPane.getTabs().add(newTab);
                tabFileMap.put(newTab, fileInfo.filePath());
                //TabManager.printOpenTabs(); //to debug
            }
        }
    };

    //create a new file/or open existing file and tab
    public static void createOrOpenFile(TabPane tabPane, Map<Tab, String> tabFileMap, Stage primaryStage) {
        FileInfo fileInfo = FileUtils.createOrOpenFileAndGetContent(primaryStage);
        if (fileInfo != null) {
            //boolean added = TabManager.addFileToOpenTabs(fileInfo.fileName(), fileInfo.filePath());
            boolean added = TabManager.addFileToOpenTabs(fileInfo);
            if (added) {
                Tab newTab = new Tab(fileInfo.fileName());
                newTab.setOnClosed(e -> closeFile(tabPane, tabFileMap, newTab));
                tabPane.getTabs().add(newTab);
                tabFileMap.put(newTab, fileInfo.filePath());
                //TabManager.printOpenTabs(); //to debug
            }
        }
    };


    //close a file and it's tab
    private static void closeFile(TabPane tabPane, Map<Tab, String> tabFileMap, Tab tab) {
        String filePath = tabFileMap.get(tab);
        if (filePath != null) {
                FileUtils.closeFileRessource(filePath);
                String fileName = tab.getText();
                boolean removed = TabManager.removeFileFromOpenTabs(fileName, filePath);
                if (removed) {
                    tabPane.getTabs().remove(tab);
                    tabFileMap.remove(tab);
                    //TabManager.printOpenTabs(); //to debug
                }
        } else {
            //should of course not happen ?
            System.out.println("error: no file associated with selected tab.");
        }
    };
}

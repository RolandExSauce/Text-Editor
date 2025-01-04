package com.solideditor.programm.backend.services;
import com.solideditor.programm.backend.DTO.FileInfo;
import com.solideditor.programm.backend.Utils.FileUtils;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import java.util.Objects;
import java.util.Map;

//utility class to interact with controllers, will interact with <currently unknown, waiting for frontend>
public class FileService {

    //save file
    public static void saveFile(TabPane tabPane, Map<Tab, String> tabFileMap, Stage primaryStage, CodeArea codeArea) {
        // Get the current tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        // get the file path for the selected tab
        String filePath = tabFileMap.get(selectedTab);

        // retrieve fileInfo from tab manager
        FileInfo fileInfo = TabManager.geFileInfo(filePath);

        // update FileInfo with the current CodeArea content
        String updatedContent = codeArea.getText();
        fileInfo = new FileInfo(Objects.requireNonNull(fileInfo).filePath(), updatedContent);
        FileUtils.saveChangesToFile(fileInfo);
    };
    /********************************************************************************************************************/
    // Open a new file and create a tab
    public static void openFile(TabPane tabPane, Map<Tab, String> tabFileMap, Stage primaryStage, CodeArea codeArea) {
        FileInfo fileInfo = FileUtils.openFileAndGetContent(primaryStage);
        if (fileInfo != null) {
            boolean added = TabManager.addFileToOpenTabs(fileInfo);
            if (added) {
                Tab newTab = new Tab(fileInfo.fileName());
                newTab.setOnClosed(e -> closeFile(tabPane, tabFileMap, newTab));
                tabPane.getTabs().add(newTab);
                tabFileMap.put(newTab, fileInfo.filePath());

                // Set CodeArea content to the file content
                if (tabPane.getSelectionModel().getSelectedItem() == newTab) {
                    codeArea.clear();
                    codeArea.replaceText(fileInfo.fileContent());
                }
            }
        }
    };
    /********************************************************************************************************************/
    //Create a new file or open an existing one and create a tab
    public static void createOrOpenFile(TabPane tabPane, Map<Tab, String> tabFileMap, Stage primaryStage, CodeArea codeArea) {
        FileInfo fileInfo = FileUtils.createOrOpenFileAndGetContent(primaryStage);
        if (fileInfo != null) {
            boolean added = TabManager.addFileToOpenTabs(fileInfo);
            if (added) {
                Tab newTab = new Tab(fileInfo.fileName());
                newTab.setOnClosed(e -> closeFile(tabPane, tabFileMap, newTab));
                tabPane.getTabs().add(newTab);
                tabFileMap.put(newTab, fileInfo.filePath());

                // Set CodeArea content to the file content
                if (tabPane.getSelectionModel().getSelectedItem() == newTab) {
                    codeArea.clear();
                    codeArea.replaceText(fileInfo.fileContent());
                }
            }
        }
    };
    /********************************************************************************************************************/
    // Close a file and its tab
    private static void closeFile(TabPane tabPane, Map<Tab, String> tabFileMap, Tab tab) {
        String filePath = tabFileMap.get(tab);
        if (filePath != null) {
            FileUtils.closeFileRessource(filePath);
            String fileName = tab.getText();
            boolean removed = TabManager.removeFileFromOpenTabs(fileName, filePath);
            if (removed) {
                tabPane.getTabs().remove(tab);
                tabFileMap.remove(tab);
            }
        } else {
            System.out.println("Error: No file associated with the selected tab.");
        }
    };
}

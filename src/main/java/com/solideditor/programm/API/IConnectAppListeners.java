package com.solideditor.programm.API;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.Utils.FileHelpers;
import com.solideditor.programm.Backend.Utils.LayoutHelpers;
import com.solideditor.programm.Backend.services.TabManagerService;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.CodeArea;
import java.util.Map;


//For listeners
public class IConnectAppListeners {
    // ChangeListener for tracking CodeArea changes
    public static ChangeListener<String> createCodeAreaChangeListener(
            TabPane tabPane,
            Map<Tab, String> tabFileMap,
            CodeArea codeArea,
            String[] unsavedContent) {

        return (observable, oldValue, newValue) -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                String filePath = tabFileMap.get(selectedTab);
                if (filePath != null) {
                    FileInfo fileInfo = TabManagerService.getFileInfo(filePath);
                    if (fileInfo != null) {
                        // Track changes and update unsaved content
                        unsavedContent[0] = newValue;
                        FileHelpers.trackIfChangesMade(fileInfo, newValue, selectedTab);
                    }
                }
            }
        };
    }

    // Listener to detect search text field changes and highlight found words
    public static void addSearchTxtFieldListener(
            TextField searchTxtField,
            TabPane tabPane,
            Map<Tab, String> tabFileMap,
            CodeArea codeArea) {

        // Trigger search with ENTER key
        searchTxtField.setOnKeyPressed(e -> {
            if (e.getCode().equals(javafx.scene.input.KeyCode.ENTER)) {
                LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, searchTxtField.getText(), codeArea);
            }
        });

        // Trigger search on text field updates
        searchTxtField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, "", codeArea);
            } else {
                System.out.println("Search text updated: " + newValue);
                LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, newValue, codeArea);
            }
        });
    }
}

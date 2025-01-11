package com.solideditor.programm.Backend.Utils;
import com.solideditor.programm.Backend.DTO.FileInfo;
import com.solideditor.programm.Backend.services.TabManagerService;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;
import java.util.List;
import java.util.Map;

public class LayoutHelpers {

    //method to highlight words found
    public static void highlightWordSequenceFound(
            TabPane tabPane, Map<Tab, String> tabFileMap,
            String txtToSearch, CodeArea codeArea
    ) {
        // Step 1: Validate inputs
        if (tabPane == null || tabFileMap == null || codeArea == null) {
            System.err.println("Invalid input: TabPane, TabFileMap, or CodeArea is null");
            return;
        }

        // Step 2: Get the selected tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            System.err.println("No tab selected");
            return;
        }

        // Step 3: Get the file path associated with the selected tab
        String filePath = tabFileMap.get(selectedTab);
        if (filePath == null) {
            System.err.println("No file path associated with the selected tab");
            return;
        }

        // Step 4: Get file info
        FileInfo fileInfo = TabManagerService.getFileInfo(filePath);
        if (fileInfo == null) {
            System.err.println("FileInfo is null for file path: " + filePath);
            return;
        }

        // Step 5: Get content
        String content = fileInfo.fileContent();
        if (content == null) {
            System.err.println("File content is null for file path: " + filePath);
            return;
        }

        // Step 6: Clear existing styles
        try {
            codeArea.clearStyle(0, content.length());
        } catch (Exception e) {
            System.err.println("Error clearing styles: " + e.getMessage());
            return;
        }

        // Step 7: Find and highlight positions
        if (txtToSearch == null || txtToSearch.isEmpty()) {
            System.out.println("Search text is empty. No highlighting applied.");
            return;
        }

        List<Integer> positions;
        try {
            positions = FileHelpers.findSubstringPositions(content, txtToSearch);
        } catch (Exception e) {
            System.err.println("Error finding substring positions: " + e.getMessage());
            return;
        }

        // Step 8: Apply styles
        for (int index : positions) {
            int endIndex = index + txtToSearch.length();
            if (endIndex <= content.length()) {
                try {
                    codeArea.setStyleClass(index, endIndex, "highlight");
                } catch (Exception e) {
                    System.err.println("Error applying style at index " + index + ": " + e.getMessage());
                }
            }
        }
    };
    /********************************************************************************************************************/
    //Add more methods here

}

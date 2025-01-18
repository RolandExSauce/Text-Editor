package com.solideditor.programm.Backend.Utils;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.services.TabManagerService;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;
import java.util.List;
import java.util.Map;


//helper class for layout settings class
public class LayoutHelpers {

    //method to highlight words found
    public static void highlightWordSequenceFound(
            TabPane tabPane, Map<String, TabEntry>openTabs,
            String txtToSearch, CodeArea codeArea
    ) {
        // Step 1: Validate inputs
        if (tabPane == null || openTabs == null || codeArea == null) {
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
        TabEntry tabEntry = TabManagerService.getTabEntryByTab(selectedTab);

        String filePath = tabEntry.fileInfo().filePathOrTempTabName();
        if (filePath == null) {
            System.err.println("No file path associated with the selected tab");
            return;
        }

        // Step 4: Get content
        String content = tabEntry.fileInfo().fileContent();
        if (content == null) {
            System.err.println("File content is null for file path: " + filePath);
            return;
        }

        // Step 5: Clear existing styles
        try {
            codeArea.clearStyle(0, content.length());
        } catch (Exception e) {
            System.err.println("Error clearing styles: " + e.getMessage());
            return;
        }

        // Step 6: Find and highlight positions
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

        // Step 7: Apply styles
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

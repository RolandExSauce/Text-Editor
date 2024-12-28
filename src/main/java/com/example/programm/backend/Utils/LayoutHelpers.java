package com.example.programm.backend.Utils;
import com.example.programm.backend.DTO.FileInfo;
import com.example.programm.backend.services.TabManager;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LayoutHelpers {
    //method to highlight words found
    public static void highlightWordSequenceFound(
            TabPane tabPane, Map<Tab, String> tabFileMap,
            String txtToSearch, CodeArea codeArea
    ) {
        // get the current tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            System.err.println("No tab selected");
            return;
        }
        String filePath = tabFileMap.get(selectedTab);
        FileInfo fileInfo = TabManager.geFileInfo(filePath);

        String content = Objects.requireNonNull(fileInfo).fileContent();
        codeArea.clearStyle(0, content.length()); // clear existing highlights

        List<Integer> positions = FileHelpers.findSubstringPositions(content, txtToSearch);
        // apply highlight css class to all found positions
        for (int index : positions) {
            int endIndex = index + txtToSearch.length();
            if (endIndex <= content.length()) {
                codeArea.setStyleClass(index, endIndex, "highlight");
            }
        }
    };
    /********************************************************************************************************************/
    //Add more methods here

}

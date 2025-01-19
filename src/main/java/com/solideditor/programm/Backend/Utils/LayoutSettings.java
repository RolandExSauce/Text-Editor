package com.solideditor.programm.Backend.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solideditor.programm.App;
import com.solideditor.programm.Backend.DTO.TabEntry;
import com.solideditor.programm.Backend.services.TabManagerService;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


//helper class for layout settings class
public class LayoutSettings {
    private static final String pathToJSONFonts = "/com/solideditor/programm/static/fonts.json";

    //load fonts from GetFontsFromJSON class
    public static List<String> loadFonts() {
        try (InputStream resourceStream = Objects.requireNonNull(App.class.getResourceAsStream(pathToJSONFonts))) {
            Map<String, List<String>> fontMap = appFontsFromStream(resourceStream);
            return fontMap.getOrDefault("fontNames", List.of("Arial", "Times New Roman", "Verdana", "Georgia"));
        } catch (Exception e) {
            System.err.println("Failed to load fonts from JSON: " + e.getMessage());
            return List.of("Arial", "Times New Roman", "Verdana", "Georgia"); // fallback fonts
        }
    };
    /******************************************************************************************************************/
    private static Map<String, List<String>> appFontsFromStream(InputStream inputStream) {
        List<String> fonts = new ArrayList<>();
        List<String> styles = new ArrayList<>();
        Map<String, List<String>> fontMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper(); // use Jackson for parsing

        try {
            JsonNode rootNode = mapper.readTree(inputStream); // Read JSON
            JsonNode fontArray = rootNode.get("fonts");

            if (fontArray != null && fontArray.isArray()) {
                for (JsonNode fontNode : fontArray) {
                    String fontName = fontNode.get("fontName").asText();
                    String fontStyle = fontNode.get("style").asText();
                    fonts.add(fontName);
                    styles.add(fontStyle);
                }
                fontMap.put("fontNames", fonts);
                fontMap.put("fontStyles", styles);
            }
        } catch (IOException e) {
            System.err.println("Error reading or parsing the JSON stream: " + e.getMessage());
        }
        return fontMap;
    };
    /******************************************************************************************************************/
    //method to highlight words found
    public static void highlightWordSequenceFound(
            TabPane tabPane,
            String txtToSearch, CodeArea codeArea
    ) {
        //Get the selected tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            System.err.println("No tab selected");
            return;
        }
        //Get file path associated with the selected tab
        TabEntry tabEntry = TabManagerService.getTabEntryByTab(selectedTab);

        String filePath = tabEntry.fileInfo().filePathOrTempTabName();
        if (filePath == null) {
            System.err.println("No file path associated with the selected tab");
            return;
        }

        //Get content
        String content = tabEntry.fileInfo().fileContent();
        if (content == null) {
            System.err.println("File content is null for file path: " + filePath);
            return;
        }

        //Clear existing styles
        try {
            codeArea.clearStyle(0, content.length());
        } catch (Exception e) {
            System.err.println("Error clearing styles: " + e.getMessage());
            return;
        }

        //Find and highlight positions
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

        //Apply styles
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
    /******************************************************************************************************************/
    public static void changeFont(CodeArea codeArea, String fontName) {
        if (codeArea == null || fontName == null || fontName.isEmpty()) {
            System.err.println("CodeArea or FontName is invalid.");
            return;
        };
        // Apply the font dynamically as an inline style
        codeArea.setStyle("-fx-font-family: '" + fontName + "'");
    };

}
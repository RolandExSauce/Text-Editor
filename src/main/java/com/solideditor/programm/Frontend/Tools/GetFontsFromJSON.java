package com.solideditor.programm.Frontend.Tools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Utility Class to load fonts from a JSON file
public class GetFontsFromJSON {
    /**
     * Reads a JSON file containing fonts from an InputStream and returns a list of font names.
     * @param inputStream the InputStream of the JSON file
     * @return a map of font names
     */

    //return Map<List<String>, List<String>>
    public static Map<String, List<String>> appFontsFromStream(InputStream inputStream) {
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

}

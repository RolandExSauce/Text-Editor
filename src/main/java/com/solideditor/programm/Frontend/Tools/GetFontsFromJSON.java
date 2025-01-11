package com.solideditor.programm.Frontend.Tools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


// Utility Class to load fonts from a JSON file
public class GetFontsFromJSON {
    /**
     * Reads a JSON file containing fonts from an InputStream and returns a list of font names.
     * @param inputStream the InputStream of the JSON file
     * @return a list of font names
     */
    public static List<String> appFontsFromStream(InputStream inputStream) {
        List<String> fonts = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper(); // use Jackson for parsing

        try {
            JsonNode rootNode = mapper.readTree(inputStream); // Read JSON
            JsonNode fontArray = rootNode.get("fonts");

            if (fontArray != null && fontArray.isArray()) {
                for (JsonNode fontNode : fontArray) {
                    String fontName = fontNode.get("fontName").asText();
                    fonts.add(fontName);

                    // Optional: Print additional font details
                    // String style = fontNode.get("style").asText();
                    // System.out.println("Font name: " + fontName);
                    // System.out.println("Font style: " + style);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading or parsing the JSON stream: " + e.getMessage());
        }
        return fonts;
    };

}

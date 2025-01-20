package com.solideditor.programm.Frontend.Tools;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import java.util.*;


// Utility class for controller-related helper methods
public class ControllerUtils {
    /*FOR MENUBAR CONTROLLER*******************************************************************************************/
    public static void initializeComboBox(ComboBox<String> comboBox, List<String> items) {
        if (comboBox != null) {
            if (items != null && !items.isEmpty()) {
                comboBox.setItems(FXCollections.observableArrayList(items));
                comboBox.getSelectionModel().select(0);

                // Bind width dynamically to the selected item's size
                comboBox.prefWidthProperty().bind(comboBox.getEditor().
                        textProperty().length().multiply(7) // Adjust multiplier as needed
                        .add(30) // Padding for dropdown arrow and padding
                );
            } else {
                System.err.println("Items list is null or empty. Cannot populate ComboBox.");
            }
        } else {
            System.err.println("ComboBox is null. Cannot initialize.");
        }
    };

    // Add a listener to handle ComboBox selection changes
    public static void addComboBoxSelectionListener(ComboBox<String> comboBox, Runnable onSelectionChange) {
        if (comboBox != null) {
            comboBox.setOnAction(event -> {
                if (onSelectionChange != null) {
                    onSelectionChange.run();
                }
            });
        } else {
            System.err.println("ComboBox is null! Cannot add listener.");
        }
    };
    /*FOR Footer Bar CONTROLLER****************************************************************************************/
    public static int countWordsInCodeArea(String codeAreaTxt) {
        // Entferne überflüssige Leerzeichen und zähle die Wörter
        if (codeAreaTxt == null || codeAreaTxt.isBlank()) {
            return 0;
        };
        String[] words = codeAreaTxt.trim().split("\\s+");
        return words.length;
    };
}


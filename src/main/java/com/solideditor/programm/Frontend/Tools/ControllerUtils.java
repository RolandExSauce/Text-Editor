package com.solideditor.programm.Frontend.Tools;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;

// Utility class for controller-related helper methods
public class ControllerUtils {
    /*FOR MENUBAR CONTROLLER*******************************************************************************************/
    // Initialize a ComboBox with a list of items
    public static void initializeComboBox(ComboBox<String> comboBox, List<String> items) {
        if (comboBox != null) {
            if (items != null && !items.isEmpty()) {
                // Set items in the ComboBox
                comboBox.setItems(FXCollections.observableArrayList(items));

                // Set a default selected item
                comboBox.getSelectionModel().select(0);

                // Bind width dynamically to the selected item's size
                comboBox.prefWidthProperty().bind(comboBox.getEditor().textProperty().length().multiply(7) // Adjust multiplier as needed
                        .add(30) // Padding for dropdown arrow and padding
                );
            } else {
                System.err.println("Items list is null or empty. Cannot populate ComboBox.");
            }
        } else {
            System.err.println("ComboBox is null. Cannot initialize.");
        }
    }

    ;

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
    }

    ;

    /******************************************************************************************************************/
    public static void makeTextBold(CodeArea codeArea) {
        if (codeArea == null) {
            throw new IllegalArgumentException("CodeArea cannot be null");
        }

        // Get the selected text range
        int start = codeArea.getSelection().getStart();
        int end = codeArea.getSelection().getEnd();

        // Check if there's any selection
        if (start == end) {
            System.out.println("No text selected.");
            return;
        }

        // Create a StyleSpans object to apply bold style
        StyleSpans<Collection<String>> styleSpans = new StyleSpansBuilder<Collection<String>>().add(Collections.singleton("bold"), end - start).create();

        // Apply the styles to the selected text
        codeArea.setStyleSpans(start, styleSpans);
    }
}


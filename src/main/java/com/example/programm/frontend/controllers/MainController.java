package com.example.programm.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class MainController {

    public TabPane tabPlane;
    // Top-Bereich
    @FXML
    private Label label_se;


    @FXML
    private Tab tab_tab1;

    // Erste Symbolleiste
    @FXML
    private Button button_Open;

    @FXML
    private Button button_NewTab;

    @FXML
    private Button button_Save;

    @FXML
    private Button button_Print;

    @FXML
    private TextField textField_Search;

    // Zweite Symbolleiste
    @FXML
    private ComboBox<String> comboBox_Font;

    @FXML
    private Button button_Bold;

    @FXML
    private Button button_Italian;

    @FXML
    private Button button_Underline;

    // Bottom-Bereich
    @FXML
    private Label label_Words;

    @FXML
    private Slider slider_zoom;

    // Center-Bereich
    @FXML
    private TextArea textArea;

    // Initialisierung
    @FXML
    private void initialize() {
        System.out.println("Controller initialized!");

        // Beispiel: ComboBox mit Werten füllen
        comboBox_Font.getItems().addAll("Arial", "Helvetica", "Times New Roman", "Courier New");
        comboBox_Font.setValue("Arial"); // Standardwert

        // Beispiel: Wörterzähler aktualisieren
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int wordCount = countWords(newValue);
            label_Words.setText("Wörter: " + wordCount);
        });
    }

    // Zählt Wörter im Textbereich
    private int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    // Button-Action-Handler
    @FXML
    private void onOpenClicked() {
        System.out.println("Open button clicked!");
        // Logik zum Öffnen einer Datei
    }

    @FXML
    private void onNewTabClicked() {
        System.out.println("New button clicked!");
        // Logik zum Erstellen einer neuen Datei
        textArea.clear();
    }

    @FXML
    private void onSaveClicked() {
        System.out.println("Save button clicked!");
        // Logik zum Speichern des Texts
    }

    @FXML
    private void onPrintClicked() {
        System.out.println("Print button clicked!");
        // Logik zum Drucken
    }

    @FXML
    private void onSearch() {
        String searchText = textField_Search.getText();
        System.out.println("Search: " + searchText);
        // Implementiere eine Suchfunktion im Textbereich
        highlightText(searchText);
    }

    @FXML
    private void onFontClicked() {
        String selectedFont = comboBox_Font.getValue();
        System.out.println("Font changed to: " + selectedFont);
        textArea.setStyle("-fx-font-family: '" + selectedFont + "';");
    }

    @FXML
    private void onBoldClicked() {
        System.out.println("Bold button clicked!");
        textArea.setStyle("-fx-font-weight: bold;");
    }

    @FXML
    private void onItalianClicked() {
        System.out.println("Italic button clicked!");
        textArea.setStyle("-fx-font-style: italic;");
    }

    @FXML
    private void onUnderlineClicked() {
        System.out.println("Underline button clicked!");
        textArea.setStyle("-fx-underline: true;");
    }


    // Hilfsmethode: Text hervorheben
    private void highlightText(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return;
        }

        String text = textArea.getText();
        int index = text.indexOf(searchText);

        if (index != -1) {
            textArea.selectRange(index, index + searchText.length());
            System.out.println("Text found at index: " + index);
        } else {
            System.out.println("Text not found!");
        }
    }
}

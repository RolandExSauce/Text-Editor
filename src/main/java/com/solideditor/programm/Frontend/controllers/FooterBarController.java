package com.solideditor.programm.Frontend.controllers;

import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.Frontend.Tools.FooterUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;

import java.util.List;

public class FooterBarController {

    @FXML
    private Label wordCountLabel;

    private IConnectApp IConnectApp;

    /******************************************************************************************************************/

    public void setMainUtilities(IConnectApp IConnectApp) {
        this.IConnectApp = IConnectApp;



        // Sicherstellen, dass die CodeArea verfügbar ist
        CodeArea codeArea = IConnectApp.getCodeArea();
        if (codeArea != null) {
            // Listener für Textänderungen hinzufügen
            codeArea.textProperty().addListener((obs, oldText, newText) -> {
                int wordCount = FooterUtils.countWordsInCodeArea(codeArea);
                wordCountLabel.setText("Wörter: " + wordCount);
            });
        } else {
            System.err.println("CodeArea ist nicht verfügbar.");
        }
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            int wordCount = FooterUtils.countWordsInCodeArea(codeArea);
            System.out.println("Neuer Text: " + newText);
            System.out.println("Wortanzahl: " + wordCount);
            wordCountLabel.setText("Wörter: " + wordCount);
        });
    }


}

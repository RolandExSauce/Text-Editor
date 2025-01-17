package com.solideditor.programm.Frontend.Tools;

import org.fxmisc.richtext.CodeArea;

import java.util.Collections;

public class ChangeFontUtils {

    /**
     * Ändert den Font des markierten Textes in der CodeArea dynamisch.
     *
     * @param codeArea Die CodeArea, in der der Text markiert ist.
     * @param fontName Der Name der Schriftart, die angewendet werden soll.
     */
    public static void changeFontForSelectedText(CodeArea codeArea, String fontName) {
        if (codeArea == null || fontName == null || fontName.isEmpty()) {
            System.err.println("CodeArea oder FontName ist ungültig.");
            return;
        }

        // Markierten Text überprüfen
        String selectedText = codeArea.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            System.out.println("Kein Text markiert.");
            return;
        }

        // Start- und Endposition des markierten Textes
        int start = codeArea.getSelection().getStart();
        int end = codeArea.getSelection().getEnd();

        if (start == end) {
            System.out.println("Kein Text markiert.");
            return; // Kein markierter Text
        }

        // Ändere den Stil des markierten Textes
        codeArea.setStyle(start, end, Collections.singletonList("-fx-font-family: '" + fontName + "'"));

        System.out.println("Schriftart geändert zu: " + fontName);
    }
}

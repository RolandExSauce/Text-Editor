package com.solideditor.programm.Frontend.Tools;

import org.fxmisc.richtext.CodeArea;

public class FooterUtils {
    /**
     * Zählt die Wörter in der CodeArea.
     *
     * @param codeArea Die CodeArea, deren Text analysiert wird.
     * @return Die Anzahl der Wörter im Text.
     */
    public static int countWordsInCodeArea(CodeArea codeArea) {
        if (codeArea == null) {
            System.err.println("CodeArea ist null.");
            return 0;
        }

        // Hole den gesamten Text aus der CodeArea
        String text = codeArea.getText();
        System.out.println("Aktueller Text in CodeArea: " + text);

        // Entferne überflüssige Leerzeichen und zähle die Wörter
        if (text == null || text.isBlank()) {
            return 0;
        }

        String[] words = text.trim().split("\\s+");
        System.out.println("Wörter gefunden: " + words.length);
        return words.length;
    }
}

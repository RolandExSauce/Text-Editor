package com.solideditor.programm.Backend.DTO;
import javafx.scene.control.Tab;

//encapsulate data on Tab, will hold info on the current Tab, fileInfo and unsaved content
public record TabEntry(Tab tab, FileInfo fileInfo, String unsavedContent) {
    public TabEntry(Tab tab, FileInfo fileInfo) {
        this(tab, fileInfo, "");
    };
};



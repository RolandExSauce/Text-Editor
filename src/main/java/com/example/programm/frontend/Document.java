package com.example.programm.frontend;

import javafx.beans.property.*;

public class Document {
    private final StringProperty title;
    private final StringProperty content;
    private final BooleanProperty isSaved;

    public Document(String title, String content, boolean isSaved) {
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.isSaved = new SimpleBooleanProperty(isSaved);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public StringProperty contentProperty() {
        return content;
    }

    public boolean isSaved() {
        return isSaved.get();
    }

    public void setSaved(boolean isSaved) {
        this.isSaved.set(isSaved);
    }

    public BooleanProperty isSavedProperty() {
        return isSaved;
    }
}

package com.solideditor.programm.frontend.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EditController {

    @FXML
    private Label welcomeText;

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Hello, JavaFX!");
    }

    //test von roland
    private String bessereVariable;

}


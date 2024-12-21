package com.example.programm.frontend.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EditController {

    @FXML
    private Label welcomeText;

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Hello, JavaFX!");
    }
}

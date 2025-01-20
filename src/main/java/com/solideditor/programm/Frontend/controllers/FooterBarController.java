package com.solideditor.programm.Frontend.controllers;

import com.solideditor.programm.API.IConnectApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import lombok.Getter;


//footer controller
public class FooterBarController {

    private final IConnectApp iConnectApp = IConnectApp.getIconnectAppInstance();

    @FXML
    @Getter
    public Label wordCountLabel;

    @FXML
    @Getter
    public Label signCountLabel;

    @FXML
    @Getter
    public Slider zoomSlider;

    @FXML
    private void initialize() {
        if (zoomSlider != null) {
            zoomSlider.setMin(0.5);
            zoomSlider.setMax(2.0);
            zoomSlider.setValue(1.0); // Standardwert (100%)

            // Listener für den Slider hinzufügen
            zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double scale = newValue.doubleValue();
                iConnectApp.applyZoom(scale); // Zoom über IConnectApp anwenden
            });
        } else {
            System.err.println("ZoomSlider ist null! Kann Zoom nicht initialisieren.");
        }
    }
}

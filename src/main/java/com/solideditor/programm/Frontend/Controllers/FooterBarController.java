package com.solideditor.programm.Frontend.Controllers;
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
    public Slider zoomSlider;

    @FXML
    private void initialize() {
        if (zoomSlider != null) {
            zoomSlider.setMin(0.5);
            zoomSlider.setMax(2.0);
            zoomSlider.setValue(1.0); // standard value (100%)

            // add listener to slider
            zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double scale = newValue.doubleValue();
                if (iConnectApp.getCodeArea() != null) {
                    double fontSize = scale * 14; // standard font size = 14px
                    iConnectApp.getCodeArea().setStyle("-fx-font-size: " + fontSize + "px;");
                }
            });
        };
    };
}

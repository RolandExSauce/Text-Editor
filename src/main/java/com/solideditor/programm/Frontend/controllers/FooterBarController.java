package com.solideditor.programm.Frontend.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import lombok.Getter;


//footer controller
public class FooterBarController {
    public Slider sliderZoom;

    @FXML
    @Getter
    public Label wordCountLabel;

    @FXML
    @Getter
    public Label signCountLabel;

};

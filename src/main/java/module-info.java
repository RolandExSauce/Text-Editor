module com.solideditor.programm {
    requires com.fasterxml.jackson.core;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires kotlin.stdlib;
    requires java.prefs;
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;
    //requires static lombok; //if records don't do the job then maybe use lombok
    opens com.solideditor.programm.Frontend.controllers to javafx.fxml; // Allow access for FXML
    exports com.solideditor.programm.Frontend.controllers;
    opens com.solideditor.programm to javafx.fxml;
    exports com.solideditor.programm;
    exports com.solideditor.programm.Frontend.Tools;
    opens com.solideditor.programm.Frontend.Tools to javafx.fxml;
    exports com.solideditor.programm.API;
    opens com.solideditor.programm.API to javafx.fxml;
}
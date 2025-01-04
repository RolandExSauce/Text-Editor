module com.solideditor.programm {
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
    //requires static lombok; //if records don't do the job then maybe use lombok
    opens com.solideditor.programm.frontend.controllers to javafx.fxml; // Allow access for FXML
    exports com.solideditor.programm.frontend.controllers;
    opens com.solideditor.programm to javafx.fxml;
    exports com.solideditor.programm;
}
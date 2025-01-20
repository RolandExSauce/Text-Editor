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
    requires static lombok;
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    opens com.solideditor.programm.Frontend.Controllers to javafx.fxml; // Allow access for FXML
    opens com.solideditor.programm to javafx.fxml;
    exports com.solideditor.programm;
}
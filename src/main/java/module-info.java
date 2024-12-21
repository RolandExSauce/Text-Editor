module com.example.programm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    //requires kotlin.stdlib; // Add this line if Kotlin is required by a dependency
    opens com.example.programm.frontend.controllers to javafx.fxml; // Allow access for FXML
    exports com.example.programm.frontend.controllers;
    opens com.example.programm to javafx.fxml;
    exports com.example.programm;
}
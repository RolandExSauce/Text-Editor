package com.example.programm;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app.fxml"));

        // Create a scene using the loaded FXML
        Scene scene = new Scene(fxmlLoader.load());

        // Set the title of the window
        //stage.setTitle("Hello!");

        // Set the scene to the stage (window)
        //stage.setScene(scene);

        // Show the window
        stage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch();
    }
}

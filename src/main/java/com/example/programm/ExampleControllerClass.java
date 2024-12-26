package com.example.programm;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.util.HashMap;
import java.util.Map;

/*
For smooth implementation without having the Backend Team to change FileService Class,
frontend should use TabPane, Tab and of course Stage javafx Components


Text Area will dynamically be updated when switched to each tab ?

 */


public class ExampleControllerClass {

    //TODO: Of course we should get the existing Tab from the UI, same for TabPane and Stage as well
    //And not PROGRAMMATICALLY code them!

    private final TabPane tabPane = new TabPane(); // TabPane to manage file tabs
    private final Map<Tab, String> tabFileMap = new HashMap<>(); // Map to associate tabs with file paths

//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("FileChooser Example");
//
//        // Create buttons
//        Button openFileButton = new Button("Open File");
//
//        // Use FileService to handle button actions
//        openFileButton.setOnAction(e -> FileService.createNewFileTab(tabPane, tabFileMap, primaryStage));
//
//        // Layout
//        VBox layout = new VBox(10); // Spacing between components
//        layout.getChildren().addAll(tabPane, openFileButton);
//
//        // Scene
//        Scene scene = new Scene(layout, 400, 300);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }

//    public static void main(String[] args) {
//        launch(args);
//    }

}

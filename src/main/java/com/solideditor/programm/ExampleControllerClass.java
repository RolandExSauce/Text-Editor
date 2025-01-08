package com.solideditor.programm;
import com.solideditor.programm.backend.DTO.FileInfo;
import com.solideditor.programm.backend.Utils.LayoutHelpers;
import com.solideditor.programm.backend.services.FileService;
import com.solideditor.programm.backend.services.TabManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.CodeArea;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//TODO:
//Code Are should have a Pane or BorderPane as placeholder when creating GUI with SceneBuilder
//All the other components are native and should not be created programmatically of course
//add styling to css file accordingly, look inside: /at/ac/fhcampuswien/css/app.css
//Create Controller class to handle button actions
public class ExampleControllerClass extends Application {
    private final TabPane tabPane = new TabPane(); // TabPane to manage file tabs
    private final Map<Tab, String> tabFileMap = new HashMap<>(); // Map to associate tabs with file paths

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rich Text Editor");

        TextField searchField = new TextField();
        searchField.setPromptText("Search in File...");

        // Create buttons
        Button openFileButton = new Button("Open File");
        Button saveButton = new Button("Save File");
        Button newFileButton = new Button("New File");
        Button searchTextInFileButton = new Button("Search"); //TODO: Implement "ENTER" also


        Button boldButton = new Button("Bold");
        Button italicButton = new Button("Italic");

        // Create a CodeArea (from RichTextFX) for editable and styled text
        CodeArea codeArea = new CodeArea();
        codeArea.setWrapText(true); // Enable line wrapping

        // Wrap the CodeArea in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(codeArea);
        scrollPane.setFitToWidth(true); // Ensure CodeArea fits horizontally
        scrollPane.setFitToHeight(true); // Ensure CodeArea fits vertically

        // Add a listener to detect when the selected tab changes
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                // Display the content of the newly selected tab
                String filePath = tabFileMap.get(newTab);
                if (filePath != null) {
                    FileInfo fileInfo = TabManager.geFileInfo(filePath);
                    if (fileInfo != null) {
                        codeArea.clear(); // Clear previous content
                        codeArea.replaceText(fileInfo.fileContent()); // Set new content
                    }
                }
            } else {
                codeArea.clear(); // clear content when no tab is selected
            }
        });

        // handle button actions
        openFileButton.setOnAction(e -> FileService.openFile(tabPane, tabFileMap, primaryStage, codeArea));
        newFileButton.setOnAction(e -> FileService.createOrOpenFile(tabPane, tabFileMap, primaryStage, codeArea));
        saveButton.setOnAction(e -> FileService.saveFile(tabPane, tabFileMap, primaryStage, codeArea));
        searchTextInFileButton.setOnAction(e -> LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, searchField.getText(), codeArea));

        //so that we can search with enter as well
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, searchField.getText(), codeArea);
            }
        });

        //listener to detect search textfield changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, "", codeArea);
            } else {
                //TODO: ask prof ?
                //Or like this ?
                    LayoutHelpers.highlightWordSequenceFound(tabPane, tabFileMap, newValue, codeArea);
            }
        });

        //Put this in layout class

        // Add basic styling buttons
//        boldButton.setOnAction(e -> {
//            int start = codeArea.getSelection().getStart();
//            int end = codeArea.getSelection().getEnd();
//            if (start != end) {
//                codeArea.setStyleClass(start, end, "bold-text");
//            }
//        });

//        italicButton.setOnAction(e -> {
//            int start = codeArea.getSelection().getStart();
//            int end = codeArea.getSelection().getEnd();
//            if (start != end) {
//                codeArea.setStyleClass(start, end, "italic-text");
//            }
//        });

        // Create a horizontal layout for the buttons
        HBox buttonLayout = new HBox(10); // Spacing between buttons
        buttonLayout.getChildren().addAll(openFileButton, saveButton, newFileButton,searchTextInFileButton, boldButton, italicButton,searchField);

        // Create a BorderPane for the layout
        BorderPane layout = new BorderPane();
        layout.setTop(tabPane); // Tabs at the top
        layout.setCenter(scrollPane); // CodeArea in the center wrapped in ScrollPane
        layout.setBottom(buttonLayout); // Buttons at the bottom

        // Scene
        Scene scene = new Scene(layout, 600, 400);

        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/app.css")).toExternalForm());

        // Add CSS for styling
        String cssPath = Objects.requireNonNull(this.getClass().getResource("/at/ac/fhcampuswien/css/app.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

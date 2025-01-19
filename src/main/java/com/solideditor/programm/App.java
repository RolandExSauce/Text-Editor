package com.solideditor.programm;
import com.solideditor.programm.API.ApplicationLogs;
import com.solideditor.programm.Frontend.Tools.EffectUtilities;
import com.solideditor.programm.Frontend.Tools.ResizeHelper;
import com.solideditor.programm.Frontend.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;


//Entry class
public class App extends Application {

    private static final Logger logger = ApplicationLogs.getLogger();


    //TODO: start should handle exception then write in log, e.g logger.error
    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Starting application...");
        // Load main FXML file
        String mainFxmlFile = "/com/solideditor/programm/fxml/mainlayout.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainFxmlFile));
        Parent root = loader.load();

        //TODO: not working, still seeing a little border white
        // Apply clipping to enforce rounded corners
        Rectangle rect = new Rectangle();
        rect.setArcHeight(30.0);
        rect.setArcWidth(30.0);

        // Bind the rectangle's dimensions to the layout bounds of the root
        root.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            rect.setWidth(newValue.getWidth());
            rect.setHeight(newValue.getHeight());

        //System.out.println("Rectangle width: " + rect.getWidth());
        //System.out.println("Rectangle height: " + rect.getHeight());
        });
        root.setClip(rect);

        // Pass the stage to MainController
        MainController mainController = loader.getController();
        mainController.setPrimaryStage(stage);

        // Make stage undecorated so that we can style
        Scene scene = new Scene(root, 1300, 800);
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("SolidEdit");

        // Add resize and drag functionality
        ResizeHelper.addResizeListener
                (stage, 600, 400, 1600, 1200);
        EffectUtilities.makeDraggable(stage, root);
        stage.show();
    };

    //start main method and call launch method
    public static void main(String[] args) {
        launch(args);
    }
};

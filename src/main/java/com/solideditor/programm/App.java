package com.solideditor.programm;
import com.solideditor.programm.Frontend.Tools.EffectUtilities;
import com.solideditor.programm.Frontend.Tools.ResizeHelper;
import com.solideditor.programm.Frontend.Controllers.MainController;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;


//Entry class
public class App extends Application {
    private static final String className = App.class.getName();
    @Override
    public void start(Stage stage) {
        try {
            // Log an INFO message
            LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.ERR_CODE_2000);
            String mainFxmlFile = "/com/solideditor/programm/fxml/mainlayout.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(mainFxmlFile));
            Parent root = loader.load();

            // Apply clipping to enforce rounded corners
            Rectangle rect = new Rectangle();
            rect.setArcHeight(30.0);
            rect.setArcWidth(30.0);
            root.setClip(rect);

            // Bind the rectangle's dimensions to the layout bounds of the root
            root.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                rect.setWidth(newValue.getWidth());
                rect.setHeight(newValue.getHeight());
            });
            root.setClip(rect);

            // Pass the stage to MainController
            MainController mainController = loader.getController();
            mainController.setPrimaryStage(stage);

            // Make stage undecorated so that we can style
            Scene scene = new Scene(root, 1300, 800);
            scene.setFill(Color.TRANSPARENT);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("SolidEdit");

            // Add resize and drag functionality
            ResizeHelper.addResizeListener
                    (stage, 600, 400, 1600, 1200);
            EffectUtilities.makeDraggable(stage, root);
            stage.show();

            LoggingUtils.log(LoggingUtils.LogLevel.INFO, className, ErrorOrSuccessCode.ERR_CODE_2001);
        }
        catch (IOException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4000);
        } catch (RuntimeException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4001);
        };
    };

    //start main method and call launch method
    public static void main(String[] args) {
        launch(args);
    };
};

package com.solideditor.programm;
import com.solideditor.programm.Frontend.Tools.EffectUtilities;
import com.solideditor.programm.Frontend.Tools.ResizeHelper;
import com.solideditor.programm.Frontend.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;


//Entry class
public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
//

        // Initialize pathToJSON
//
        String p = String.valueOf(getClass().getResource("/com/solideditor/programm/static/fonts.json"));

        System.out.println("json found: " + p);

        String mainFxmlFile = "/com/solideditor/programm/fxml/mainlayout.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainFxmlFile));
        Parent root = loader.load();

        // pass the stage to MainController
        MainController mainController = loader.getController();
        mainController.setPrimaryStage(stage);

        // make stage undecorated so that we can style
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);

        // TODO: not working quite well, fix ? maybe ure own solution
        ResizeHelper.addResizeListener(stage);
        EffectUtilities.makeDraggable(stage, root);

        stage.setTitle("SolidEdit");
        stage.show();
    };

    //start main method and call launch method
    public static void main(String[] args) {
        launch(args);
    }
};

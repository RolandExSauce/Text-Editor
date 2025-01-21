package com.solideditor.programm.Frontend.Controllers;
import com.solideditor.programm.API.IConnectApp;
import com.solideditor.programm.API.IConnectAppUtils;
import com.solideditor.programm.Backend.Services.FileIOService;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


//dialog controller
public class DialogController {
    private static final IConnectApp iConnectApp
            = IConnectApp.getIconnectAppInstance(); // get singleton instance
    private static final String className = DialogController.class.getSimpleName(); //get dialog name for logger
    private static double xOffset = 0;
    private static double yOffset = 0;

    //fxml paths for dialogs
    private static final String pathToUnsavedDialog = "/com/solideditor/programm/fxml/unsaved-warning-dialog.fxml";
    private static final String pathToMaxTabLimitDialog = "/com/solideditor/programm/fxml/max-tab-limit-dialog.fxml";
    private static final String pathToDeletedFileDialog = "/com/solideditor/programm/fxml/deletedFile.fxml";

    @Setter
    private Stage dialogStage;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDiscard;
    @FXML
    private Button btnCancel;

    private static CompletableFuture<DialogResult> dialogResultFuture;

    //to decide which dialog to open
    public enum DialogKIND {
        UNSAVED_CHANGES,
        FILE_NOT_EXIST,
        MAX_TAB_LIMIT
    };

    // Enum for dialog results
    public enum DialogResult {
        SAVE,
        DISCARD,
        CANCEL,
        NONE
    };

    //Save changes call service save method and close
    @FXML
    private void onSave() {
        if (dialogResultFuture != null) {
            dialogResultFuture.complete(DialogResult.SAVE);
        }
        closeDialog();
    }

    //cancel dialog
    @FXML
    private void onDiscard() {
        if (dialogResultFuture != null) {
            dialogResultFuture.complete(DialogResult.DISCARD);
        };
        closeDialog();
    };

    //just close dialog
    @FXML
    private void onCancelDialog() {
        if (dialogResultFuture != null) {
            dialogResultFuture.complete(DialogResult.CANCEL);
        }
        closeDialog();
    };

    //close dialog
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    };

    // Method to open the dialog
    public static CompletableFuture<DialogResult> openDialog(DialogKIND dialogKIND) {

        dialogResultFuture = new CompletableFuture<>();

        try {
            //load the dialog FXML
            String dialogToLoad = switch (dialogKIND) {
                case UNSAVED_CHANGES -> pathToUnsavedDialog;
                case MAX_TAB_LIMIT -> pathToMaxTabLimitDialog;
                default -> pathToDeletedFileDialog; // Default case
            };

            //generate dialog title
            String dialogTitle = switch (dialogKIND) {
                case UNSAVED_CHANGES -> "Unsaved Changes";
                case MAX_TAB_LIMIT -> "Max Tab Limit";
                default -> "File Not Found"; // default
            };

            FXMLLoader loader = new FXMLLoader(DialogController.class.getResource(dialogToLoad));
            Parent root = loader.load();
            Scene dialogScene = new Scene(root);

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // block interaction with the main window
            dialogStage.initStyle(StageStyle.UNDECORATED); // removes the top bar
            dialogStage.setScene(dialogScene);

            // enable dragging for the undecorated stage
            dialogScene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            dialogScene.setOnMouseDragged(event -> {
                dialogStage.setX(event.getScreenX() - xOffset);
                dialogStage.setY(event.getScreenY() - yOffset);
            });

            dialogStage.setTitle(dialogTitle);
            dialogStage.show();

            // Pass the stage to the dialog controller
            DialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

        } catch (IOException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4000);
        } catch (RuntimeException ex) {
            LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4001);
        }
        return dialogResultFuture;
    };

}

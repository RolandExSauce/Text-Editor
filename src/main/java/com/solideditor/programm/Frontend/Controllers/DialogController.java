package com.solideditor.programm.Frontend.Controllers;
import com.solideditor.programm.Logging.ErrorOrSuccessCode;
import com.solideditor.programm.Logging.LoggingUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;


//dialog controller
public class DialogController {
    private static final String className = DialogController.class.getSimpleName();
    private Stage dialogStage;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnDiscard;

    @FXML
    private Button btnCancel;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    //Save changes call service save method and close
    @FXML
    private void onSave() {
        System.out.println("Save button clicked!");
        closeDialog();
    }


    //Dont save changes but close dialog
    @FXML
    private void onDiscard() {
        System.out.println("Discard button clicked!");
        closeDialog();
    }


    //just close dialog
    @FXML
    private void onCancelDialog() {
        System.out.println("Cancel button clicked!");
        closeDialog();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    };

    // Method to open the dialog


        public static void openDialog() {
            try {
                //FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/solideditor/programm/fxml/unsaved-warning-dialog.fxml"));
                // Load the dialog FXML
                FXMLLoader loader = new FXMLLoader(DialogController.class.getResource("/com/solideditor/programm/fxml/unsaved-warning-dialog.fxml"));
                Scene dialogScene = new Scene(loader.load());

                // Create a new stage for the dialog
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
                dialogStage.setScene(dialogScene);
                dialogStage.setTitle("Unsaved Changes");
                dialogStage.show();

                // Pass the stage to the dialog controller
                DialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);

            } catch (IOException ex) {
                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4000);
            } catch (RuntimeException ex) {
                LoggingUtils.log(LoggingUtils.LogLevel.ERROR, className, ErrorOrSuccessCode.ERR_CODE_4001);
            }
        }


}

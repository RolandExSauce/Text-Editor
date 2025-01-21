package com.solideditor.programm.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DialogController {
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

    @FXML
    private void onSave() {
        System.out.println("Save button clicked!");
        closeDialog();
    }

    @FXML
    private void onDiscard() {
        System.out.println("Discard button clicked!");
        closeDialog();
    }

    @FXML
    private void onCancelDialog() {
        System.out.println("Cancel button clicked!");
        closeDialog();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}

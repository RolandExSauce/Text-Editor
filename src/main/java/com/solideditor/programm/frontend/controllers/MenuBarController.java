package com.solideditor.programm.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MenuBarController {

    @FXML
    public void onPrintClicked() {
        // Load dialog 1 (max-tab-limit-dialog)
        showDialog("static/fxml/max-tab-limit-dialog.fxml", "Max Tab Limit Dialog");
    }

    private void showDialog(String fxmlPath, String title) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle(title);
            // Load FXML and show the dialog (simplified for brevity)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCloseDialog() {
        // Close the current dialog
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void onSave() {
        // Save logic
        System.out.println("Changes saved");
    }

    @FXML
    public void onDiscard() {
        // Discard changes logic
        System.out.println("Changes discarded");
    }

    @FXML
    public void onCancelDialog() {
        // Close the dialog
        onCloseDialog();
    }
}
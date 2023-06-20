package com.example.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class AddressProgressBarController implements Initializable {
    @FXML
    Label messageLabel;
    @FXML
    ProgressBar progressBar;

    public void updateStatus(String message, double progress){
        this.messageLabel.setText(message);
        this.progressBar.setProgress(progress);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

package com.example.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class UserInfoController implements Initializable {
    @FXML
    Label userInfoLabel;

    public void setMessage(String message){
        this.userInfoLabel.setText(message);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}

package com.example.licenta.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class RatingMessagePromptController implements Initializable {
    @FXML
    private TextArea ratingMessageField;

    public String getMessage(){return this.ratingMessageField.getText();}


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Add character limit
        ratingMessageField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (ratingMessageField.getText().length() > 250) {
                    String s = ratingMessageField.getText().substring(0, 250);
                    ratingMessageField.setText(s);
                }
            }
        });
    }
}

package com.example.licenta.controllers;

import com.example.licenta.Address;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.Rating;

import java.net.URL;
import java.util.ResourceBundle;

public class AddressListItemController implements Initializable{
    @FXML
    private Label addressNameLabel, ipLabel, scanTypeLabel, testerInfoLabel, scanStatusLabel;
    @FXML
    private Button downloadButton, deleteButton, testerInfoButton, startChatButton;
    @FXML
    private Rating testerRating;
    @FXML
    private VBox labelBox, buttonBox;
    @FXML
    private HBox extendedBox;
    @FXML
    private AnchorPane root;

    private boolean isExtended;

    private Address address;

    public void extend(){
        if(!this.isExtended){
            this.isExtended = true;


//            this.root.getChildren().addAll(labelBox, buttonBox);
            this.root.setPrefWidth(300);
            this.root.setStyle("-fx-background-color: #8141ff;");

            extendedBox.setVisible(true);
        }
    }

    public void retract(){
        if(this.isExtended){
            this.isExtended = false;

            extendedBox.setVisible(false);

            this.root.setStyle("-fx-background-color: white;");
            this.root.setPrefHeight(60);
            //this.root.getChildren().removeAll(labelBox, buttonBox);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isExtended = false;
        this.root.setStyle("-fx-background-color: white;");
        extendedBox.setVisible(false);
        this.root.setPrefHeight(60);

        //this.root.getChildren().removeAll(labelBox, buttonBox);

    }
}

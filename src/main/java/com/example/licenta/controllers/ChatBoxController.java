package com.example.licenta.controllers;

import com.example.licenta.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBoxController implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private VBox messagePane;
    @FXML
    private AnchorPane root;
    @FXML
    private Button sendButton;

    private String receiverUsername;
    private int addressId;

    private int messageNumber;

    private static final int maxTextLength = 500;

    public VBox getMessagePane(){return  this.messagePane;}
    public int getMessageNumber(){
        return this.messageNumber;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public void sentMessage(String message){
        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        textFlow.setMaxWidth(250);

        HBox messageContainer = new HBox(textFlow);
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        messageContainer.setPadding(new Insets(5, 5, 5, 10));

        this.messagePane.getChildren().add(messageContainer);

        this.messageNumber++;
    }

    public void receiveMessage(String message){
        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(255,255,255);" +
                "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        textFlow.setMaxWidth(250);

        HBox messageContainer = new HBox(textFlow);
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5, 5, 5, 10));

        this.messagePane.getChildren().add(messageContainer);

        this.messageNumber++;
    }

    private void sendAction(ActionEvent event){
        String message = this.messageTextArea.getText();
        message = message.trim();

        if(!message.isEmpty()){
            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                    "-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            textFlow.setMaxWidth(250);

            HBox messageContainer = new HBox(textFlow);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setPadding(new Insets(5, 5, 5, 10));

            this.messagePane.getChildren().add(messageContainer);

            this.messageTextArea.clear();

            Client.getInstance().sendMessage(message, this.receiverUsername, addressId);

            this.messageNumber++;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messageNumber = 0;

        this.messagePane.setPadding(new Insets(10));
        this.messagePane.setSpacing(15);
        this.messagePane.setFillWidth(true);

        this.scrollPane.vvalueProperty().bind(this.messagePane.heightProperty());

        messageTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxTextLength) {
                messageTextArea.setText(oldValue); // Limit the text length
            }
        });

        sendButton.setOnAction(this::sendAction);
    }

    public void resetCounter() {
        this.messageNumber = 0;
    }
}

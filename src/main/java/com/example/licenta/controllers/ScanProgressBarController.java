package com.example.licenta.controllers;

import com.example.licenta.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ScanProgressBarController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private Label messageLabel;
    @FXML
    private ProgressBar progressBar;
    private DataOutputStream output;
    private DataInputStream input;

    private void close(){
        ((Stage) this.root.getScene().getWindow()).close();
    }

    private  void updateProgress(String message, int progress){
        this.messageLabel.setText(message);
        this.progressBar.setProgress((double) progress/100);
    }

    public void begin(){
        try {
            String message = this.input.readUTF();
            int progress = 0;

            while(!message.equals("Done")) {
                String finalMessage = message;
                int finalProgress = progress;
                Platform.runLater(() -> {
                    this.updateProgress(finalMessage, finalProgress);
                });

                message = input.readUTF();
                progress = input.readInt();
            }

            Thread.sleep(3000);
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        Platform.runLater(this::close);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            this.output = Client.getInstance().getOutput();
            this.input = Client.getInstance().getInput();
    }
}

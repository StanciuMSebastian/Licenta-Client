package com.example.licenta.controllers;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScanProgressWindow extends Application {
    private Stage stage;
    private Scene scene;
    private VBox root;
    private ProgressBar progressBar;
    private Label mesageLabel;

    private void updateProgress(String message, int progress){
        if(!message.isBlank())
            this.mesageLabel.setText(message);

        this.progressBar.setProgress(progress);
    }

    private void close(){
        ((Stage) this.scene.getWindow()).close();
    }



    @Override
    public void start(Stage primaryStage){
        root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        mesageLabel = new Label("Thinking . . .");

        root.getChildren().addAll(mesageLabel, progressBar);

        scene = new Scene(root, 300, 200);

        primaryStage.getScene().getRoot().setDisable(true);

        stage = new Stage();
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> primaryStage.getScene().getRoot().setDisable(false));

//        primaryStage.setScene(scene);
//        primaryStage.show();
    }
}

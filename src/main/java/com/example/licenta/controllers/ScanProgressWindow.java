package com.example.licenta.controllers;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScanProgressWindow extends Application {

    @Override
    public void start(Stage primaryStage){
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        Label mesageLabel = new Label("Thinking . . .");

        root.getChildren().addAll(mesageLabel, progressBar);

        Scene scene = new Scene(root, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

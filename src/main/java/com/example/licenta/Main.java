package com.example.licenta;

import com.example.licenta.controllers.LoginController;
import com.example.licenta.controllers.MainController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application {
    public static double xOffset = 0;
    public static double yOffset = 0;

    public static void pressMouse(MouseEvent mouseEvent) {
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    public static void dragMouse(MouseEvent mouseEvent, Stage stage) {
        stage.setX(mouseEvent.getScreenX() - xOffset);
        stage.setY(mouseEvent.getScreenY() - yOffset);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        if(!Client.getInstance().connect())
            System.out.println("Could not connect to the server");

        scene.setOnMousePressed(Main::pressMouse);
        scene.setOnMouseDragged(mouseEvent -> dragMouse(mouseEvent, stage));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static String showHybridChoiceDialog(){
        ArrayList<String> dialogChoices = new ArrayList<>();

        dialogChoices.add("Automatic");
        dialogChoices.add("Manual");


        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Download Report", dialogChoices);

        choiceDialog.setHeaderText("Choose which report you want to download");

        Optional<String> choice = choiceDialog.showAndWait();

        return choice.orElse("Cancel");

    }

    public static void main(String[] args) {
        launch();
    }
}
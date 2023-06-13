package com.example.licenta.controllers;

import com.example.licenta.Client;
import com.example.licenta.Main;
import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private ComboBox roleComboBox;
    @FXML
    private AnchorPane loginPane, registerPane;
    @FXML
    private CheckBox loginPasswordCheckbox, registerPasswordCheckbox;
    @FXML
    private JFXButton loginButton, registerButton, confirmRegisterButton, backButton;
    @FXML
    private JFXPasswordField loginPasswordField, registerPasswordField, registerConfirmPasswordField;
    @FXML
    private JFXTextField loginUsername, loginPasswordText, registerEmail, registerUsername, registerPasswordText, registerConfirmPasswordText;

    @FXML
    private void minimize(ActionEvent event){
        Stage stage = (Stage) rootPane.getScene().getWindow();

        stage.setIconified(true);
    }

    @FXML
    private void exit(ActionEvent event) throws InterruptedException {
        Client.getInstance().disconnect();

        System.out.println("Exit 0");
        System.exit(0);
    }

    @FXML
    private void pressConfirmRegister(ActionEvent event){
        try{
            String response = "ok";
            String username = registerUsername.getText();
            String email = registerEmail.getText();
            String password = registerPasswordField.getText();
            String confirmPassword = registerConfirmPasswordField.getText();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");

            if(roleComboBox.getValue() == null){
                alert.setHeaderText("Please select a role");
                alert.showAndWait();
                return;
            }

            String role = roleComboBox.getValue().toString();

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pattern = Pattern.compile(emailRegex);

            if(username.equals(""))
                response = "Please fill the username box";

            if(email.equals(""))
                response = "Please fill the email box";

            if(password.equals(""))
                response = "Please fill the password box";

            if(confirmPassword.equals(""))
                response = "Please fill the confirm password box";

            if(!confirmPassword.equals(password))
                response = "The passwords don't match";

            if(password.length() < 6)
                response = "The password must be at least 6 characters long";

            if(!pattern.matcher(email).matches())
                response = "Invalid email";

            if(!response.equals("ok")){
                alert.setContentText(response);
                alert.showAndWait();
            }else{
                response = Client.getInstance().register(username, email, role, password);

                if(!response.contains("Accept")){
                    alert.setContentText(response);
                    alert.showAndWait();
                }else{
                    fadeOut();
                }
            }
        }catch(Exception e){
            System.out.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @FXML
    private void pressLogin(ActionEvent event){
        try{
            String response = "ok";
            String username = loginUsername.getText();
            String password = loginPasswordField.getText();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ALARM");

            if(username.equals(""))
                response = "Please fill the username box";

            if(password.equals(""))
                response = "Please fill the password box";

            if(password.length() < 6)
                response = "The password must be at least 6 characters long";

            if(!response.equals("ok")){
                alert.setContentText(response);
                alert.showAndWait();
            }else{
                response = Client.getInstance().login(username, password);

                if(!response.contains("Accept")){
                    alert.setContentText(response);
                    alert.showAndWait();
                }else{
                    fadeOut();
                }
            }
        }catch(Exception e){
            System.out.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @FXML
    private void pressRegister(ActionEvent event){
        this.registerPane.setVisible(true);
        this.loginPane.setVisible(false);
    }

    @FXML
    private void pressBack(ActionEvent event){
        this.registerPane.setVisible(false);
        this.loginPane.setVisible(true);
    }

    private void loadMainScreen(){
        try{
            Parent mainView = (BorderPane) FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("main-view.fxml")));
            Scene mainScene = new Scene(mainView);
            Stage currentStage = (Stage) rootPane.getScene().getWindow();

            mainScene.setOnMousePressed(Main::pressMouse);
            mainScene.setOnMouseDragged(mouseEvent -> Main.dragMouse(mouseEvent, currentStage));

            currentStage.setScene(mainScene);
        }catch(Exception e){
            System.out.println("Exception " + e);
            e.printStackTrace();
        }
    }

    private void fadeOut() throws InterruptedException {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        fadeTransition.setOnFinished((ActionEvent event) -> {
            loadMainScreen();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginPane.setVisible(true);
        this.registerPane.setVisible(false);

        this.loginPasswordField.managedProperty().bind(loginPasswordCheckbox.selectedProperty().not());
        this.loginPasswordField.visibleProperty().bind(loginPasswordCheckbox.selectedProperty().not());

        this.loginPasswordText.managedProperty().bind(loginPasswordCheckbox.selectedProperty());
        this.loginPasswordText.visibleProperty().bind(loginPasswordCheckbox.selectedProperty());

        this.loginPasswordText.textProperty().bindBidirectional(this.loginPasswordField.textProperty());

        this.registerPasswordField.managedProperty().bind(registerPasswordCheckbox.selectedProperty().not());
        this.registerPasswordField.visibleProperty().bind(registerPasswordCheckbox.selectedProperty().not());
        this.registerConfirmPasswordField.managedProperty().bind(registerPasswordCheckbox.selectedProperty().not());
        this.registerConfirmPasswordField.visibleProperty().bind(registerPasswordCheckbox.selectedProperty().not());

        this.registerPasswordText.managedProperty().bind(registerPasswordCheckbox.selectedProperty());
        this.registerPasswordText.visibleProperty().bind(registerPasswordCheckbox.selectedProperty());
        this.registerConfirmPasswordText.managedProperty().bind(registerPasswordCheckbox.selectedProperty());
        this.registerConfirmPasswordText.visibleProperty().bind(registerPasswordCheckbox.selectedProperty());

        this.registerPasswordText.textProperty().bindBidirectional(this.registerPasswordField.textProperty());
        this.registerConfirmPasswordText.textProperty().bindBidirectional(this.registerConfirmPasswordField.textProperty());

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        // for testing purposes
        this.loginUsername.setText("Client1");
        this.loginPasswordField.setText("123456");
        //this.pressLogin(new ActionEvent());
    }

    public LoginController(){

    }
}

package com.example.licenta.controllers;

import com.example.licenta.Address;
import com.example.licenta.Client;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AddressEditorController implements Initializable, AddAddressInterface {
    @FXML
    private TextField newAddressIpField, newAddressNameField;
    @FXML
    private ComboBox scanModeComboBox;
    @FXML
    private Label descriptionLabel;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private CheckBox activeScanCheckbox;
    private boolean hasShownWarning = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ButtonType okButtonType = dialogPane.getButtonTypes().get(1);

        activeScanCheckbox.setDisable(true);

        Button okButton = (Button) dialogPane.lookupButton(okButtonType);

        okButton.addEventFilter(ActionEvent.ACTION, event1 ->{
            if(newAddressIpField.getText().isBlank() ||
                    newAddressNameField.getText().isBlank()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Please complete all the fields");
                alert.showAndWait();

                event1.consume();
                return;
            }

            if(scanModeComboBox.getValue() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Please select a scan mode");
                alert.showAndWait();

                event1.consume();
            }
        });

        scanModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, odlValue, newValue) -> {
            if(newValue != null){
                String description = "";

                switch(newValue.toString()){
                    case "Automatic" -> {
                        description = "Your web application will be scanned automatically by our server";
                        activeScanCheckbox.setDisable(false);
                    }
                    case "Manual" -> {
                        description = "Your web application will be scanned by another user";
                        activeScanCheckbox.setDisable(true);
                    }
                    case "Hybrid" -> {
                        description = "Your web application will be scanned first by our server, then by another user";
                        activeScanCheckbox.setDisable(false);
                    }
                }



                descriptionLabel.setText(description);
            }
        });

        activeScanCheckbox.setOnAction(event ->{
            if(activeScanCheckbox.isSelected()){
                if(!this.hasShownWarning){
                    hasShownWarning = true;

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("You have checked active scan mode");
                    alert.setContentText("Active scanning an web app involves simulating a real attack on the target, thus taking it longer to finnish and giving a more in-depth look about the present vulnerabilities.");
                    alert.showAndWait();
                }
            }
        });
    }

    @Override
    public Address getAddress() {
        Address newAddress = new Address(newAddressIpField.getText(),
                newAddressNameField.getText(),
                scanModeComboBox.getValue().toString(),
                Client.getInstance().getUsername(),
                0,
                false,
                false,
                false);

        newAddress.setHasActiveScan(this.activeScanCheckbox.isSelected());

        return newAddress;
    }
}

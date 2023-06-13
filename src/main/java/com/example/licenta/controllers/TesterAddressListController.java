package com.example.licenta.controllers;

import com.example.licenta.Address;
import com.example.licenta.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

public class TesterAddressListController implements  AddAddressInterface, Initializable {
    @FXML
    private DialogPane dialogPane;
    @FXML
    private TableView<TableEntry> tableView;
    @FXML
    private TableColumn<TableEntry, String> addressColumn, usernameColumn;
    static private ObservableList<TableEntry> tableList;

    public class TableEntry{
        private String username;
        private Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAddressName(){return address.getName();}

        public TableEntry(String username, Address address) {
            this.username = username;
            this.address = address;
        }
    }

    @Override
    public Address getAddress() {
        return tableView.getSelectionModel().getSelectedItem().getAddress();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ButtonType okButtonType = dialogPane.getButtonTypes().get(1);

        Button okButton = (Button) dialogPane.lookupButton(okButtonType);

        okButton.addEventFilter(ActionEvent.ACTION, event1 ->{
            TableEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
            if(selectedEntry == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Please select an address");
                alert.showAndWait();

                event1.consume();
                return;
            }
        });

        Map<Address, String> addresses = Client.getInstance().getUntestedAddresses();
        ArrayList<TableEntry> tableEntries = new ArrayList<>();

        for(Map.Entry<Address, String> e : addresses.entrySet()){
            tableEntries.add(new TableEntry(e.getValue(), e.getKey()));
        }

        tableList = FXCollections.observableArrayList();
        tableList.addAll(tableEntries);

        addressColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, String>("addressName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, String>("username"));

        tableView.setItems(tableList);
    }
}

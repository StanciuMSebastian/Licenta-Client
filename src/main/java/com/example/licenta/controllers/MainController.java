package com.example.licenta.controllers;

import com.example.licenta.*;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.json.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private BorderPane rootPane;
    @FXML
    private Text welcomeText;
    @FXML
    private JFXComboBox scanModeComboBox;
    @FXML
    private Button scan_button, newAddressButton, refreshButton;
    @FXML
    private ListView<Address> addressListView;

    @FXML
    private VBox itemHolder;
    private TextField addressText, addressTextField, nameTextField, newAddressNameField, newAddressIpField;

    static private ObservableList<Address> addressList;

    UpdateAddressListener updateListener;

    static public int getDoneAddressList(){
        int counter = 0;

        for(Address a : addressList){
            if(a.isAutomaticScanComplete() || a.isManualScanComplete())
                counter++;
        }

        return  counter;
    }

    @FXML
    private void minimize(ActionEvent event){
        Stage stage = (Stage) rootPane.getScene().getWindow();

        stage.setIconified(true);
    }

    private boolean loadNewAddressDialog(String resourceName, String dialogTitle){
        try {
            Client.getInstance().switchBlockedStatus();
            // Load the dialog pane
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource(resourceName));

            DialogPane newAddressPane = fxmlLoader.load();
            AddAddressInterface controller = fxmlLoader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(newAddressPane);
            dialog.setTitle(dialogTitle);

            // Modify the OK button behavior

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent())
                if (result.get().getButtonData() == ButtonType.OK.getButtonData())
                {
                    Address newAddress = controller.getAddress();

                    String response = Client.getInstance().addAddress(newAddress);
                    System.out.println(response);

                    if(response.contains("Accepted")){
                        if(Client.getInstance().getRole().equals("Tester")){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("You have been assigned to this address successfully");
                            alert.showAndWait();
                        }else{
                            newAddress.setAddressId(Integer.parseInt(response.split(" ")[1]));

                            if(!newAddress.getScanType().equals("Manual")){
                                this.showProgressBar();
                                updateAddressList(false);
                            }else{
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("Your address has been added");
                                alert.showAndWait();
                            }
                        }
                        addressList.add(newAddress);
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error: " + response);
                        alert.showAndWait();
                    }
                }
        }catch(Exception e){
            System.out.println("Exception " + e);
            e.printStackTrace();
        }

        Client.getInstance().switchBlockedStatus();

        return false;
    }
    @FXML
    private void clickAddAddress(ActionEvent event){
       if(Client.getInstance().getRole().equals("Client")){
           loadNewAddressDialog("address-editor.fxml", "Add new address");
       }
       else if (Client.getInstance().getRole().equals("Tester")){
           loadNewAddressDialog("tester-address-list.fxml", "Select an address to scan");
       }
    }

    @FXML
    private void exit(ActionEvent event) throws InterruptedException {
        Client.getInstance().disconnect();

        System.out.println("Exit 0");
        System.exit(0);
    }
    @FXML
    private void logOut(ActionEvent event) throws IOException, InterruptedException {
        fadeOut();

        this.updateListener.interrupt();
        Client.getInstance().logOut();
    }

    private void fadeOut() throws InterruptedException {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        fadeTransition.setOnFinished((ActionEvent event) -> {
            loadLoginScene();
        });
    }

    private void loadLoginScene() {
        try{
            Parent loginView = FXMLLoader.load(Main.class.getResource("login-view.fxml"));
            Scene loginScene = new Scene(loginView);
            Stage currentStage = (Stage) rootPane.getScene().getWindow();

            loginScene.setOnMousePressed(Main::pressMouse);
            loginScene.setOnMouseDragged(mouseEvent -> Main.dragMouse(mouseEvent, currentStage));

            currentStage.setScene(loginScene);
        }catch(Exception e){
            System.out.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @FXML
    public void updateAddressList(ActionEvent event){
        updateAddressList(true);
    }
    static public void updateAddressList(boolean showAlert){

        Client.getInstance().initAddresses();

        int extendedAddressIndex = -1;

        for(Address a : addressList)
        {
            a.retract();
//            if(a.isExtended()){
//                extendedAddressIndex = addressList.indexOf(a);
//                break;
//            }
        }

        addressList.removeAll(addressList);
        addressList.addAll(Client.getInstance().getAddresses());

        if(extendedAddressIndex != -1)
            addressList.get(extendedAddressIndex).extend();

        if(showAlert){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Your addresses have been updated");
            alert.showAndWait();
        }
    }

    public void showProgressBar(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("ScanProgressBar.fxml"));

            Parent progressBarRoot = fxmlLoader.load();
            ScanProgressBarController controller = fxmlLoader.getController();

            Stage progressBarStage = new Stage();
            progressBarStage.initModality(Modality.APPLICATION_MODAL);
            progressBarStage.initStyle(StageStyle.UNDECORATED);
            progressBarStage.setScene(new Scene(progressBarRoot));

            Stage mainWindowStage = (Stage) rootPane.getScene().getWindow();
            rootPane.setDisable(true);

            Thread secondaryThread = new Thread(controller::begin);
            secondaryThread.start();
            progressBarStage.showAndWait();
            //Client.getInstance().showScanProgress(controller);

            rootPane.setDisable(false);

        }catch (Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateListener = new UpdateAddressListener();
        updateListener.start();

        addressList = FXCollections.observableArrayList();

        addressListView.setCellFactory(param -> new ItemCell());

        addressListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Address selectedItem = addressListView.getSelectionModel().getSelectedItem();

                for(Address a : addressListView.getItems()){
                    if(a.equals(selectedItem))
                        a.extend();
                    else
                        a.retract();
                }

                addressListView.refresh();
            }
        });

        welcomeText.setText("Welcome\n" + Client.getInstance().getUsername());
        addressListView.setItems(addressList);
        addressList.addAll(Client.getInstance().getAddresses());
        addressListView.refresh();

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        rootPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Scene is available, perform your logic here
                String cssPath = Main.class.getResource("styles.css").toExternalForm();;
                newValue.getStylesheets().add(cssPath);
            }
        });
    }

    public void handleMain_button() throws IOException, InterruptedException, JSONException {
        String address = addressText.getText();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        //Stage window = (Stage)scan_button.getScene().getWindow();
        //window.setScene(new Scene(fxmlLoader.load(), 500, 500));

        //Process p = Runtime.getRuntime().exec("zap.sh -cmd -quickout ~/Documents/out.json -quickurl " + address);
        //p.waitFor();


        File jsonFile = new File("/home/stanciul420/Documents/out.json");
        String jsonData = "";

        Scanner myReader = new Scanner(jsonFile);
        while (myReader.hasNextLine())
            jsonData = jsonData.concat(myReader.nextLine());

        JSONObject obj = new JSONObject(jsonData);
        System.out.println(obj.getString("@generated"));
        JSONArray site = obj.getJSONArray("site");
        JSONArray alerts = site.getJSONObject(0).getJSONArray("alerts");

        for(int i = 0; i < alerts.length(); i++){
            String name = alerts.getJSONObject(i).getString("name");
            int risk = alerts.getJSONObject(i).getInt("riskcode");
            System.out.println("\nVulnerability name: " + name + "\nRisk: " + risk);
        }
    }

    public MainController(){

    }


    private class ItemCell extends ListCell<Address> {
        private Address address;

        public ItemCell() {
            super();
        }

        @Override
        protected void updateItem(Address item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null && !empty) {
                this.address = item;
                setGraphic(item.getvBox());
            } else {
                setGraphic(null);
            }
        }
    }
}

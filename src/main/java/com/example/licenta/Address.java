package com.example.licenta;

import com.example.licenta.controllers.AddAddressInterface;
import com.example.licenta.controllers.ChatBoxController;
import com.example.licenta.controllers.RatingMessagePromptController;
import com.example.licenta.controllers.UserInfoController;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Address {
    private String ip, name, scanType, testerUsername, clientUsername;
    private int addressId;
    private double rating;
    private boolean areScansComplete, isAutomaticScanComplete, isManualScanComplete, isExtended, hasActiveScan;

    private JFXButton deleteButton, downloadButton, uploadButton, userInfoButton, startChatButton;
    private Label addressName, addressFeaturesLabel;
    private VBox vBox;
    private Rating testerRating;
    private VBox detailsBox;
    private HBox hBox;
    private Line separator;


    public double getRating(){
        return this.rating;
    }
    public void setRating(double rating){
        this.rating = rating;
    }

    public boolean areScansComplete(){
        return this.areScansComplete;
    }

    public boolean isAutomaticScanComplete(){
        return this.isAutomaticScanComplete;
    }

    public boolean isManualScanComplete(){
        return this.isManualScanComplete;
    }

    public boolean isHasActiveScan() {
        return hasActiveScan;
    }

    public void setHasActiveScan(boolean hasActiveScan) {
        this.hasActiveScan = hasActiveScan;
    }

    public void doneAutomaticScan(){
        this.isAutomaticScanComplete = true;

        if(this.scanType.equals("Automatic") || this.isManualScanComplete)
            this.areScansComplete = true;
    }


    public void doneManualScan(){
        this.isManualScanComplete = true;

        if(this.scanType.equals("Manual") || this.isAutomaticScanComplete)
            this.areScansComplete = true;
    }


    public VBox getvBox() {
        return vBox;
    }

    public HBox gethBox() {
        return hBox;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public String getClientUsername(){
        return this.clientUsername;
    }

    public int getAddressId() { return this.addressId;}
    public void setAddressId(int id){this.addressId = id;}
    public String getScanType(){return this.scanType;}

    public String getTesterUsername(){return this.testerUsername;}

    public boolean isExtended(){
        return this.isExtended;
    }
    public void setTesterUsername(String username){
        this.testerUsername = username;

        if(!username.isBlank()){
            this.startChatButton.setDisable(false);
            this.userInfoButton.setDisable(false);

            this.testerRating = initRating();
        }
    }

    private Rating initRating(){
        Rating newRating = new Rating(5, 5);
        newRating.setUpdateOnHover(true);

        newRating.setOnMouseClicked(event ->{
            this.rating = newRating.getRating();
            String message = "";
            try{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Main.class.getResource("rating-message-prompt.fxml"));

                DialogPane newAddressPane = fxmlLoader.load();
                RatingMessagePromptController controller = fxmlLoader.getController();

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(newAddressPane);

                Optional<ButtonType> result = dialog.showAndWait();

                if(result.isPresent() && result.get().getButtonData() == ButtonType.OK.getButtonData()){
                    message = controller.getMessage();

                    Client.getInstance().giveRating(newRating.getRating(), this.testerUsername, message);

                    newRating.setDisable(true);
                    newRating.setRating(this.rating);
                }
            }catch(Exception e ){
                System.out.println("Exception: " + e);
                e.printStackTrace();
            }
        });

        if(this.rating != -1){
            newRating.setDisable(true);
            newRating.setRating(this.rating);
        }

        if(!this.isManualScanComplete)
            newRating.setDisable(false);


        return newRating;
    }

    public Address(String ip, String name, String scanType, String clientUsername,
                   int addressId, boolean areScansComplete, boolean isAutomaticScanComplete, boolean isManualScanComplete) {
        this.hasActiveScan = false;
        this.testerUsername = "";
        this.areScansComplete = areScansComplete;
        this.isAutomaticScanComplete = isAutomaticScanComplete;
        this.isManualScanComplete = isManualScanComplete;
        this.ip = ip;
        this.name = name;
        this.scanType = scanType;
        this.clientUsername = clientUsername;
        this.addressId = addressId;
        this.downloadButton = new JFXButton("Download");
        this.deleteButton = new JFXButton("Delete");
        this.userInfoButton = new JFXButton("User Info");
        this.startChatButton = new JFXButton("Start chat");
        this.addressName = new Label(name);
        this.addressFeaturesLabel = new Label();

        this.addressName.setFont(new Font(20));

        this.addressFeaturesLabel.setTextFill(Color.BLACK);

        separator = new Line(); // Use Separator or Line element
        separator.getStyleClass().add("custom-line");

        FontAwesomeIconView downloadIcon = new FontAwesomeIconView();
        FontAwesomeIconView deleteIcon = new FontAwesomeIconView();

        downloadIcon.setGlyphName("DOWNLOAD");
        deleteIcon.setGlyphName("TRASH");

        downloadIcon.setGlyphSize(20);
        deleteIcon.setGlyphSize(20);

        downloadButton.setGraphic(downloadIcon);
        deleteButton.setGraphic(deleteIcon);

        if(!(isAutomaticScanComplete || isManualScanComplete))
            downloadButton.setDisable(true);

        downloadButton.setOnAction(event ->{
            String downloadChoice;

            if(this.isAutomaticScanComplete && this.isManualScanComplete){
                downloadChoice = Main.showHybridChoiceDialog();
            }else if(this.isAutomaticScanComplete){
                downloadChoice = "Automatic";
            }else{
                downloadChoice = "Manual";
            }

            if(downloadChoice.equals("Cancel"))
                return;


            Client.getInstance().download(this, downloadChoice);
        });

        startChatButton.setOnAction(this::startChat);

        deleteButton.setOnAction(event ->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Remove Address");
            alert.setHeaderText("Delete Address " + this.name + "?");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                this.retract();
                Client.getInstance().deleteAddress(this);
            }
        });

        this.vBox = new VBox(this.addressName);


        if(Client.getInstance().getRole().equals("Tester")){
            this.uploadButton = new JFXButton("Upload");
            FontAwesomeIconView uploadIcon = new FontAwesomeIconView();

            if(this.isManualScanComplete)
                uploadButton.setDisable(true);

            uploadIcon.setGlyphName("UPLOAD");
            uploadIcon.setGlyphSize(20);


            uploadButton.setTooltip(new Tooltip("Upload"));
            uploadButton.setGraphic(uploadIcon);
            uploadButton.setOnAction(event->{
                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Upload Vulnerability Report");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                //fileChooser.setInitialFileName("Report.html");
                fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

                File file = fileChooser.showOpenDialog(new Stage());

                if(file != null)
                    Client.getInstance().upload(this, file);
            });
        }

        if(Client.getInstance().getRole().equals("Client") && !this.getScanType().equals("Automatic")){


            FontAwesomeIconView infoIcon = new FontAwesomeIconView();
            infoIcon.setGlyphName("INFO");
            infoIcon.setGlyphSize(20);

            if(this.testerUsername.isBlank()){
                this.userInfoButton.setDisable(true);
                this.startChatButton.setDisable(true);
            }else{
                testerRating = initRating();
            }

            userInfoButton.setGraphic(infoIcon);

            userInfoButton.setOnAction(event ->{
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(Main.class.getResource("user_info.fxml"));

                    DialogPane newAddressPane = fxmlLoader.load();
                    UserInfoController controller = fxmlLoader.getController();

                    String userInfo = Client.getInstance().getUserInfo(this.testerUsername);

                    if(!userInfo.contains("Error")){
                        controller.setMessage(userInfo);

                        Dialog<ButtonType> dialog = new Dialog<>();
                        dialog.setDialogPane(newAddressPane);
                        dialog.setTitle("User information");

                        dialog.showAndWait();
                    }
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }
            });



        }

        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5));

        //detailsBox.setSpacing(5);
        //detailsBox.setPadding(new Insets(5));
    }

    private void startChat(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("chat-box.fxml"));

            AnchorPane chatRootPane = fxmlLoader.load();
            ChatBoxController controller = fxmlLoader.getController();

            controller.setAddressId(this.addressId);
            if(Client.getInstance().getUsername().equals(this.testerUsername))
                controller.setReceiverUsername(this.clientUsername);
            else
                controller.setReceiverUsername(this.testerUsername);

            Client.getInstance().getMessages(controller);
            ChatUpdateListener chatListener = new ChatUpdateListener(controller);
            chatListener.start();

            Scene chatScene = new Scene(chatRootPane);

            Parent rootNode = this.vBox.getParent();

            while(rootNode.getParent() != null)
                rootNode = rootNode.getParent();

            rootNode.setDisable(true);

            Stage chatStage = new Stage();
            chatStage.setScene(chatScene);
            chatStage.showAndWait();

            rootNode.setDisable(false);
        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public String toString(){
        return "Name: " + this.name + "\nIP: " + this.ip + "\nScantype: " + this.scanType + "\nClientId: ";
    }

    private void rewriteAddressFeatures(){
        String addressFeaturesString = "\nAddress IP: " + this.ip + "\nScan Type: " + this.scanType;

        if(Client.getInstance().getRole().equals("Client")){
            if(!this.testerUsername.equals("")){
                addressFeaturesString += "\nAssigend to tester: " + this.testerUsername;
            }else if(!this.scanType.equals("Automatic")){
                addressFeaturesString += "\nNo tester assigned";
            }
        }else if(Client.getInstance().getRole().equals("Tester")){
            addressFeaturesString += "\nClient name: " + this.clientUsername;
        }

        if(this.areScansComplete){
            addressFeaturesString += "\nScanned\n\n";
        }else{
            addressFeaturesString += "\nNot Scanned\n\n";
        }

        addressFeaturesLabel.setText(addressFeaturesString);
    }

    public void extend(){
        if(!this.isExtended){
            rewriteAddressFeatures();
            //vBox.getChildren().addAll(hBox);
            this.addressName.setTextFill(Color.WHITE);
            vBox.getChildren().add(this.addressFeaturesLabel);

            if(Client.getInstance().getRole().equals("Tester"))
                vBox.getChildren().add(this.uploadButton);

            if(Client.getInstance().getRole().equals("Client") || this.scanType.equals("Hybrid"))
                vBox.getChildren().add(this.downloadButton);

            vBox.getChildren().add(this.deleteButton);

            if(Client.getInstance().getRole().equals("Client") && !this.getScanType().equals("Automatic")){
                if(this.testerRating != null)
                    vBox.getChildren().add(testerRating);

                vBox.getChildren().add(this.userInfoButton);
                vBox.getChildren().add(this.startChatButton);
            }

            if(Client.getInstance().getRole().equals("Tester")){
                vBox.getChildren().add(this.startChatButton);
            }

            this.isExtended = true;
        }
    }

    public void retract(){
        if(this.isExtended){
            //vBox.getChildren().remove(hBox);
            this.addressName.setTextFill(Color.BLACK);
            //vBox.getChildren().removeAll(this.addressFeatures, this.downloadButton, this.deleteButton);
            vBox.getChildren().removeAll(vBox.getChildren());
            vBox.getChildren().add(this.addressName);

            this.isExtended = false;
        }
    }


}

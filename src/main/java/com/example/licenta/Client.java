package com.example.licenta;

import com.example.licenta.controllers.ChatBoxController;
import com.example.licenta.controllers.MainController;
import com.example.licenta.controllers.ScanProgressBarController;
import com.example.licenta.controllers.ScanProgressWindow;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {

    private Client() {addresses = new Vector<Address>();}
    private static Client instance;
    private int ID;
    private String username;
    private String role;
    private DataOutputStream output;
    private DataInputStream input;
    private boolean isConnected;
    private Vector<Address> addresses;

    boolean isBlocked = false;

    private ChatBoxController chatController;

    public void destroyChatController(){
        this.chatController = null;
    }

    public ChatBoxController getChatController() {
        return chatController;
    }

    public boolean isBlocked(){
        return this.isBlocked;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public DataInputStream getInput() {
        return input;
    }

    public String getRole(){return this.role;}

    public String getUsername(){
        return this.username;
    }
    public int getID(){return this.ID;}

    public Vector<Address> getAddresses(){
        return this.addresses;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public void switchBlockedStatus(){
        this.isBlocked = !this.isBlocked;
    }

    public void getMessages(ChatBoxController controller){
        try{
            this.isBlocked = true;

            this.chatController = controller;
            controller.resetCounter() ;
            output.writeUTF("Get messages");
            output.writeInt(this.ID);
            output.writeInt(controller.getAddressId());
            output.writeUTF(controller.getReceiverUsername());

            int messageCount = input.readInt();

            for(int i = 0; i < messageCount; i++){
                String senderUsername = input.readUTF();
                String receiverUsername = input.readUTF();
                String messageText = input.readUTF();
                long longTimeStamp = input.readLong();

                Date timestamp = new Date(longTimeStamp);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
                String formattedDate = dateFormat.format(timestamp);

                if(senderUsername.equals(this.username))
                    Platform.runLater(() ->{
                        controller.sentMessage(messageText);
                    });
                else if(receiverUsername.equals(this.username))
                    Platform.runLater(() ->{
                        controller.receiveMessage(messageText);
                    });
            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        this.isBlocked = false;
    }

    public void sendMessage(String message, String receiverUsername, int addressId){
        try{
            this.isBlocked = true;

            output.writeUTF("Send Message");
            output.writeUTF(this.username);
            output.writeUTF(receiverUsername);
            output.writeUTF(message);
            output.writeInt(addressId);

            String response = input.readUTF();

            if(!response.equals("ok")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(response);
                alert.showAndWait();
            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        this.isBlocked = false;
    }

    public boolean checkForUpdates(){
        try{

            int addressCount = MainController.getDoneAddressList();

            output.writeUTF("Address Update");
            output.writeInt(addressCount);

            String response = input.readUTF();

            // Return true if you need an update, false if not

            return !response.equals("ok");

        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        return false;
    }

    public void giveRating(double rating, String testerUsername, String message){
        try{
            output.writeUTF("Give User Rating");
            output.writeUTF(testerUsername);
            output.writeDouble(rating);
            output.writeUTF(message);

            String response = input.readUTF();

            if(!response.equals("ok")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setHeaderText(response);
                alert.setTitle("ERROR");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.OK);
                alert.setContentText("Your rating has been sent");
                alert.showAndWait();
            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    public String getUserInfo(String userName){
        try{
            output.writeUTF("User info");
            output.writeUTF(userName);
            output.writeInt(this.getID());

            String response = input.readUTF();

            if(response.contains("Error")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setHeaderText(response);
                alert.setTitle("ERROR");
                alert.showAndWait();
            }

            return response;
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return "";
    }

    public Map<Address, String> getUntestedAddresses(){
        Map<Address, String> retMap = new HashMap<>();

        try{
            output.writeUTF("Get Untested Addresses");

            boolean isNotDone = input.readBoolean();

            while(isNotDone){
                String username = input.readUTF();
                String addressName = input.readUTF();
                String addressIp = input.readUTF();
                String scanType = input.readUTF();
                String clientUsername = input.readUTF();
                int clientId = input.readInt();
                boolean areScansComplete = input.readBoolean();
                boolean isAutomaticScanComplete = input.readBoolean();
                boolean isManualScanComplete = input.readBoolean();

                retMap.put(new Address(addressIp, addressName, scanType, clientUsername, clientId,
                                areScansComplete, isAutomaticScanComplete, isManualScanComplete),
                        username);

                isNotDone = input.readBoolean();
            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return retMap;
    }

    public void initAddresses(){
        try{
            this.addresses.removeAll(addresses);

            output.writeUTF("Init Addresses");
            output.writeUTF(this.role);
            output.writeInt(this.ID);

            boolean isNotDone = input.readBoolean();

            while(isNotDone){
                //System.out.println(input.available());
                //System.out.println(Arrays.toString(input.readAllBytes()));

                String addressName = input.readUTF();
                String addressIp = input.readUTF();
                String scanType = input.readUTF();
                String clientUsername = input.readUTF();
                int addressId = input.readInt();
                double rating = input.readDouble();
                boolean areScansComplete = input.readBoolean();
                boolean isAutomaticScanComplete = input.readBoolean();
                boolean isManualScanComplete = input.readBoolean();

                Address newAddress = new Address(addressIp, addressName, scanType, clientUsername, addressId,
                        areScansComplete, isAutomaticScanComplete, isManualScanComplete);

                newAddress.setRating(rating);

                String testerUsername = input.readUTF();
                newAddress.setTesterUsername(testerUsername);

                addresses.add(newAddress);

                isNotDone = input.readBoolean();
            }

        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    public static Client getInstance(){
        if(instance == null)
            instance = new Client();

        return instance;
    }
    public void disconnect(){
        try{
            output.writeUTF("exit");
            this.isConnected = false;
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    public void logOut(){
        this.ID = -1;
        this.username = "";
        this.role = "";
        this.addresses.removeAll(this.addresses);

        this.disconnect();
    }

    public String login(String username, String password){
        try{
            String response;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            if(!isConnected){
                if(!connect()){
                    response = "Could not connect to the server";
                    System.out.println(response);
                    return response;
                }
            }

            output.writeUTF("login");
            output.writeUTF(username);
            output.writeInt(encodedPassword.length);
            output.write(encodedPassword, 0, encodedPassword.length);

            response = input.readUTF();

            if(response.contains("Accept")){
                this.username = username;
                this.ID = Integer.parseInt(response.split(" ")[1]);
                this.role = response.split(" ")[2];

                initAddresses();
            }

            return response;
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return "Something went wrong";
    }

    public String register(String username, String email, String role, String password){
        try{
            String response;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            if(!isConnected){
                if(!connect()){
                    response = "Could not connect to the server";
                    System.out.println(response);
                    return response;
                }
            }

            output.writeUTF("register");
            output.writeUTF(username);
            output.writeUTF(email);
            output.writeUTF(role);
            output.writeInt(encodedPassword.length);
            output.write(encodedPassword, 0, encodedPassword.length);

            response = input.readUTF();

            if(response.contains("Accept")){
                this.username = username;
                this.ID = Integer.parseInt(response.split(" ")[1]);
                this.role = role;

                // initAddresses();
            }

            return response;
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return "Something went wrong";
    }

    public String addAddress(Address newAddress){
        try{
            input.skipBytes(input.available());
            output.writeUTF("Add address");

            if(this.role.equals("Client")){
                output.writeUTF(newAddress.getName());
                output.writeUTF(newAddress.getIp());
                output.writeUTF(newAddress.getScanType());
                output.writeBoolean(newAddress.isHasActiveScan());
            }else if(this.role.equals("Tester")){
                output.writeInt(newAddress.getAddressId());
            }


            return input.readUTF();
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        return null;
    }

    public String deleteAddress(Address address){
        try{
            output.flush();

            this.isBlocked =  true;

            output.writeUTF("Delete address");
            output.writeInt(this.ID);
            output.writeInt(address.getAddressId());

            String response = input.readUTF();

            if(response.equals("ok")){
                this.addresses.remove(address);
                MainController.updateAddressList(false);
            }

            this.isBlocked =  false;
            return response;

        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        return null;
    }

    public void download(Address address, String reportType){
        try{
            this.isBlocked =  true;

            output.writeUTF("Download report");
            output.flush();

            output.writeInt(this.ID);
            output.writeInt(address.getAddressId());
            output.writeUTF(reportType);

            String response = input.readUTF();

            if(response.equals("ok")){


                String filename = input.readUTF();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Vulnerability Report");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.setInitialFileName(filename);

                File file = fileChooser.showSaveDialog(new Stage());

                if(file == null)
                    return;

                file.createNewFile();

                FileOutputStream fOut = new FileOutputStream(file);

                Long fileLength = input.readLong();
                byte[] buffer = new byte[4096];
                int bytesRead = 0;

                while(fileLength > 0 &&(bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, fileLength))) != -1){
                    fOut.write(buffer, 0, bytesRead);
                    fileLength -= bytesRead;
                }

                fOut.close();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your address was downloaded with success!", ButtonType.OK);
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setHeaderText(response);
                alert.setTitle("ERROR");
                alert.showAndWait();
            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        this.isBlocked =  false;
    }

    public String upload(Address address, File file){
        try{
            this.isBlocked = true;

            FileInputStream fStream = new FileInputStream(file);


            output.writeUTF("File Upload");
            output.writeUTF(file.getName());
            output.writeInt(address.getAddressId());
            output.writeInt(this.ID);
            output.writeLong(file.length());

            int bytes = 0;
            byte[] buffer = new byte[4096];

            String response = input.readUTF();
            if(!response.equals("ok")){
                Alert alert = new Alert(Alert.AlertType.ERROR, response, ButtonType.OK);
                alert.showAndWait();

                this.isBlocked = false;
                return response;
            }

            while((bytes = fStream.read(buffer)) != -1){
                output.write(buffer, 0, bytes);
                output.flush();
            }

            fStream.close();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your report was uploaded with success!", ButtonType.OK);
            alert.showAndWait();

            MainController.updateAddressList(false);

        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }


        this.isBlocked = false;
        return null;
    }

    public boolean connect(){
        try{
            String hostName = "localhost";
            int port = 2000;
            Socket s = new Socket(hostName, port);

            this.output = new DataOutputStream(s.getOutputStream());
            this.input = new DataInputStream(s.getInputStream());

            this.isConnected = true;
        }catch(Exception e){
            this.isConnected = false;
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return isConnected;
    }

    public boolean checkForChatUpdates(ChatBoxController controller) {
        try{
            output.writeUTF("Check for chat updates");
            output.writeInt(chatController.getMessageNumber());
            output.writeInt(chatController.getAddressId());
            output.writeUTF(chatController.getReceiverUsername());
            output.writeUTF(this.username);

            String response = input.readUTF();

            if(response.equals("Update needed"))
                return true;
        }catch(Exception e){
            this.isConnected = false;
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }


        return false;
    }
}

package com.example.licenta;

import com.example.licenta.controllers.MainController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class UpdateAddressListener extends Thread{

    @Override
    public void run() {
        super.run();

        try{
            Thread.sleep(10000 );
            while(true){
                if(Client.getInstance().checkForUpdates()){
                    Platform.runLater(MainController::updateAddressList);
                }

                Thread.sleep(10000 );
            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    public UpdateAddressListener(){
    }
}

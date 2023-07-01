package com.example.licenta;

import com.example.licenta.controllers.ChatBoxController;
import javafx.application.Platform;

public class ChatUpdateListener extends Thread{
    private ChatBoxController controller;


    @Override
    public void run() {
        super.run();

        try{
            while(controller != null){
                Thread.sleep(1000);
                if(!Client.getInstance().isBlocked && Client.getInstance().checkForChatUpdates(controller)){
                    Platform.runLater(()->{
                        controller.getMessagePane().getChildren().removeAll(controller.getMessagePane().getChildren());
                    });

                    Client.getInstance().getMessages(controller);
                }

            }
        }catch(Exception e){
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    public ChatUpdateListener(ChatBoxController controller){
        this.controller = controller;
    }
}

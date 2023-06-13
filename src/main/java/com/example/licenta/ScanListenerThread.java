package com.example.licenta;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ScanListenerThread extends Thread{
    private boolean isRunning;
    private DataOutputStream output;
    private DataInputStream input;

    public void run(){
        
    }

    public void stopThread(){
        isRunning = false;
    }

    public ScanListenerThread(DataOutputStream output, DataInputStream input) {
        this.isRunning = true;
        this.output = output;
        this.input = input;
    }
}

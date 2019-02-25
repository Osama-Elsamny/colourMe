package com.colourMe.networking.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ClientHandler extends Thread implements Runnable{
    // TODO: Replace classLog with java logging utilities
    private volatile boolean finished = false;
    private final int IO_DELAY = 20; // TODO: Optimize Delay
    private BufferedReader in;
    private PrintWriter out;
    private Socket connectionSocket; // Do not remove despite IntelliJs suggestions


    // TODO: Move functions to Utilities/Helper Class
    private void classLog(String message){
        String LOG_FORMAT = "ClientHandler (%s) : %s ";
        System.out.println(String.format(LOG_FORMAT, super.getName(), message));
    }

    private void handleException(Exception ex, String startMessage){
        classLog(startMessage + ex.getMessage());
        ex.printStackTrace(System.out);
    }

    ClientHandler(Socket socket){
        try {
            this.connectionSocket = socket;
            this.in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(connectionSocket.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch(Exception ex) {
            handleException(ex, "Exception while constructing ClientHandler : ");
        }
    }

    private void finish() {
        this.finished = true;
    }

    private String readMessage(){
        String message = "";

        try {
            classLog("Waiting for client message");
            message = in.readLine();
            Thread.sleep(IO_DELAY);
            classLog("Received '" + message + "'from Client");
            message = message == null ? "exit" : message;

        } catch (Exception ex) {
            handleException(ex, "Exception while reading message from InputStream: ");
        }

        return message;
    }

    private boolean sendMessage(String message){
        try {
            this.out.println(message);
            classLog("Sent '" + message + "' to Client");
            Thread.sleep(IO_DELAY);
            return true;

        } catch(Exception ex) {
            handleException(ex, "Exception while sending message to Client");
            return false;
        }

    }

    // TODO: Finalize message format and re-implement this method
    private boolean processMessage(String message) {
        // TODO: create message router for handling different messages
        // Current implementation
        // Have the server take input from the client and echo it back
        // This should be placed in a loop that listens for a terminator text e.g. exit
        try {
            sendMessage("Server: " + message);

            if (message.toLowerCase().trim().equals("exit") || super.isInterrupted()) {
                classLog("Connection is committing suicide ...");
                sendMessage("Connection is committing suicide ... ");
                this.finish();
            }
            return true;
        } catch(Exception ex) {
            handleException(ex, "An exception occurred while processing a message");
            return false;
        }
    }

    @Override
    public void run(){
        sendMessage("Connected to Game Server! Enter exit to end connection.");
        sendMessage("Test communication by sending messages through client program");

        while (!this.finished) {
            String message = readMessage();
            processMessage(message);
        }

        classLog("Thread is terminating");
    }
}

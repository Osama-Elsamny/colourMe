package com.colourMe.networking.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Server {
    private LinkedList<Thread> clientThreads;

    public static void main(String[] args) {
        new Server().connectToServer();
    }


    public void connectToServer() {
        //Try connect to the server on an unused port eg 8000. A successful connection will return a socket
        try (ServerSocket serverSocket = new ServerSocket(8000)) {


            String line;

            System.out.println("Game server has started, get ready for some serious action.");
            while(true) {
                Socket connectionSocket = serverSocket.accept();

                // System.out.println("Accepted new connection.");
                ClientHandler handler = new ClientHandler(connectionSocket);
                handler.start();
            }

        } catch (IOException e) {
            System.out.println("Oh no, an error occurred :(\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}

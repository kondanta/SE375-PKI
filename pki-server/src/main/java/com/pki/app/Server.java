package com.pki.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server {
    private ServerSocket serverSocket;
    private boolean isServerOn = true;
    private Calendar timestamp = Calendar.getInstance();
    private SimpleDateFormat formatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

    public Server() {
        try {
            serverSocket = new ServerSocket(4400);
        } catch (IOException e) {
            System.out.println("System could not create server on port 4400. Quiting...");
            System.exit(-1);
        }
    }

    public void start() {
        while (this.isServerOn) {
            try {
                // Accepts incoming connections
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.run();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



package com.pki.app;

import com.pki.crypto.Sign;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler extends Thread {
    private Socket client;
    private boolean isRunning = true;
    private boolean loop = true;
    BufferedReader in;
    PrintWriter out;

    ClientHandler(Socket s) {
        super("Connection");
        client = s;
    }

    @Override
    public void run() {

        System.out.println("Client's addr: " + client.getInetAddress().getHostAddress());

        try {
            // Setting up the channels
            in =  new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            String textFromClient;

            while (loop) {
//                textFromClient = in.readLine();
                while ((textFromClient = in.readLine()) != null) {
                    if (textFromClient.equalsIgnoreCase("quit")) {
                        out.close();
                        in.close();
                        loop = false;
                        break;
                    } else if(textFromClient.split("-")[0].equalsIgnoreCase("get")){
                        // TODO: db query here
                        String email = textFromClient.split("-")[1];
                        out.println("Looking for the user!");
                        out.flush();
                        out.println("Cannot find the user!");
                        out.flush();
                        out.println("exit");
                    }
                    else if (textFromClient.equalsIgnoreCase("end")) {
                        isRunning = false;
                    } else if (!client.isConnected()) {
                        out.close();
                        in.close();
                        break;
                    } else {
                        out.flush();
                    }
                }
                out.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void signIncomingUserData(String incomingData) {
        // TODO: change createFile to database query.
        new Sign(incomingData, "KeyPair/privateKey").createFile("Signed/SignedData.txt");
    }

    public boolean isServerClosed() {
        return isRunning;
    }
}
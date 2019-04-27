package com.pki.app;

import com.pki.crypto.Sign;

import java.io.*;
import java.net.Socket;

class ClientHandler extends Thread {
    private Socket client;
    private boolean isRunning = true;

    ClientHandler(Socket s) {
        client = s;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        System.out.println("Client's addr: " + client.getInetAddress().getHostAddress());

        try {
            // Setting up the channels
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

            while (isRunning) {
                // TODO: Certification goes here
                //String test = in.readLine();
                System.out.println("Test");
                signIncomingUserData("test");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                client.close();
                System.out.println("Closing the thread.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void signIncomingUserData(String incomingData) {
        // TODO: change createFile to database query.
        new Sign(incomingData, "KeyPair/privateKey").createFile("Signed/SignedData.txt");
    }
}
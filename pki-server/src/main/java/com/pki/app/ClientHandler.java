package com.pki.app;

import com.pki.crypto.Sign;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;

// must implement Serializable in order to be sent
class Message implements Serializable{
    private final String text;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

class ClientHandler extends Thread {
    private Socket client;
    private boolean isRunning = true;
    private boolean loop = true;
    ObjectInputStream in;
    ObjectOutputStream out;

    ClientHandler(Socket s) {
        super("Connection");
        client = s;
    }

    @Override
    public void run() {

        System.out.println("Client's addr: " + client.getInetAddress().getHostAddress());

        try {
            // Setting up the channels
            in =  new ObjectInputStream(client.getInputStream());

            //out = new ObjectOutputStream(client.getOutputStream());
            //obIn = new ObjectInputStream(client.getInputStream());
            //String textFromClient;

            List<Message> object = (List<Message>) in.readObject();
            System.out.println("Received [" + object.size() + "] messages from: " + client);
            // print out the text of every message
            System.out.println("All messages:");
            object.forEach((msg)-> System.out.println(msg.getText()));

            PublicKey obj = (PublicKey) in.readObject();
            System.out.println(obj);

            System.out.println("Closing sockets");
            client.close();
        } catch (IOException | ClassNotFoundException e) {
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
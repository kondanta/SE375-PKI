package com.pki.app;

import com.pki.crypto.Certification;
import com.pki.database.Sqlite;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
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

            List<byte[]> a = new ArrayList<>();
            object.forEach((data) -> a.add(data.getText().getBytes()));

            a.add(obj.getEncoded());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( a.get(0) );
            outputStream.write(a.get(1));
            outputStream.write(a.get(2));

            byte[] arr = outputStream.toByteArray();

            new Certification().signIncomingUserData(arr, "cert");
            new Sqlite();

            System.out.println("Closing sockets");
            client.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    public boolean isServerClosed() {
        return isRunning;
    }
}
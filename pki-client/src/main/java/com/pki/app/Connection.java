package com.pki.app;

import com.pki.crypto.AsymmetricCryptography;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// must implement Serializable in order to be sent
class Message implements Serializable {
    private final String text;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

public class Connection {

    public void register() throws Exception {
        // need host and port, we want to connect to the ServerSocket at port 7777
        Socket socket = new Socket("172.16.1.13", 4400);
        System.out.println("Connected!");

        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create an object output stream from the output stream so we can send an
        // object through it
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // make a bunch of messages to send.
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Michael Scott"));
        messages.add(new Message("theWorldsBestBoss@DunderMifflin.com"));

        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(messages);
        objectOutputStream.flush();
        objectOutputStream.writeObject(new AsymmetricCryptography().getPublic("KeyPair/publicKey"));

        System.out.println("Closing socket and terminating program.");
        new Sqlite().db();
        socket.close();
    }
}

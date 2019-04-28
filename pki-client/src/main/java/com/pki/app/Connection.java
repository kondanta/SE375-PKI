package com.pki.app;
import com.pki.crypto.AsymmetricCryptography;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;

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
public class Connection {

    public void register() throws Exception {
        // need host and port, we want to connect to the ServerSocket at port 7777
        Socket socket = new Socket("172.16.1.13", 4400);
        System.out.println("Connected!");

        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create an object output stream from the output stream so we can send an object through it
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // make a bunch of messages to send.
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Hello from the other side!"));
        messages.add(new Message("How are you doing?"));
        messages.add(new Message("What time is it?"));
        messages.add(new Message("Hi hi hi hi."));



        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(messages);
        objectOutputStream.flush();
        objectOutputStream.writeObject(new AsymmetricCryptography().getPublic("KeyPair/publicKey"));

        System.out.println("Closing socket and terminating program.");
        socket.close();
    }
}



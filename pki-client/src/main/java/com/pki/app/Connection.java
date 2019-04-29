package com.pki.app;

import com.pki.crypto.AsymmetricCryptography;
import com.pki.crypto.SignVerify;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
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
    private Socket socket;

    Connection() {
        try {
            socket = new Socket("localhost", 4400);
        } catch (IOException e) {
            System.out.println("Cannot create the socket!");
            System.exit(-1);
        }
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        boolean isGetFile = f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public void getUser() {
        // if expiration flag is true, also ask for the expiration.
        // get the output stream from the socket.
        try {
            OutputStream outputStream = socket.getOutputStream();

            InputStream inputStream = socket.getInputStream();
            // create an object output stream from the output stream so we can send an
            // object through it
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            objectOutputStream.writeUTF("get");
            objectOutputStream.flush();

            objectOutputStream.writeUTF("Michael Scott");
            objectOutputStream.flush();

            boolean isErrorFlag = objectInputStream.readBoolean();

            if (isErrorFlag) {
                System.out.println("User did not found!");

            } else {

                byte[] data = (byte[]) objectInputStream.readObject();
                PublicKey pkey = (PublicKey) objectInputStream.readObject();

                writeToFile("KeyPair/serverKey", pkey.getEncoded());
                writeToFile("Signed/data", data);

                new SignVerify("Signed/data", "KeyPair/serverKey");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void register() throws Exception {
        // need host and port, we want to connect to the ServerSocket at port 4400
        System.out.println("Connected!");

        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        // create an object output stream from the output stream so we can send an
        // object through it
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        objectOutputStream.writeUTF("register");
        objectOutputStream.flush();

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Michael Scott"));
        messages.add(new Message("theWorldsBestBoss@DunderMifflin.com"));

        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(messages);
        objectOutputStream.flush();
        objectOutputStream.writeObject(new AsymmetricCryptography().getPublic("KeyPair/publicKey"));

        String expiration = objectInputStream.readUTF();

        System.out.println("Expiration date of the certificate: " + expiration);

        System.out.println("Closing socket and terminating program.");

        socket.close();
    }
}

package com.pki.app;

import com.pki.crypto.AsymmetricCryptography;
import com.pki.crypto.Certification;
import com.pki.crypto.GenerateKeys;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
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

class ClientHandler extends Thread {
    private Socket client;
    private boolean isRunning = true;
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
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            String field = in.readUTF();
            if (field.equalsIgnoreCase("register")) {

                List<byte[]> values = new ArrayList<>();

                List<Message> object = (List<Message>) in.readObject();
                System.out.println("Received [" + object.size() + "] messages from: " + client);

                // print out the text of every message
                System.out.println("All messages:");
                object.forEach((msg) -> System.out.println(msg.getText()));

                PublicKey obj = (PublicKey) in.readObject();
                System.out.println(obj);

                object.forEach((data) -> values.add(data.getText().getBytes()));
                values.add(obj.getEncoded());


                String userName = object.get(0).getText().toLowerCase().trim().replace(" ", "-");
                byte[] goingToBeSignedData = createCertBundle(values);
                new Certification().signIncomingUserData(goingToBeSignedData, userName);

                new GenerateKeys().writeToFile("Signed/" + userName + "-key", obj.getEncoded());
                String expirationDate = createTimeStamp();
                out.writeUTF(expirationDate);
                out.flush();

                createFile("Signed/" + userName + "-timestamp", expirationDate);
                System.out.println("Closing sockets");
                client.close();
            } else if (field.equalsIgnoreCase("get")) {
                String username = in.readUTF();
                String usernameSanitized = username.toLowerCase().trim().replace(" ", "-");
                String filePath = "Signed/" + usernameSanitized;
                String filePathOfKey = "Signed/" + usernameSanitized + "-key";
                String timestamp = readFile("Signed/" + usernameSanitized + "-timestamp", StandardCharsets.UTF_8);

                try {
                    byte[] data = Files.readAllBytes(new File(filePath).toPath());
                    PublicKey usersKey = new AsymmetricCryptography().getPublic(filePathOfKey);
                    PublicKey publicKey = new AsymmetricCryptography().getPublic("KeyPair/publicKey");
                    out.writeBoolean(false);
                    out.writeObject(data);
                    out.flush();
                    out.writeObject(publicKey);
                    out.flush();
                    out.writeObject(usersKey);
                    out.flush();
                    out.writeUTF(username);
                    out.flush();
                    out.writeUTF(timestamp);
                    out.flush();


                } catch (NoSuchFileException e) {
                    out.writeBoolean(true);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (field.equalsIgnoreCase("exit")) {
                this.isRunning = false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String createTimeStamp() {
        Calendar now = Calendar.getInstance();

        now.setTime(now.getTime());
        now.add(Calendar.MONTH, 6);

        return now.get(Calendar.DATE) + "-" + now.get(Calendar.MONTH) + "-" +
                now.get(Calendar.YEAR);
    }

    private byte[] createCertBundle(List<byte[]> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(data.get(0));
            outputStream.write(data.get(1));
            outputStream.write(data.get(2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }


    public boolean isServerClosed() {
        return isRunning;
    }

    private void createFile(String fileName, String data) {
        File f = new File(fileName);
        f.getParentFile().mkdirs();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeUTF(data);
            oos.close();
            System.out.println("File is ready!");
        } catch (IOException e) {
            System.out.println("Couldn't create the expiration date for the user!");
            e.printStackTrace();
        }
    }

    String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
package com.pki.app;

import com.pki.crypto.AsymmetricCryptography;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Connection {
    Socket socket;
    PrintWriter dos;
    BufferedReader br;

    Connection() {
        try {
            socket = new Socket("localhost", 4400);
            dos = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            System.out.println("I cannot open the port. Quiting...");
            System.exit(-1);
        }
    }


    public void sendGetPublicKeyRequest(String email) throws IOException {
        dos.println("get-" + email);
        dos.flush();

        String response;
        while ((response = br.readLine()) != null) {
            System.out.println("Waiting server's response...");
            if (response.equalsIgnoreCase("done")) break;
            if (response.equalsIgnoreCase("exit")) {
                System.out.println("User did not found!");
                dos.println("quit");
                break;
            }
        }
        System.out.println("Done!");
        closeSocket();
    }

    public void register() throws Exception {

        dos.println("register");
        dos.flush();

        dos.println("ali veli");
        dos.flush();

        dos.println("ali@veli.com");
        dos.flush();

        handleKey();

        String response;
        while ((response = br.readLine()) != null) {
            if (response.equalsIgnoreCase("done")) {
                System.out.println("Registered");
                dos.println("quit");
                break;

            }
        }

        closeSocket();
    }

    void handleKey() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            try {
                oos.writeObject(new AsymmetricCryptography().getPublic("KeyPair/publicKey"));
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() throws IOException {
        socket.close();
        br.close();
        dos.close();
    }

}

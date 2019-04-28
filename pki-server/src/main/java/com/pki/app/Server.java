package com.pki.app;

import com.pki.crypto.GenerateKeys;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


public class Server {
    private ServerSocket serverSocket;
    private Socket client;
    ClientHandler clientHandler;
    private boolean isServerOn = true;
    //private Calendar timestamp = Calendar.getInstance();
    //private SimpleDateFormat formatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

    public Server() {
        try {
            serverSocket = new ServerSocket(4400);
            createKeyPairForAuthorization();
        } catch (IOException e) {
            System.out.println("System could not create server on port 4400. Quiting...");
            System.exit(-1);
        }
    }

    public void start() throws IOException {
        while (this.isServerOn) {
            try {
                // Accepts incoming connections
                client = serverSocket.accept();

                clientHandler = new ClientHandler(client);
                clientHandler.run();

                isServerOn = clientHandler.isServerClosed();
                System.out.println("Finished");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        client.close();
        serverSocket.close();
    }

    private void createKeyPairForAuthorization() {
        GenerateKeys gk;

        try {
            if (!new File("KeyPair/publicKey").exists()) {
                gk = new GenerateKeys(4096);
                gk.createKeys();
                gk.writeToFile("KeyPair/publicKey", gk.getPublicKey().getEncoded());
                gk.writeToFile("KeyPair/privateKey", gk.getPrivateKey().getEncoded());
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Cannot create key pairs' files. Quiting...");
            System.exit(-1);
        }
    }


}



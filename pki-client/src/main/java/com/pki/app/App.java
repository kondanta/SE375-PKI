package com.pki.app;

import com.pki.crypto.GenerateKeys;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


// This part of the client side, starts the application
public class App {
    public static void main(String[] args) throws Exception {
        GenerateKeys gk;
        try {
            if (!new File("KeyPair/publicKey").exists()) {
                gk = new GenerateKeys(4096);
                gk.createKeys();
                gk.writeToFile("KeyPair/publicKey", gk.getPublicKey().getEncoded());
                gk.writeToFile("KeyPair/privateKey", gk.getPrivateKey().getEncoded());
            } else {
                System.out.print("Key exists\n");
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            System.err.println(e.getMessage());
        }

        Connection connection = new Connection();
        //connection.register();

        connection.getUser();
    }
}


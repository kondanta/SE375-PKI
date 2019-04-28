package com.pki.app;

import com.pki.crypto.GenerateKeys;
import com.pki.crypto.Sign;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Scanner;


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


        String signedText = "Signed Text";
        new Sign(signedText, "KeyPair/privateKey").createFile("Signed/SignedDocument.txt");

        Scanner scanner = new Scanner(System.in);
        String arg = "";
        while (!arg.equalsIgnoreCase("q")) {
            System.out.println("========== Usage ==========");
            System.out.println("-r for register, -g for get user key -q for quit");
            arg = scanner.nextLine();

            if (arg.equalsIgnoreCase("-r")) {
                Scanner in = new Scanner(System.in);
                System.out.println("name: ");
                String name = in.nextLine();
                System.out.println("email");
                String email = in.nextLine();

                new Connection().register(name, email);
            } else if (arg.equalsIgnoreCase("-g")) {
                Scanner in = new Scanner(System.in);
                System.out.println("name: ");
                String name = in.nextLine();
                new Connection().getUser(name);

            } else if (arg.equalsIgnoreCase("-q")) {
                break;
            }
        }
    }
}


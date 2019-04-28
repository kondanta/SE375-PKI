package com.pki.app;

import java.io.*;
import java.net.Socket;

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

    public void sendMessageToServer() throws IOException {
        String msg = "test";
        dos.println(msg);
        dos.flush();
        dos.println("quit");
        System.out.println(br.readLine());
        br.close();
        dos.close();
    }

    public void sendGetPublicKeyRequest(String email) throws IOException {
        dos.println("get-" + email);
        dos.flush();

        String response;
        while((response = br.readLine()) != null) {
            System.out.println("Waiting server's response...");
            if(response.equalsIgnoreCase("done")) break;
            if(response.equalsIgnoreCase("exit")) {
                System.out.println("User did not found!");
                dos.println("quit");
                break;
            }
        }
        System.out.println("Done!");
        closeSocket();
    }

    public void closeSocket() throws IOException {
        socket.close();
        br.close();
        dos.close();
    }

}

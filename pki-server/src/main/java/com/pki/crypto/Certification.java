package com.pki.crypto;

//import javax.security.cert.Certificate;

public class Certification {

    public void signIncomingUserData(byte[] incomingData, String fileName) {
        new Sign(incomingData, "KeyPair/privateKey").createFile("Signed/" + fileName);
    }
}
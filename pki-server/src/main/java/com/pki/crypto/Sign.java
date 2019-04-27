package com.pki.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

public class Sign {
    private List<byte[]> list;

    // Constructor takes message and the key
    public Sign(String data, String keyFile) {
        list = new ArrayList<>();
        list.add(data.getBytes());
        list.add(sign(data, keyFile)); // keyfile: KeyPair/privateKey
    }

    // Signs the data using generated key.
    private byte[] sign(String data, String keyFile) {
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(new AsymmetricCryptography().getPrivate(keyFile));
            rsa.update(data.getBytes());
            return rsa.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    // We do not care mkdirs() result for now
    public void createFile(String fileName) {
        File f = new File(fileName);
        f.getParentFile().mkdirs();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(list);
            oos.close();
            System.out.println("File is ready!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

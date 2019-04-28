package com.pki.crypto;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Signature;
import java.util.List;

public class SignVerify {
    private List<byte[]> list;

    @SuppressWarnings("unchecked")
    // We suppress uncheck because we have to cast read object to List<byte>[]
    public SignVerify(String fileName, String keyFile) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            this.list = (List<byte[]>) ois.readObject();
            ois.close();

            System.out.println(
                    verifySign(list.get(0), list.get(1), keyFile) ? "VERIFIED" + "\n-=================-\n"
                            + new String(list.get(0)) : "NOT VERIFIED!"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifySign(byte[] data, byte[] sign, String keyFile) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(new AsymmetricCryptography().getPublic(keyFile));
            signature.update(data);

            // checks if the sign matches or not
            return signature.verify(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

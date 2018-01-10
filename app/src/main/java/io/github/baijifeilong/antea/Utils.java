package io.github.baijifeilong.antea;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/9/21 13:16
 */

class Utils {
    static class EncryptionException extends Exception {
        EncryptionException(String message) {
            super(message);
        }
    }

    static class DecryptionException extends Exception {
        DecryptionException(String message) {
            super(message);
        }
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    static String encryptString(String content, String password) throws EncryptionException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] keyBytes = new byte[16];
            System.arraycopy(messageDigest.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return bytesToHex(cipher.doFinal(content.getBytes("utf-8")));
        } catch (Exception e) {
            throw new EncryptionException(e.getLocalizedMessage());
        }
    }

    static String decryptString(String content, String password) throws DecryptionException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] keyBytes = new byte[16];
            System.arraycopy(messageDigest.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(cipher.doFinal(hexStringToByteArray(content)));
        } catch (Exception e) {
            throw new DecryptionException(e.getLocalizedMessage());
        }
    }
}

package io.github.baijifeilong.antea;

import org.junit.Test;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.*;

/**
 * Created by BaiJiFeiLong@gmail.com on 2017/9/21 14:55
 */
public class UtilsTest {
    @Test
    public void encryptString() throws Exception {
        String s = Utils.encryptString("hello", "123");
        System.out.println(s);
        System.out.println(Utils.decryptString(s, "123"));
    }

    @Test
    public void decryptString() throws Exception {
        String text = "hello";
        String password = "123";

        String encryptedText = encrypt(text, password);

        System.out.println(encryptedText);
        System.out.println(decrypt(encryptedText, password));
    }

    private String encrypt(String text, String password) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, new SecureRandom(password.getBytes()));
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
        System.out.println("key = " + new String(secretKeySpec.getEncoded()));

        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return Utils.bytesToHex(cipher.doFinal(text.getBytes()));
    }

    private String decrypt(String encryptedText, String password) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, new SecureRandom(password.getBytes()));
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
        System.out.println("key = " + new String(secretKeySpec.getEncoded()));

        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return Utils.bytesToHex(cipher.doFinal(encryptedText.getBytes()));
    }
}
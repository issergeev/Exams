package com.issergeev.exams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Encryption {
    private static final String alphabet = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz1234567890!@#$%^&*()_";
    private static SecureRandom random = new SecureRandom();

    public static String Encrypt(String password) {
        String generatedPassword = "";
        String salt = generateSalt();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return salt + " " + generatedPassword;
    }

    private static String generateSalt() {
        StringBuilder salt = new StringBuilder("");
        for (int i = 0; i < 10; i++) {
            salt.append(alphabet.charAt(random.nextInt(alphabet.length() - 1)));
        }

        return salt.toString();
    }

    public static String EncryptWithSalt(String password, String salt) {
        String generatedPassword = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return generatedPassword;
    }
}
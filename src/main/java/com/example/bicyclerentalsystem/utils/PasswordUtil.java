package com.example.bicyclerentalsystem.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    // Generate SHA-256 hash with salt
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] hashedPassword = md.digest(password.getBytes());

            // store salt + hash in Base64 format
            byte[] saltedHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltedHash, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(saltedHash);

        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verify password
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        try {
            byte[] saltedHash = Base64.getDecoder().decode(storedHash);

            // salt is first 16 bytes
            byte[] salt = new byte[16];
            System.arraycopy(saltedHash, 0, salt, 0, 16);

            // actual stored hash
            byte[] storedPasswordHash = new byte[saltedHash.length - 16];
            System.arraycopy(saltedHash, 16, storedPasswordHash, 0, storedPasswordHash.length);

            // hash raw password again with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] newHash = md.digest(rawPassword.getBytes());

            // compare
            if (newHash.length != storedPasswordHash.length)
                return false;

            for (int i = 0; i < newHash.length; i++) {
                if (newHash[i] != storedPasswordHash[i])
                    return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // Generate salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}

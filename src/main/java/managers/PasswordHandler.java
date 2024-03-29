package managers;

import helpers.SecuredPassword;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Objects;

public class PasswordHandler {

    private final PasswordListener passwordListener;
    private static final byte[] unWantedByte = "\n".getBytes();

    public PasswordHandler(PasswordListener passwordListener) {
        this.passwordListener = passwordListener;
    }

    public SecuredPassword generateSecuredPassword(String passwordToHash) {
        String salt = getSalt();
        String securePassword = getSecurePassword(passwordToHash, salt);
        return new SecuredPassword(securePassword, salt);
    }

    public boolean isPasswordValid(String nickname, String password) {
        SecuredPassword passwordFromDB = passwordListener.getSecuredPassFromDB(nickname);

        if (Objects.equals(getSecurePassword(password, passwordFromDB.getSalt()), passwordFromDB.getPassword())) {
            return true;
        }
        return false;
    }

    private static String getSecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            messageDigest.update(salt.getBytes());

            // Get the hash's bytes
            byte[] bytes = messageDigest.digest(passwordToHash.getBytes());

            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder stringBuilder = new StringBuilder();

            replaceUnwantedBytes(bytes);

            for (int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            generatedPassword = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private String getSalt() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            // Create array for salt
            byte[] salt = new byte[16];
            // Get a random salt
            secureRandom.nextBytes(salt);
            // return salt
            replaceUnwantedBytes(salt);
            return new String(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "UNKNOWN";
    }

    private static void replaceUnwantedBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == unWantedByte[0]) {
                System.out.println("Replacing unwanted byte.");
                bytes[i] = '2';
            }
        }
    }
}
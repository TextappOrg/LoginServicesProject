package CryptoPackage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class EncryptClass {
    private static final String ALGO = "PBKDF2WithHmacSHA512";
    private final int hashBites;
    private final int iterations;
    private int saltSize;
    private byte[] MC;


    public EncryptClass(int hashBites, int saltSize, int iterations) {
        this.saltSize = saltSize;
        this.iterations = iterations;
        this.hashBites = hashBites;
    }

    public EncryptClass(int hashBites, int iterations) {
        this.hashBites = hashBites;
        this.iterations = iterations;
    }

    private byte[] getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[this.saltSize];
        secureRandom.nextBytes( salt );
        this.MC = salt;
        return salt;
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] hash = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec pbeKeySpec = new PBEKeySpec( hash, salt, this.iterations, this.hashBites );
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( ALGO );
        byte[] hsh = keyFactory.generateSecret( pbeKeySpec ).getEncoded();
        return new String( hsh );
    }

    public boolean chkPass(String userInput, String fromDb, byte[] namak) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] hash1 = userInput.toCharArray();

        PBEKeySpec pbeKeySpec1 = new PBEKeySpec( hash1, namak, this.iterations, this.hashBites );
        SecretKeyFactory keyFactory1 = SecretKeyFactory.getInstance( ALGO );
        byte[] cake1 = keyFactory1.generateSecret( pbeKeySpec1 ).getEncoded();

        String comp1 = new String( cake1 );
        return comp1.equals( fromDb );
    }

    public byte[] getMC() {
        return this.MC;
    }
}


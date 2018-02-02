package UtilityPackage;

import javax.validation.constraints.NotNull;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class HandymanClass {

    /**
     * The function generates a UUID which will be a concatenation of <br> a UUID, a hex-string of the current time in
     * nanos and
     * the
     * user input
     *
     * @param input the input that will be appended to the UUID
     * @return String
     */
    @org.jetbrains.annotations.NotNull
    public static String makeUID(String input) {
        String uuId = UUID.randomUUID().toString();
        String salter = Long.toHexString( System.nanoTime() + System.currentTimeMillis() );
        return uuId + "_" + salter + "_" + input.hashCode();
    }

    @org.jetbrains.annotations.NotNull
    public static String decodeBase64String(@NotNull String input) throws UnsupportedEncodingException {
        byte[] btArr = Base64.getUrlDecoder().decode( input );
        return new String( btArr, "UTF-8" );
    }

    public static String generateToken(){
        try {
            byte[] timestamp = Long.toHexString(System.nanoTime() + System.currentTimeMillis()).getBytes();
            byte[] hashBites = new byte[48];
            SecureRandom sha512PRNG = SecureRandom.getInstance("SHA1PRNG");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
            keyGenerator.init(512,sha512PRNG);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] secretBytes = secretKey.getEncoded();
            sha512PRNG.nextBytes(hashBites);
            sha512PRNG.nextBytes(timestamp);
            return new String(timestamp)+new String(hashBites)+ new String(secretBytes);
        } catch (NoSuchAlgorithmException e) {
            e.getMessage();
            return "";
        }
    }
}
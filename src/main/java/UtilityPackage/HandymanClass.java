package UtilityPackage;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
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
        String salter = Long.toHexString( System.nanoTime() );
        return uuId + "_" + salter + "_name:_" + input;
    }

    public static String decodeBase64String(@NotNull String input) throws UnsupportedEncodingException {
        byte[] btArr = Base64.getDecoder().decode( input );
        return new String( btArr, "UTF-8" );
    }
}
package uk.co.whitbread.piba.account.util;

import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SessionTokenUtil {
    private static final String SECURE_HASH_ALGORITHM = "SHA-256";
    private static final String MEMORABLE_WORD_SECURE_METHOD_NAME = "updatememorableword";

    private SessionTokenUtil() {}

    public static byte[] generateNonceGuidBytes() {
        return UUID.randomUUID().toString().toUpperCase().getBytes();
    }

    public static String generateNonceGuidBase64() {
        return Base64.encodeBase64String(generateNonceGuidBytes());
    }

    public static String generateHashedSessionToken(String sharedSecret, String timestamp, String nonce)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String decodedNonce = new String(Base64.decodeBase64(nonce), StandardCharsets.UTF_8);

        // advised by Worldline to add MEMORABLE_WORD_SECURE_METHOD_NAME
        String concatenatedValues = decodedNonce + timestamp + sharedSecret + MEMORABLE_WORD_SECURE_METHOD_NAME;
        MessageDigest md = MessageDigest.getInstance(SECURE_HASH_ALGORITHM);
        md.update(concatenatedValues.getBytes(StandardCharsets.UTF_8));
        byte[] sha256Hash = md.digest();
        return Base64.encodeBase64String(sha256Hash);
    }
}

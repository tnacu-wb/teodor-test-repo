package uk.co.whitbread.business.tether.utils;


import com.google.common.primitives.Bytes;
import org.apache.commons.codec.binary.Base64;
import worldline.mst.bsm.api.b2b.pi.data.SessionTokenType;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SessionTokenUtil {
    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String SECURE_HASH_ALGORITHM = "SHA-256";
    private static final String LOGIN_SECURE_METHOD_NAME = "integratedlogin";
    private static final String REFRESH_SECURE_METHOD_NAME = "refreshSession";

    private SessionTokenUtil() {
    }

    public static byte[] generateNonceGuidBytes() {
        return UUID.randomUUID().toString().toUpperCase().getBytes();
    }

    public static String generateNonceGuidBase64() {
        return Base64.encodeBase64String(generateNonceGuidBytes());
    }

    public static String generateHashedSessionToken(String sharedSecret, String timestamp, String nonce)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String decodedNonce = new String(Base64.decodeBase64(nonce), CHARSET_UTF_8);

        // advised by Worldline to add SECURE_METHOD_NAME
        String concatenatedValues = decodedNonce + timestamp + sharedSecret + LOGIN_SECURE_METHOD_NAME;

        MessageDigest md = MessageDigest.getInstance(SECURE_HASH_ALGORITHM);

        md.update(concatenatedValues.getBytes(CHARSET_UTF_8));

        byte[] sha256Hash = md.digest();

        return Base64.encodeBase64String(sha256Hash);
    }

    public static SessionTokenType generateSessionToken(String sharedSecret, String sessionId, String timestamp)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        SessionTokenType sessionToken = new SessionTokenType();

        byte[] nonceBytes = generateNonceGuidBytes();
        sessionToken.setNonce(Base64.encodeBase64String(nonceBytes));

        sessionToken.setNonce(generateNonceGuidBase64());
        sessionToken.setSessionId(sessionId);
        sessionToken.setTimestamp(timestamp);

        MessageDigest md = MessageDigest.getInstance(SECURE_HASH_ALGORITHM);

        byte[] concat = Bytes.concat(nonceBytes,
                sessionToken.getTimestamp().getBytes(CHARSET_UTF_8),
                sharedSecret.getBytes(CHARSET_UTF_8),
                REFRESH_SECURE_METHOD_NAME.toLowerCase().getBytes(CHARSET_UTF_8));

        sessionToken.setHash(Base64.encodeBase64String(md.digest(concat)));
        return sessionToken;
    }
}

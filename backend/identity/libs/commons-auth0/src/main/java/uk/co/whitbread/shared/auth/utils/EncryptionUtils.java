package uk.co.whitbread.shared.auth.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;

import java.security.SecureRandom;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.length;
import static uk.co.whitbread.shared.auth.service.EncryptionService.GENERIC_CIPHER_ERROR_MESSAGE;

@Slf4j
public class EncryptionUtils {

    public static final int INIT_VECTOR_BYTE_SIZE = 16;
    private static final int INIT_VECTOR_HEX_SIZE = INIT_VECTOR_BYTE_SIZE * 2;

    public static byte[] convertStringToBytes(String s) {
        return s.getBytes(UTF_8);
    }

    public static byte[] convertBase64ToBytes(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public static byte[] convertHexToBytes(String hexString) {
        try {
            return Hex.decodeHex(hexString);
        } catch (Exception e) {
            log.error("Error while trying to convert from hexadecimal to bytes", e);
            throw new InvalidLoginException(GENERIC_CIPHER_ERROR_MESSAGE, e);
        }
    }

    public static String convertBytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String convertBytesToHex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static String convertBytesToString(byte[] bytes) {
        return new String(bytes, UTF_8);
    }

    /**
     * @param securedMessage the concatenation of the initialisation vector (encoded in hexadecimal)
     *                       and the encrypted content (encoded in base64). The initialisation vector is the first part
     *                       of the message, it is 16 bytes encoded in hexadecimal (so 32 chars).
     * @return initialisation vector in bytes
     */
    public static byte[] getInitVectorFromSecuredMessage(String securedMessage) {
        int securedMessageLength = length(securedMessage);
        if (securedMessageLength < INIT_VECTOR_HEX_SIZE) {
            log.error("Invalid secured message length: {}", securedMessageLength);
            throw new InvalidLoginException(GENERIC_CIPHER_ERROR_MESSAGE);
        }

        String initialisationVectorHex = securedMessage.substring(0, INIT_VECTOR_HEX_SIZE);
        return convertHexToBytes(initialisationVectorHex);
    }

    /**
     * The encrypted content starts after the 32nd char (after the initialisation vector) and is base64 encoded.
     *
     * @param securedMessage the concatenation of the initialisation vector (encoded in hexadecimal)
     *                       and the encrypted content (encoded in base64)
     * @return encrypted content in bytes
     */
    public static byte[] getEncryptedContent(String securedMessage) {
        String resultBase64 = securedMessage.substring(INIT_VECTOR_HEX_SIZE);
        return convertBase64ToBytes(resultBase64);
    }

    public static byte[] generateRandomInitialisationVector() {
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] result = new byte[INIT_VECTOR_BYTE_SIZE];
        randomSecureRandom.nextBytes(result);
        return result;
    }
}

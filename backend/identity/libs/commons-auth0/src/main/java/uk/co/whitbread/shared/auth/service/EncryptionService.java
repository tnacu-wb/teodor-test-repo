package uk.co.whitbread.shared.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.properties.EncryptionProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.convertBase64ToBytes;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.convertBytesToBase64;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.convertBytesToHex;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.convertBytesToString;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.convertStringToBytes;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.generateRandomInitialisationVector;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.getEncryptedContent;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.getInitVectorFromSecuredMessage;

/**
 * Used for encrypting/decrypting guest history number and sessionId used in Auth0.
 * What we call a secured message here is the following arbitrary structure (concatenated):
 * <p>initialisation vector in hexadecimal + encrypted content in base64</p>
 */
@Slf4j
@RequiredArgsConstructor
public class EncryptionService {

    public static final String GENERIC_CIPHER_ERROR_MESSAGE = "Invalid data";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    private final EncryptionProperties properties;

    public String writeSecuredMessage(String toSecureString) {
        byte[] initialisationVector = generateRandomInitialisationVector();
        String initialisationVectorHex = convertBytesToHex(initialisationVector);
        String encryptedContentBase64 = encryptContentBase64(convertStringToBytes(toSecureString), initialisationVector);
        return initialisationVectorHex + encryptedContentBase64;
    }

    public String readSecuredMessage(String securedMessage) {
        byte[] initialisationVector = getInitVectorFromSecuredMessage(securedMessage);
        byte[] encryptedContent = getEncryptedContent(securedMessage);
        return decryptContentToString(encryptedContent, initialisationVector);
    }

    public byte[] processCipher(byte[] toProcessContent, byte[] initialisationVector, int cipherMode) {
        Cipher cipher = getAesCipher();
        SecretKeySpec key = getSecretKey();
        IvParameterSpec initialisationVectorParameterSpec = new IvParameterSpec(initialisationVector);
        try {
            cipher.init(cipherMode, key, initialisationVectorParameterSpec);
            return cipher.doFinal(toProcessContent);
        } catch (Exception e) {
            log.error("Error while processing cipher with mode {}", cipherMode, e);
            throw new InvalidLoginException(GENERIC_CIPHER_ERROR_MESSAGE, e);
        }
    }

    public SecretKeySpec getSecretKey() {
        String keyBase64 = properties.getKey();
        if (isBlank(keyBase64)) {
            log.error("Secret key is not configured");
            throw new InvalidLoginException(GENERIC_CIPHER_ERROR_MESSAGE);
        }
        byte[] key = convertBase64ToBytes(keyBase64);
        return new SecretKeySpec(key, "AES");
    }

    public Cipher getAesCipher() {
        try {
            return Cipher.getInstance(AES_ALGORITHM);
        } catch (Exception e) {
            log.error("Error while trying to get Aes Cipher", e);
            throw new InvalidLoginException(GENERIC_CIPHER_ERROR_MESSAGE, e);
        }
    }

    public String encryptContentBase64(byte[] toEncryptContent, byte[] initialisationVector) {
        byte[] encryptedContent = encryptContent(toEncryptContent, initialisationVector);
        return convertBytesToBase64(encryptedContent);
    }

    public byte[] encryptContent(byte[] toEncryptContent, byte[] initialisationVector) {
        return processCipher(toEncryptContent, initialisationVector, Cipher.ENCRYPT_MODE);
    }

    public String decryptContentToString(byte[] toDecryptContent, byte[] initialisationVector) {
        byte[] decryptedContent = decryptContent(toDecryptContent, initialisationVector);
        return convertBytesToString(decryptedContent);
    }

    public byte[] decryptContent(byte[] toDecryptContent, byte[] initialisationVector) {
        return processCipher(toDecryptContent, initialisationVector, Cipher.DECRYPT_MODE);
    }
}

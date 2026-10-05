package uk.co.whitbread.hotel.register.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.properties.RegisterProperties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service("emailEncryptionService")
@RequiredArgsConstructor
public class EncryptionService {

    public static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    public static final String SECRET_FACTORY_ALG = "PBKDF2WithHmacSHA256";
    private final RegisterProperties properties;
    private static final String AES = "AES";

    public String encrypt(String base64ToEncrypt) throws Exception {

        final byte[] decode = Base64.getDecoder().decode(base64ToEncrypt);
        String strToEncrypt = new String(decode, StandardCharsets.UTF_8);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, properties.getStaticIV().getBytes());

        SecretKeySpec secretKey = getSecretKeySpec(properties.getSalt());

        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.toLowerCase().getBytes(StandardCharsets.UTF_8)));
    }

    // method only used for test purposes
    protected String decrypt(String strToDecrypt) throws Exception {

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, properties.getStaticIV().getBytes());

        SecretKeySpec secretKey = getSecretKeySpec(properties.getSalt());

        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] decodedStr = Base64.getDecoder().decode(strToDecrypt);
        byte[] decryptedPayload = cipher.doFinal(decodedStr);

        return new String(decryptedPayload);
    }

    private SecretKeySpec getSecretKeySpec(String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_FACTORY_ALG);
        KeySpec spec = new PBEKeySpec(properties.getSecretKey().toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), AES);
    }
}

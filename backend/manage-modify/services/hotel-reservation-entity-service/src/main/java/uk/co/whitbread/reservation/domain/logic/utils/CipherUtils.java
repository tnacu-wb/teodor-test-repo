package uk.co.whitbread.reservation.domain.logic.utils;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CipherUtils {

  public static final String ENCRYPT_ALG = "AES/GCM/NoPadding";
  private static final int TAG_LENGTH_BIT = 128;

  // We prefix the 16 bytes IV to the encrypted text (ciphertext), because we need the same IV for decryption.
  public static String encryptWithPrefixInitVector(String text) throws InvalidAlgorithmParameterException,
      NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
      BadPaddingException {

    var secret = new SecretKeySpec(getSecretKey().getBytes(), "AES");
    var iv = CipherUtils.getInitVectorSecureRandom();
    var cipherText = encrypt(text.getBytes(), secret, iv);
    int ivLength = iv.length;
    int cipherTextLength = cipherText.length;
    if (ivLength > Integer.MAX_VALUE - cipherTextLength) {
      throw new IllegalArgumentException("IV or ciphertext length is too large for buffer allocation.");
    }
    var bytes = ByteBuffer.allocate(ivLength + cipherTextLength)
        .put(iv)
        .put(cipherText)
        .array();
    return Base64.getEncoder().encodeToString(bytes);
  }

  private static byte[] encrypt(byte[] textBytes, SecretKey secret, byte[] iv)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException {

    var cipher = Cipher.getInstance(ENCRYPT_ALG);
    cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
    return cipher.doFinal(textBytes);
  }

  public static String decryptWithPrefixInitVector(String text)
      throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException {

    var secret = new SecretKeySpec(getSecretKey().getBytes(), "AES");
    var bb = ByteBuffer.wrap(Base64.getDecoder().decode(text.getBytes()));

    var iv = new byte[16];
    bb.get(iv);

    var cipherText = new byte[bb.remaining()];
    bb.get(cipherText);

    return decrypt(cipherText, secret, iv);
  }

  private static String decrypt(byte[] textBytes, SecretKey secret, byte[] iv)
      throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException {

    var cipher = Cipher.getInstance(ENCRYPT_ALG);
    cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
    return new String(cipher.doFinal(textBytes));
  }

  private static byte[] getInitVectorSecureRandom() throws NoSuchAlgorithmException {

    var iv = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(iv);
    return iv;
  }

  // TODO For now, it's a hardcoded string, but in the future will be retrieved from AWS Secrets Manager
  private static String getSecretKey() {
    return "secretkey12D(G+KbPeShVmYq3t6w9z$";
  }
}
package uk.co.whitbread.shared.auth.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.properties.EncryptionProperties;
import uk.co.whitbread.shared.auth.utils.EncryptionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.shared.auth.utils.EncryptionUtils.INIT_VECTOR_BYTE_SIZE;

@RunWith(MockitoJUnitRunner.class)
public class EncryptionServiceTest {
    public static final String ENCRYPTION_KEY = "OJs67WE8z4Mfe/iuIDkbMFr1e5t5EsCrqFfJaJUFvNQ=";
    public static final String PLAIN_MESSAGE = "This is a plain message";
    public static final byte[] INIT_VECTOR = new byte[]{0, 1, 2, 3, 4, 5};
    public static final String BASE64_STRING = "/2R7EuTJMPrM8edGjNIzXAu3D3zsbxQw8pGN38yhOwc=";
    public static final byte[] ENCRYPTED_CONTENT = new byte[]{-1, 100, 123, 18, -28, -55, 48, -6, -52, -15, -25, 70, -116,
            -46, 51, 92, 11, -73, 15, 124, -20, 111, 20, 48, -14, -111, -115, -33, -52, -95, 59, 7};
    public static final byte[] PLAIN_CONTENT = new byte[]{84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 112, 108, 97, 105,
            110, 32, 109, 101, 115, 115, 97, 103, 101};
    public static final byte[] KEY_CONTENT = new byte[]{56, -101, 58, -19, 97, 60, -49, -125, 31, 123, -8, -82, 32, 57,
            27, 48, 90, -11, 123, -101, 121, 18, -64, -85, -88, 87, -55, 104, -107, 5, -68, -44};
    public static final String INIT_VECTOR_HEX = "31a9d27976164d106009ee06120d09ba";
    public static final String ENCRYPTED_CONTENT_BASE64 = "/2R7EuTJMPrM8edGjNIzXAu3D3zsbxQw8pGN38yhOwc=";
    public static final String SECURED_MESSAGE = INIT_VECTOR_HEX + ENCRYPTED_CONTENT_BASE64;

    @Spy
    @InjectMocks
    private EncryptionService testObj;

    @Mock
    private EncryptionProperties properties;

    @Before
    public void setUp() {
        when(properties.getKey()).thenReturn(ENCRYPTION_KEY);
    }

    @Test
    public void shouldReadActualSecuredMessage() {
        String result = testObj.readSecuredMessage(SECURED_MESSAGE);
        assertEquals(PLAIN_MESSAGE, result);
    }

    @Test
    public void shouldWriteAndReadSecuredMessage() {
        String secured = testObj.writeSecuredMessage(PLAIN_MESSAGE);
        assertNotNull(secured);
        assertNotEquals(PLAIN_MESSAGE, secured);
        String plain = testObj.readSecuredMessage(secured);
        assertNotNull(plain);
        assertNotEquals(plain, secured);
        assertEquals(PLAIN_MESSAGE, plain);
    }

    @Test
    public void shouldWriteSecuredMessage() {
        String result = testObj.writeSecuredMessage(PLAIN_MESSAGE);
        assertNotEquals(PLAIN_MESSAGE, result);
    }

    @Test
    public void shouldReadSecuredMessage() {
        String result = testObj.readSecuredMessage(SECURED_MESSAGE);
        assertNotEquals(SECURED_MESSAGE, result);
    }

    @Test
    public void shouldGetInitVectorFromSecuredMessage() {
        byte[] result = EncryptionUtils.getInitVectorFromSecuredMessage(SECURED_MESSAGE);
        assertArrayEquals(EncryptionUtils.convertHexToBytes(INIT_VECTOR_HEX), result);
    }

    @Test(expected = InvalidLoginException.class)
    public void shouldErrorWhenGetInitVectorFromSecuredMessageWithWrongLength() {
        EncryptionUtils.getInitVectorFromSecuredMessage("short string");
    }

    @Test
    public void shouldGetEncryptedContent() {
        byte[] result = EncryptionUtils.getEncryptedContent(SECURED_MESSAGE);
        assertArrayEquals(ENCRYPTED_CONTENT, result);
    }

    @Test
    public void shouldGenerateRandomInitialisationVector() {
        byte[] initialisationVector1 = EncryptionUtils.generateRandomInitialisationVector();
        assertEquals(initialisationVector1.length, INIT_VECTOR_BYTE_SIZE);
        byte[] initialisationVector2 = EncryptionUtils.generateRandomInitialisationVector();
        assertEquals(initialisationVector2.length, INIT_VECTOR_BYTE_SIZE);
        assertFalse(Arrays.equals(initialisationVector1, initialisationVector2));
    }

    @Test
    public void shouldEncryptContentBase64() {
        doReturn(ENCRYPTED_CONTENT).when(testObj).encryptContent(PLAIN_CONTENT, INIT_VECTOR);
        String result = testObj.encryptContentBase64(PLAIN_CONTENT, INIT_VECTOR);
        assertEquals(BASE64_STRING, result);
    }

    @Test
    public void shouldEncryptContent() {
        doReturn(ENCRYPTED_CONTENT).when(testObj).processCipher(PLAIN_CONTENT, INIT_VECTOR, Cipher.ENCRYPT_MODE);
        byte[] result = testObj.encryptContent(PLAIN_CONTENT, INIT_VECTOR);
        assertEquals(ENCRYPTED_CONTENT, result);
    }

    @Test
    public void shouldDecryptContentToString() {
        doReturn(PLAIN_CONTENT).when(testObj).decryptContent(ENCRYPTED_CONTENT, INIT_VECTOR);
        String result = testObj.decryptContentToString(ENCRYPTED_CONTENT, INIT_VECTOR);
        assertEquals(PLAIN_MESSAGE, result);
    }

    @Test
    public void shouldDecryptContent() {
        doReturn(PLAIN_CONTENT).when(testObj).processCipher(ENCRYPTED_CONTENT, INIT_VECTOR, Cipher.DECRYPT_MODE);
        byte[] result = testObj.decryptContent(ENCRYPTED_CONTENT, INIT_VECTOR);
        assertEquals(PLAIN_CONTENT, result);
    }

    @Test
    public void shouldGetSecretKey() {
        SecretKeySpec result = testObj.getSecretKey();
        assertEquals(new SecretKeySpec(KEY_CONTENT, "AES"), result);
    }

    @Test(expected = InvalidLoginException.class)
    public void shouldErrorWhenGetSecretKeyWithoutAnyKeyConfigured() {
        when(properties.getKey()).thenReturn(null);
        testObj.getSecretKey();
    }
}
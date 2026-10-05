package uk.co.whitbread.hotel.register.controller;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.hotel.captcha.exception.CypherVerificationException;
import uk.co.whitbread.hotel.register.model.EncryptRequest;
import uk.co.whitbread.hotel.register.model.EncryptResponse;
import uk.co.whitbread.hotel.register.service.EncryptionService;

import java.security.InvalidKeyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CypherControllerTest {

    @Mock
    EncryptRequest mockEncryptRequest;

    @Mock
    EncryptionService mockEncryptionService;

    @InjectMocks
    private CypherController underTest;

    @Test
    public void blankEncryptedTextCypherBase64() {

        when(mockEncryptRequest.getEmailEncodedInBase64()).thenReturn("");

        assertThrows(CypherVerificationException.class,
                () -> underTest.cypherBase64(mockEncryptRequest),
                "Request should not be empty");
    }

    @Test
    public void encryptCypherBase64() {

        EncryptRequest encryptRequest = new EncryptRequest("oldPlainText",
                Base64.encodeBase64String("encodedInBase64".getBytes()));

        when(mockEncryptRequest.getEmailEncodedInBase64()).thenReturn(encryptRequest.getEmailEncodedInBase64());

        ResponseEntity<EncryptResponse> response =  underTest.cypherBase64(mockEncryptRequest);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void encryptExceptionCypherBase64() throws Exception {

        EncryptRequest encryptRequest = new EncryptRequest("oldPlainText",
                Base64.encodeBase64String("encodedInBase64".getBytes()));

        when(mockEncryptRequest.getEmailEncodedInBase64()).thenReturn(encryptRequest.getEmailEncodedInBase64());
        when(mockEncryptionService.encrypt(mockEncryptRequest.getEmailEncodedInBase64())).thenThrow(InvalidKeyException.class);

        assertThrows(CypherVerificationException.class,
                () -> underTest.cypherBase64(mockEncryptRequest),
                "Error while trying to encrypt text.");
    }

    @Test
    public void blankEncryptedTextCypherPlainText() {

        when(mockEncryptRequest.getEmailEncodedInBase64()).thenReturn("");

        assertThrows(CypherVerificationException.class,
                () -> underTest.cypherBase64(mockEncryptRequest),
                "Request should not be empty");
    }

    @Test
    public void encryptCypherPlainText() {

        EncryptRequest encryptRequest = new EncryptRequest("oldPlainText",
                Base64.encodeBase64String("encodedInBase64".getBytes()));

        when(mockEncryptRequest.getPlainText()).thenReturn(encryptRequest.getPlainText());

        ResponseEntity<EncryptResponse> response =  underTest.cypherPlainText(mockEncryptRequest);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void encryptExceptionCypherPlainText() throws Exception {

        EncryptRequest encryptRequest = new EncryptRequest("oldPlainText",
                Base64.encodeBase64String("encodedInBase64".getBytes()));

        when(mockEncryptRequest.getPlainText()).thenReturn(encryptRequest.getPlainText());
        when(mockEncryptionService.encrypt(mockEncryptRequest.getPlainText())).thenThrow(InvalidKeyException.class);

        assertThrows(CypherVerificationException.class,
                () -> underTest.cypherPlainText(mockEncryptRequest),
                "Error while trying to encrypt text.");
    }
}
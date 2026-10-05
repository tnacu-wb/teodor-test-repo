package uk.co.whitbread.shared.auth.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import java.text.ParseException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProviderTokenVerifierTest {

    private static final String ISSUER = "https://auth0.whitbread.uk/";
    private static final String NAMESPACE = "https://premierinn.com";

    @Mock
    private ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> jwtProcessor;

    @Test
    void verifyAndDecodeToken_validToken_returnsVerifiedToken() throws Exception {
        //Arrange
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .claim("name", "user@example.com")
                .build();
        when(jwtProcessor.process(any(String.class), isNull())).thenReturn(claims);
        var verifier = new ProviderTokenVerifier(jwtProcessor, ISSUER, NAMESPACE);

        //Act
        Optional<VerifiedToken> result = verifier.verifyAndDecodeToken("valid.jwt.token");

        //Assert
        assertTrue(result.isPresent());
        assertEquals(NAMESPACE, result.get().namespace());
        assertEquals("user@example.com", result.get().claims().getStringClaim("name"));
    }

    @Test
    void verifyAndDecodeToken_issuerMismatch_returnsEmpty() throws Exception {
        //Arrange
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("https://wrong-issuer.com/")
                .build();
        when(jwtProcessor.process(any(String.class), isNull())).thenReturn(claims);
        var verifier = new ProviderTokenVerifier(jwtProcessor, ISSUER, NAMESPACE);

        //Act
        Optional<VerifiedToken> result = verifier.verifyAndDecodeToken("token.wrong.issuer");

        //Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void verifyAndDecodeToken_joseException_returnsEmpty() throws Exception {
        //Arrange
        when(jwtProcessor.process(any(String.class), isNull()))
                .thenThrow(new JOSEException("Bad signature"));
        var verifier = new ProviderTokenVerifier(jwtProcessor, ISSUER, NAMESPACE);

        //Act
        Optional<VerifiedToken> result = verifier.verifyAndDecodeToken("bad.sig.token");

        //Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void verifyAndDecodeToken_badJOSEException_returnsEmpty() throws Exception {
        //Arrange
        when(jwtProcessor.process(any(String.class), isNull()))
                .thenThrow(new BadJOSEException("Expired"));
        var verifier = new ProviderTokenVerifier(jwtProcessor, ISSUER, NAMESPACE);

        //Act
        Optional<VerifiedToken> result = verifier.verifyAndDecodeToken("expired.jwt.token");

        //Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void verifyAndDecodeToken_parseException_returnsEmpty() throws Exception {
        //Arrange
        when(jwtProcessor.process(any(String.class), isNull()))
                .thenThrow(new ParseException("Malformed", 0));
        var verifier = new ProviderTokenVerifier(jwtProcessor, ISSUER, NAMESPACE);

        //Act
        Optional<VerifiedToken> result = verifier.verifyAndDecodeToken("malformed-token");

        //Assert
        assertTrue(result.isEmpty());
    }
}

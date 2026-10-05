package uk.co.whitbread.shared.auth.jwt;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProviderTokenVerifierTest {

    private static final String ALGORITHM = "RSA";
    private static final String SESSION_ID = "1234";
    private static final String PROFILE_CLAIM = "profile";
    private static final String BEARER_PREFIX = "Bearer";

    private KeyPair keyPair;

    @Mock
    private PublicKeyProvider keyProvider;

    private TokenExtractor tokenExtractor;


    @InjectMocks
    @Spy
    private ProviderTokenVerifier underTest;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        keyPair = generateRandomKeyPair();
        tokenExtractor = new TokenExtractor();
        when(keyProvider.retrievePublicKey(anyString())).thenReturn(keyPair.getPublic());
        doReturn("auth").when(underTest).getIssuer();

    }

    @Test
    public void verifyAndRetrieveToken_ValidToken() {
        // Given a valid token within a authorization header
        String authorization = getAuthorizationHeader(
                generateToken(keyPair, getCurrentDateWithRange(1))
        );
        Optional<String> tokenOpt = tokenExtractor.extractToken(authorization);
        String extractedToken = tokenOpt.orElseThrow(() -> new RuntimeException("Could not extract token"));


        // When we verify and retrieve a sessionID
        Optional<DecodedJWT> optJwt = underTest.verifyAndDecodeToken(extractedToken);

        assertTrue(optJwt.isPresent());

    }

    @Test
    public void verifyAndRetrieveToken_ExpiredToken() {
        // Given an expired valid token within a authorization header
        String authorization = getAuthorizationHeader(
                generateToken(keyPair, getCurrentDateWithRange(-1))
        );

        Optional<String> tokenOpt = tokenExtractor.extractToken(authorization);
        String extractedToken = tokenOpt.orElseThrow(() -> new RuntimeException("Could not extract token"));


        // A specific token expired exception is thrown
        exception.expect(TokenVerificationException.class);
        exception.expectCause(CoreMatchers.isA(TokenExpiredException.class));

        // When we verify and retrieve a session ID
        underTest.verifyAndDecodeToken(extractedToken);
    }

    @Test
    public void verifyAndRetrieveToken_InvalidToken() {
        // Given an invalid token within a authorization header
        String authorization = getAuthorizationHeader("invalid_token");


        // A specific token decode exception is thrown
        exception.expect(TokenVerificationException.class);
        exception.expectCause(CoreMatchers.isA(JWTDecodeException.class));

        // When we verify and retrieve a session ID
        underTest.verifyAndDecodeToken(authorization);
    }

    private KeyPair generateRandomKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(2048, random);
        return keyGen.generateKeyPair();
    }

    private String generateToken(KeyPair keyPair, Date expiryDate) throws JWTCreationException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(PROFILE_CLAIM, new Profile(SESSION_ID));

        return Jwts.builder()
                .issuer("auth")
                .expiration(expiryDate)
                .claims().add(claims).and()
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private Date getCurrentDateWithRange(int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    private String getAuthorizationHeader(String token) {
        return String.format("%s %s",
                BEARER_PREFIX,
                token
        );
    }

    @Data
    @RequiredArgsConstructor
    private static class Profile {
        private final String sessionId;
    }
}
package uk.co.whitbread.shared.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nimbusds.jwt.JWTClaimsSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.auth.account.CCUIDetails;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.jwt.ProviderTokenVerifier;
import uk.co.whitbread.shared.auth.jwt.TokenExtractor;
import uk.co.whitbread.shared.auth.jwt.VerifiedToken;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final String NAMESPACE = "https://premierinn.com";
    private static final String AUTH_HEADER = "Bearer valid.jwt.token";

    @Mock
    private ProviderTokenVerifier verifier;

    private TokenService tokenService;

    private JWTClaimsSet fullClaims;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(List.of(verifier), new TokenExtractor());
        fullClaims = new JWTClaimsSet.Builder()
                .claim("name", "user@example.com")
                .claim("email", "ccui@example.com")
                .claim("bookingFlow", "standard")
                .claim("profile", Map.of(
                        "companyId", "C123",
                        "employeeId", "E456",
                        "sessionId", "sess-789",
                        "accessLevel", "admin"
                ))
                .claim(NAMESPACE + "/customerAccountId", "CA-100")
                .claim(NAMESPACE + "/companyAccountId", "CO-200")
                .claim(NAMESPACE + "/employeeAccountId", "EA-300")
                .claim(NAMESPACE + "/email", "ns-user@example.com")
                .build();
    }

    @Test
    void retrieveAndVerifyCCUIToken_validToken_returnsCCUIDetails() {
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        CCUIDetails result = tokenService.retrieveAndVerifyCCUIToken(AUTH_HEADER);

        assertEquals("ccui@example.com", result.getEmail());
        assertEquals("standard", result.getBookingFlow());
    }

    @Test
    void retrieveAndVerifyCCUIToken_invalidToken_throwsException() {
        when(verifier.verifyAndDecodeToken(anyString())).thenReturn(Optional.empty());

        assertThrows(TokenVerificationException.class,
                () -> tokenService.retrieveAndVerifyCCUIToken(AUTH_HEADER));
    }

    @Test
    void retrieveAndVerifyCCUIToken_nullAuth_returnsEmptyCCUIDetails() {
        CCUIDetails result = tokenService.retrieveAndVerifyCCUIToken(null);

        assertNull(result.getEmail());
        assertNull(result.getBookingFlow());
    }

    @Test
    void retrieveAndVerifyToken_validToken_returnsSessionId() {
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        Optional<String> result = tokenService.retrieveAndVerifyToken(AUTH_HEADER);

        assertTrue(result.isPresent());
        assertEquals("sess-789", result.get());
    }

    @Test
    void retrieveEmailAndVerifyToken_validToken_returnsEmail() {
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        Optional<String> result = tokenService.retrieveEmailAndVerifyToken(AUTH_HEADER);

        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get());
    }

    @Test
    void retrieveEmployeeDetailsAndVerifyToken_validToken_returnsDetails() {
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        EmployeeDetails result = tokenService.retrieveEmployeeDetailsAndVerifyToken(AUTH_HEADER);

        assertEquals("C123", result.getCompanyId());
        assertEquals("E456", result.getEmployeeId());
    }

    @Test
    void retrieveEmployeeDetailsAndVerifyToken_nullAuth_returnsNullFields() {
        EmployeeDetails result = tokenService.retrieveEmployeeDetailsAndVerifyToken(null);

        assertNull(result.getCompanyId());
        assertNull(result.getEmployeeId());
    }

    @Test
    void retrieveCustomerAccountIdAndVerifyToken_validToken_returnsId() {
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        Optional<String> result = tokenService.retrieveCustomerAccountIdAndVerifyToken(AUTH_HEADER);

        assertTrue(result.isPresent());
        assertEquals("CA-100", result.get());
    }

    @Test
    void retrieveCdhEmployeeDetailsAndVerifyToken_validToken_returnsDetails() {
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        CdhEmployeeDetails result = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTH_HEADER);

        assertEquals("CO-200", result.getCompanyAccountId());
        assertEquals("EA-300", result.getEmployeeAccountId());
        assertEquals("ns-user@example.com", result.getUserEmail());
        assertEquals("admin", result.getAccessLevel());
    }

    @Test
    void retrieveCdhEmployeeDetailsAndVerifyToken_nullAuth_returnsEmptyDetails() {
        CdhEmployeeDetails result = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null);

        assertNull(result.getCompanyAccountId());
        assertNull(result.getEmployeeAccountId());
        assertNull(result.getUserEmail());
        assertNull(result.getAccessLevel());
    }

    @Test
    void multipleVerifiers_firstMatchWins() {
        ProviderTokenVerifier failingVerifier = org.mockito.Mockito.mock(ProviderTokenVerifier.class);
        when(failingVerifier.verifyAndDecodeToken(anyString())).thenReturn(Optional.empty());
        when(verifier.verifyAndDecodeToken(anyString()))
                .thenReturn(Optional.of(new VerifiedToken(fullClaims, NAMESPACE)));

        tokenService = new TokenService(List.of(failingVerifier, verifier), new TokenExtractor());

        Optional<String> result = tokenService.retrieveEmailAndVerifyToken(AUTH_HEADER);
        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get());
    }

    @Test
    void allVerifiersFail_throwsTokenVerificationException() {
        when(verifier.verifyAndDecodeToken(anyString())).thenReturn(Optional.empty());

        assertThrows(TokenVerificationException.class,
                () -> tokenService.retrieveEmailAndVerifyToken(AUTH_HEADER));
    }

    @Test
    void interruptedWhileWaiting_restoresInterruptStatus() {

        Thread.currentThread().interrupt();
        try {
            assertThrows(TokenVerificationException.class,
                    () -> tokenService.retrieveEmailAndVerifyToken(AUTH_HEADER));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }
}

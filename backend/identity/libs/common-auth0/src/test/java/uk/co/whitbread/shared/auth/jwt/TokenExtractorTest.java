package uk.co.whitbread.shared.auth.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenExtractorTest {

    private TokenExtractor tokenExtractor;

    @BeforeEach
    void setUp() {
        tokenExtractor = new TokenExtractor();
    }

    // --- extractToken ---

    @Test
    void extractToken_validBearerHeader_returnsToken() {
        Optional<String> result = tokenExtractor.extractToken("Bearer abc.def.ghi");
        assertTrue(result.isPresent());
        assertEquals("abc.def.ghi", result.get());
    }

    @Test
    void extractToken_nullHeader_returnsEmpty() {
        assertTrue(tokenExtractor.extractToken(null).isEmpty());
    }

    @Test
    void extractToken_emptyString_returnsEmpty() {
        assertTrue(tokenExtractor.extractToken("").isEmpty());
    }

    @Test
    void extractToken_missingBearerPrefix_returnsEmpty() {
        assertTrue(tokenExtractor.extractToken("abc.def.ghi").isEmpty());
    }

    @Test
    void extractToken_extraWhitespace_returnsToken() {
        Optional<String> result = tokenExtractor.extractToken("  Bearer   abc.def.ghi  ");
        assertTrue(result.isPresent());
        assertEquals("abc.def.ghi", result.get());
    }

    // --- retrieveEmail ---

    @Test
    void retrieveEmail_presentClaim_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("name", "user@example.com")
                .build();
        assertEquals(Optional.of("user@example.com"), tokenExtractor.retrieveEmail(claims));
    }

    @Test
    void retrieveEmail_missingClaim_returnsEmpty() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().build();
        assertTrue(tokenExtractor.retrieveEmail(claims).isEmpty());
    }

    // --- retrieveCCUIEmail ---

    @Test
    void retrieveCCUIEmail_presentClaim_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("email", "ccui@example.com")
                .build();
        assertEquals(Optional.of("ccui@example.com"), tokenExtractor.retrieveCCUIEmail(claims));
    }

    // --- retrieveBookingFlow ---

    @Test
    void retrieveBookingFlow_presentClaim_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("bookingFlow", "standard")
                .build();
        assertEquals(Optional.of("standard"), tokenExtractor.retrieveBookingFlow(claims));
    }

    // --- profile-nested claims ---

    @Test
    void retrieveCompanyId_fromProfile_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("profile", Map.of("companyId", "C123"))
                .build();
        assertEquals(Optional.of("C123"), tokenExtractor.retrieveCompanyId(claims));
    }

    @Test
    void retrieveEmployeeId_fromProfile_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("profile", Map.of("employeeId", "E456"))
                .build();
        assertEquals(Optional.of("E456"), tokenExtractor.retrieveEmployeeId(claims));
    }

    @Test
    void retrieveSessionId_fromProfile_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("profile", Map.of("sessionId", "sess-789"))
                .build();
        assertEquals(Optional.of("sess-789"), tokenExtractor.retrieveSessionId(claims));
    }

    @Test
    void retrieveAccessLevel_fromProfile_returnsValue() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("profile", Map.of("accessLevel", "admin"))
                .build();
        assertEquals(Optional.of("admin"), tokenExtractor.retrieveAccessLevel(claims));
    }

    @Test
    void retrieveProfileClaim_missingProfile_returnsEmpty() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().build();
        assertTrue(tokenExtractor.retrieveCompanyId(claims).isEmpty());
    }

    @Test
    void retrieveProfileClaim_profileNotAMap_returnsEmpty() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("profile", "not-a-map")
                .build();
        assertTrue(tokenExtractor.retrieveCompanyId(claims).isEmpty());
    }

    @Test
    void retrieveProfileClaim_keyMissing_returnsEmpty() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("profile", Map.of("other", "value"))
                .build();
        assertTrue(tokenExtractor.retrieveCompanyId(claims).isEmpty());
    }

    // --- namespace-prefixed claims ---

    @Test
    void retrieveCustomerAccountId_withNamespace_returnsValue() {
        String namespace = "https://premierinn.com";
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim(namespace + "/customerAccountId", "CA-100")
                .build();
        assertEquals(Optional.of("CA-100"),
                tokenExtractor.retrieveCustomerAccountId(claims, namespace));
    }

    @Test
    void retrieveCompanyAccountId_withNamespace_returnsValue() {
        String namespace = "https://premierinn.com";
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim(namespace + "/companyAccountId", "CO-200")
                .build();
        assertEquals(Optional.of("CO-200"),
                tokenExtractor.retrieveCompanyAccountId(claims, namespace));
    }

    @Test
    void retrieveEmployeeAccountId_withNamespace_returnsValue() {
        String namespace = "https://premierinn.com";
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim(namespace + "/employeeAccountId", "EA-300")
                .build();
        assertEquals(Optional.of("EA-300"),
                tokenExtractor.retrieveEmployeeAccountId(claims, namespace));
    }

    @Test
    void retrieveUserEmail_withNamespace_returnsValue() {
        String namespace = "https://premierinn.com";
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim(namespace + "/email", "ns-user@example.com")
                .build();
        assertEquals(Optional.of("ns-user@example.com"),
                tokenExtractor.retrieveUserEmail(claims, namespace));
    }

    @Test
    void retrieveNamespacedClaim_missingClaim_returnsEmpty() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().build();
        assertTrue(tokenExtractor.retrieveCustomerAccountId(claims, "https://premierinn.com").isEmpty());
    }
}

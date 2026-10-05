package uk.co.whitbread.shared.auth.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.constants.ClaimNames;

@Slf4j
public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer";
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String NAMESPACE_SEPARATOR = "/";
    private static final String STRING_CLAIM_PARSE_WARNING = "Failed to parse claim '{}' as String: {}";
    private static final String UNEXPECTED_PROFILE_CLAIM_TYPE_WARNING = "Unexpected profile claim type: {}";

    /**
     * Extracts the raw JWT token string from a Bearer authorization header.
     *
     * @param authorization the Authorization header value
     * @return the token string, or empty if the header is missing or malformed
     */
    public Optional<String> extractToken(String authorization) {
        String[] parts = Optional.ofNullable(authorization)
            .map(String::trim)
            .map(s -> s.split(WHITESPACE_REGEX))
            .orElse(new String[0]);

        if (parts.length >= 2 && BEARER_PREFIX.equals(parts[0])) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }

    public Optional<String> retrieveEmail(JWTClaimsSet claims) {
        return getStringClaim(claims, ClaimNames.NAME);
    }

    public Optional<String> retrieveCCUIEmail(JWTClaimsSet claims) {
        return getStringClaim(claims, ClaimNames.EMAIL);
    }

    public Optional<String> retrieveBookingFlow(JWTClaimsSet claims) {
        return getStringClaim(claims, ClaimNames.BOOKING_FLOW);
    }

    public Optional<String> retrieveCompanyId(JWTClaimsSet claims) {
        return getProfileClaim(claims, ClaimNames.COMPANY_ID);
    }

    public Optional<String> retrieveEmployeeId(JWTClaimsSet claims) {
        return getProfileClaim(claims, ClaimNames.EMPLOYEE_ID);
    }

    public Optional<String> retrieveSessionId(JWTClaimsSet claims) {
        return getProfileClaim(claims, ClaimNames.SESSION_ID);
    }

    public Optional<String> retrieveAccessLevel(JWTClaimsSet claims) {
        return getProfileClaim(claims, ClaimNames.ACCESS_LEVEL);
    }

    public Optional<String> retrieveCustomerAccountId(JWTClaimsSet claims, String namespace) {
        return getStringClaim(claims, namespacedClaim(namespace, ClaimNames.CUSTOMER_ACCOUNT_ID));
    }

    public Optional<String> retrieveCompanyAccountId(JWTClaimsSet claims, String namespace) {
        return getStringClaim(claims, namespacedClaim(namespace, ClaimNames.COMPANY_ACCOUNT_ID));
    }

    public Optional<String> retrieveEmployeeAccountId(JWTClaimsSet claims, String namespace) {
        return getStringClaim(claims, namespacedClaim(namespace, ClaimNames.EMPLOYEE_ACCOUNT_ID));
    }

    public Optional<String> retrieveUserEmail(JWTClaimsSet claims, String namespace) {
        return getStringClaim(claims, namespacedClaim(namespace, ClaimNames.EMAIL));
    }

    private Optional<String> getStringClaim(JWTClaimsSet claims, String claimName) {
        try {
            return Optional.ofNullable(claims.getStringClaim(claimName));
        } catch (ParseException e) {
            log.warn(STRING_CLAIM_PARSE_WARNING, claimName, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> getProfileClaim(JWTClaimsSet claims, String key) {
        Object profileObj = claims.getClaim(ClaimNames.PROFILE);
        if (profileObj instanceof Map<?, ?> profile) {
            Object value = ((Map<String, Object>) profile).get(key);
            if (value instanceof String s) {
                return Optional.of(s);
            }
        } else if (profileObj != null) {
            log.warn(UNEXPECTED_PROFILE_CLAIM_TYPE_WARNING, profileObj.getClass().getName());
        }
        return Optional.empty();
    }

    private String namespacedClaim(String namespace, String claimName) {
        return namespace + NAMESPACE_SEPARATOR + claimName;
    }
}

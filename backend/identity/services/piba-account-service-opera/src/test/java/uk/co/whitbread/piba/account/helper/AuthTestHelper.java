package uk.co.whitbread.piba.account.helper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

public class AuthTestHelper {

    private static final String ISSUER = "http://localhost/";
    private static final String NAMESPACE = "http://localhost";
    private static final String AUDIENCE = "http://localhost/api/v2/";

    public static void configureAuth0Context(TenantRepository tenantRepository, JwtDecoder jwtDecoder) {
        Tenant tenant = new Tenant();
        tenant.setIssuer(ISSUER);
        tenant.setNamespace(NAMESPACE);
        tenant.setAudience(AUDIENCE);
        tenantRepository.save(tenant);

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", ISSUER);
        claims.put("sub", "auth0|testuser");
        claims.put("aud", List.of(AUDIENCE));
        claims.put("iat", Instant.now());
        claims.put("exp", Instant.now().plusSeconds(3600));

        Jwt jwt = new Jwt("mock-token-value", Instant.now(), Instant.now().plusSeconds(3600),
            headers, claims);

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
    }
}

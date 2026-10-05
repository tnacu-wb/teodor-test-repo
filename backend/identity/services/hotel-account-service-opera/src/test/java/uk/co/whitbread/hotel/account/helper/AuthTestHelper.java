package uk.co.whitbread.hotel.account.helper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

public final class AuthTestHelper {

    private static final String TOKEN = "value";
    private static final String TENANT_NAME = "PI BB Tenant";
    private static final String ISSUER = "http://localhost/";
    private static final String NAMESPACE = "http://localhost";
    private static final String AUDIENCE = "http://localhost/api/v2/";

    private AuthTestHelper() {
    }

    public static void configureAuth0Context(TenantRepository tenantRepository, JwtDecoder jwtDecoder) {
        tenantRepository.save(buildTenant());
        when(jwtDecoder.decode(anyString())).thenReturn(buildJwt());
    }

    public static Jwt buildJwt() {
        return Jwt.withTokenValue(TOKEN)
            .issuer(ISSUER)
            .issuedAt(Instant.now())
            .expiresAt(Instant.MAX)
            .header("alg", "none")
            .claim("iss", ISSUER)
            .claim("sub", "test-user")
            .claim("http://localhost/role", List.of())
            .build();
    }

    public static Tenant buildTenant() {
        Tenant tenant = new Tenant();
        tenant.setName(TENANT_NAME);
        tenant.setIssuer(ISSUER);
        tenant.setNamespace(NAMESPACE);
        tenant.setAudience(AUDIENCE);
        return tenant;
    }
}

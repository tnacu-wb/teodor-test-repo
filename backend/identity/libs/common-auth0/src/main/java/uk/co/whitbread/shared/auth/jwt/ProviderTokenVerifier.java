package uk.co.whitbread.shared.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.tenant.Tenant;

@Slf4j
public class ProviderTokenVerifier {

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    private final String issuer;
    private final String namespace;

    public ProviderTokenVerifier(Tenant tenant) {
        this.issuer = tenant.getIssuer();
        this.namespace = tenant.getNamespace();
        try {
            URL jwksUrl = new URL(tenant.getJwkUri());
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(jwksUrl);
            JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
            this.jwtProcessor = new DefaultJWTProcessor<>();
            this.jwtProcessor.setJWSKeySelector(keySelector);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                "Invalid JWKS URL for tenant '" + tenant.getName() + "': " + tenant.getJwkUri(), e);
        }
    }

    ProviderTokenVerifier(ConfigurableJWTProcessor<SecurityContext> jwtProcessor,
                          String issuer, String namespace) {
        this.jwtProcessor = jwtProcessor;
        this.issuer = issuer;
        this.namespace = namespace;
    }

    /**
     * Verifies the given JWT token string and returns the decoded claims
     * wrapped with the tenant namespace.
     *
     * @param token raw JWT string (without "Bearer " prefix)
     * @return verified token with claims and namespace, or empty if verification fails
     */
    public Optional<VerifiedToken> verifyAndDecodeToken(String token) {
        try {
            JWTClaimsSet claims = jwtProcessor.process(token, null);
            if (!issuer.equals(claims.getIssuer())) {
                log.debug("Issuer mismatch: expected '{}', got '{}'", issuer, claims.getIssuer());
                return Optional.empty();
            }
            return Optional.of(new VerifiedToken(claims, namespace));
        } catch (JOSEException | BadJOSEException | ParseException e) {
            log.debug("Token verification failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}

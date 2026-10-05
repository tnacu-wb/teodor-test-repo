package uk.co.whitbread.shared.auth.jwt;

import com.nimbusds.jwt.JWTClaimsSet;

/**
 * Wraps a verified JWT's claims together with the tenant namespace
 * that was used to verify it. This allows downstream claim extraction
 * to use the correct namespace for namespace-prefixed claims.
 */
public record VerifiedToken(JWTClaimsSet claims, String namespace) {
}

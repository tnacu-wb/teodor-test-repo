package uk.co.whitbread.shared.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor
@Getter(PROTECTED)
public class ProviderTokenVerifier {

    private final PublicKeyProvider keyProvider;
    private final String issuer;

    public Optional<DecodedJWT> verifyAndDecodeToken(String token) {

        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return Optional.of(decodedJWT);
        } catch (JWTVerificationException jwtException) {
            return Optional.empty();
        }

    }

    private DecodedJWT verifyToken(String token) {
        try {
            PublicKey publicKey = getKeyProvider().retrievePublicKey(token);
            JWTVerifier verifier = buildVerifier(publicKey);
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new TokenVerificationException("Provided token was invalid or expired", exception);
        }
    }

    private JWTVerifier buildVerifier(PublicKey publicKey) {
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
        return JWT.require(algorithm)
                .withIssuer(getIssuer())
                .build();
    }

}

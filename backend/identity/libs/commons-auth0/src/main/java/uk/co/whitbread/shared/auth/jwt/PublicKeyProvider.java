package uk.co.whitbread.shared.auth.jwt;


import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;

import java.security.PublicKey;

import static org.apache.commons.lang3.StringUtils.isBlank;

@AllArgsConstructor
public class PublicKeyProvider {

    private final JwkProvider provider;

    public PublicKey retrievePublicKey(String token) {
        DecodedJWT jwt = JWT.decode(token);
        String keyId = jwt.getKeyId();

        if (isBlank(keyId)) {
            throw new TokenVerificationException("Key ID (kid) could not be found in token header.");
        }

        try {
            Jwk jwk = provider.get(keyId);
            return jwk.getPublicKey();
        } catch (JwkException e) {
            throw new TokenVerificationException("Error while retrieving Public Key in JWK.", e);
        }
    }

}

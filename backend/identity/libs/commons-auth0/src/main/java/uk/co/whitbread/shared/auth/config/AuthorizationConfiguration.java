package uk.co.whitbread.shared.auth.config;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import uk.co.whitbread.shared.auth.jwt.ProviderTokenVerifier;
import uk.co.whitbread.shared.auth.jwt.PublicKeyProvider;
import uk.co.whitbread.shared.auth.jwt.TokenExtractor;
import uk.co.whitbread.shared.auth.properties.EncryptionProperties;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;
import uk.co.whitbread.shared.auth.properties.PasswordlessProperties;
import uk.co.whitbread.shared.auth.properties.TokenProperties;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.service.EncryptionService;
import uk.co.whitbread.shared.auth.service.PasswordlessService;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class AuthorizationConfiguration {

    @Bean
    @Conditional(TokenServiceCondition.class)
    public TokenService authTokenService(TokenProperties properties) {
        List<ProviderTokenVerifier> providerTokenVerifiers = tokenVerifiers(properties);
        return new TokenService(providerTokenVerifiers, new TokenExtractor());
    }

    @Bean
    @Conditional(ManagementServiceCondition.class)
    public ManagementService authManagementService(ManagementProperties managementProperties) {
        return new ManagementService(managementProperties);
    }

    @Bean
    @Conditional(PasswordlessServiceCondition.class)
    public PasswordlessService passwordlessService(PasswordlessProperties properties) {
        return new PasswordlessService(properties);
    }

    @Bean
    @Conditional(EncryptionServiceCondition.class)
    public EncryptionService encryptionService(EncryptionProperties properties) {
        return new EncryptionService(properties);
    }

    protected List<ProviderTokenVerifier> tokenVerifiers(TokenProperties properties) {
        return properties.getProviders()
                .stream()
                .map(authProvider -> {

                    JwkProvider jwkProvider = new JwkProviderBuilder(authProvider.getHost()).build();
                    PublicKeyProvider publicKeyProvider = new PublicKeyProvider(jwkProvider);

                    return new ProviderTokenVerifier(publicKeyProvider, authProvider.getIssuer());
                })
                .collect(toList());
    }

}

package uk.co.whitbread.shared.auth.config;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.auth.jwt.ProviderTokenVerifier;
import uk.co.whitbread.shared.auth.jwt.TokenExtractor;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.SecurityProperties;
import uk.co.whitbread.shared.auth.webclient.WebClientRetryHandler;

/**
 * Registers TokenService and ManagementService beans ported from commons-auth0.
 * Imported by {@link uk.co.whitbread.shared.auth.EnableAuthorization}.
 *
 * <p>Does NOT create a SecurityFilterChain — that is handled by
 * {@link AuthConfiguration} (from {@code @EnableAuth}).
 */
@Configuration
@EnableConfigurationProperties({ManagementProperties.class})
public class LegacyAuthorizationConfiguration {

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    @ConditionalOnProperty(prefix = "auth", name = "tenants[0].issuer")
    public TokenService authTokenService(SecurityProperties securityProperties) {
        List<ProviderTokenVerifier> verifiers = securityProperties.getTenants().stream()
                .map(ProviderTokenVerifier::new)
                .collect(Collectors.toList());
        return new TokenService(verifiers, new TokenExtractor());
    }

    @Bean
    @ConditionalOnMissingBean(ManagementService.class)
    @ConditionalOnProperty(prefix = "auth.management",
            name = {"domain", "client-id", "client-secret", "audience"})
    public ManagementService authManagementService(ManagementProperties properties,
            WebClientRetryHandler webClientRetryHandler) {
        return new ManagementService(properties, WebClient.builder(), webClientRetryHandler);
    }
}

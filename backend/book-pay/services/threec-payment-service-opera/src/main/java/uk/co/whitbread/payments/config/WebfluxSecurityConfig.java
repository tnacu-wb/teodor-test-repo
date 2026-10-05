package uk.co.whitbread.payments.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec.FrameOptionsSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;


@EnableWebFluxSecurity
@Configuration
public class WebfluxSecurityConfig {

    @Value("${whitbread.api.key}")
    private String httpHeaderName;
    @Value("${whitbread.api.value}")
    private String httpHeaderValue;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {

        http
                .headers(headerSpec -> headerSpec.frameOptions(FrameOptionsSpec::disable))
                .csrf(CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                    .pathMatchers("/reconcile").access(getAuthorizationContextReactiveAuthorizationManager())
                    .pathMatchers("/payments/*/refund").access(getAuthorizationContextReactiveAuthorizationManager())
                    .pathMatchers("/refunds").access(getAuthorizationContextReactiveAuthorizationManager())
                    .pathMatchers("/refunds/partial").access(getAuthorizationContextReactiveAuthorizationManager())
                    .anyExchange().permitAll());
        return http.build();
    }

    private ReactiveAuthorizationManager<AuthorizationContext> getAuthorizationContextReactiveAuthorizationManager() {
        return (mono, authorizationContext) -> {
            var headers = authorizationContext.getExchange().getRequest().getHeaders();
            if (headers.getFirst(httpHeaderName) == null) {
                return Mono.just(new AuthorizationDecision(false));
            }
            return Mono.just(new AuthorizationDecision(headers.getFirst(httpHeaderName).equals(httpHeaderValue)));
        };
    }
}

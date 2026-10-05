package uk.co.whitbread.shared.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.filter.AuthenticationFilter;
import uk.co.whitbread.shared.auth.properties.TokenProperties;

public class AuthFilterConfiguration {

    @Bean
    public AuthenticationFilter authenticationFilter(TokenService tokenService, TokenProperties properties, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return new AuthenticationFilter(tokenService, properties,resolver);
    }

}

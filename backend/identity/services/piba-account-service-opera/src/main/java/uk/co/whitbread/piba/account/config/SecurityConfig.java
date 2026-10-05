package uk.co.whitbread.piba.account.config;

import org.springframework.context.annotation.Bean;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
public class SecurityConfig {

    @Bean
    public ApplicationEndpointConfigurer applicationEndpointConfigurer() {
        return endpointRegistry -> endpointRegistry
            .requestMatchers("/piba/**", "/v2/piba/**").authenticated()
            .anyRequest().permitAll();
    }
}

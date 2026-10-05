package uk.co.whitbread.piba.registration.config;

import org.springframework.context.annotation.Bean;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
public class SecurityConfig {

    @Bean
    public ApplicationEndpointConfigurer applicationEndpoint() {
        return registry -> registry
                .requestMatchers("/piba/**").authenticated()
                .anyRequest().permitAll();
    }
}

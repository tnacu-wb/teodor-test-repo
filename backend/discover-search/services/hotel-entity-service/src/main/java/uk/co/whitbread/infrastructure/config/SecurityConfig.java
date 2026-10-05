package uk.co.whitbread.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public ApplicationEndpointConfigurer applicationEndpoint() {
    return registry -> registry
        .anyRequest().permitAll();
  }
}
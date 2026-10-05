package uk.co.whitbread.digitalkey.infrastructure.config;

import org.springframework.context.annotation.Bean;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
public class SecurityConfig {

  @Bean
  public ApplicationEndpointConfigurer applicationEndpoint() {
    return authorizeHttpRequests -> authorizeHttpRequests
        .requestMatchers("/v1/digital-key/**").permitAll()
        .anyRequest().permitAll();
  }
}

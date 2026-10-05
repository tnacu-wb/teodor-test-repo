package uk.co.whitbread.spending.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
public class SecurityConfig {

  @Bean
  public ApplicationEndpointConfigurer applicationEndpoint() {
    return registry -> registry
        .requestMatchers(HttpMethod.POST, "/v1/spending/custom").authenticated()
        .anyRequest().permitAll();
  }
}

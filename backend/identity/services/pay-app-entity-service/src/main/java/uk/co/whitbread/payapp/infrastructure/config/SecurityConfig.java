package uk.co.whitbread.payapp.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
public class SecurityConfig {

  @Bean
  public ApplicationEndpointConfigurer securityAuthorizedUrl() {

    return registry -> registry
        .requestMatchers(HttpMethod.POST, "/v1/pay-app/custom").authenticated()
        .anyRequest().permitAll();
  }
}
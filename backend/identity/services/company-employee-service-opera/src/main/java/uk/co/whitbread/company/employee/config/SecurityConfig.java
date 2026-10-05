package uk.co.whitbread.company.employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@Configuration
@EnableAuth
public class SecurityConfig {

  @Bean
  public ApplicationEndpointConfigurer applicationEndpoint() {
    return registry -> registry.anyRequest().permitAll();
  }
}
package uk.co.whitbread.booking.infrastructure.config;



import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.EnableAuthorization;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
@EnableAuthorization
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public ApplicationEndpointConfigurer applicationEndpoint() {

    return registry -> registry
        .requestMatchers(HttpMethod.GET, "/v1/bookings/actuator/health/readiness").permitAll()
        .anyRequest().permitAll();
  }
}
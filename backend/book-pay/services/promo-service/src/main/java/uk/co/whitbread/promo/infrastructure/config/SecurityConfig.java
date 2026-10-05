package uk.co.whitbread.promo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import uk.co.whitbread.shared.auth.EnableAuth;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;

@EnableAuth
public class SecurityConfig {

  @Bean
    public ApplicationEndpointConfigurer applicationEndpoint() {
    return requests -> requests
                .requestMatchers(
                        HttpMethod.POST,
                        "/v1/sample/hotels/availabilities/custom"
                ).authenticated()

                .requestMatchers(
                        HttpMethod.GET,
                        "/**/custom"
                ).permitAll()

                .anyRequest()
                .permitAll();
  }
}

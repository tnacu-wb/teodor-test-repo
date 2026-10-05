package uk.co.whitbread.availabilitycacheservice.infrastructure.config.snowdrop;

import feign.auth.BasicAuthRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class SnowdropConfiguration {

  private final SnowdropProperties snowdropProperties;

  @Bean
  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor(
        snowdropProperties.getAuthentication().getUsername(),
        snowdropProperties.getAuthentication().getPassword()
    );
  }

}

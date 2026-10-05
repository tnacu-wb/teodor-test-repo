package uk.co.whitbread.availabilitycacheservice.infrastructure.config.aem;

import feign.auth.BasicAuthRequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@Slf4j
public class AemFeignConfiguration {

  private final AemFeignClientProperties aemFeignClientProperties;

  @Bean
  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    log.trace("aem credentials are username - {}",
        aemFeignClientProperties.getUsername());
    return new BasicAuthRequestInterceptor(
        aemFeignClientProperties.getUsername(),
        aemFeignClientProperties.getPassword()
    );
  }
}

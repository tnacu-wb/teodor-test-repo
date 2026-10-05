package uk.co.whitbread.company.infrastructure.rest.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;

@Data
@Slf4j
@Configuration
public class RestClientConfig {

  private final CdhAdapterProperties cdhAdapterProperties;

  @Bean(name = "cdhAdapterRestClient")
  RestClient cdhAdapterRestClient() {
    return RestClient.builder()
        .baseUrl(cdhAdapterProperties.getHost())
        .build();
  }

  @Bean
  WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

}

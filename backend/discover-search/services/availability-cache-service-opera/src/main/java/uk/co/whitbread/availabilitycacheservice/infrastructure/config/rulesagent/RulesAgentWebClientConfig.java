package uk.co.whitbread.availabilitycacheservice.infrastructure.config.rulesagent;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RulesAgentWebClientConfig {
  
  @Bean
  @Qualifier("rulesAgentWebClient")
  public WebClient rulesAgentWebClient(RulesAgentProperties properties) {
    return WebClient.builder().baseUrl(properties.getHost()).build();
  }
}

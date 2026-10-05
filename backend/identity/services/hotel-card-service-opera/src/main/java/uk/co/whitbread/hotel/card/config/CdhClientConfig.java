package uk.co.whitbread.hotel.card.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.cdh.CustomerDataHubClient;
import uk.co.whitbread.shared.cdh.oauth.OAuthProvider;

@Configuration
public class CdhClientConfig {

  @Bean
  @Primary
  public CustomerDataHubClient customerDataHubClient(
      @Qualifier("cdhWebClient") WebClient cdhWebClient,
      ExchangeStrategies exchangeStrategies,
      OAuthProvider oauthProvider) {
    WebClient configuredCdhWebClient = cdhWebClient.mutate()
        .exchangeStrategies(exchangeStrategies)
        .build();

    return new CustomerDataHubClient(configuredCdhWebClient, oauthProvider);
  }
}

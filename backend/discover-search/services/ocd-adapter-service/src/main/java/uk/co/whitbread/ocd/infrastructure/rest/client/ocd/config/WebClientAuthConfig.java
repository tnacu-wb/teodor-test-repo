package uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config;

import static uk.co.whitbread.ocd.infrastructure.rest.utils.ConfigurationUtils.logRequest;

import io.netty.handler.logging.LogLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.ClientCredentialsReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.properties.OcdProperties;

@RequiredArgsConstructor
@Configuration
public class WebClientAuthConfig {

  private final OcdProperties ocdProperties;

  @Bean
  public ReactiveOAuth2AuthorizedClientProvider customAuthClientProvider(WebClient.Builder webClientBuilder) {
    final WebClientReactiveClientCredentialsTokenResponseClient clientCredentialsResponseClient =
        new WebClientReactiveClientCredentialsTokenResponseClient();
    final PasswordReactiveOAuth2AuthorizedClientProvider passwordProvider =
        new PasswordReactiveOAuth2AuthorizedClientProvider();

    var accessTokenResponseWebClient = createAccessTokenResponseWebClient(webClientBuilder);
    clientCredentialsResponseClient.setWebClient(accessTokenResponseWebClient);
    passwordProvider.setWebClient(accessTokenResponseWebClient);

    return new ReactiveOAuth2AuthorizedClientProvider() {
      final ClientCredentialsReactiveOAuth2AuthorizedClientProvider clientCredentialsProvider =
          new ClientCredentialsReactiveOAuth2AuthorizedClientProvider();

      {
        clientCredentialsProvider.setAccessTokenResponseClient(clientCredentialsResponseClient);
      }

      @Override
      public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
        if (Boolean.TRUE.equals(ocdProperties.getIsClientCredentialsEnabled())) {
          return clientCredentialsProvider.authorize(context)
              .flatMap(authorizedClient -> authorizedClient != null
                  ? Mono.just(new OAuth2AuthorizedClient(
                  authorizedClient.getClientRegistration(),
                  authorizedClient.getPrincipalName(),
                  authorizedClient.getAccessToken()))
                  : Mono.empty());
        } else {
          return passwordProvider.authorize(context)
              .flatMap(authorizedClient -> authorizedClient != null
                  ? Mono.just(new OAuth2AuthorizedClient(
                  authorizedClient.getClientRegistration(),
                  authorizedClient.getPrincipalName(),
                  authorizedClient.getAccessToken()))
                  : Mono.empty());
        }
      }
    };
  }

  private WebClient createAccessTokenResponseWebClient(WebClient.Builder webClientBuilder) {
    var httpClient = HttpClient.create()
        .compress(true);
    if (Boolean.TRUE.equals(ocdProperties.getWireTapAccessToken())) {
      httpClient =
          httpClient.wiretap(this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
    }
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .filter((clientRequest, exchangeFunction) -> {
          ClientRequest newClientRequest = ClientRequest.from(clientRequest)
              .headers(httpHeaders -> {
                if (Boolean.TRUE.equals(ocdProperties.getIsClientCredentialsEnabled())) {
                  httpHeaders.set(OcdConstants.ENTERPRISE_ID, ocdProperties.getEnterpriseId());
                }
                httpHeaders.set(OcdConstants.APP_KEY_HEADER, ocdProperties.getAppKey());
              })
              .build();
          return exchangeFunction.exchange(newClientRequest);
        })
        .filter(logRequest())
        .build();
  }
}

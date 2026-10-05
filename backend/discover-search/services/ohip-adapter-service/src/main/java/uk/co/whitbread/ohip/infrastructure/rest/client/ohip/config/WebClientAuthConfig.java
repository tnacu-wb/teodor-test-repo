package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.utils.ConfigurationUtils.logRequest;

import io.netty.handler.logging.LogLevel;
import java.time.Duration;
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
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;

@RequiredArgsConstructor
@Configuration
public class WebClientAuthConfig {

  private final OhipProperties ohipProperties;

  @Bean
  public ReactiveOAuth2AuthorizedClientProvider customAuthClientProvider(WebClient.Builder webClientBuilder,
      final UnleashWrapper<FeatureFlag> unleashWrapper,
      final FeatureFlag featureFlag) {
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

        var flag = featureFlag.getUseTokenRefreshSkew();
        if (flag != null && unleashWrapper.isEnabled(flag)) {
          clientCredentialsProvider.setClockSkew(Duration.ofMinutes(ohipProperties.getTokenRefreshClockSkew()));
          passwordProvider.setClockSkew(Duration.ofMinutes(ohipProperties.getTokenRefreshClockSkew()));
        }
      }

      @Override
      public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
        if (Boolean.TRUE.equals(ohipProperties.getIsClientCredentialsEnabled())) {
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
    if (Boolean.TRUE.equals(ohipProperties.getWireTapAccessToken())) {
      httpClient =
          httpClient.wiretap(this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
    }
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .filter((clientRequest, exchangeFunction) -> {
          ClientRequest newClientRequest = ClientRequest.from(clientRequest)
              .headers(httpHeaders -> {
                if (Boolean.TRUE.equals(ohipProperties.getIsClientCredentialsEnabled())) {
                  httpHeaders.set(OhipConstants.ENTERPRISE_ID, ohipProperties.getEnterpriseId());
                }
                httpHeaders.set(OhipConstants.APP_KEY_HEADER, ohipProperties.getAppKey());
              })
              .build();
          return exchangeFunction.exchange(newClientRequest);
        })
        .filter(logRequest())
        .build();
  }
}

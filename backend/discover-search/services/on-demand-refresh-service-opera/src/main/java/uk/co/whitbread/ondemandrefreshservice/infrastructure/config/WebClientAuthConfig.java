package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.ConfigurationUtils.logRequest;

import io.netty.handler.logging.LogLevel;
import java.time.Duration;
import java.util.function.Function;
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
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters.UnleashWrapper;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.FeatureFlag;

@RequiredArgsConstructor
@Configuration
public class WebClientAuthConfig {

  private final OperaProperties operaProperties;

  @Bean
  public ReactiveOAuth2AuthorizedClientProvider customAuthClientProvider(
      WebClient.Builder webClientBuilder,
      final UnleashWrapper<FeatureFlag> unleashWrapper,
      final FeatureFlag featureFlag) {
    final WebClientReactiveClientCredentialsTokenResponseClient clientCredentialsResponseClient =
        new WebClientReactiveClientCredentialsTokenResponseClient();

    var accessTokenResponseWebClient = createAccessTokenResponseWebClient(webClientBuilder);
    clientCredentialsResponseClient.setWebClient(accessTokenResponseWebClient);

    return new CustomReactiveOAuth2AuthorizedClientProvider(clientCredentialsResponseClient,
        accessTokenResponseWebClient, unleashWrapper, featureFlag);
  }

  private WebClient createAccessTokenResponseWebClient(Builder webClientBuilder) {
    var httpClient = HttpClient.create()
        .compress(true);
    if (Boolean.TRUE.equals(operaProperties.getWireTapAccessToken())) {
      httpClient =
          httpClient.wiretap(this.getClass().getCanonicalName(), LogLevel.INFO,
              AdvancedByteBufFormat.TEXTUAL);
    }
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .filter((clientRequest, exchangeFunction) -> {
          ClientRequest newClientRequest = ClientRequest.from(clientRequest)
              .headers(httpHeaders -> {
                if (Boolean.TRUE.equals(operaProperties.getIsClientCredentialsEnabled())) {
                  httpHeaders.set(OperaConstants.ENTERPRISE_ID, operaProperties.getEnterpriseId());
                }
                httpHeaders.set(OperaConstants.APP_KEY_HEADER, operaProperties.getAppKey());
              })
              .build();
          return exchangeFunction.exchange(newClientRequest);
        })
        .filter(logRequest())
        .build();
  }

  private class CustomReactiveOAuth2AuthorizedClientProvider implements
      ReactiveOAuth2AuthorizedClientProvider {

    final ClientCredentialsReactiveOAuth2AuthorizedClientProvider clientCredentialsProvider =
        new ClientCredentialsReactiveOAuth2AuthorizedClientProvider();
    final PasswordReactiveOAuth2AuthorizedClientProvider passwordProvider =
        new PasswordReactiveOAuth2AuthorizedClientProvider();

    public CustomReactiveOAuth2AuthorizedClientProvider(
        WebClientReactiveClientCredentialsTokenResponseClient clientCredentialsResponseClient,
        WebClient accessTokenResponseWebClient,
        final UnleashWrapper<FeatureFlag> unleashWrapper,
        final FeatureFlag featureFlag) {

      clientCredentialsProvider.setAccessTokenResponseClient(clientCredentialsResponseClient);
      passwordProvider.setWebClient(accessTokenResponseWebClient);

      var flag = featureFlag.getUseTokenRefreshSkew();
      if (flag != null && unleashWrapper.isEnabled(flag)) {
        clientCredentialsProvider.setClockSkew(Duration.ofMinutes(operaProperties.getTokenRefreshClockSkew()));
        passwordProvider.setClockSkew(Duration.ofMinutes(operaProperties.getTokenRefreshClockSkew()));
      }
    }

    @Override
    public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
      if (Boolean.TRUE.equals(operaProperties.getIsClientCredentialsEnabled())) {
        return clientCredentialsProvider.authorize(context)
            .flatMap(getoAuth2AuthorizedClientMonoFunction());
      } else {
        return passwordProvider.authorize(context)
            .flatMap(getoAuth2AuthorizedClientMonoFunction());
      }
    }
  }

  private static Function<OAuth2AuthorizedClient, Mono<? extends OAuth2AuthorizedClient>> getoAuth2AuthorizedClientMonoFunction() {
    return authorizedClient -> authorizedClient != null
        ? Mono.just(new OAuth2AuthorizedClient(
        authorizedClient.getClientRegistration(),
        authorizedClient.getPrincipalName(),
        authorizedClient.getAccessToken()))
        : Mono.empty();
  }
}
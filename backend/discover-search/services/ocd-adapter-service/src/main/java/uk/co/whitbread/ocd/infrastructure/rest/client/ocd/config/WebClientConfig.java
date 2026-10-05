package uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config;

import static java.util.Arrays.asList;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.properties.OcdProperties;
import uk.co.whitbread.ocd.infrastructure.rest.utils.ConfigurationUtils;

@Data
@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

  public static final String CLIENT_CREDENTIALS = "client_credentials";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";

  private final OcdProperties ocdProperties;
  private final JsonMapper objectMapper;


  @Bean
  public ReactiveClientRegistrationRepository registrationRepository() {
    boolean isClientCredentialsEnabled = ocdProperties.getIsClientCredentialsEnabled();
    var clientRegistrationBuilder = ClientRegistration
        .withRegistrationId(OcdConstants.REGISTRATION_ID)
        .tokenUri(
            StringUtils.join(asList(ocdProperties.getHost(), ocdProperties.getAuthEndpoint()),
                '/'))
        .clientId(ocdProperties.getClientId())
        .clientSecret(ocdProperties.getClientSecret())
        .authorizationGrantType(new AuthorizationGrantType(
            isClientCredentialsEnabled ? CLIENT_CREDENTIALS : PASSWORD
        ))
        .scope(isClientCredentialsEnabled ? ocdProperties.getScope() : null)
        .build();
    return new InMemoryReactiveClientRegistrationRepository(clientRegistrationBuilder);
  }

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("ocdConnectionProvider")
        .maxIdleTime(Duration.ofSeconds(10))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofSeconds(120))
        .maxConnections(500)
        .build();
  }

  @Bean
  public HttpClient httpClient(@Value("${config.httpClient.connectionTimeout}") Integer connectionTimeout,
                               @Value("${config.httpClient.responseTimeout}") Long responseTimeout) {
    return HttpClient.create(connectionProvider())
        //connection timeout is a period within which a connection between a client and a server must be established
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
        //TCP check probes when the connection is idle
        .option(ChannelOption.SO_KEEPALIVE, true)
        //response timeout is the time we wait to receive a response after sending a request
        .responseTimeout(Duration.ofSeconds(responseTimeout))
        //Keep-Alive support for the outgoing request
        .keepAlive(true)
        .doOnRequest(
            (request, connection) -> connection.addHandlerFirst(new WebClientLoggingHandler()));
  }

  @Bean("ocdWebClient")
  public WebClient ocdWebClient(
      final ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
      WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    final ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
    oauth2Client.setDefaultClientRegistrationId(OcdConstants.REGISTRATION_ID);
    final int size = 16 * 1024 * 1024;

    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(ocdProperties.getHost())
        .filter(oauth2Client)
        .exchangeStrategies(ExchangeStrategies.builder().codecs(
            configurer -> {
              configurer.defaultCodecs().maxInMemorySize(size);
              configurer.defaultCodecs().jacksonJsonEncoder(
                  new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
              configurer.defaultCodecs().jacksonJsonDecoder(
                  new JacksonJsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            }).build())
        .filter(ConfigurationUtils.logRequest())
        .defaultHeaders(headers -> {
          headers.add(OcdConstants.APP_KEY_HEADER, ocdProperties.getAppKey());
          headers.add(OcdConstants.CHANNEL_CODE_HEADER, ocdProperties.getChannelCode());
        })
        .build();
  }

  @Bean
  public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
      ReactiveClientRegistrationRepository clientRegistrationRepository,
      ReactiveOAuth2AuthorizedClientProvider reactiveOAuth2AuthorizedClientProvider) {

    final AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager =
        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository));

    clientManager.setAuthorizedClientProvider(reactiveOAuth2AuthorizedClientProvider);
    if (Boolean.FALSE.equals(ocdProperties.getIsClientCredentialsEnabled())) {
      clientManager.setContextAttributesMapper(authorizeRequest -> Mono.just(Map.of(
          USERNAME, ocdProperties.getUsername(),
          PASSWORD, ocdProperties.getPassword())));
    }

    return clientManager;
  }
}



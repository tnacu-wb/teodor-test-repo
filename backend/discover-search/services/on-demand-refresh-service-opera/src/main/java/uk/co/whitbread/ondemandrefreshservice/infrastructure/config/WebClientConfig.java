package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import static java.util.Arrays.asList;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.REGISTRATION_ID;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util.WebClientUtils.getPrematureCloseExceptionRetrySpec;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.HashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service.properties.OcdAdapterProperties;

@Data
@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

  private static final String CLIENT_CREDENTIALS = "client_credentials";
  private static final String PASSWORD = "password";
  private static final String USERNAME_ATTRIBUTE = "username";
  private static final String PASSWORD_ATTRIBUTE = "password";

  private final OperaProperties operaProperties;
  private final OcdAdapterProperties ocdAdapterProperties;
  private final JsonMapper jsonMapper;

  @Bean
  @ConditionalOnMissingBean(WebClient.Builder.class)
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  public ReactiveClientRegistrationRepository registrationRepository() {
    boolean isClientCredentialsEnabled = operaProperties.getIsClientCredentialsEnabled();
    var clientRegistrationBuilder = ClientRegistration
        .withRegistrationId(REGISTRATION_ID)
        .tokenUri(
            StringUtils.join(
                asList(operaProperties.getHost(), operaProperties.getAuthEndpoint()),
                '/'))
        .clientId(operaProperties.getClientId())
        .clientSecret(operaProperties.getClientSecret())
        .authorizationGrantType(new AuthorizationGrantType(
            isClientCredentialsEnabled ? CLIENT_CREDENTIALS : PASSWORD));
    if (isClientCredentialsEnabled && StringUtils.isNotBlank(operaProperties.getScope())) {
      clientRegistrationBuilder
          .scope(operaProperties.getScope());
    }
    return new InMemoryReactiveClientRegistrationRepository(clientRegistrationBuilder.build());
  }

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("operaConnectionProvider")
        .maxIdleTime(Duration.ofSeconds(10))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofSeconds(120))
        .maxConnections(500)
        .build();
  }

  @Bean
  public HttpClient httpClient(
      @Value("${config.httpClient.connectionTimeout}") Integer connectionTimeout,
      @Value("${config.httpClient.responseTimeout}") Long responseTimeout,
      ConnectionProvider connectionProvider) {
    return HttpClient.create(connectionProvider)
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

  @Bean("operaWebClient")
  public WebClient operaWebClient(
      final ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
      WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    final ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
    oauth2Client.setDefaultClientRegistrationId(REGISTRATION_ID);

    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(operaProperties.getHost())
        .filter(oauth2Client)
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .filter((request, next) -> {
          if (request.method() == HttpMethod.GET || request.method() == HttpMethod.PUT) {
            return next.exchange(request).retryWhen(getPrematureCloseExceptionRetrySpec());
          }
          return next.exchange(request);
        })
        .defaultHeader(OperaConstants.APP_KEY_HEADER, operaProperties.getAppKey())
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
    if (Boolean.FALSE.equals(operaProperties.getIsClientCredentialsEnabled())) {
      clientManager.setContextAttributesMapper(authorizeRequest -> {
        var attributes = new HashMap<String, Object>();
        attributes.put(USERNAME_ATTRIBUTE, operaProperties.getUsername());
        attributes.put(PASSWORD_ATTRIBUTE, operaProperties.getPassword());
        return Mono.just(attributes);
      });
    }
    return clientManager;
  }

  @Bean(name = "ocdAdapterWebClient")
  public WebClient ocdAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
            .baseUrl(ocdAdapterProperties.getHost())
            .exchangeStrategies(exchangeStrategies())
            .filter(logRequest())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private void logRequest(ClientRequest clientRequest) {
    log.debug("{} {}{} {} Headers {}",
            clientRequest.method().name(),
            clientRequest.url().getHost(),
            clientRequest.url().getPath(),
            clientRequest.url().getQuery(),
            clientRequest.headers());
  }

  private ExchangeStrategies exchangeStrategies() {
    return ExchangeStrategies.builder().codecs(
        configurer -> {
          configurer.registerDefaults(false);
          configurer.customCodecs()
              .register(new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
          configurer.customCodecs()
              .register(new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
        }).build();
  }
}
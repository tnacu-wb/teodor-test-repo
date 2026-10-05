package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import static java.util.Arrays.asList;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.REGISTRATION_ID;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.TOKEN_SERVICE_REGISTRATION_ID;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.utils.ConfigurationUtils.logRequest;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.getPrematureCloseExceptionRetrySpec;

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
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.provider.DynamicOAuth2AuthorizedClientProvider;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.provider.TokenServiceOAuth2AuthorizedClientProvider;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.TokenServiceClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.properties.TokenServiceProperties;


@Data
@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

  public static final String CLIENT_CREDENTIALS = "client_credentials";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  private final OhipProperties ohipProperties;
  private final JsonMapper objectMapper;

  // Placeholder values for Spring Security OAuth2 ClientRegistration.
  // The actual token fetching happens via TokenServiceClient, not through Spring's OAuth2 flow.
  // These values are only used to satisfy Spring Security's framework requirements.
  private static final String PLACEHOLDER_TOKEN_SERVICE_URI = "placeholder://token-service";
  private static final String PLACEHOLDER_TOKEN_SERVICE_CLIENT_ID = "token-service-client";
  private static final String PLACEHOLDER_TOKEN_SERVICE_CLIENT_SECRET = "placeholder-secret";

  // Boot 4.0: WebClientAutoConfiguration no longer activates in servlet apps.
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  public ReactiveClientRegistrationRepository registrationRepository() {
    boolean isClientCredentialsEnabled = ohipProperties.getIsClientCredentialsEnabled();
    var operaClientRegistration = ClientRegistration
        .withRegistrationId(REGISTRATION_ID)
        .tokenUri(
            StringUtils.join(asList(ohipProperties.getHost(), ohipProperties.getAuthEndpoint()),
                '/'))
        .clientId(ohipProperties.getClientId())
        .clientSecret(ohipProperties.getClientSecret())
        .authorizationGrantType(new AuthorizationGrantType(
            isClientCredentialsEnabled ? CLIENT_CREDENTIALS : PASSWORD
        ))
        .scope(isClientCredentialsEnabled ? ohipProperties.getScope() : null)
        .build();

    var tokenServiceClientRegistration = ClientRegistration
        .withRegistrationId(TOKEN_SERVICE_REGISTRATION_ID)
        .tokenUri(PLACEHOLDER_TOKEN_SERVICE_URI)
        .clientId(PLACEHOLDER_TOKEN_SERVICE_CLIENT_ID)
        .clientSecret(PLACEHOLDER_TOKEN_SERVICE_CLIENT_SECRET)
        .authorizationGrantType(new AuthorizationGrantType(CLIENT_CREDENTIALS))
        .build();

    return new InMemoryReactiveClientRegistrationRepository(
        operaClientRegistration,
        tokenServiceClientRegistration
    );
  }

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("ohipConnectionProvider")
        .maxIdleTime(Duration.ofSeconds(10))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofSeconds(120))
        .maxConnections(500)
        .build();
  }

  @Bean
  public HttpClient httpClient(
      @Value("${config.httpClient.connectionTimeout}") Integer connectionTimeout,
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

  @Bean("ohipWebClient")
  public WebClient ohipWebClient(
      final ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
      final UnleashWrapper<FeatureFlag> unleashWrapper,
      final FeatureFlag featureFlag,
      WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    final ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

    final int size = 16 * 1024 * 1024;

    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(ohipProperties.getHost())
        .filter((request, next) -> {
          // Dynamically set registration ID based on feature flag for each request
          var flag = featureFlag.getUseTokenService();
          if (flag != null && unleashWrapper.isEnabled(flag)) {
            oauth2Client.setDefaultClientRegistrationId(TOKEN_SERVICE_REGISTRATION_ID);
            log.debug("UsÏing token service registration ID: {}", TOKEN_SERVICE_REGISTRATION_ID);
          } else {
            oauth2Client.setDefaultClientRegistrationId(REGISTRATION_ID);
            log.debug("Using Opera registration ID: {}", REGISTRATION_ID);
          }
          return oauth2Client.filter(request, next);
        })
        .exchangeStrategies(ExchangeStrategies.builder().codecs(
            configurer -> {
              configurer.defaultCodecs().maxInMemorySize(size);
              configurer.defaultCodecs().jacksonJsonEncoder(
                  new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
              configurer.defaultCodecs().jacksonJsonDecoder(
                  new JacksonJsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            }).build())
        .filter(logRequest())
        .filter((request, next) -> {
          if (request.method() == org.springframework.http.HttpMethod.GET) {
            return next.exchange(request).retryWhen(getPrematureCloseExceptionRetrySpec());
          }
          return next.exchange(request);
        })
        .defaultHeader(OhipConstants.APP_KEY_HEADER, ohipProperties.getAppKey())
        .build();
  }

  @Bean("rulesAgentWebClient")
  public WebClient rulesAgentWebClient(
      final RulesAgentProperties rulesAgentProperties,
      WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(rulesAgentProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
      ReactiveClientRegistrationRepository clientRegistrationRepository,
      ReactiveOAuth2AuthorizedClientProvider reactiveOAuth2AuthorizedClientProvider,
      TokenServiceClient tokenServiceClient) {

    final AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager =
        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository));

    // Create individual providers
    TokenServiceOAuth2AuthorizedClientProvider tokenServiceProvider =
        new TokenServiceOAuth2AuthorizedClientProvider(tokenServiceClient,
            clientRegistrationRepository);

    // Use dynamic provider that routes based on registration ID
    DynamicOAuth2AuthorizedClientProvider dynamicProvider = new DynamicOAuth2AuthorizedClientProvider(
        tokenServiceProvider,
        reactiveOAuth2AuthorizedClientProvider
    );

    clientManager.setAuthorizedClientProvider(dynamicProvider);

    if (Boolean.FALSE.equals(ohipProperties.getIsClientCredentialsEnabled())) {
      clientManager.setContextAttributesMapper(authorizeRequest -> Mono.just(Map.of(
          USERNAME, ohipProperties.getUsername(),
          PASSWORD, ohipProperties.getPassword())));
    }

    return clientManager;
  }

  @Bean
  public RulesAgentClient rulesAgentClient(
      final WebClient rulesAgentWebClient,
      final RulesAgentProperties rulesAgentProperties) {
    return new RulesAgentClient(rulesAgentWebClient, rulesAgentProperties);
  }

  @Bean("tokenServiceWebClient")
  public WebClient tokenServiceWebClient(
      final TokenServiceProperties tokenServiceProperties,
      WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(tokenServiceProperties.getHost())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  private ExchangeStrategies exchangeStrategies() {
    return ExchangeStrategies.builder().codecs(
        configurer -> {
          configurer.registerDefaults(false);
          configurer.customCodecs().register(
              new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
          configurer.customCodecs().register(
              new JacksonJsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
        }).build();
  }
}


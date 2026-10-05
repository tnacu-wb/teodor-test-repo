package uk.co.whitbread.infrastructure.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.function.Function;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.infrastructure.rest.client.AvailabilityCacheClient;
import uk.co.whitbread.infrastructure.rest.client.AvailabilityCacheV1Client;
import uk.co.whitbread.infrastructure.rest.client.BasketServiceClient;
import uk.co.whitbread.infrastructure.rest.client.CdhAdapterClient;
import uk.co.whitbread.infrastructure.rest.client.CompanyEntityServiceClient;
import uk.co.whitbread.infrastructure.rest.client.Dynamics365Client;
import uk.co.whitbread.infrastructure.rest.client.MigrationStatusClient;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.RulesAgentClient;
import uk.co.whitbread.infrastructure.rest.client.accounts.service.AccountServiceClient;
import uk.co.whitbread.infrastructure.rest.client.accounts.service.properties.AccountServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.infrastructure.rest.client.companyentity.service.properties.CompanyEntityServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.content.ContentServiceClient;
import uk.co.whitbread.infrastructure.rest.client.microsoftoauth.OAuthProvider;

@RequiredArgsConstructor
@Slf4j
@Data
@Configuration
public class WebClientBeanConfig {

  private final JsonMapper objectMapper;
  private final OAuthProvider oAuthProvider;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("hotelEntityConnectionProvider")
            .metrics(false)
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

  @Bean("ohipWebClient")
  public WebClient ohipWebClient(final OhipProperties ohipProperties, WebClient.Builder webClientBuilder,
                                HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(ohipProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("dynamics365WebClient")
  public WebClient dynamics365WebClient(final Dynamics365Properties dynamics365Properties,
      WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(dynamics365Properties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .filter(logResponse())
        .filter(retryOn401Function())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("availabilityCacheWebClient")
  public WebClient availabilityCacheWebClient(
      final AvailabilityCacheProperties availabilityCacheProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(availabilityCacheProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("rulesAgentWebClient")
  public WebClient rulesAgentWebClient(final RulesAgentProperties rulesAgentProperties,
      WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(rulesAgentProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("migrationStatusWebClient")
  public WebClient migrationStatusWebClient(
      final MigrationStatusProperties migrationStatusProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(migrationStatusProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("contentServiceWebClient")
  public WebClient contentServiceWebClient(
      final ContentServiceProperties contentServiceProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(contentServiceProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("accountServiceWebClient")
  public WebClient accountServiceWebClient(
      final AccountServiceProperties accountServiceProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(accountServiceProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("basketServiceWebClient")
  public WebClient basketServiceWebClient(
      final BasketProperties basketProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(basketProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("promoServiceWebClient")
  public WebClient promoServiceWebClient(
      final PromoServiceProperties promoServiceProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(promoServiceProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("cdhAdapterWebClient")
  public WebClient cdhAdapterWebClient(
      final CdhAdapterProperties cdhAdapterProperties, WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(cdhAdapterProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public CdhAdapterClient cdhAdapterClient(
      final WebClient cdhAdapterWebClient,
      final CdhAdapterProperties cdhAdapterProperties) {
    return new CdhAdapterClient(cdhAdapterProperties, cdhAdapterWebClient);
  }

  @Bean
  public MigrationStatusClient migrationStatusClient(
      final WebClient migrationStatusWebClient,
      final MigrationStatusProperties migrationStatusProperties) {
    return new MigrationStatusClient(migrationStatusWebClient, migrationStatusProperties);
  }

  @Bean
  public OhipClient ohipClient(
      final WebClient ohipWebClient, final OhipProperties ohipProperties) {
    return new OhipClient(ohipWebClient, ohipProperties);
  }

  @Bean
  public Dynamics365Client dynamics365Client(
      final WebClient dynamics365WebClient,
      final Dynamics365Properties dynamics365Properties,
      final OAuthProvider oauthprovider) {
    return new Dynamics365Client(dynamics365WebClient, dynamics365Properties, oauthprovider);
  }

  @Bean
  public AvailabilityCacheClient availabilityCacheClient(
      final WebClient availabilityCacheWebClient,
      final AvailabilityCacheProperties availabilityCacheProperties) {
    return new AvailabilityCacheClient(availabilityCacheWebClient, availabilityCacheProperties);
  }

  @Bean
  public AvailabilityCacheV1Client availabilityCacheV1Client(
      final WebClient availabilityCacheWebClient,
      final AvailabilityCacheProperties availabilityCacheProperties) {
    return new AvailabilityCacheV1Client(availabilityCacheWebClient, availabilityCacheProperties);
  }

  @Bean
  public RulesAgentClient rulesAgentClient(
      final WebClient rulesAgentWebClient,
      final RulesAgentProperties rulesAgentProperties) {
    return new RulesAgentClient(rulesAgentWebClient, rulesAgentProperties);
  }

  @Bean
  public ContentServiceClient contentServiceClient(
      final WebClient contentServiceWebClient,
      final ContentServiceProperties contentServiceProperties) {
    return new ContentServiceClient(contentServiceWebClient, contentServiceProperties);
  }

  @Bean
  public AccountServiceClient accountServiceClient(
      final WebClient accountServiceWebClient,
      final AccountServiceProperties accountServiceProperties) {
    return new AccountServiceClient(accountServiceWebClient, accountServiceProperties);
  }

  @Bean("companyEntityServiceWebClient")
  public WebClient companyEntityServiceWebClient(
      final CompanyEntityServiceProperties companyEntityServiceProperties,
      WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(companyEntityServiceProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public CompanyEntityServiceClient companyEntityServiceClient(
      final WebClient companyEntityServiceWebClient,
      final CompanyEntityServiceProperties companyEntityServiceProperties) {
    return new CompanyEntityServiceClient(companyEntityServiceWebClient, companyEntityServiceProperties);
  }

  @Bean
  public BasketServiceClient basketServiceClient(
      final WebClient basketServiceWebClient,
      final BasketProperties basketProperties) {
    return new BasketServiceClient(basketServiceWebClient, basketProperties);
  }

  public static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private static void logRequest(ClientRequest clientRequest) {
    log.info("{} {}{} {} Headers {}",
        clientRequest.method().name(),
        clientRequest.url().getHost(),
        clientRequest.url().getPath(),
        clientRequest.url().getQuery(),
        clientRequest.headers());
  }

  private ExchangeStrategies exchangeStrategies() {
    JsonMapper decoderMapper = objectMapper.rebuild()
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
    return ExchangeStrategies.builder().codecs(
        configurer -> {
          configurer.defaultCodecs()
              .jacksonJsonEncoder(
                  new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
          configurer.defaultCodecs()
              .jacksonJsonDecoder(
                  new JacksonJsonDecoder(decoderMapper, MediaType.APPLICATION_JSON));
          configurer.defaultCodecs().maxInMemorySize(-1);
        }).build();
  }

  public static ExchangeFilterFunction logResponse() {

    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("Dynamics365 API Response: {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }

  private ExchangeFilterFunction retryOn401Function() {

    return (request, next) -> next.exchange(request)
        .flatMap((Function<ClientResponse, Mono<ClientResponse>>) clientResponse -> {
          if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
            log.debug("401 Unauthorised - refreshing Auth Token");
            String newToken = refreshToken();
            ClientRequest retryRequest = ClientRequest.from(request)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(newToken))
                .build();
            logRequest(retryRequest);
            return next.exchange(retryRequest);
          } else {
            return Mono.just(clientResponse);
          }
        });
  }

  /**
   * Refreshes the Bearer token by evicting the expired cached token and then generating a new cached token.
   * If cache eviction fails, a new non-cached token is generated.
   *
   *  @return a new Bearer token
   */
  private String refreshToken() {
    String expiredToken = oAuthProvider.getBearerTokenNonCached();
    oAuthProvider.evictBearerTokenCache();
    String newToken = oAuthProvider.getBearerTokenNonCached();
    if (newToken.equals(expiredToken)) {
      newToken = oAuthProvider.getBearerToken();
      log.warn("Token cache eviction failed. Generated a new non-cached token.");
    } else {
      log.debug("Generated new token.");
    }
    return newToken;
  }
}

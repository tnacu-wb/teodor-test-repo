package uk.co.whitbread.avail.business.events.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.avail.business.events.domain.model.feature.FeatureFlag;
import uk.co.whitbread.avail.business.events.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OcdAdapterOutPort;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OperaAuthenticationPort;
import uk.co.whitbread.avail.business.events.infrastructure.adapters.HotelAvailabilityDbBatchService;
import uk.co.whitbread.avail.business.events.infrastructure.adapters.OperaAuthenticationService;
import uk.co.whitbread.avail.business.events.infrastructure.client.EventSubscriptionWebSocketClient;
import uk.co.whitbread.avail.business.events.infrastructure.client.OperaRestClient;
import uk.co.whitbread.avail.business.events.infrastructure.client.WebSocketGraphqlClientProvider;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.RateRestrictionsMapper;
import uk.co.whitbread.avail.business.events.infrastructure.repository.HotelEntityRepository;
import uk.co.whitbread.avail.business.events.infrastructure.repository.ProcessedEventJpaRepository;
import uk.co.whitbread.avail.business.events.infrastructure.repository.RoomEntityJpaRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BeanConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Bean
  public JsonMapper jsonMapper() {
    return JsonMapper.builder()
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
  }

  @Bean
  public RestTemplate authorizationRestTemplate(final OperaProperties operaProperties) {
    RestTemplate restTemplate = new RestTemplate();

    String credentials = operaProperties.getClientId() + ":" + operaProperties.getClientSecret();
    String encoded = Base64.getEncoder()
        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

    restTemplate.getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
      log.debug("{} {}", request.getMethod(), request.getURI());
      return execution.execute(request, body);
    });

    return restTemplate;
  }

  @Bean
  public OperaRestClient operaRestClient(final RestTemplate authorizationRestTemplate) {
    return new OperaRestClient(authorizationRestTemplate);
  }

  @Bean
  public OperaAuthenticationPort operaAuthenticationPort(
      final OperaProperties operaProperties,
      final ObjectMapper objectMapper,
      final OperaRestClient operaRestClient,
      final UnleashWrapper<FeatureFlag> unleashWrapper,
      final FeatureFlag featureFlag) {
    return new OperaAuthenticationService(operaProperties, objectMapper, operaRestClient,
        unleashWrapper, featureFlag);
  }

  @Bean
  public WebSocketGraphqlClientProvider webSocketGraphqlClientProvider(
      final OperaProperties operaProperties,
      final OhipGraphQlMockProperties ohipGraphQlMockProperties) {

    return new WebSocketGraphqlClientProvider(operaProperties, ohipGraphQlMockProperties);
  }

  @Bean
  public RateRestrictionsMapper rateRestrictionsMapper(
      final RoomEntityJpaRepository roomEntityJpaRepository) {
    return new RateRestrictionsMapper(roomEntityJpaRepository);
  }

  @Bean
  public HotelAvailabilityDbBatchService hotelAvailabilityDbBatchService(
      final HotelEntityRepository hotelEntityRepository,
      final ProcessedEventJpaRepository processedEventJpaRepository,
      final UnleashWrapper<FeatureFlag> unleashWrapper,
      final ContentOutPort contentOutPort,
      final OcdAdapterOutPort ocdAdapterOutPort,
      final RateRestrictionsMapper rateRestrictionsMapper) {
    return new HotelAvailabilityDbBatchService(
        hotelEntityRepository, processedEventJpaRepository, unleashWrapper, contentOutPort,
        ocdAdapterOutPort, rateRestrictionsMapper);
  }

  @Bean
  public EventSubscriptionWebSocketClient eventSubscriptionWebSocketClient(
      final OperaProperties operaProperties,
      final WebSocketGraphqlClientProvider webSocketGraphqlClientProvider,
      final HotelAvailabilityDbBatchService hotelAvailabilityDbBatchService,
      final OperaAuthenticationPort operaAuthenticationPort) {

    return new EventSubscriptionWebSocketClient(
        operaProperties,
        webSocketGraphqlClientProvider,
        hotelAvailabilityDbBatchService,
        operaAuthenticationPort);
  }


}

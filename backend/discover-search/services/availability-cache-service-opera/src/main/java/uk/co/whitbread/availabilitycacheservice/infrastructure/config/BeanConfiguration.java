package uk.co.whitbread.availabilitycacheservice.infrastructure.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.availabilitycacheservice.domain.logic.GqtHotelAvailabilitiesService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.HotelAvailabilitiesService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.HotelDataProcessService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.LosRestrictionProcessService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.OperaHotelAvailabilitiesService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.PaginationAndSortingByPriceService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.PriceFinderHotelAvailabilitiesService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.RulesAgentInPortImpl;
import uk.co.whitbread.availabilitycacheservice.domain.logic.hotelprice.HotelPriceService;
import uk.co.whitbread.availabilitycacheservice.domain.logic.hotelprice.LocationPriceService;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.GqtHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelDataProcessPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.OperaHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PaginationAndSortingByPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PriceFinderHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.HotelPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.LocationPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelRatesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderLocationAvailabilititesOutPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RateClassificationLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.AemLocationPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.LocationPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.GqtHotelAvailPostProcessorService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.GqtHotelAvailabilitiesPersistenceSvc;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.HotelAvailabilitiesPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.HotelAvailabilitiesPostProcessorService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.HotelRatesPostProcessorService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.OperaHotelAvailPersistenceSvc;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.OperaHotelsPostProcessorService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.PriceFinderAvailPostProcessorService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.PriceFinderHotelAvailabilitiesPersistenceSvc;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.PriceFinderLocationAvailabilitiesOutPortImpl;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice.AemLocationsService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice.HotelLocationPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice.HotelLocationService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice.HotelPricePersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice.LocationPricePersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice.SnowdropHotelLookupService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.RulesAgentClientWebFlux;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.aem.AemFeignClientProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.content.ContentServiceProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.rulesagent.RulesAgentProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.AemFeignClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.SnowdropFeignClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.snowdrop.HotelDetailsMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.AvailabilityProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.PriceFinderProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.GqtHotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelLocationJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelMigrationStatusReaderRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.PriceFinderHotelAvailabilitiesRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.RulesAgentClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@RequiredArgsConstructor
@Configuration
@EnableAsync
@Slf4j
public class BeanConfiguration {

  private final JsonMapper objectMapper;

  @Value("${async-thread-config.corePoolSize:2}")
  private int corePoolSize;

  @Value("${async-thread-config.maxPoolSize:2}")
  private int maxPoolSize;

  @Value("${async-thread-config.queueCapacity:16}")
  private int queueCapacity;

  @Value("${low.inventory.threshold}")
  private int lowInventoryThreshold = 10;

  @Value("${enable.citytax.currency}")
  private boolean isCitytaxCurrencySupported = true;

  @Bean
  public HotelAvailabilitiesPort hotelAvailabilities(
          final HotelAvailabilitiesPersistencePort hotelAvailabilitiesPersistence,
          final HotelDataProcessPort hotelDataProcessPort,
          final PaginationAndSortingByPricePort paginationAndSortingByPrice,
          final ContentClientLookUpService contentClientLookUpService,
          final CityTaxFeatureUtil cityTaxFeatureUtil) {
    return new HotelAvailabilitiesService(hotelAvailabilitiesPersistence, hotelDataProcessPort,
            paginationAndSortingByPrice, contentClientLookUpService, cityTaxFeatureUtil);
  }

  @Bean
  public OperaHotelAvailabilitiesPort operaHotelAvailabilitiesPort(
          final OperaHotelPersistenceAdapter operaHotelPersistenceAdapter) {
    return new OperaHotelAvailabilitiesService(operaHotelPersistenceAdapter);
  }

  @Bean
  public HotelAvailabilitiesPersistencePort hotelAvailabilitiesPersistence(
          final HotelJpaRepository hotelJpaRepository,
          final HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPersistencePostProcessorPort) {
    return new HotelAvailabilitiesPersistenceAdapter(hotelJpaRepository,
            hotelAvailabilitiesPersistencePostProcessorPort);
  }

  @Bean
  public HotelDataProcessPort hotelDataProcess(final RateClassificationLookupPort rateClassificationLookupPort) {
    return new HotelDataProcessService(rateClassificationLookupPort);
  }

  @Bean
  public PaginationAndSortingByPricePort paginationAndSortingByPrice() {
    return new PaginationAndSortingByPriceService();
  }

  @Bean
  public HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPersistencePostProcessor(
          final LosRestrictionPort losRestrictionPort,
          final HotelRatesPostProcessorPort hotelRatesPostProcessorPort,
          final CityTaxFeatureUtil cityTaxFeatureUtil) {
    return new HotelAvailabilitiesPostProcessorService(losRestrictionPort, hotelRatesPostProcessorPort,
            cityTaxFeatureUtil, lowInventoryThreshold, isCitytaxCurrencySupported);
  }

  @Bean
  public LosRestrictionPort losRestrictionPort(final LosRestrictionRulesEvaluator losRestrictionRulesEvaluator) {
    return new LosRestrictionProcessService(losRestrictionRulesEvaluator);
  }

  @Bean
  public HotelPricePersistencePort hotelPricePersistencePort(final HotelJpaRepository hotelJpaRepository,
                                                             final CityTaxFeatureUtil cityTaxFeatureUtil,
                                                             ContentClientLookUpService contentClientLookUpService) {
    return new HotelPricePersistenceAdapter(hotelJpaRepository, cityTaxFeatureUtil, contentClientLookUpService);
  }

  @Bean
  public HotelPricePort hotelPricePort(final HotelPricePersistencePort hotelPricePersistence,
                                       final ContentClientLookUpService contentClientLookUpService) {
    return new HotelPriceService(hotelPricePersistence, contentClientLookUpService);
  }

  @Bean
  public AemLocationPort aemLocationPort(final AemFeignClient aemFeignClient,
                                         final AemFeignClientProperties aemFeignClientProperties) {
    return new AemLocationsService(aemFeignClient, aemFeignClientProperties);
  }

  @Bean
  public LocationPricePersistencePort locationPricePersistencePort(
          final JdbcTemplate jdbcTemplate,
          final DatabaseProperties databaseProperties,
          final HotelLocationJpaRepository hotelLocationJpaRepository) {
    return new LocationPricePersistenceAdapter(jdbcTemplate, databaseProperties, hotelLocationJpaRepository);
  }

  @Bean
  public LocationPricePort locationPricePort(final LocationPricePersistencePort locationPricePersistencePort) {
    return new LocationPriceService(locationPricePersistencePort);
  }

  @Bean
  public HotelLocationPersistencePort hotelLocationPersistencePort(
          final HotelLocationJpaRepository hotelLocationJpaRepository) {
    return new HotelLocationPersistenceAdapter(hotelLocationJpaRepository);
  }

  @Bean
  public SnowdropHotelLookupPort snowdropHotelLookupPort(final SnowdropFeignClient snowdropFeignClient,
                                                         final HotelDetailsMapper hotelDetailsMapper) {
    return new SnowdropHotelLookupService(snowdropFeignClient, hotelDetailsMapper);
  }

  @Bean
  public HotelLocationPort hotelLocationPort(final HotelLocationPersistencePort hotelLocationPersistencePort,
                                             final SnowdropHotelLookupPort snowdropHotelLookupPort,
                                             final AemLocationPort aemLocationPort) {
    return new HotelLocationService(hotelLocationPersistencePort, snowdropHotelLookupPort, aemLocationPort);
  }

  @Bean
  public HotelRatesPostProcessorPort hotelRatesPostProcessorPort() {
    return new HotelRatesPostProcessorService();
  }

  @Bean
  public OperaHotelPersistenceAdapter operaHotelPersistenceAdapter(
          final HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository,
          final OperaHotelsPostProcessorPort operaHotelsPostProcessorPort,
          final HotelMigrationStatusReaderRepository hotelMigStatusReaderRepository,
          final HotelPriceProperties hotelPriceProperties) {

    return new OperaHotelAvailPersistenceSvc(hotelAvailabilitiesJpaRepository,
        operaHotelsPostProcessorPort, hotelMigStatusReaderRepository, hotelPriceProperties);
  }

  @Bean
  public OperaHotelsPostProcessorPort operaHotelsPostProcessorPort(
          final LosRestrictionPort losRestrictionPort,
          final HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPostProcessorPort,
          final CityTaxFeatureUtil cityTaxFeatureUtil, final ContentClientLookUpService contentClientLookUpService) {

    return new OperaHotelsPostProcessorService(losRestrictionPort, hotelAvailabilitiesPostProcessorPort,
            cityTaxFeatureUtil, contentClientLookUpService);
  }


  @Bean
  public RulesAgentInPort rulesAgentInPort(RulesAgentOutPort rulesAgentOutPort) {
    return new RulesAgentInPortImpl(rulesAgentOutPort);
  }

  @Bean
  public RulesAgentClient rulesAgentClient(
      final WebClient rulesAgentWebClient,
      final RulesAgentProperties rulesAgentProperties) {
    return new RulesAgentClient(rulesAgentWebClient, rulesAgentProperties);
  }

  @Bean(name = "updateHotelLocation")
  public Executor updateHotelLocationTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("UpdateHotelLocation-");
    executor.initialize();
    return executor;
  }

  @Bean
  public GqtHotelAvailabilitiesPort gqtHotelAvailabilitiesPort(
          final GqtHotelAvailabilitiesPersistencePort gqtHotelAvailabilitiesPersistencePort,
          final ContentClientLookUpService contentClientLookUpService, final CityTaxFeatureUtil cityTaxFeatureUtil) {
    return new GqtHotelAvailabilitiesService(gqtHotelAvailabilitiesPersistencePort, contentClientLookUpService,
            cityTaxFeatureUtil);
  }

  @Bean
  public GqtHotelAvailPostProcessorPort gqtHotelAvailPostProcessorPort(final CityTaxFeatureUtil cityTaxFeatureUtil) {
    return new GqtHotelAvailPostProcessorService(cityTaxFeatureUtil);
  }

  @Bean
  public GqtHotelAvailabilitiesPersistencePort gqtHotelAvailabilitiesPersistencePort(
          final GqtHotelAvailabilitiesJpaRepository gqtHotelAvailabilitiesJpaRepository,
          final HotelMigrationStatusReaderRepository hotelMigStatusReaderRepository,
          final GqtHotelAvailPostProcessorPort gqtHotelAvailPostProcessorPort) {
    return new GqtHotelAvailabilitiesPersistenceSvc(gqtHotelAvailabilitiesJpaRepository,
            hotelMigStatusReaderRepository, gqtHotelAvailPostProcessorPort);
  }

  @Bean
  public RulesAgentClientWebFlux rulesAgentClientWebFlux(
          RulesAgentProperties rulesAgentProperties,
          @Qualifier("rulesAgentWebClient") WebClient rulesAgentWebClient) {
    return new RulesAgentClientWebFlux(rulesAgentProperties, rulesAgentWebClient);
  }

  @Bean
  public PriceFinderHotelAvailabilitiesPort priceFinderHotelAvailabilitiesPort(
          final PriceFinderAvailabilitiesPersistencePort priceFinderAvailabilitiesPersistencePort,
          final PriceFinderLocationAvailabilititesOutPort priceFinderLocationAvailabilititesOutPort,
          final PriceFinderAvailabilitiesPostProcessorPort postProcessorPort,
          final RulesAgentClientWebFlux rulesAgentClientWebFlux) {
    return new PriceFinderHotelAvailabilitiesService(priceFinderAvailabilitiesPersistencePort,
            priceFinderLocationAvailabilititesOutPort, postProcessorPort, rulesAgentClientWebFlux);
  }

  @Bean
  public PriceFinderLocationAvailabilititesOutPort priceFinderLocationAvailabilititesPort(
          final PriceFinderAvailabilitiesPersistencePort priceFinderAvailabilitiesPersistencePort,
          final SnowdropHotelLookupPort snowdropHotelLookupPort,
          final PriceFinderAvailabilitiesPostProcessorPort postProcessorPort,
          final PriceFinderProperties priceFinderProperties,
          final RulesAgentInPort rulesAgentInPort) {
    return new PriceFinderLocationAvailabilitiesOutPortImpl(priceFinderAvailabilitiesPersistencePort,
            snowdropHotelLookupPort, postProcessorPort, priceFinderProperties, rulesAgentInPort);
  }

  @Bean
  public PriceFinderAvailabilitiesPostProcessorPort priceFinderAvailabilitiesPostProcessorPort(
          final LosRestrictionPort losRestrictionPort, final AvailabilityProperties properties,
          final ContentClientLookUpService contentClientLookUpService, final CityTaxFeatureUtil cityTaxFeatureUtil) {
    return new PriceFinderAvailPostProcessorService(losRestrictionPort, properties, contentClientLookUpService,
            cityTaxFeatureUtil);
  }

  @Bean
  public PriceFinderAvailabilitiesPersistencePort priceFinderAvailabilitiesPersistencePort(
          final PriceFinderHotelAvailabilitiesRepository priceFinderHotelAvailabilitiesRepository) {

    return new PriceFinderHotelAvailabilitiesPersistenceSvc(priceFinderHotelAvailabilitiesRepository);
  }

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("availabilityCacheConnectionProvider")
        .metrics(false)
        .maxConnections(100)
        .maxIdleTime(Duration.ofSeconds(10))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofSeconds(30))
        .pendingAcquireMaxCount(200)
        .pendingAcquireTimeout(Duration.ofSeconds(10))
        .build();
  }

  @Bean
  public HttpClient httpClient(@Value("${config.httpClient.connectionTimeout}") Integer connectionTimeout,
      @Value("${config.httpClient.responseTimeout}") Long responseTimeout) {
    return reactor.netty.http.client.HttpClient.create(connectionProvider())
            //connection timeout is a period within which a connection between a client and a server must be established
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            // TCP check probes when the connection is idle
            .option(ChannelOption.SO_KEEPALIVE, true)
            //response timeout is the time we wait to receive a response after sending a request
            .responseTimeout(Duration.ofSeconds(responseTimeout))
            // Keep-Alive support for the outgoing request
            .keepAlive(true).doOnRequest((request,
                                          connection) ->
                    connection.addHandlerFirst(new WebClientLoggingHandler()));
  }

  @Bean("contentServiceWebClient")
  public WebClient contentServiceWebClient(final ContentServiceProperties contentServiceProperties,
                                           WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder.clientConnector(
                    new ReactorClientHttpConnector(httpClient))
            .baseUrl(contentServiceProperties.getHost())
            .exchangeStrategies(exchangeStrategies()).filter(logRequest()).defaultHeader(HttpHeaders.CONTENT_TYPE,
                    MediaType.APPLICATION_JSON_VALUE).build();
  }

  // Boot 4.0: WebClientAutoConfiguration no longer activates in servlet apps.
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  public static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private static void logRequest(ClientRequest clientRequest) {
    log.info("{} {}{} {} Headers {}", clientRequest.method().name(), clientRequest.url().getHost(),
            clientRequest.url().getPath(), clientRequest.url().getQuery(), clientRequest.headers());
  }

  private ExchangeStrategies exchangeStrategies() {
    return ExchangeStrategies.builder().codecs(configurer -> {
      configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(objectMapper,
              MediaType.APPLICATION_JSON));
      configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(objectMapper,
              MediaType.APPLICATION_JSON));
      configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024);
    }).build();
  }
}

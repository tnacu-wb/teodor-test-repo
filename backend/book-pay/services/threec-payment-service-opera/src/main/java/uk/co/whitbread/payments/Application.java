package uk.co.whitbread.payments;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.unleash.features.config.UnleashAutoConfiguration;
import reactor.core.publisher.Hooks;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
import uk.co.whitbread.payments.converters.CustomMapper;
import uk.co.whitbread.payments.converters.MapperForFraud;
import uk.co.whitbread.payments.properties.BasketBookingProperties;
import uk.co.whitbread.payments.properties.HotelBookingProperties;
import uk.co.whitbread.payments.properties.HotelCardProperties;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import java.time.Duration;

@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
@Import(UnleashAutoConfiguration.class)
public class Application {

  @Autowired
  private ThreeCProperties threeCProperties;

  @Autowired
  private HotelBookingProperties hotelBookingProperties;

  @Autowired
  private BasketBookingProperties basketBookingProperties;

  @Autowired
  private HotelCardProperties hotelCardProperties;

  @Value(value = "${swagger.api.version}")
  private String swaggerVersion;
  
  private static final String CUSTOM = "custom";

  public static void main(String[] args) {
    Hooks.enableAutomaticContextPropagation();
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().version(swaggerVersion)
            .title("3C Payment Service REST API"));
  }

  @Bean
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean("threeC")
  public WebClient webClient() {
    ConnectionProvider provider = ConnectionProvider.builder(CUSTOM)
        .maxConnections(threeCProperties.getConnection().getMaxConnections())
        .maxIdleTime(Duration.ofSeconds(threeCProperties.getConnection().getMaxIdleTime()))
        .maxLifeTime(Duration.ofSeconds(threeCProperties.getConnection().getMaxLifetime()))
        .pendingAcquireTimeout(
            Duration.ofSeconds(threeCProperties.getConnection().getAcquiredTimeout()))
        .evictInBackground(Duration.ofSeconds(threeCProperties.getConnection().getEvictTimeout()))
        .build();
    HttpClient httpClient = HttpClient
        .create(provider)
        .resolver(DefaultAddressResolverGroup.INSTANCE)
        .keepAlive(threeCProperties.getConnection().isKeepAlive())
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
            (int) threeCProperties.getTimeout().getConnection())
        .responseTimeout(Duration.ofMillis(threeCProperties.getTimeout().getResponse()));

    if (threeCProperties.getConnection().isWiretapEnabled()) {
      httpClient.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG,
          AdvancedByteBufFormat.TEXTUAL);
    }

    return WebClient.builder()
        .baseUrl(threeCProperties.getServer())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(configurer -> configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder()))
        .build();
  }

  @Bean("hotel-booking")
  public WebClient hotelBookingWebClient() {
    ConnectionProvider provider = ConnectionProvider.builder(CUSTOM)
        .maxConnections(hotelBookingProperties.getMaxConnections())
        .maxIdleTime(Duration.ofSeconds(hotelBookingProperties.getMaxIdleTime()))
        .maxLifeTime(Duration.ofSeconds(hotelBookingProperties.getMaxLifetime()))
        .pendingAcquireTimeout(Duration.ofSeconds(hotelBookingProperties.getAcquiredTimeout()))
        .evictInBackground(Duration.ofSeconds(hotelBookingProperties.getEvictTimeout()))
        .build();
    HttpClient httpClient = HttpClient
        .create(provider)
        .resolver(DefaultAddressResolverGroup.INSTANCE)
        .keepAlive(hotelBookingProperties.isKeepAlive())
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) hotelBookingProperties.getConnection())
        .responseTimeout(Duration.ofMillis(hotelBookingProperties.getResponse()));

    return WebClient.builder()
        .baseUrl(hotelBookingProperties.getHost())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  @Bean("basket-booking")
  public WebClient basketBookingWebClient() {
    ConnectionProvider provider = ConnectionProvider.builder(CUSTOM)
        .maxConnections(basketBookingProperties.getMaxConnections())
        .maxIdleTime(Duration.ofSeconds(basketBookingProperties.getMaxIdleTime()))
        .maxLifeTime(Duration.ofSeconds(basketBookingProperties.getMaxLifetime()))
        .pendingAcquireTimeout(Duration.ofSeconds(basketBookingProperties.getAcquiredTimeout()))
        .evictInBackground(Duration.ofSeconds(basketBookingProperties.getEvictTimeout()))
        .build();
    HttpClient httpClient = HttpClient
        .create(provider)
        .resolver(DefaultAddressResolverGroup.INSTANCE)
        .keepAlive(basketBookingProperties.isKeepAlive())
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) basketBookingProperties.getConnection())
        .responseTimeout(Duration.ofMillis(basketBookingProperties.getResponse()));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  @Bean("hotelCard")
  public WebClient hotelCardWebClient() {
    ConnectionProvider provider = ConnectionProvider.builder(CUSTOM)
        .maxConnections(hotelCardProperties.getMaxConnections())
        .maxIdleTime(Duration.ofSeconds(hotelCardProperties.getMaxIdleTime()))
        .maxLifeTime(Duration.ofSeconds(hotelCardProperties.getMaxLifetime()))
        .pendingAcquireTimeout(Duration.ofSeconds(hotelCardProperties.getAcquiredTimeout()))
        .evictInBackground(Duration.ofSeconds(hotelCardProperties.getEvictTimeout()))
        .build();
    HttpClient httpClient = HttpClient
        .create(provider)
        .resolver(DefaultAddressResolverGroup.INSTANCE)
        .keepAlive(hotelCardProperties.isKeepAlive())
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) hotelCardProperties.getConnection())
        .responseTimeout(Duration.ofMillis(hotelCardProperties.getResponse()));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  public CustomMapper mapperWithFraud() {
    return new MapperForFraud();
  }

  @Bean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("uk.co.whitbread.bart.booking.api");
    return marshaller;
  }

  @Bean("stub")
  public WebClient stubWebClient() {
    return WebClient.builder().baseUrl(threeCProperties.getServer()).build();
  }

}

package uk.co.whitbread.reservation;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.unleash.features.config.UnleashAutoConfiguration;
import reactor.core.publisher.Hooks;

@EnableCaching
@EnableAsync
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {
    DataRedisAutoConfiguration.class})
@Import(UnleashAutoConfiguration.class)
public class HotelReservationEntityServiceApplication {

  private final String swaggerApiVersion;

  public HotelReservationEntityServiceApplication(
      @Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(HotelReservationEntityServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.reservation.infrastructure.rest")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.reservation")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Sample API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }
}

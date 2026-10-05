package uk.co.whitbread;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.unleash.features.config.UnleashAutoConfiguration;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@EnableCaching
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {DataRedisAutoConfiguration.class})
@Import(UnleashAutoConfiguration.class)
public class HotelEntityServiceApplication {

  final String swaggerApiVersion;

  public HotelEntityServiceApplication(
      @Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(HotelEntityServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.infrastructure.rest.controller")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.infrastructure.rest.controller")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiHotelInfoDocumentation() {
    return GroupedOpenApi.builder()
        .group("HotelInfo")
        .packagesToScan("uk.co.whitbread.infrastructure.rest.controller.hotel")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Hotel Entity Service API Documentation")
        .description(
            "<p>Provides the option to search for a specific hotel availability information, given "
                +
                "a search criteria.</p>")
        .version(swaggerApiVersion);
  }
}

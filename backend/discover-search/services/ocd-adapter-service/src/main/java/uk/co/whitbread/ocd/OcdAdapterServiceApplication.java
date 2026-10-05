package uk.co.whitbread.ocd;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import reactor.core.publisher.Hooks;
import uk.co.whitbread.shared.commons.logging.logger.FluentdLogger;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(
    basePackages = {"uk.co.whitbread"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FluentdLogger.class)
    }
)
public class OcdAdapterServiceApplication {


  String swaggerApiVersion;

  public OcdAdapterServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  static void main(String[] args) {
    SpringApplication.run(OcdAdapterServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }


  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.ocd.infrastructure.rest.controller")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.ocd.infrastructure.rest.controller")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiHotelInfoDocumentation() {
    return GroupedOpenApi.builder()
        .group("TaxInfo")
        .packagesToScan("uk.co.whitbread.ocd.infrastructure.rest.controller.tax")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("OCD Adapter Service API Documentation")
        .description(
            "<p>Provides the option to retrieves tax-inclusive prices from the OCD, given "
                +
                "a search criteria.</p>")
        .version(swaggerApiVersion);
  }
}

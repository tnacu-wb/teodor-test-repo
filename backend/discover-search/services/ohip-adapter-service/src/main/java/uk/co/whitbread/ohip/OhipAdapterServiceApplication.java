package uk.co.whitbread.ohip;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.concurrent.ForkJoinPool;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@EnableCaching
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {
    SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class,
    ServletWebSecurityAutoConfiguration.class,
    DataRedisAutoConfiguration.class})
@Slf4j
public class OhipAdapterServiceApplication {

  final String swaggerApiVersion;

  public OhipAdapterServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  static void main(String[] args) {
    SpringApplication.run(OhipAdapterServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
    log.info("Available cpu's :{}", Runtime.getRuntime().availableProcessors());
    log.info("Fork join pool threads :{}", ForkJoinPool.commonPool().getParallelism());
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.ohip.infrastructure.rest")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiReservationDocumentation() {
    return GroupedOpenApi.builder()
        .group("Reservation")
        .packagesToScan("uk.co.whitbread.ohip.infrastructure.rest.controller.reservation")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiPackagesDocumentation() {
    return GroupedOpenApi.builder()
        .group("Packages")
        .packagesToScan("uk.co.whitbread.ohip.infrastructure.rest.controller.packages")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiHotelInfoDocumentation() {
    return GroupedOpenApi.builder()
        .group("HotelInfo")
        .packagesToScan("uk.co.whitbread.ohip.infrastructure.rest.controller.hotel")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiRatePlansDocumentation() {
    return GroupedOpenApi.builder()
        .group("RatePlans")
        .packagesToScan("uk.co.whitbread.ohip.infrastructure.rest.controller.rates")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.ohip")
        .build();
  }


  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("OHIP Adapter Service API Documentation")
        .description("<p>The OHIP Adapter Micro-service embeds all the specific logic that is "
            + "required by the Oracle Opera property management system. It is responsible for "
            + "translating the Whitbread data models onto the Opera data model, and vice versa, "
            + "plus the handling of the interface integration with the Opera OHIP APIs. When a  "
            + "new PMS solution is going to be integrated into the Whitbread booking platform, a "
            + "similar, new Adapter micro-service will be created to take care of the specifics "
            + "of that integration.</p>")
        .version(swaggerApiVersion);
  }
}
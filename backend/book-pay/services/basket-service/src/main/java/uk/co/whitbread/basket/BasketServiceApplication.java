package uk.co.whitbread.basket;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@EnableAsync
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {
    SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class BasketServiceApplication {

  private final String swaggerApiVersion;

  public BasketServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.basket.infrastructure.rest")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiBasketDocumentation() {
    return GroupedOpenApi.builder()
        .group("Basket")
        .packagesToScan("uk.co.whitbread.basket.infrastructure.rest.controller.basket")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.basket")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Basket Service API Documentation")
        .description("<p>The basket feature will be modeled as a distinct micro-service "
            + "that will interact mainly with the Reservation Entity micro-service to "
            + "manage any interactions performed by the end-users against a stay definition. "
            + "Based on the user’s selection, the Reservation entity will be responsible for "
            + "creating the necessary number of cart items in the basket "
            + "(i.e. one Opera reservation for each room). "
            + "The same Reservation micro-service must provide all the details that are relevant "
            + "for the items placed in the basket.</p>")
        .version(swaggerApiVersion);
  }

  public static void main(String[] args) {
    SpringApplication.run(BasketServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }
}

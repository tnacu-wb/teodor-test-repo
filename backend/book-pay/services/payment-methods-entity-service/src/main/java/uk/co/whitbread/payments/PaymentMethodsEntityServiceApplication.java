package uk.co.whitbread.payments;

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
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@EnableCaching
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {DataRedisAutoConfiguration.class})
public class PaymentMethodsEntityServiceApplication {

  private final String swaggerApiVersion;

  public PaymentMethodsEntityServiceApplication(
      @Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(PaymentMethodsEntityServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.payments")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.payments")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiPaymentMethodsDocumentation() {
    return GroupedOpenApi.builder()
        .group("PaymentMethods")
        .packagesToScan("uk.co.whitbread.payments.infrastructure.rest.controller.payment")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Payment Methods Entity Service API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }
}

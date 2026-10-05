package uk.co.whitbread.refund.processor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class RefundRequestProcessorApplication {


  String swaggerApiVersion;

  public RefundRequestProcessorApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(RefundRequestProcessorApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan("uk.co.whitbread.refund.processor")
            .pathsToMatch("/**")
            .build();
  }

  @Bean
  public GroupedOpenApi openApiRefundProcessorDocumentation() {
    return GroupedOpenApi.builder()
        .group("Refund")
        .packagesToScan("uk.co.whitbread.refund.processor.infrastructure.rest.controller")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
            .group("Internal")
            .pathsToMatch("/**")
            .packagesToExclude("uk.co.whitbread.refund.processor")
            .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Refund Request Processor API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }
}

package uk.co.whitbread.digitalkey;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class DigitalKeyServiceApplication {


  String swaggerApiVersion;

  public static void main(String[] args) {
    SpringApplication.run(DigitalKeyServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.digitalkey")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.digitalkey")
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

package uk.co.whitbread.reservation;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class BookingServiceApplication {


  String swaggerApiVersion;

  public BookingServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  static void main(String[] args) {
    SpringApplication.run(BookingServiceApplication.class, args);
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.reservation")
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

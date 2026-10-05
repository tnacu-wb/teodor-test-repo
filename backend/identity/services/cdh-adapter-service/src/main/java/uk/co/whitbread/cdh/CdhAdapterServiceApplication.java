package uk.co.whitbread.cdh;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class CdhAdapterServiceApplication {


  String swaggerApiVersion;

  public CdhAdapterServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  static void main(String[] args) {
    SpringApplication.run(CdhAdapterServiceApplication.class, args);
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan("uk.co.whitbread.cdh")
            .pathsToMatch("/**")
            .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
            .group("Internal")
            .pathsToMatch("/**")
            .packagesToExclude("uk.co.whitbread.cdh")
            .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
            .title("CDH API Documentation")
            .description("<p>CDH Api Documentation</p>")
            .version(swaggerApiVersion);
  }
}

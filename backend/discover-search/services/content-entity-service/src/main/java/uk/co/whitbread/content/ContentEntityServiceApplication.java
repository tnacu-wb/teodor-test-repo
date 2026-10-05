package uk.co.whitbread.content;

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
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {
    DataRedisAutoConfiguration.class})
@Import(UnleashAutoConfiguration.class)
public class ContentEntityServiceApplication {

  private final String swaggerApiVersion;

  public ContentEntityServiceApplication(
      @Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  static void main(String[] args) {
    SpringApplication.run(ContentEntityServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.content")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.content.infrastructure")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiContentDocumentation() {
    return GroupedOpenApi.builder()
        .group("Content")
        .packagesToScan("uk.co.whitbread.content.infrastructure.rest.controller")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Content Entity Service API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }
}

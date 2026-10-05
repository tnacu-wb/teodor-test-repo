package uk.co.whitbread.payapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Hooks;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableAsync
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {
    DataRedisAutoConfiguration.class})
@EnableCaching
public class PayAppEntityServiceApplication {


  String swaggerApiVersion;

  public PayAppEntityServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  static void main(String[] args) {
    SpringApplication.run(PayAppEntityServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.payapp")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.payapp")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Pay App Entity API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }

  // Needed for OAuthServiceErrorDecoder com.fasterxml.jackson.databind.ObjectMapper constructor param.
  // Remove this after migrating to using cdh-adapter-service
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  // Jackson 3 mapper for Spring MVC (used by HTTP message converters)
  // By default, FAIL_ON_NULL_FOR_PRIMITIVES is false in Jackson 2.x, but true in Jackson 3.x, so we need
  // to disable it explicitly to avoid deserialization errors when null values are encountered for primitive types.
  @Bean
  @Primary
  public tools.jackson.databind.json.JsonMapper jsonMapper() {
    return tools.jackson.databind.json.JsonMapper.builder()
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
  }
}

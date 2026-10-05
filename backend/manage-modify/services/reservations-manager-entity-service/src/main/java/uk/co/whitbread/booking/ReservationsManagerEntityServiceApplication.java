package uk.co.whitbread.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@EnableFeignClients("uk.co.whitbread.shared.cdh.oauth")
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {
    DataRedisAutoConfiguration.class})
public class ReservationsManagerEntityServiceApplication {

  String swaggerApiVersion;

  public ReservationsManagerEntityServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(ReservationsManagerEntityServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.booking")
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
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper objectMapper() {
    return new ObjectMapper().findAndRegisterModules();
  }

  @Bean
  @ConditionalOnMissingBean(Tracer.class)
  public Tracer tracer() {
    return Tracer.NOOP;
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Reservation Manager Entity API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }
}

package uk.co.whitbread.promo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.unleash.features.config.UnleashAutoConfiguration;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@SpringBootApplication(
        scanBasePackages = {"uk.co.whitbread"},
        exclude = {
          SecurityAutoConfiguration.class,
          ManagementWebSecurityAutoConfiguration.class,
          ServletWebSecurityAutoConfiguration.class,
        }
)

@ConfigurationPropertiesScan
@Import(UnleashAutoConfiguration.class)
public class PromoServiceApplication {
  String swaggerApiVersion;

  public PromoServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(PromoServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.promo")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.promo")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Promo API Documentation")
        .description("<p>TBU</p>")
        .version(swaggerApiVersion);
  }
}

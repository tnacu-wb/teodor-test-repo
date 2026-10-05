package uk.co.whitbread.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;


@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class DashboardApplication {

  final String swaggerApiVersion;

  public DashboardApplication(@Value("${swagger.api.version}") final String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  @Bean
  public GroupedOpenApi swaggerApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.dashboard")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi swaggerInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .packagesToExclude("uk.co.whitbread.dashboard")
        .pathsToMatch("/hoteldashboard")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("App Dashboard Documentation")
        .description("<p>App Dashboard - Should only display bookings within x number of "
            + "days from arrivalDate. **Default value of 14 days**.</p>")
        .version(swaggerApiVersion);
  }

  @Primary
  @Bean
  public JsonMapper objectMapper() {
    return JsonMapper.builder()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .changeDefaultPropertyInclusion(
            incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL)
                .withContentInclusion(JsonInclude.Include.NON_NULL))
        .build();
  }

  static void main(final String[] args) {
    SpringApplication.run(DashboardApplication.class, args);
  }
}

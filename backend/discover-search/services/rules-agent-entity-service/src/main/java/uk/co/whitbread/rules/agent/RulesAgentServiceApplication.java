package uk.co.whitbread.rules.agent;

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
public class RulesAgentServiceApplication {

  String swaggerApiVersion;

  public RulesAgentServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
    this.swaggerApiVersion = swaggerApiVersion;
  }

  public static void main(String[] args) {
    SpringApplication.run(RulesAgentServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }

  @Bean
  public GroupedOpenApi openApiDocumentation() {
    return GroupedOpenApi.builder()
        .group("Api")
        .packagesToScan("uk.co.whitbread.rules.agent")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiInternalDocumentation() {
    return GroupedOpenApi.builder()
        .group("Internal")
        .pathsToMatch("/**")
        .packagesToExclude("uk.co.whitbread.rules.agent")
        .build();
  }

  @Bean
  public GroupedOpenApi openApiRulesAgentDocumentation() {
    return GroupedOpenApi.builder()
        .group("RulesAgent")
        .packagesToScan("uk.co.whitbread.rules.agent.infrastructure.rest.controller")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI().info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Rules Agent Entity Service API Documentation")
        .description("<p>The service reads all the rules' configuration "
            + "from DB into memory, handles the incoming requests from the other components and "
            + "polls periodically (e.g. 60 sec) the database for any updates made by the rule "
            + "manager service.</p>")
        .version(swaggerApiVersion);
  }
}

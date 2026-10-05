package uk.co.whitbread.piba.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.shared.auth.EnableAuthorization;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages= {"uk.co.whitbread"}, exclude = {DataRedisAutoConfiguration.class})
@EnableAuthorization
public class PibaAccountServiceApplication {

    @Value("${swagger.api.version}")
    String swaggerApiVersion;

    @Bean
    public GroupedOpenApi swaggerApiDocumentation(){
        return GroupedOpenApi.builder()
                .group("Api")
                .packagesToScan("uk.co.whitbread.piba.account")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi swaggerInternalDocumentation(){
        return GroupedOpenApi.builder()
                .group("Internal")
                .packagesToExclude("uk.co.whitbread.piba.account")
                .pathsToMatch("/piba/account")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    @Primary
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("Piba account Microservice API Documentation")
                .description("API Documentation")
                .version(swaggerApiVersion);
    }

    public static void main(String[] args) {
        SpringApplication.run(PibaAccountServiceApplication.class, args);
    }
}

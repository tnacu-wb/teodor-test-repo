package uk.co.whitbread.piba;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.shared.commons.logging.config.TraceLoggingConfiguration;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataRedisAutoConfiguration.class)
@ComponentScan(basePackages = "uk.co.whitbread",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = TraceLoggingConfiguration.class))
@EnableCaching
public class PibaRegistrationServiceApplication {
    @Value("${swagger.api.version}")
    String swaggerApiVersion;

    @Bean
    public GroupedOpenApi openApiDocumentation(){
        return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan("uk.co.whitbread.piba")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Piba registration Api Documentation")
            .description("API Documentation")
            .version(swaggerApiVersion);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Primary
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class)
            .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PibaRegistrationServiceApplication.class, args);
    }
}

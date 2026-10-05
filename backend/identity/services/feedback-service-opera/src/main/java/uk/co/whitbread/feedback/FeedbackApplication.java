package uk.co.whitbread.feedback;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.feedback.properties.ConfigProperties;

import static java.time.Duration.ofMillis;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class FeedbackApplication {

    @Value("${swagger.api.version}")
    private String swaggerApiVersion;

    @Autowired
    private ConfigProperties configProperties;

    static void main(String[] args) {
        SpringApplication.run(FeedbackApplication.class, args);
    }

    @Bean
    public GroupedOpenApi openApiDocumentation() {
        return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan("uk.co.whitbread.feedback")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public GroupedOpenApi openApiInternalDocumentation() {
        return GroupedOpenApi.builder()
            .group("Internal")
            .pathsToMatch("/feedback")
            .packagesToExclude("uk.co.whitbread.feedback")
            .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Feedback Service Opera API Documentation")
            .description("<p>Endpoints for managing customer feedback.</p>")
            .version(swaggerApiVersion);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.
                connectTimeout(ofMillis(configProperties.getRequestTimeout())).
                readTimeout(ofMillis(configProperties.getRequestTimeout())).
                build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }


}

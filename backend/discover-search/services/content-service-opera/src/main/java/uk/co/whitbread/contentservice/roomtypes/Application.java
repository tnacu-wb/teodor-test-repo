package uk.co.whitbread.contentservice.roomtypes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication(scanBasePackages = "uk.co.whitbread")
@EnableFeignClients
public class Application {

    private static final String PACKAGE_LOCATION = "uk.co.whitbread.contentservice.roomtypes";

    @Value("${request.timeout:10000}")
    private int requestTimeout;
    @Value("${swagger.api.version}")
    private String swaggerApiVersion;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(PACKAGE_LOCATION);
        return marshaller;
    }

    @Bean
    public JavaTimeModule javaTimeModule() {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
        return javaTimeModule;
    }

    @Bean
    public ObjectMapper objectMapper(final JavaTimeModule javaTimeModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    @Bean
    public GroupedOpenApi openApiDocumentation() {
        return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan(PACKAGE_LOCATION)
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public GroupedOpenApi openApiInternalDocumentation() {
        return GroupedOpenApi.builder()
            .group("Internal")
            .pathsToMatch("/feedback")
            .packagesToExclude(PACKAGE_LOCATION)
            .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Content API for Room Types")
            .description("<p>This API gives information about different room types</p>")
            .version(swaggerApiVersion);
    }
}

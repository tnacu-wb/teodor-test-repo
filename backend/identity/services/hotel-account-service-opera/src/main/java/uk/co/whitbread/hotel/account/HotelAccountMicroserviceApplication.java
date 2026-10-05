package uk.co.whitbread.hotel.account;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.unleash.features.config.UnleashAutoConfiguration;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.EnableAuthorization;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.webclient.WebClientRetryHandler;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableDiscoveryClient
@EnableAuthorization
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = "uk.co.whitbread")
@Import(UnleashAutoConfiguration.class)
public class HotelAccountMicroserviceApplication {

    final String swaggerApiVersion;

    public HotelAccountMicroserviceApplication(
            @Value("${swagger.api.version}") String swaggerApiVersion) {
        this.swaggerApiVersion = swaggerApiVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(HotelAccountMicroserviceApplication.class, args);
    }

    @Bean
    public GroupedOpenApi openApiDocumentation() {
        return GroupedOpenApi.builder()
                .group("Api")
                .packagesToScan("uk.co.whitbread.hotel.account")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi openApiInternalDocumentation() {
        return GroupedOpenApi.builder()
                .group("Internal")
                .pathsToMatch("/hotelaccount")
                .packagesToExclude("uk.co.whitbread.hotel.account")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Hotel account Microservice API Documentation")
                .description("API Documentation")
                .version(swaggerApiVersion);
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        return template;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("uk.co.whitbread.shared.azureemail.api",
            "uk.co.whitbread.shared.azureemail.genericEmail.api");
        return marshaller;
    }

    @Bean
    @Primary
    public ObjectMapper hotelAccountObjectMapper() {
        return JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "auth.management",
            name = {"domain", "client-id", "client-secret", "audience"})
    public ManagementService leisureManagementService(Auth0Properties managementProperties,
                                                      WebClient.Builder webClientBuilder,
                                                      WebClientRetryHandler webClientRetryHandler) {
        managementProperties.setConnection(managementProperties.getB2cConnection());
        return new ManagementService(managementProperties, webClientBuilder, webClientRetryHandler);
    }

    @Bean
    @ConditionalOnProperty(prefix = "auth.management",
            name = {"domain", "client-id", "client-secret", "audience"})
    public ManagementService businessManagementService(Auth0Properties managementProperties,
                                                       WebClient.Builder webClientBuilder,
                                                       WebClientRetryHandler webClientRetryHandler) {
        managementProperties.setConnection(managementProperties.getB2bConnection());
        return new ManagementService(managementProperties, webClientBuilder, webClientRetryHandler);
    }

}

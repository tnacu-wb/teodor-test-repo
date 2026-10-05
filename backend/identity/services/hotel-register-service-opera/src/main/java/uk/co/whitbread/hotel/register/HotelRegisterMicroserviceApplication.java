package uk.co.whitbread.hotel.register;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.unleash.features.config.UnleashAutoConfiguration;
import uk.co.whitbread.hotel.register.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.webclient.WebClientRetryHandler;
import uk.co.whitbread.shared.commons.logging.config.TraceLoggingConfiguration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "uk.co.whitbread")
@SpringBootApplication(exclude = DataRedisAutoConfiguration.class)
@ComponentScan(basePackages = "uk.co.whitbread",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = TraceLoggingConfiguration.class))
@EnableConfigurationProperties
@EnableAsync
@EnableScheduling
@Import(UnleashAutoConfiguration.class)
public class HotelRegisterMicroserviceApplication {

    public static final String PACKAGES_TO_SCAN = "uk.co.whitbread.hotel.register";
    final String swaggerApiVersion;

    public HotelRegisterMicroserviceApplication(
            @Value("${swagger.api.version}") String swaggerApiVersion) {
        this.swaggerApiVersion = swaggerApiVersion;
    }

    public static void main(String[] args) {
        SpringApplication.run(HotelRegisterMicroserviceApplication.class, args);
    }

    @Bean
    public GroupedOpenApi openApiDocumentation() {
        return GroupedOpenApi.builder()
                .group("Api")
                .packagesToScan(PACKAGES_TO_SCAN)
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi openApiHotelRegisterDocumentation() {
        return GroupedOpenApi.builder()
                .group("HotelRegister")
                .packagesToScan(PACKAGES_TO_SCAN)
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi openApiInternalDocumentation() {
        return GroupedOpenApi.builder()
                .group("Internal")
                .packagesToScan(PACKAGES_TO_SCAN)
                .pathsToMatch("/hotelregister")
                .packagesToExclude("uk.co.whitbread.hotel")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Hotel register Microservice API Documentation")
                .description("API Documentation")
                .version(swaggerApiVersion);
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
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
    public ManagementService leisureManagementService(Auth0Properties managementProperties,
            WebClient.Builder webClientBuilder, WebClientRetryHandler webClientRetryHandler) {
        managementProperties.setConnection(managementProperties.getB2cConnection());
        return new ManagementService(managementProperties, webClientBuilder, webClientRetryHandler);
    }

    @Bean
    public ManagementService businessManagementService(Auth0Properties managementProperties,
            WebClient.Builder webClientBuilder, WebClientRetryHandler webClientRetryHandler) {
        managementProperties.setConnection(managementProperties.getB2bConnection());
        return new ManagementService(managementProperties, webClientBuilder, webClientRetryHandler);
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
}

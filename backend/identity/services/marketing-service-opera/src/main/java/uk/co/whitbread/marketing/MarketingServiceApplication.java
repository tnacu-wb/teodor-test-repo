package uk.co.whitbread.marketing;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.Getter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.properties.CustomerHubPropertiesLegacy;
import uk.co.whitbread.marketing.properties.PermissionManagementApiProperties;

import java.util.Arrays;
import uk.co.whitbread.shared.auth.EnableAuthorization;

import static java.time.Duration.ofMillis;
import static lombok.AccessLevel.PROTECTED;

@EnableDiscoveryClient
@EnableFeignClients
@EnableAuthorization
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class MarketingServiceApplication {

    @Value("${swagger.api.version}")
    String swaggerApiVersion;

    @Autowired
    @Getter(PROTECTED)
    private CustomerHubPropertiesLegacy customerHubPropertiesLegacy;

    @Autowired
    @Getter(PROTECTED)
    private PermissionManagementApiProperties permissionManagementApiProperties;

    public static void main(String[] args) {
        SpringApplication.run(MarketingServiceApplication.class, args);
    }

    @Bean
    public GroupedOpenApi openApiDocumentation(){
        return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan("uk.co.whitbread.marketing")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Marketing Api Documentation")
            .description("API Documentation")
            .version(swaggerApiVersion);
    }

    @Bean("customerHubRestTemplate")
    public RestTemplate customerHubRestTemplate() {
        int timeout = getCustomerHubPropertiesLegacy().getTimeout();
        return new RestTemplateBuilder().
                connectTimeout(ofMillis(timeout)).
                readTimeout(ofMillis(timeout))
                .messageConverters(messageConverter())
                .build();
    }

    @Bean("permissionManagementApiRestTemplate")
    public RestTemplate permissionManagementApiRestTemplate() {
        int timeout = getPermissionManagementApiProperties().getTimeout();
        return new RestTemplateBuilder().
                connectTimeout(ofMillis(timeout)).
                readTimeout(ofMillis(timeout))
                .messageConverters(messageConverter())
                .build();
    }

    private JacksonJsonHttpMessageConverter messageConverter() {
        JacksonJsonHttpMessageConverter messageConverter =
            new JacksonJsonHttpMessageConverter(objectMapper());
        messageConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.valueOf("application/*+json")
        ));
        return messageConverter;
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
        marshaller.setPackagesToScan("uk.co.whitbread.bart.marketing.api", "uk.co.whitbread.bart.unified.api");
        return marshaller;
    }

    @Bean
    public JsonMapper objectMapper() {
        return JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .changeDefaultPropertyInclusion(inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_NULL))
            .findAndAddModules()
            .build();
    }

}

package uk.co.whitbread.hotel.info;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.client.config.CookieSpecs;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.common.exceptions.mapping.ErrorCodeMappingConfig;
import uk.co.whitbread.hotel.info.config.AEMConfiguration;

import java.util.concurrent.Executor;

@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = "uk.co.whitbread",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ErrorCodeMappingConfig.class))
@EnableConfigurationProperties
public class Application {

    @Value("${swagger.api.version}")
    final String swaggerApiVersion;

    @Value("${executor.poolSize.aem}")
    final int aemPoolSize;

    private final AEMConfiguration aemConfiguration;

    public Application(@Value("${swagger.api.version}") String swaggerApiVersion,
                       @Value("${executor.poolSize.aem}") int aemPoolSize,
                       AEMConfiguration aemConfiguration) {
        this.swaggerApiVersion = swaggerApiVersion;
        this.aemPoolSize = aemPoolSize;
        this.aemConfiguration = aemConfiguration;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory createRequestFactory) {
        AEMConfiguration.Authentication authentication = aemConfiguration.getAuthentication();

        if (authentication.isEnabled()) {
            builder = builder.basicAuthentication(authentication.getUsername(), authentication.getPassword());
        }

        builder = builder.requestFactory(() -> createRequestFactory);

        return builder.build();
    }

    @Bean
    public ClientHttpRequestFactory createRequestFactory(ConnectionKeepAliveStrategy connectionKeepAliveStrategy) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(aemConfiguration.getConnectionManager().getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(aemConfiguration.getConnectionManager().getDefaultMaxPerRoute());
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(aemConfiguration.getConnectionRequestTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(aemConfiguration.getSocketTimeout()))
                .build();
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setDefaultRequestConfig(requestConfig)
                .build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        return objectMapper;
    }

    @Bean
    public ErrorCodeMappingConfig errorCodeMappingConfig() {
        return new ErrorCodeMappingConfig();
    }

    @Bean
    @Hidden
    public GroupedOpenApi openApiDocumentation() {
        return GroupedOpenApi.builder()
                .group("Api")
                .packagesToScan("uk.co.whitbread.hotel.info")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi openApiInternalDocumentation() {
        return GroupedOpenApi.builder()
                .displayName("All endpoints relating to the status of the Microservice")
                .group("Internal")
                .pathsToMatch("/hotelinfo")
                .packagesToExclude("uk.co.whitbread.hotel.info")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Hotel Info API Documentation")
                .description("<p>This Microservice acts mainly as a proxy to AEM.<br/> Please refer to the " +
                        "<a href=\"https://whitbreadis.atlassian.net/wiki/display/DSA/AEM+endpoints/\">" +
                        "AEM documentation</a> for more information about the schema(s)</p>")
                .version(swaggerApiVersion);
    }

    @Bean(name = "aemExecutor")
    public Executor getAEMExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(aemPoolSize);
        executor.initialize();

        return executor;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

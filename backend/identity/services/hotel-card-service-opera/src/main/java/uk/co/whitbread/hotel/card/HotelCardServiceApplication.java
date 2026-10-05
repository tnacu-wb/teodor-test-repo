package uk.co.whitbread.hotel.card;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.unleash.features.config.UnleashAutoConfiguration;
import uk.co.whitbread.shared.auth.EnableAuthorization;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableCaching
@EnableDiscoveryClient
@EnableScheduling
@ComponentScan(basePackages = "uk.co.whitbread", excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE, classes = ManagementProperties.class))
@SpringBootApplication
@EnableAuthorization
@Import(UnleashAutoConfiguration.class)
public class HotelCardServiceApplication implements WebMvcConfigurer {

    @Value("${swagger.api.version}")
    String swaggerApiVersion;

    public static void main(String[] args) {
        SpringApplication.run(HotelCardServiceApplication.class, args);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
        // text plain needed for prometheus
    }
}

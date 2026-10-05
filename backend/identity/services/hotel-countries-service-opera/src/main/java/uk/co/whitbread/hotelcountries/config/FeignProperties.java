package uk.co.whitbread.hotelcountries.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feign-clients")
@Data
public class FeignProperties {
    private FeignClientProperties aem;
}

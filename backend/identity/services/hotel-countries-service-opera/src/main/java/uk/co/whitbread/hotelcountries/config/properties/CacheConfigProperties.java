package uk.co.whitbread.hotelcountries.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "hotel.countries")
@Component
@Data
public class CacheConfigProperties {
    private Map<String, CacheConfigProperty> cache;
}
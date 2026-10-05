package uk.co.whitbread.contentservice.roomtypes.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "content-service")
@Component
@Data
public class CacheConfigProperties {
    private Map<String, CacheConfigProperty> cache;
}
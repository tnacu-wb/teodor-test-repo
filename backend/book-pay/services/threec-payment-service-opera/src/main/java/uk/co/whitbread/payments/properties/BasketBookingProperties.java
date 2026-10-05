package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "basket-booking")
@Configuration
@Data
public class BasketBookingProperties {
    private String schema;
    private int port;
    private boolean keepAlive;
    private int maxConnections;
    private int maxIdleTime;
    private int maxLifetime;
    private int acquiredTimeout;
    private int evictTimeout;
    private long response;
    private long connection;
    private String makeBookingEndpoint;
    private String securityKey;
    private String securityValue;
    private String host;
}

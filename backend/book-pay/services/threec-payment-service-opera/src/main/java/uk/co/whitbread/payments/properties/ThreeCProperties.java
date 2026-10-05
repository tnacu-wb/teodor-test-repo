package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "3c")
@Configuration
@Data
public class ThreeCProperties {
    String server;
    Endpoints endpoints;
    ProviderAccounts provider;
    IPage iPage;
    Connection connection;
    Timeout timeout;

    @Data
    public static class Endpoints {
        String initialise;
        String transactions;
        String tokenCreate;
        String tokenUpdate;
        String refund;
        String reconciliation;
    }

    @Data
    public static class IPage {
        String title;
        String providerUrl;
    }

    @Data
    public static class Connection {
        boolean keepAlive;
        int maxConnections;
        int maxIdleTime;
        int maxLifetime;
        int acquiredTimeout;
        int evictTimeout;
        boolean wiretapEnabled;
    }

    @Data
    public static class Timeout {
        long response;
        long connection;
    }
}

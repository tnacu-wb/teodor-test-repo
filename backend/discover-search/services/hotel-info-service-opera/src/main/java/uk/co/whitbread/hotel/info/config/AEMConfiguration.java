package uk.co.whitbread.hotel.info.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.Language;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "aem")
public class AEMConfiguration {

    private static final String DEFAULT_MAPPING = "default";

    private String url;
    private String allHotelsUrl;
    private int connectionRequestTimeout;
    private int socketTimeout;
    private int defaultKeepAlive;
    private Authentication authentication;
    private ConnectionManager connectionManager;
    private Map<String, String> hostMapping = new HashMap<>();

    public String getHost(Country country, Language language) {
        final String defaultHost = hostMapping.get(DEFAULT_MAPPING);
        final String hostKey = country.toString().toLowerCase() + "-" + language.toString().toLowerCase();
        return hostMapping.getOrDefault(hostKey, defaultHost);
    }

    @Data
    public static class Authentication {
        private boolean enabled;
        private String username;
        private String password;
    }

    @Data
    public static class ConnectionManager {
        private int maxTotal;
        private int defaultMaxPerRoute;
    }

}

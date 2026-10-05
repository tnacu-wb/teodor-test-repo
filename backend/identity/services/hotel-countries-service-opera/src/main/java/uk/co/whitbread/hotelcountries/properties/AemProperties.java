package uk.co.whitbread.hotelcountries.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "aem")
@RefreshScope
public class AemProperties {

    private static final String DEFAULT_MAPPING = "default";

    private int port;
    private Authentication authentication;
    private String countriesFile;
    private Map<String, String> hostMapping = new HashMap<>();

    public String getHost(String country, String language) {
        final String defaultHost = hostMapping.get(DEFAULT_MAPPING);
        final String hostKey = country.toLowerCase() + "-" + language.toLowerCase();
        return hostMapping.getOrDefault(hostKey, defaultHost);
    }

    public String getParameterisedUrl(String country, String language) {
        final String baseUrl = "http://" + getHost(country, language) + ":" + port;
        return String.format("%s/%s/%s/%s", baseUrl, country.toLowerCase(), language.toLowerCase(), countriesFile);
    }

    @Data
    public static class Authentication {

        private boolean enabled;
        private String username;
        private String password;
    }
}

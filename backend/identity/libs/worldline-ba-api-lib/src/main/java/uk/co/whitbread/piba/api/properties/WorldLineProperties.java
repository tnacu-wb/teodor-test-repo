package uk.co.whitbread.piba.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "worldline")
@Data
public class WorldLineProperties {

    private String clientMessageId;
    private String cultureCode;
    private Piba piba;
    private Gb gb;
    private De de;

    @Data
    public static class Piba {
        private String username;
        private String password;
        private Service service;

        @Data
        public static class Service {
            private String url;
        }
    }

    @Data
    public static class Gb {
        private String clientMessageId;
        private String cultureCode;
        private String username;
        private String password;
        private String url;
    }

    @Data
    public static class De {
        private String clientMessageId;
        private String cultureCode;
        private String username;
        private String password;
        private String url;
    }
}
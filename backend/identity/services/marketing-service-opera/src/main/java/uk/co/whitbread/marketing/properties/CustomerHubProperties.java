package uk.co.whitbread.marketing.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "customer-hub.oauth")
public class CustomerHubProperties {

    private String subscriptionKeyHeaderName;
    private String subscriptionKey;
    private String subscriptionKeyV2;
    private int timeout;
    private String sourceSystem;
    private String editMarketingPreferencesUrl;
    private String getMarketingPreferencesUrl;
    private boolean enabled;

}

package uk.co.whitbread.marketing.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "permission-management-api.oauth")
public class PermissionManagementApiProperties {

    private String subscriptionKeyHeaderName;
    private String subscriptionKey;
    private int timeout;
    private String sourceSystem;
    private String updateMarketingPreferencesUrl;
    private String getMarketingPreferencesUrl;
    private String getMarketingPreferencesV2Url;
    private String confirmDoubleOptInUrl;
    private String unsubscribeUrl;

}

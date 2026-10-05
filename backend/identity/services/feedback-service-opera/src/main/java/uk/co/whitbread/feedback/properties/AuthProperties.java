package uk.co.whitbread.feedback.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Data
public class AuthProperties {

    @Value("${security.oauth2.authorityUrl.url}")
    private String authorityUrl;

    @Value("${security.oauth2.clientId}")
    private String clientId;

    @Value("${security.oauth2.clientSecret}")
    private String clientSecret;

    @Value("${security.oauth2.credentials.username}")
    private String username;

    @Value("${security.oauth2.credentials.password}")
    private String password;

    @Value("${crm.baseUrl}")
    private String crmResourceUrl;

    @Value("${crm.incidents.feedback.url}")
    private String incidentsUrl;

    @Value("${crm.incidents.caseid.url}")
    private String caseidUrl;

    @Value("${crm.api.version}")
    private String crmApiVersion;

    @Value("${crm.incidents.contactType}")
    private String incidentsContactType;
}
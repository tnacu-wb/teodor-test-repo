package uk.co.whitbread.shared.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
@RefreshScope
@ConfigurationProperties(prefix = "auth.passwordless")
@Data
public class PasswordlessProperties {
    private String domain;
    private String clientId;
    private String clientSecret;
}

package uk.co.whitbread.shared.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.jwt.AuthProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
@RefreshScope
@ConfigurationProperties(prefix = "auth.token")
@Data
public class TokenProperties {
    private List<AuthProvider> providers = new ArrayList<>();
    private List<String> urlPathRegex;
}

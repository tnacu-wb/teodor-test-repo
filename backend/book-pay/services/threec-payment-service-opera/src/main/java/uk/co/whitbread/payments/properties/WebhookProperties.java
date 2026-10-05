package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "webhook")
@Configuration
@Data
public class WebhookProperties {
    String environment;
    String success;
    String failure;
}

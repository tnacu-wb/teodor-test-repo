package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "redirect")
@Configuration
@Data
public class RedirectProperties {
    String environment;
    String success;
    String failure;
    String defaultUrl;
    private List<String> regex;
}

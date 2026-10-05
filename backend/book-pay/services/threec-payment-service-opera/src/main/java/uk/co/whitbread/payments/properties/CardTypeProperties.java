package uk.co.whitbread.payments.properties;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "planet-bart-mappings")
@Configuration
@Data
public class CardTypeProperties {

    private Map<String, String> cardTypes;
}

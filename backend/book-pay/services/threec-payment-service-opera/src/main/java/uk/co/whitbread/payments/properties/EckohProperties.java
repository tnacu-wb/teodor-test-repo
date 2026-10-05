package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "eckoh")
@Configuration
@Data
public class EckohProperties {

    boolean enabled;

}
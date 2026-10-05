package uk.co.whitbread.infrastructure.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-suppression")
public class RateSuppressionProperties {

  List<String> roomClasses;

}

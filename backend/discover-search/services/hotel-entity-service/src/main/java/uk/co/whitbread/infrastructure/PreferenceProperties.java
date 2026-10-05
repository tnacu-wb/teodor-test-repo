package uk.co.whitbread.infrastructure;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "preference")
public class PreferenceProperties {

  Map<String, String> condition;

}

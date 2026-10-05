package uk.co.whitbread.basket.domain.logic.config;

import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.threec")
public class ThreecProp {

  private Map<String, Set<Integer>> returnCodes;
}

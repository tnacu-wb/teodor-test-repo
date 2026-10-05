package uk.co.whitbread.content.infrastructure.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "content-entity.hotels.filters")
public class SrpFiltersConfig {

  private List<String> facilityCodes;
}
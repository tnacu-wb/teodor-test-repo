package uk.co.whitbread.availabilitycacheservice.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "price-finder")
public class PriceFinderProperties {

  private Integer locationRadiusInMiles;
}

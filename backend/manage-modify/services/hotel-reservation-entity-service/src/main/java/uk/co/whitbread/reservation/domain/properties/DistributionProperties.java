package uk.co.whitbread.reservation.domain.properties;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "distribution")
@Configuration
@Data
public class DistributionProperties {

  private Map<Integer, String> meals;
  private String fixedRateAuthority;

}

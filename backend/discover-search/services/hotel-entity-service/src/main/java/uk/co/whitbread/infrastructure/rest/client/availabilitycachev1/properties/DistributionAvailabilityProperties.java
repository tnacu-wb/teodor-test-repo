package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "distribution-availability")
public class DistributionAvailabilityProperties {
  private List<String> accAvailabilityRoomTypes = new ArrayList<>();

  private Set<String> accAvailabilityRates = new HashSet<>();
}

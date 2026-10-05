package uk.co.whitbread.reservation.domain.properties;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "thirdpartybooking")
public class ThirdpartyBookingProperties {

  private Set<String> subchannel = new HashSet<>();

  private Set<String> providersExcluded = new HashSet<>();

  private int daysWithinArrival;
}

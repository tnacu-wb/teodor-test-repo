package uk.co.whitbread.booking.domain.properties;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ciol.rules")
public class CheckInOnlineProperties {

  private int maxRooms;
  private int daysWithinArrival;
  private Set<String> ciolRatesExcluded = new HashSet<>();
  private Set<String> ciolHotels = new HashSet<>();
  private Set<String> ciolCountryCodes = new HashSet<>();
  private Set<String> ciolEmails = new HashSet<>();
  private int deRegCardRooms;
  private Set<String> deRegCardHotels = new HashSet<>();

}

package uk.co.whitbread.reservation.domain.properties;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cool.rules")
public class CheckOutOnlineProperties {

  private int startingHour;
  private int daysUntilDeparture;
  private int endingHour;
  private int endingHourLco;
  private Set<String> coolReservationStatus = new HashSet<>();
  private Set<String> pilotHotels = new HashSet<>();

}

package uk.co.whitbread.booking.domain.properties;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "digitalkey.rules")
public class DigitalKeyProperties {

  private int maxRooms;
  private Set<String> dkHotels = new HashSet<>();

}

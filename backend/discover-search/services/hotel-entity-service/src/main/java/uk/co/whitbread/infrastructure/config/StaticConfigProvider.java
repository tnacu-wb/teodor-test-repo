package uk.co.whitbread.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.domain.logic.HotelAvailabilitySorter;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.AvailabilityProperties;


/**
 * This class initializes static configuration from the Spring context into the application.
 */
@Configuration
@RequiredArgsConstructor
public class StaticConfigProvider {

  private final AvailabilityProperties availabilityProperties;

  @PostConstruct
  public void initStaticConfiguration() {
    HotelAvailabilitySorter.setAvailabilityProperties(availabilityProperties);
  }
}

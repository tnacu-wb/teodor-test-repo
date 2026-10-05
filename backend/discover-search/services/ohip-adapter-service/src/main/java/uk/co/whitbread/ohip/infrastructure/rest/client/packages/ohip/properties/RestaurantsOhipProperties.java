package uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RestaurantsOhipProperties {

  private final String restaurantsEndpoint;

  public RestaurantsOhipProperties(@Value("${config.service.ohip.enterpriseEndpoint}") String restaurantsEndpoint) {
    this.restaurantsEndpoint = restaurantsEndpoint;
  }
}

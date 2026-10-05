package uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FrontDeskOhipProperties {

  private final String creditCardInfoEndpoint;

  public FrontDeskOhipProperties(
      @Value("${config.service.ohip.creditCardInfoEndpoint}") String creditCardInfoEndpoint) {
    this.creditCardInfoEndpoint = creditCardInfoEndpoint;
  }
}

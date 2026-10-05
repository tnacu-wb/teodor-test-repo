package uk.co.whitbread.ocd.infrastructure.rest.client.ocd.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class OcdOfferProperties {

  private final String propertyOfferEndpoint;

  public OcdOfferProperties(@Value("${config.service.ocd.propertyOfferEndpoint}")
                               String propertyOfferEndpoint) {
    this.propertyOfferEndpoint = propertyOfferEndpoint;
  }
}

package uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config.service.ohip")
public class RatePlansOhipProperties {

  private String ratePlansEndpoint;

  private String ratePlanPromoEndpoint;

  private String negotiatedRatesByProfileIdEndpoint;

  private String hubId;

  private String promotionCodeEndpoint;

}
 
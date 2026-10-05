package uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipAdapterProperties {

  private String host;
  private String reservationBillingAddressEndpoint;
  private String customReferenceNumberEndpoint;
  private String updateReservationCcAgentIdEndpoint;
  private String getNegotiatedRatesEndpoint;
  private String updateCharacterUdfsEndpoint;
  private String reservationsPaymentTypeByReservationIds;
  private String saveReservationPreRegister;
  private String getReservationByBasketEndpoint;
}


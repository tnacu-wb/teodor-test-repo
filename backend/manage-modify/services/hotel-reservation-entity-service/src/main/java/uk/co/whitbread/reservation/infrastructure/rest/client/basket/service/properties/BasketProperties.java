package uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.basket")
public class BasketProperties {

  private String host;
  private String basketEndpoint;
  private String basketByReferenceEndpoint;
  private String createEndpoint;
  private String addItemEndpoint;
  private String cancelEndpoint;
  private String stayItemType;
  private List<String> stayConfirmationData;
  private String refundEndpoint;
  private String removeItemEndpoint;
  private String linkAmendReservations;
  private String saveChargesEndpoint;
  private String chargesByReservationIdEndpoint;
  private String emailConfirmationEndpoint;
  private String initiatePaymentEndpoint;
  private String processAmendEndpoint;
  private String erroredBookingEndpoint;
  private String initiateCcuiPaymentProcess;
  private String updateAllowancesEndpoint;
  private String updateOccupancySupplementEndpoint;
  private String chargesByReservationIdsEndpoint;
  private String removeItemsEndpoint;
  private String changeStatusEndpoint;
  private String addPromotionToBasketEndpoint;
  private String changeIdContextEndpoint;
  private String createReservationEndpoint;
}

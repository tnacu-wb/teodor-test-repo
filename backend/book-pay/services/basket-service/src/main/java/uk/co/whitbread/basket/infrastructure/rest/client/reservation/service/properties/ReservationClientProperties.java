package uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.reservation")
public class ReservationClientProperties {

  private String host;
  private String reservationEndpoint;
  private String discountEndpoint;
  private String businessItemsEndpoint;
  private String companyQuestionAndAnswerEndpoint;
  private String depositsEndpoint;
  private String marketingPreferencesEndpoint;
  private String specialRequestsEndpoint;
  private String attachProfileToReservationsEndpoint;
  private String routingInstructionsEndpoint;
  private String updateReservationEndPoint;
  private String createProfilesEndPoint;
  private String updateAlertsEndPoint;
  private String previewDepositsEndpoint;
  private String saveDepositFoliosEndpoint;
}

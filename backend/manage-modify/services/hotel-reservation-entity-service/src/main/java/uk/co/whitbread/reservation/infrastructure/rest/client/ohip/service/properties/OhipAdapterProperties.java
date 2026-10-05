package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipAdapterProperties {

  private String host;
  private Integer connectionTimeoutMillis;
  private Integer responseTimeoutMillis;
  private String amendSummaryDetails;
  private String bookingAllowancesEndpoint;
  private String cancelEndpoint;
  private String cancelOnHoldReservationEndpoint;
  private String cancelPoliciesEndpoint;
  private String cancelReservationEndpoint;
  private String charityPackagesDetailsEndpoint;
  private String confirmAmend;
  private String confirmAmendForSingleCall;
  private String confirmReservationEndpoint;
  private String copyReservationsEndpoint;
  private String createMemoEndpoint;
  private String depositsEndpoint;
  private String externalReservationEndpoint;
  private String reservationIdEndpoint;
  private String generatedDepositFoliosEndpoint;
  private String hotelInfoEndpoint;
  private String marketingPreferencesEndpoint;
  private String moveReservationPaymentEndpoint;
  private String ratePlansEndpoint;
  private String reservationBookerEndpoint;
  private String reservationEndpoint;
  private String reservationGuestEndpoint;
  private String reservationsByBasketReservationIds;
  private String reservationsPackagesEndpoint;
  private String routingInstructionsEndpoint;
  private String searchBookingsEndpoint;
  private String updateCompanyQuestionAndAnswerRequestEndpoint;
  private String availabilityByIdsEndpoint;
  private String availabilityByIdsEndpointV2;
  private String updateBusinessItemsEndpoint;
  private String updateDiscountEndpoint;
  private String updateEmailReservationEndpoint;
  private String updateReasonForStayEndpoint;
  private String updateReservationOverrideReasonsEndpoint;
  private String updateReservationCcAgentIdEndpoint;
  private String updateReservationRateCodeEndpoint;
  private String updateReservations;
  private String updateSpecialRequestsEndpoint;
  private String memosEndpoint;
  private String attachProfileToReservationsEndpoint;
  private String packagesEndpoint;
  private String changeLogEndpoint;
  private String updateReservationSingleCall;
  private String createProfilesEndPoint;
  private String fileAttachmentEndpoint;
  private String preCheckInStatus;
  private String updateExternalReferenceEndpoint;
  private String reservationsPackagesScheduledEndpoint;
  private String linkReservationToLeisureCustomerEndpoint;
  private String reservationsPreferencesEndpoint;
  private String reservationsAlertsEndpoint;
  private String updateCancellationPoliciesEndpoint;
  private String updateRoomTypeEndpoint;
  private String updateUdfc20Endpoint;
}


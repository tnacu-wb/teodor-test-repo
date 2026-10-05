package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ReservationOhipProperties {

  private final String defaultProfileId;
  private final String reservationEndpoint;
  private final String fileAttachmentEndpoint;
  private final String preCheckInStatusEndpoint;
  private final String deleteFileAttachmentEndpoint;
  private final String reservationIdEndpoint;
  private final String cancellationPoliciesEndpoint;
  private final String depositFoliosEndpoint;
  private final String contextId;
  private final String amadeusDistributionContextId;
  private final String travelPortDistributionContextId;
  private final String hubId;
  private final String defaultHotelId;
  private final String guaranteeCode;
  private final Integer maxConcurrency;
  private final Long nonDigitalThreadSleep;
  private final Long updateCcAgentIdThreadSleep;
  private final Long fetchReservationThreadSleep;
  private final String confirmationGuaranteeCode;
  private final String depositReceivedGuaranteeCode;
  private final String nonGuaranteeCode;
  private final String companyGuaranteeCode;
  private final String defaultPaymentMethod;
  private final String defaultPaymentMethodDs;
  private final Integer folio;
  private final BigDecimal depositCashierId;
  private final String cancellationEndpoint;
  private final String hotelConfig;
  private final String cancelPoliciesEndpoint;
  private final String policySchedulesEndpoint;
  private final String deleteReservationEndpoint;

  private final String profilesEndpoint;
  private final String externalReservationEndpoint;
  private final String rateInfoEndpoint;
  private final String cotSpecialRequest;
  private final String defaultMarketCode;
  private final List<String> nonDigitalPaymentHotels;

  private final String depositsEndpoint;
  private final String routingInstructions;

  private final String foliosEndPoint;

  private final String isUdf08Enabled;

  public ReservationOhipProperties(
      @Value("${config.service.ohip.defaultProfileId}") String defaultProfileId,
      @Value("${config.service.ohip.reservationEndpoint}") String reservationEndpoint,
      @Value("${config.service.ohip.fileAttachmentEndpoint}") String fileAttachmentEndpoint,
      @Value("${config.service.ohip.preCheckInStatusEndpoint}") String preCheckInStatusEndpoint,
      @Value("${config.service.ohip.deleteFileAttachmentEndpoint}") String deleteFileAttachmentEndpoint,
      @Value("${config.service.ohip.reservationIdEndpoint}") String reservationIdEndpoint,
      @Value("${config.service.ohip.cancellationPoliciesEndpoint}") String cancellationPoliciesEndpoint,
      @Value("${config.service.ohip.depositFoliosEndpoint}") String depositFoliosEndpoint,
      @Value("${config.service.ohip.contextId}") String contextId,
      @Value("${config.service.ohip.amadeusDistributionContextId}") String amadeusDistributionContextId,
      @Value("${config.service.ohip.travelPortDistributionContextId}") String travelPortDistributionContextId,
      @Value("${config.service.ohip.guaranteeCode}") String guaranteeCode,
      @Value("${config.service.ohip.maxConcurrency}") Integer maxConcurrency,
      @Value("${config.service.ohip.nonDigitalThreadSleep}") Long nonDigitalThreadSleep,
      @Value("${config.service.ohip.fetchReservationThreadSleep}") Long fetchReservationThreadSleep,
      @Value("${config.service.ohip.updateCcAgentIdThreadSleep}") Long updateCcAgentIdThreadSleep,
      @Value("${config.service.ohip.confirmationGuaranteeCode}") String confirmationGuaranteeCode,
      @Value("${config.service.ohip.depositReceivedGuaranteeCode}") String depositReceivedGuaranteeCode,
      @Value("${config.service.ohip.nonGuaranteeCode}") String nonGuaranteeCode,
      @Value("${config.service.ohip.companyGuaranteeCode}") String companyGuaranteeCode,
      @Value("${config.service.ohip.defaultPaymentMethod}") String defaultPaymentMethod,
      @Value("${config.service.ohip.defaultPaymentMethodDs}") String defaultPaymentMethodDs,
      @Value("${config.service.ohip.folio}") Integer folio,
      @Value("${config.service.ohip.depositCashierId}") BigDecimal depositCashierId,
      @Value("${config.service.ohip.cancellationEndpoint}") String cancellationEndpoint,
      @Value("${config.service.ohip.hotelConfigEndpoint}") String hotelConfig,
      @Value("${config.service.ohip.profilesEndpoint}") String profilesEndpoint,
      @Value("${config.service.ohip.hubId}") String hubId,
      @Value("${config.service.ohip.defaultHotelId}") String defaultHotelId,
      @Value("${config.service.ohip.externalReservationEndpoint}") String externalReservationEndpoint,
      @Value("${config.service.ohip.rateInfoEndpoint}") String rateInfoEndpoint,
      @Value("${config.service.ohip.cotSpecialRequest}") String cotSpecialRequest,
      @Value("${config.service.ohip.defaultMarketCode}") String defaultMarketCode,
      @Value("${config.service.ohip.nonDigitalPaymentHotels}") String nonDigitalPaymentHotels,
      @Value("${config.service.ohip.depositsEndpoint}") String depositsEndpoint,
      @Value("${config.service.ohip.cancelPoliciesEndpoint}") String cancelPoliciesEndpoint,
      @Value("${config.service.ohip.policySchedulesEndpoint}") String policySchedulesEndpoint,
      @Value("${config.service.ohip.deleteReservationEndpoint}") String deleteReservationEndpoint,
      @Value("${config.service.ohip.routingInstructions}") String routingInstructions,
      @Value("${config.service.ohip.foliosEndPoint}") String foliosEndPoint,
      @Value("${config.service.ohip.isUdf08Enabled}") String isUdf08Enabled) {
    this.defaultProfileId = defaultProfileId;
    this.reservationEndpoint = reservationEndpoint;
    this.fileAttachmentEndpoint = fileAttachmentEndpoint;
    this.preCheckInStatusEndpoint = preCheckInStatusEndpoint;
    this.deleteFileAttachmentEndpoint = deleteFileAttachmentEndpoint;
    this.reservationIdEndpoint = reservationIdEndpoint;
    this.cancellationPoliciesEndpoint = cancellationPoliciesEndpoint;
    this.depositFoliosEndpoint = depositFoliosEndpoint;
    this.contextId = contextId;
    this.amadeusDistributionContextId = amadeusDistributionContextId;
    this.travelPortDistributionContextId = travelPortDistributionContextId;
    this.guaranteeCode = guaranteeCode;
    this.maxConcurrency = maxConcurrency;
    this.nonDigitalThreadSleep = nonDigitalThreadSleep;
    this.fetchReservationThreadSleep = fetchReservationThreadSleep;
    this.updateCcAgentIdThreadSleep = updateCcAgentIdThreadSleep;
    this.confirmationGuaranteeCode = confirmationGuaranteeCode;
    this.depositReceivedGuaranteeCode = depositReceivedGuaranteeCode;
    this.nonGuaranteeCode = nonGuaranteeCode;
    this.companyGuaranteeCode = companyGuaranteeCode;
    this.defaultPaymentMethod = defaultPaymentMethod;
    this.defaultPaymentMethodDs = defaultPaymentMethodDs;
    this.folio = folio;
    this.depositCashierId = depositCashierId;
    this.cancellationEndpoint = cancellationEndpoint;
    this.hotelConfig = hotelConfig;
    this.profilesEndpoint = profilesEndpoint;
    this.hubId = hubId;
    this.externalReservationEndpoint = externalReservationEndpoint;
    this.rateInfoEndpoint = rateInfoEndpoint;
    this.cotSpecialRequest = cotSpecialRequest;
    this.defaultMarketCode = defaultMarketCode;
    this.nonDigitalPaymentHotels = setNonDigitalPaymentHotels(nonDigitalPaymentHotels);
    this.depositsEndpoint = depositsEndpoint;
    this.cancelPoliciesEndpoint = cancelPoliciesEndpoint;
    this.policySchedulesEndpoint = policySchedulesEndpoint;
    this.deleteReservationEndpoint = deleteReservationEndpoint;
    this.defaultHotelId = defaultHotelId;
    this.routingInstructions = routingInstructions;
    this.foliosEndPoint = foliosEndPoint;
    this.isUdf08Enabled = isUdf08Enabled;
  }

  private List<String> setNonDigitalPaymentHotels(String nonDigitalPaymentHotels) {
    return Arrays.asList(nonDigitalPaymentHotels.split(","))
        .stream().map(h -> h.replaceAll("\\W+", "").toUpperCase()).filter(h -> !h.isBlank()).toList();
  }
}



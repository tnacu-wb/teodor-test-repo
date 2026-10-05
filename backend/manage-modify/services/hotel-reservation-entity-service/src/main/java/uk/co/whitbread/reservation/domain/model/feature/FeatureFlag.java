package uk.co.whitbread.reservation.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature ccuiAmendPiba;
  private Feature saveAllowancesInBasket;
  private Feature useBasketAllowances;
  private Feature enableAmendPayNow;
  private Feature applyOccupancySupplement;
  private Feature piSearchByOperaConfirmation;
  private Feature ccuiSearchByOperaConfirmation;
  private Feature ccuiAgentIdLog;
  private Feature aemSearchRules;
  private Feature enableAbsoluteDeadline;
  private Feature maxRoomsAmend;
  private Feature releasePiBbMobileDeRegCard;
  private Feature releasePiBbMobileCheckOut;
  private Feature companyNameFeatureFlag;
  private Feature mobileCiolPiba;
  private Feature mobileCiolPibaCnp;
  private Feature releasePiCcuiCityTaxUk;
  private Feature mobileDigitalKey;
  private Feature mobileAcceptsOtaBooking;
  private Feature mobilePreRegisteredRepurpose;
  private Feature mobileCiolPrepaid3rdParty;
  private Feature setDefaultPaymentMethodDs;
  private Feature setCnpBookingAlerts;
}

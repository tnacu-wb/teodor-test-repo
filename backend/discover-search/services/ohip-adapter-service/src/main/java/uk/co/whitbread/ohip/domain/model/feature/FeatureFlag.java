package uk.co.whitbread.ohip.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature depositFolioPostAfterDisableOnHold;
  private Feature pibaBooking;
  private Feature fixedRate;
  private Feature captureBillingAddressBb;
  private Feature noDuplicateProfileCreationBb;
  private Feature noDuplicateProfileCreationCcui;
  private Feature noDuplicateProfileCreationPi;
  private Feature availabilityFromDifferentRoomClasses;
  private Feature flexRateStrikethroughBB;
  private Feature useTokenService;
  private Feature mobileAcceptsOtaBooking;
  private Feature distributionBookingFee;
  private Feature createReservationWithSoftBundles;
  private Feature useTokenRefreshSkew;
  private Feature consumptionDetailsDefaultQuantity;
  private Feature mobilePreRegisteredRepurpose;
  private Feature freeFnbExtras;
  private Feature setCnpBookingAlerts;
  private Feature setDefaultPaymentMethodDs;
  private Feature depositFolioCreationVerification;
}

package uk.co.whitbread.basket.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature saveAllowancesInBasket;
  private Feature savePaymentInstructionFolioThree;
  private Feature savePaymentInstructionFolioThreeBb;
  private Feature pibaBooking;
  private Feature ccuiAgentIdLog;
  private Feature captureBillingAddressBb;
  private Feature checkInOnline;
  private Feature threecpReturnCodesMapping;
  private Feature paypalErrorMapping;
  private Feature companyNameFeatureFlag;
  private Feature prepaidBookingChargesTtl;
  private Feature saveSecureBooking;
  private Feature redeemPromoCode;
  private Feature mobilePreRegisteredRepurpose;
  private Feature publishDatatransBookingCompletedEvent;
}
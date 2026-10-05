package uk.co.whitbread.payments.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {

  @Data
  public static class Feature {

    private String key;
    private boolean fallback;
  }

  private Feature disablePayments;
  private Feature ccuiDeEnablePaymentsWithin72h;
  private Feature ccuiUkEnablePaymentsWithin72h;
  private Feature enableHubHotelsPoa;
}
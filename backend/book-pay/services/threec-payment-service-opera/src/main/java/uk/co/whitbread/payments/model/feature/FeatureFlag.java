package uk.co.whitbread.payments.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature mockPaymentReturnCode;
  private Feature releasePibaCnpIframeSplit;
}
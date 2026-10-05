package uk.co.whitbread.avail.business.events.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature cityTaxUk;
  private Feature cityTaxUkFallback;
  private Feature useTokenRefreshSkew;
}

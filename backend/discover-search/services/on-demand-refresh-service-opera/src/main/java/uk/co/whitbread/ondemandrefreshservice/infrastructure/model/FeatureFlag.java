package uk.co.whitbread.ondemandrefreshservice.infrastructure.model;

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

package uk.co.whitbread.company.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature companyNameValidation;
  private Feature cdhApiDeprecation;
}

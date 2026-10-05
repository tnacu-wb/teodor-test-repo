package uk.co.whitbread.company.employee.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {

  private Feature cdhApiDeprecation;

  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

}

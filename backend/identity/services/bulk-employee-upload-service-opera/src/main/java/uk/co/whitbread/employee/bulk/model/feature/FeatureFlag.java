package uk.co.whitbread.employee.bulk.model.feature;

import lombok.Data;

@Data
@SuppressWarnings("java:S1118") // This annotation should be removed when adding a Feature Flag
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

}

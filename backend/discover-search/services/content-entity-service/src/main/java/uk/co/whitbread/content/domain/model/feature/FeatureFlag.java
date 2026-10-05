package uk.co.whitbread.content.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature fetchTripadvisorFeedback;
  private Feature freeFnbExtras;
}
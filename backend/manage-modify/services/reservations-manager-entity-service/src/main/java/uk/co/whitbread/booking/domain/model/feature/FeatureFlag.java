package uk.co.whitbread.booking.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature piBbMobileCheckInOnline;
  private Feature releasePiBbMobileDeRegCard;
  private Feature mobileCiolPiba;
  private Feature mobileCiolPibaCnp;
  private Feature mobileDigitalKey;
}
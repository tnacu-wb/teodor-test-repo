package uk.co.whitbread.availabilitycacheservice.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {

  private Feature releasePiCcuiCityTaxUk;
  private Feature releasePiCcuiCityTaxUkFallback;

  @Data
  public static class Feature {

    private String key;
    private boolean fallback;
  }
}

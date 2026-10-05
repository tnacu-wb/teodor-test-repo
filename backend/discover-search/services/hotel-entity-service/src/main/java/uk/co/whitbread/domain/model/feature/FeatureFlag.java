package uk.co.whitbread.domain.model.feature;

import lombok.Data;

@Data
public class FeatureFlag {
  @Data
  public static class Feature {
    private String key;
    private boolean fallback;
  }

  private Feature occupancySupplement;
  private Feature extrasItemsEciLcoPi;
  private Feature extrasItemsEciLcoBb;
  private Feature extrasItemsEciLcoCcui;
  private Feature extrasItemsUwfPi;
  private Feature extrasItemsUwfBb;
  private Feature extrasItemsUwfCcui;
  private Feature filterTwinOnPriority;
  private Feature aemSearchRules;
  private Feature showMlosCcui;
  private Feature extrasItemsBottleOfProsecco;
  private Feature releasePiCcuiCityTaxUk;
  private Feature releaseBbCityTaxUk;
  private Feature extrasItemsPi;
  private Feature extrasItemsBb;
  private Feature extrasItemsCcui;
  private Feature freeFnbExtrasPi;
  private Feature freeFnbExtrasBb;
  private Feature freeFnbExtrasCcui;
  private Feature piSrpDynamicFilters;
  private Feature companyRateSuppression;
  private Feature srpDynamicFilters;
}
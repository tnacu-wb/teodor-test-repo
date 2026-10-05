package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties;

import lombok.Data;

@Data
public class RecommendedSearchOption {

  private double hubModifier;
  private double dropOffModifier;
  private double dropOffPoint;
  private double distanceModifier;
  private double priceModifier;
}

package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {

  private String fullyBooked;
  private String availabilityWarning;
  private String openingSoon;
  private String openingOn;
  private String distanceUnitPlural;
  private String fromLocation;
  private String priceFrom;
  private String viewDetails;
  private Facilities facilities;
  private Promotions promotions;
}

package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {

  private String fullyBooked;
  private String availabilityWarning;
  private String openingSoon;
  private String openingOn;
  private String distanceUnitPlural;
  private String fromLocation;
  private String priceFrom;
  private String viewDetails;
  private FacilitiesDto facilities;
  private PromotionsDto promotions;
}

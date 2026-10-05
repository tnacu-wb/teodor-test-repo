package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteDto {
  private String id;
  private String name;
  private String aztecSiteReference;
  private String defaultOccasionId;
  private String timezone;
  private FeaturesDto features;

}

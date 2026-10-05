package uk.co.whitbread.reservation.domain.model.out.outlets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Site {
  private String id;
  private String name;
  private String aztecSiteReference;
  private String defaultOccasionId;
  private String timezone;
  private Features features;

}

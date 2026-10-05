package uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationsDto {
  private String locationId;
  private String locationName;
  private Integer locationOrder;
}

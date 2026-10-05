package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryAvailabilityDto {

  private String date;
  private Integer total;
  private Integer available;
}

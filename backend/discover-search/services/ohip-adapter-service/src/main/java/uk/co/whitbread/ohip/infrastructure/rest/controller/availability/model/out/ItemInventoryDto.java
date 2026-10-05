package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInventoryDto {

  private String description;
  private String code;
  private String name;
  private List<InventoryAvailabilityDto> inventories;
}

package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInventoryResponseDto {

  private List<ItemInventoryDto> itemsInventory;
}

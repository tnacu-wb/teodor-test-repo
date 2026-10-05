package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StayUpsellItemDto {

  private String roomId;
  private StayPriceDto unitCost;
  private StayPriceDto subtotal;
  private Integer quantity;
  private String code;
  private String legend;
  private String category;
}

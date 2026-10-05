package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StayRoomBreakdownDto {

  private String roomId;
  private StayPriceDto totalRoomCost;
  private StayPriceDto cityTax;
}

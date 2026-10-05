package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StayRoomDto {

  private String roomId;
  private String roomType;
  private Integer adults;
  private Integer children;
  private Boolean cot;
  private StayPriceDto roomCost;
  private String bookingStatus;
  private StayGuestDto guest;
}

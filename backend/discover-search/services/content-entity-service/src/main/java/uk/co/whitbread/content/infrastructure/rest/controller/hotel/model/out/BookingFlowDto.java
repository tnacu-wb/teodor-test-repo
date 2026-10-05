package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlowDto {

  private List<BookingFlowItemDto> bookingFlowItems;
  private List<TargetBookingFlowItemDto> targetBookingFlowItems;
}
